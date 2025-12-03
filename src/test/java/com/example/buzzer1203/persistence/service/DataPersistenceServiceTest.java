package com.example.buzzer1203.persistence.service;

import com.example.buzzer1203.persistence.model.PersistenceRequest;
import com.example.buzzer1203.persistence.util.FileUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 数据持久化服务单元测试
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DataPersistenceServiceTest {

    @Autowired
    private DataPersistenceService dataPersistenceService;

    private static final String TEST_FILE_NAME = "test_data";
    private static final String RESOURCES_DIR = "src/main/resources/";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 测试前准备：删除所有测试文件
     */
    @BeforeEach
    public void setUp() {
        deleteTestFiles();
    }

    /**
     * 测试后清理：删除所有测试文件
     */
    @AfterEach
    public void tearDown() {
        deleteTestFiles();
    }

    /**
     * 测试基本数据持久化功能（不检查重复）
     */
    @Test
    public void testPersistDataWithoutDuplicateCheck() {
        String jsonData = "{\"id\": 1, \"name\": \"test\", \"value\": \"test_value\"}";
        PersistenceRequest request = new PersistenceRequest(
                jsonData,
                TEST_FILE_NAME,
                false,
                null,
                PersistenceRequest.DuplicateAction.INSERT_ONLY
        );

        boolean result = dataPersistenceService.persistData(request);
        assertTrue(result, "Data should be persisted successfully");

        // 验证文件是否创建
        String currentDate = LocalDate.now().format(DATE_FORMATTER);
        String expectedFileName = TEST_FILE_NAME + "_" + currentDate + ".json";
        File file = new File(RESOURCES_DIR + expectedFileName);
        assertTrue(file.exists(), "File should be created");
    }

    /**
     * 测试数据持久化功能（检查重复，无重复数据）
     */
    @Test
    public void testPersistDataWithDuplicateCheckNoDuplicate() {
        String jsonData = "{\"id\": 2, \"name\": \"test2\", \"value\": \"test_value2\"}";
        PersistenceRequest request = new PersistenceRequest(
                jsonData,
                TEST_FILE_NAME,
                true,
                Arrays.asList("id"),
                PersistenceRequest.DuplicateAction.INSERT_ONLY
        );

        boolean result = dataPersistenceService.persistData(request);
        assertTrue(result, "Data should be persisted successfully");

        // 验证文件是否创建
        String currentDate = LocalDate.now().format(DATE_FORMATTER);
        String expectedFileName = TEST_FILE_NAME + "_" + currentDate + ".json";
        File file = new File(RESOURCES_DIR + expectedFileName);
        assertTrue(file.exists(), "File should be created");
    }

    /**
     * 测试数据持久化功能（检查重复，有重复数据，动作：只做新增）
     */
    @Test
    public void testPersistDataWithDuplicateCheckAndInsertOnly() {
        String jsonData = "{\"id\": 3, \"name\": \"test3\", \"value\": \"test_value3\"}";

        // 第一次写入
        PersistenceRequest request1 = new PersistenceRequest(
                jsonData,
                TEST_FILE_NAME,
                true,
                Arrays.asList("id"),
                PersistenceRequest.DuplicateAction.INSERT_ONLY
        );
        boolean result1 = dataPersistenceService.persistData(request1);
        assertTrue(result1, "First data should be persisted successfully");

        // 第二次写入相同数据
        boolean result2 = dataPersistenceService.persistData(request1);
        assertTrue(result2, "Second data should be skipped but return true");
    }

    /**
     * 测试数据持久化功能（检查重复，有重复数据，动作：新增同时删除旧的数据）
     */
    @Test
    public void testPersistDataWithDuplicateCheckAndInsertAndDeleteOld() {
        String jsonData1 = "{\"id\": 4, \"name\": \"test4\", \"value\": \"test_value4\"}";
        String jsonData2 = "{\"id\": 4, \"name\": \"test4_updated\", \"value\": \"test_value4_updated\"}";

        // 第一次写入
        PersistenceRequest request1 = new PersistenceRequest(
                jsonData1,
                TEST_FILE_NAME,
                true,
                Arrays.asList("id"),
                PersistenceRequest.DuplicateAction.INSERT_AND_DELETE_OLD
        );
        boolean result1 = dataPersistenceService.persistData(request1);
        assertTrue(result1, "First data should be persisted successfully");

        // 第二次写入相同ID但不同内容的数据
        PersistenceRequest request2 = new PersistenceRequest(
                jsonData2,
                TEST_FILE_NAME,
                true,
                Arrays.asList("id"),
                PersistenceRequest.DuplicateAction.INSERT_AND_DELETE_OLD
        );
        boolean result2 = dataPersistenceService.persistData(request2);
        assertTrue(result2, "Second data should be persisted successfully");
    }

    /**
     * 测试数据持久化功能（检查重复，有重复数据，动作：修改）
     */
    @Test
    public void testPersistDataWithDuplicateCheckAndUpdate() {
        String jsonData1 = "{\"id\": 5, \"name\": \"test5\", \"value\": \"test_value5\"}";
        String jsonData2 = "{\"id\": 5, \"name\": \"test5_updated\", \"value\": \"test_value5_updated\"}";

        // 第一次写入
        PersistenceRequest request1 = new PersistenceRequest(
                jsonData1,
                TEST_FILE_NAME,
                true,
                Arrays.asList("id"),
                PersistenceRequest.DuplicateAction.UPDATE
        );
        boolean result1 = dataPersistenceService.persistData(request1);
        assertTrue(result1, "First data should be persisted successfully");

        // 第二次写入相同ID但不同内容的数据
        PersistenceRequest request2 = new PersistenceRequest(
                jsonData2,
                TEST_FILE_NAME,
                true,
                Arrays.asList("id"),
                PersistenceRequest.DuplicateAction.UPDATE
        );
        boolean result2 = dataPersistenceService.persistData(request2);
        assertTrue(result2, "Second data should update existing data successfully");
    }

    /**
     * 测试数据持久化功能（检查重复，有重复数据，动作：不做处理）
     */
    @Test
    public void testPersistDataWithDuplicateCheckAndNoAction() {
        String jsonData = "{\"id\": 6, \"name\": \"test6\", \"value\": \"test_value6\"}";

        // 第一次写入
        PersistenceRequest request1 = new PersistenceRequest(
                jsonData,
                TEST_FILE_NAME,
                true,
                Arrays.asList("id"),
                PersistenceRequest.DuplicateAction.NO_ACTION
        );
        boolean result1 = dataPersistenceService.persistData(request1);
        assertTrue(result1, "First data should be persisted successfully");

        // 第二次写入相同数据
        boolean result2 = dataPersistenceService.persistData(request1);
        assertTrue(result2, "Second data should be skipped but return true");
    }

    /**
     * 测试无效JSON数据
     */
    @Test
    public void testPersistDataWithInvalidJson() {
        String invalidJson = "{\"id\": 7, \"name\": \"test7\", \"value\": \"test_value7\"";
        PersistenceRequest request = new PersistenceRequest(
                invalidJson,
                TEST_FILE_NAME,
                false,
                null,
                PersistenceRequest.DuplicateAction.INSERT_ONLY
        );

        boolean result = dataPersistenceService.persistData(request);
        assertFalse(result, "Invalid JSON should not be persisted");
    }

    /**
     * 删除所有测试文件
     */
    private void deleteTestFiles() {
        List<File> testFiles = FileUtil.findFilesByBaseName(RESOURCES_DIR, TEST_FILE_NAME);
        for (File file : testFiles) {
            FileUtil.deleteFile(file.getAbsolutePath());
        }
    }
}
