package com.example.buzzer1203.persistence.service;

import com.example.buzzer1203.persistence.model.PersistenceRequest;
import com.example.buzzer1203.persistence.util.FileUtil;
import com.example.buzzer1203.persistence.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 数据持久化服务类，负责将JSON数据写入文件并处理重复数据
 */
@Service
public class DataPersistenceService {

    private static final Logger logger = LoggerFactory.getLogger(DataPersistenceService.class);
    private static final String RESOURCES_DIR = "src/main/resources/";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    // 使用锁机制处理高并发场景
    private final Map<String, Lock> fileLocks = new HashMap<>();

    /**
     * 将数据持久化到文件
     * @param request 持久化请求
     * @return 操作结果
     */
    public boolean persistData(PersistenceRequest request) {
        String baseFileName = request.getFileName();
        String currentDate = LocalDate.now().format(DATE_FORMATTER);
        String fullFileName = baseFileName + "_" + currentDate + ".json";
        String filePath = RESOURCES_DIR + fullFileName;

        // 获取文件锁，处理高并发
        Lock lock = getFileLock(baseFileName);
        lock.lock();

        try {
            return executeWithRetry(() -> doPersistData(request, filePath, baseFileName));
        } finally {
            lock.unlock();
        }
    }

    /**
     * 实际执行数据持久化操作
     * @param request 持久化请求
     * @param filePath 文件路径
     * @param baseFileName 基础文件名
     * @return 操作结果
     * @throws IOException IO异常
     */
    private boolean doPersistData(PersistenceRequest request, String filePath, String baseFileName) throws IOException {
        String jsonData = request.getJsonData();
        boolean checkDuplicate = request.isCheckDuplicate();
        List<String> checkFields = request.getCheckFields();
        PersistenceRequest.DuplicateAction duplicateAction = request.getDuplicateAction();

        // 验证JSON数据是否有效
        if (!JsonUtil.isValidJson(jsonData)) {
            logger.error("Invalid JSON data: {}", jsonData);
            return false;
        }

        // 如果需要检查重复数据
        if (checkDuplicate && checkFields != null && !checkFields.isEmpty()) {
            // 检查所有日期下的同名文件
            List<File> allFiles = FileUtil.findFilesByBaseName(RESOURCES_DIR, baseFileName);

            // 解析JSON数据
            Map<String, Object> newData = JsonUtil.parseJsonToMap(jsonData);
            if (newData == null) {
                logger.error("Invalid JSON data: {}", jsonData);
                return false;
            }

            // 检查重复数据
            for (File file : allFiles) {
                List<Map<String, Object>> existingDataList = JsonUtil.parseJsonArrayFromFile(file.getAbsolutePath());
                if (existingDataList != null) {
                    for (Map<String, Object> existingData : existingDataList) {
                        if (isDuplicate(newData, existingData, checkFields)) {
                            logger.info("Duplicate data found in file: {}", file.getName());
                            return handleDuplicateAction(request, file, newData, duplicateAction);
                        }
                    }
                }
            }
        }

        // 没有重复数据或不需要检查，直接写入当前文件
        return writeDataToFile(filePath, jsonData);
    }

