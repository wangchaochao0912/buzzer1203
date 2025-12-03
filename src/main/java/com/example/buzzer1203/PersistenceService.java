package com.example.buzzer1203;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PersistenceService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    private final ConcurrentHashMap<String, ReentrantLock> fileLocks = new ConcurrentHashMap<>();

    // 重复动作枚举
    public enum DuplicateAction {
        ONLY_INSERT,       // 只做新增
        INSERT_AND_DELETE, // 新增同时删除旧的数据
        UPDATE,             // 修改
        NO_ACTION           // 不做处理
    }

    // 锁超时时间配置（毫秒），可根据未来业务扩展调整
    private static final long LOCK_TIMEOUT_MS = 10000; // 10秒，比原来的5秒增加了一倍

    /**
     * 数据写入方法
     * @param jsonData JSON格式的数据
     * @param fileName 指定文件名
     * @param checkDuplicate 是否校验数据重复
     * @param duplicateFields 判断重复所校验的字段集合
     * @param duplicateAction 重复后的动作
     * @throws IOException
     * @throws InterruptedException
     */
    public void writeData(String jsonData, String fileName, boolean checkDuplicate, 
                           Set<String> duplicateFields, DuplicateAction duplicateAction) 
            throws IOException, InterruptedException {

        // 重试机制：最多重试3次
        int retryCount = 0;
        while (retryCount < 3) {
            try {
                // 1. 解析JSON数据
                Map<String, Object> dataMap = objectMapper.readValue(jsonData, Map.class);

                // 2. 获取resources目录路径
                String resourcesPath = Objects.requireNonNull(
                        getClass().getClassLoader().getResource("")).getPath();

                // 3. 检查是否需要校验重复
                if (checkDuplicate) {
                    // 查找所有同名文件（包括所有日期）
                    File resourcesDir = new File(resourcesPath);
                    Pattern pattern = Pattern.compile(fileName + "_(\\d{8})\\.json");
                    List<File> matchedFiles = new ArrayList<>();

                    if (resourcesDir.exists() && resourcesDir.isDirectory()) {
                        File[] files = resourcesDir.listFiles();
                        if (files != null) {
                            for (File file : files) {
                                Matcher matcher = pattern.matcher(file.getName());
                                if (matcher.matches()) {
                                    matchedFiles.add(file);
                                }
                            }
                        }
                    }

                    // 检查是否有重复数据
                    for (File file : matchedFiles) {
                        String fileContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                        List<Map<String, Object>> existingDataList = objectMapper.readValue(fileContent, List.class);

                        for (int i = 0; i < existingDataList.size(); i++) {
                            Map<String, Object> existingData = existingDataList.get(i);
                            boolean isDuplicate = true;

                            // 检查所有指定的重复字段
                            for (String field : duplicateFields) {
                                if (!Objects.equals(existingData.get(field), dataMap.get(field))) {
                                    isDuplicate = false;
                                    break;
                                }
                            }

                            if (isDuplicate) {
                                // 处理重复数据
                                switch (duplicateAction) {
                                    case ONLY_INSERT:
                                        // 只做新增，不处理重复数据
                                        break;
                                    case INSERT_AND_DELETE:
                                        // 新增同时删除旧的数据
                                        existingDataList.remove(i);
                                        FileUtils.writeStringToFile(file, objectMapper.writeValueAsString(existingDataList), StandardCharsets.UTF_8);
                                        break;
                                    case UPDATE:
                                        // 修改旧的数据
                                        existingDataList.set(i, dataMap);
                                        FileUtils.writeStringToFile(file, objectMapper.writeValueAsString(existingDataList), StandardCharsets.UTF_8);
                                        return; // 修改完成，直接返回
                                    case NO_ACTION:
                                        // 不做处理，直接返回
                                        return;
                                }
                            }
                        }
                    }
                }

                // 4. 生成新的文件名（文件名+当前年月日）
                String currentDate = dateFormat.format(new Date());
                String newFileName = fileName + "_" + currentDate + ".json";
                File newFile = new File(resourcesPath + File.separator + newFileName);

                // 5. 获取文件锁，确保线程安全
                ReentrantLock lock = fileLocks.computeIfAbsent(newFileName, k -> new ReentrantLock());
                boolean locked = false;
                try {
                    // 设置锁超时时间，从配置常量读取，方便未来调整
                    locked = lock.tryLock(LOCK_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                    if (locked) {
                        // 6. 读取或创建文件内容
                        List<Map<String, Object>> dataList;
                        if (newFile.exists()) {
                            String fileContent = FileUtils.readFileToString(newFile, StandardCharsets.UTF_8);
                            dataList = objectMapper.readValue(fileContent, List.class);
                        } else {
                            dataList = new ArrayList<>();
                        }

                        // 7. 添加新数据
                        dataList.add(dataMap);

                        // 8. 写入文件
                        FileUtils.writeStringToFile(newFile, objectMapper.writeValueAsString(dataList), StandardCharsets.UTF_8);

                        // 写入成功，跳出重试循环
                        break;
                    } else {
                        // 锁超时，抛出异常
                        throw new RuntimeException("获取文件锁超时: " + LOCK_TIMEOUT_MS + "ms");
                    }
                } finally {
                    if (locked) {
                        lock.unlock();
                    }
                }

            } catch (Exception e) {
                retryCount++;
                if (retryCount == 3) {
                    // 重试3次失败，抛出异常
                    throw e;
                }
                // 等待一段时间后重试
                Thread.sleep(1000 * retryCount); // 每次重试等待时间递增
            }
        }
    }
}
