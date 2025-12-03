package com.buzzer.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * 数据持久化服务类，用于将JSON数据写入resources目录下的文件
 * 支持重复校验、重试机制和分布式场景处理
 */
public class DataPersistenceService {

    private static final Logger logger = LoggerFactory.getLogger(DataPersistenceService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    private static final int MAX_RETRY_COUNT = 3;
    private static final long RETRY_INTERVAL_MS = 1000;

    // 分布式锁前缀，实际使用时应替换为Redis等分布式锁实现
    private static final String DISTRIBUTED_LOCK_PREFIX = "data-persistence-lock-";

    // 本地锁，用于单机高并发场景
    private final Map<String, ReentrantLock> localLocks = new HashMap<>();

    /**
     * 重复数据处理策略枚举
     */
    public enum DuplicateStrategy {
        IGNORE,      // 不做处理
        INSERT,      // 只做新增（允许重复）
        INSERT_DELETE_OLD,  // 新增同时删除旧数据
        UPDATE       // 修改旧数据
    }

    /**
     * 写入数据到指定文件
     *
     * @param jsonData          JSON格式的数据
     * @param fileName          基础文件名（不包含日期）
     * @param checkDuplicate    是否校验数据重复
     * @param duplicateFields   用于判断重复的字段集合
     * @param duplicateStrategy 重复数据处理策略
     * @return 是否写入成功
     * @throws JsonProcessingException 如果JSON处理失败
     */
    public boolean writeData(String jsonData, String fileName, boolean checkDuplicate, 
                            Set<String> duplicateFields, DuplicateStrategy duplicateStrategy) 
                            throws JsonProcessingException {
        // 解析JSON数据
        JsonNode dataNode = objectMapper.readTree(jsonData);
        String today = dateFormat.format(new Date());
        String timestamp = String.valueOf(System.currentTimeMillis());
        String fullFileName = fileName + "_" + today + "_" + timestamp + ".json";

        // 获取resources目录路径
        String resourcesPath = getResourcesPath();
        File targetFile = new File(resourcesPath, fullFileName);

        // 获取锁（本地锁，分布式场景需要替换为Redis锁）
        ReentrantLock lock = getLock(fullFileName);
        lock.lock();

        try {
            // 检查重复数据
            if (checkDuplicate && duplicateFields != null && !duplicateFields.isEmpty()) {
                boolean hasDuplicate = checkDuplicateData(dataNode, fileName, duplicateFields);
                if (hasDuplicate) {
                    switch (duplicateStrategy) {
                        case IGNORE:
                            logger.info("数据已存在，不做处理: {}", duplicateFields);
                            return false;
                        case INSERT:
                            logger.info("数据已存在，仍然新增: {}", duplicateFields);
                            break;
                        case INSERT_DELETE_OLD:
                            logger.info("数据已存在，新增同时删除旧数据: {}", duplicateFields);
                            deleteDuplicateData(dataNode, fileName, duplicateFields);
                            break;
                        case UPDATE:
                            logger.info("数据已存在，修改旧数据: {}", duplicateFields);
                            updateDuplicateData(dataNode, fileName, duplicateFields);
                            return true;
                        default:
                            logger.warn("未知的重复处理策略: {}", duplicateStrategy);
                            return false;
                    }
                }
            }

            // 写入数据（带重试机制）
            return writeWithRetry(targetFile, jsonData);

        } catch (IOException e) {
            logger.error("写入数据失败: {}", e.getMessage(), e);
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取resources目录的绝对路径
     *
     * @return resources目录路径
     */
    private String getResourcesPath() {
        ClassLoader classLoader = getClass().getClassLoader();
        File resourcesDir = new File(classLoader.getResource("").getFile());
        return resourcesDir.getAbsolutePath();
    }

    /**
     * 获取文件对应的锁
     *
     * @param fileName 文件名
     * @return ReentrantLock实例
     */
    private ReentrantLock getLock(String fileName) {
        synchronized (localLocks) {
            return localLocks.computeIfAbsent(fileName, k -> new ReentrantLock());
        }
    }

    /**
     * 检查是否存在重复数据
     *
     * @param dataNode        要检查的数据节点
     * @param fileName        基础文件名
     * @param duplicateFields 重复校验字段
     * @return 是否存在重复数据
     * @throws IOException 如果文件读取失败
     */
    private boolean checkDuplicateData(JsonNode dataNode, String fileName, Set<String> duplicateFields) 
            throws IOException {
        // 获取所有同名文件（包含所有日期）
        List<File> files = getAllFilesWithName(fileName);

        for (File file : files) {
            String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            JsonNode existingNode = objectMapper.readTree(content);

            // 检查所有指定字段是否都相同
            boolean isDuplicate = true;
            for (String field : duplicateFields) {
                JsonNode dataField = dataNode.get(field);
                JsonNode existingField = existingNode.get(field);

                if (dataField == null || existingField == null || !dataField.equals(existingField)) {
                    isDuplicate = false;
                    break;
                }
            }

            if (isDuplicate) {
                return true;
            }
        }

        return false;
    }

    /**
     * 删除重复数据
     *
     * @param dataNode        要删除的数据节点
     * @param fileName        基础文件名
     * @param duplicateFields 重复校验字段
     * @throws IOException 如果文件操作失败
     */
    private void deleteDuplicateData(JsonNode dataNode, String fileName, Set<String> duplicateFields) 
            throws IOException {
        List<File> files = getAllFilesWithName(fileName);

        for (File file : files) {
            String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            JsonNode existingNode = objectMapper.readTree(content);

            boolean isDuplicate = true;
            for (String field : duplicateFields) {
                JsonNode dataField = dataNode.get(field);
                JsonNode existingField = existingNode.get(field);

                if (dataField == null || existingField == null || !dataField.equals(existingField)) {
                    isDuplicate = false;
                    break;
                }
            }

            if (isDuplicate) {
                FileUtils.deleteQuietly(file);
                logger.info("已删除重复数据文件: {}", file.getName());
            }
        }
    }

    /**
     * 更新重复数据
     *
     * @param dataNode        要更新的数据节点
     * @param fileName        基础文件名
     * @param duplicateFields 重复校验字段
     * @throws IOException 如果文件操作失败
     */
    private void updateDuplicateData(JsonNode dataNode, String fileName, Set<String> duplicateFields) 
            throws IOException {
        List<File> files = getAllFilesWithName(fileName);

        for (File file : files) {
            String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            JsonNode existingNode = objectMapper.readTree(content);

            boolean isDuplicate = true;
            for (String field : duplicateFields) {
                JsonNode dataField = dataNode.get(field);
                JsonNode existingField = existingNode.get(field);

                if (dataField == null || existingField == null || !dataField.equals(existingField)) {
                    isDuplicate = false;
                    break;
                }
            }

            if (isDuplicate) {
                FileUtils.writeStringToFile(file, objectMapper.writeValueAsString(dataNode), 
                                           StandardCharsets.UTF_8, false);
                logger.info("已更新重复数据文件: {}", file.getName());
            }
        }
    }

    /**
     * 获取所有同名文件（包含所有日期）
     *
     * @param fileName 基础文件名
     * @return 同名文件列表
     */
    private List<File> getAllFilesWithName(String fileName) {
        String resourcesPath = getResourcesPath();
        File resourcesDir = new File(resourcesPath);

        if (!resourcesDir.exists() || !resourcesDir.isDirectory()) {
            return Collections.emptyList();
        }

        // 匹配文件名格式：fileName_yyyyMMdd_timestamp.json
        String pattern = fileName + "_[0-9]{8}_[0-9]+" + ".json";

        return Arrays.stream(resourcesDir.listFiles())
                .filter(file -> file.isFile() && file.getName().matches(pattern))
                .collect(Collectors.toList());
    }

    /**
     * 带重试机制的文件写入
     *
     * @param targetFile 目标文件
     * @param content    写入内容
     * @return 是否写入成功
     * @throws IOException 如果多次重试后仍然失败
     */
    private boolean writeWithRetry(File targetFile, String content) throws IOException {
        for (int retry = 0; retry < MAX_RETRY_COUNT; retry++) {
            try {
                // 创建父目录（如果不存在）
                FileUtils.forceMkdirParent(targetFile);
                // 写入文件
                FileUtils.writeStringToFile(targetFile, content, StandardCharsets.UTF_8, false);
                logger.info("数据写入成功: {}", targetFile.getName());
                return true;
            } catch (IOException e) {
                logger.error("第{}次写入失败: {}", retry + 1, e.getMessage());
                if (retry < MAX_RETRY_COUNT - 1) {
                    try {
                        Thread.sleep(RETRY_INTERVAL_MS);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        throw new IOException("写入操作被中断", ex);
                    }
                }
            }
        }

        throw new IOException("多次重试后写入仍然失败: " + targetFile.getName());
    }
}