    /**
     * 检查两条数据是否重复
     * @param newData 新数据
     * @param existingData 已存在数据
     * @param checkFields 检查字段
     * @return 是否重复
     */
    private boolean isDuplicate(Map<String, Object> newData, Map<String, Object> existingData, List<String> checkFields) {
        for (String field : checkFields) {
            Object newValue = newData.get(field);
            Object existingValue = existingData.get(field);

            if (newValue == null && existingValue == null) {
                continue;
            }

            if (newValue == null || existingValue == null || !newValue.equals(existingValue)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 处理重复数据的动作
     * @param request 持久化请求
     * @param file 包含重复数据的文件
     * @param newData 新数据
     * @param duplicateAction 重复动作
     * @return 操作结果
     * @throws IOException IO异常
     */
    private boolean handleDuplicateAction(PersistenceRequest request, File file, Map<String, Object> newData, PersistenceRequest.DuplicateAction duplicateAction) throws IOException {
        switch (duplicateAction) {
            case INSERT_ONLY:
                // 只做新增，忽略重复数据
                logger.info("Duplicate action: INSERT_ONLY, ignoring duplicate and inserting new data");
                // 获取当前文件路径
                String currentFileName = getCurrentFileName(request.getFileName());
                String currentFilePath = RESOURCES_DIR + currentFileName;
                // 执行新增动作
                return writeDataToFile(currentFilePath, request.getJsonData());
            case INSERT_AND_DELETE_OLD:
                // 新增同时删除旧的数据
                logger.info("Duplicate action: INSERT_AND_DELETE_OLD");
                List<Map<String, Object>> existingDataList = JsonUtil.parseJsonArrayFromFile(file.getAbsolutePath());
                if (existingDataList != null) {
                    // 删除旧数据
                    existingDataList.removeIf(data -> isDuplicate(data, newData, request.getCheckFields()));
                    // 写入更新后的数据
                    String updatedJson = JsonUtil.convertToJson(existingDataList);
                    Files.write(Paths.get(file.getAbsolutePath()), updatedJson.getBytes());
                }
                // 写入新数据到当前文件
                return writeDataToFile(RESOURCES_DIR + getCurrentFileName(request.getFileName()), request.getJsonData());
            case UPDATE:
                // 修改旧数据
                logger.info("Duplicate action: UPDATE");
                existingDataList = JsonUtil.parseJsonArrayFromFile(file.getAbsolutePath());
                if (existingDataList != null) {
                    for (int i = 0; i < existingDataList.size(); i++) {
                        if (isDuplicate(existingDataList.get(i), newData, request.getCheckFields())) {
                            existingDataList.set(i, newData);
                            String updatedJson = JsonUtil.convertToJson(existingDataList);
                            Files.write(Paths.get(file.getAbsolutePath()), updatedJson.getBytes());
                            return true;
                        }
                    }
                }
                return false;
            case NO_ACTION:
                // 不做处理
                logger.info("Duplicate action: NO_ACTION, skipping data");
                return true;
            default:
                logger.error("Unknown duplicate action: {}", duplicateAction);
                return false;
        }
    }

    /**
     * 将数据写入文件
     * @param filePath 文件路径
     * @param jsonData JSON数据
     * @return 操作结果
     * @throws IOException IO异常
     */
    private boolean writeDataToFile(String filePath, String jsonData) throws IOException {
        Path path = Paths.get(filePath);
        List<String> lines = new ArrayList<>();

        // 如果文件存在，读取现有内容
        if (Files.exists(path)) {
            lines = Files.readAllLines(path);
        }

        // 添加新数据
        lines.add(jsonData);

        // 写入文件
        Files.write(path, lines);
        logger.info("Data written to file: {}", filePath);
        return true;
    }

    /**
     * 获取当前日期的文件名
     * @param baseFileName 基础文件名
     * @return 当前日期的文件名
     */
    private String getCurrentFileName(String baseFileName) {
        String currentDate = LocalDate.now().format(DATE_FORMATTER);
        return baseFileName + "_" + currentDate + ".json";
    }

    /**
     * 获取文件锁
     * @param baseFileName 基础文件名
     * @return 文件锁
     */
    private Lock getFileLock(String baseFileName) {
        synchronized (fileLocks) {
            return fileLocks.computeIfAbsent(baseFileName, k -> new ReentrantLock());
        }
    }

    /**
     * 带重试机制的执行器
     * @param task 任务
     * @return 操作结果
     */
    private boolean executeWithRetry(CallableWithException<Boolean> task) {
        for (int retry = 0; retry < MAX_RETRIES; retry++) {
            try {
                return task.call();
            } catch (Exception e) {
                logger.error("Persistence failed (attempt {} of {}): {}", retry + 1, MAX_RETRIES, e.getMessage());
                if (retry < MAX_RETRIES - 1) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 带异常的Callable接口
     */
    @FunctionalInterface
    private interface CallableWithException<T> {
        T call() throws Exception;
    }
}
