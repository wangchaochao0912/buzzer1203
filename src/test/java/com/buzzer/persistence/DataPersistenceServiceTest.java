package com.buzzer.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DataPersistenceServiceTest {

    private DataPersistenceService persistenceService;
    private String testFileName = "test_data";
    private String resourcesPath;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    @BeforeEach
    void setUp() {
        persistenceService = new DataPersistenceService();
        // 获取resources目录路径
        resourcesPath = persistenceService.getClass().getClassLoader().getResource("").getFile();
    }

    @AfterEach
    void tearDown() {
        // 清理测试文件
        try {
            File resourcesDir = new File(resourcesPath);
            if (resourcesDir.exists() && resourcesDir.isDirectory()) {
                String pattern = testFileName + "_[0-9]{8}_[0-9]+" + ".json";
                File[] testFiles = resourcesDir.listFiles((dir, name) -> name.matches(pattern));
                if (testFiles != null) {
                    for (File file : testFiles) {
                        FileUtils.deleteQuietly(file);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testWriteDataWithoutDuplicateCheck() throws JsonProcessingException {
        // 测试不进行重复校验的写入
        String jsonData = "{\"id\":1,\"name\":\"test\",\"value\":\"test_value\"}";
        Set<String> duplicateFields = new HashSet<>();
        duplicateFields.add("id");

        boolean result = persistenceService.writeData(jsonData, testFileName, false, 
                                                    duplicateFields, DataPersistenceService.DuplicateStrategy.IGNORE);

        assertTrue(result);

        // 验证文件是否创建
        File resourcesDir = new File(resourcesPath);
        File[] testFiles = resourcesDir.listFiles((dir, name) -> name.startsWith(testFileName + "_") && name.endsWith(".json"));
        assertTrue(testFiles != null && testFiles.length > 0);

        // 验证文件内容
        try {
            String content = FileUtils.readFileToString(testFiles[0], StandardCharsets.UTF_8);
            assertEquals(jsonData, content);
        } catch (IOException e) {
            fail("读取测试文件失败: " + e.getMessage());
        }
    }

    @Test
    void testWriteDataWithDuplicateCheckAndIgnoreStrategy() throws JsonProcessingException {
        // 第一次写入数据
        String jsonData = "{\"id\":1,\"name\":\"test\",\"value\":\"test_value\"}";
        Set<String> duplicateFields = new HashSet<>();
        duplicateFields.add("id");

        boolean firstResult = persistenceService.writeData(jsonData, testFileName, true, 
                                                          duplicateFields, DataPersistenceService.DuplicateStrategy.IGNORE);
        assertTrue(firstResult);

        // 第二次写入相同数据，使用IGNORE策略
        boolean secondResult = persistenceService.writeData(jsonData, testFileName, true, 
                                                           duplicateFields, DataPersistenceService.DuplicateStrategy.IGNORE);
        assertFalse(secondResult);

        // 验证文件数量是否为1
        File resourcesDir = new File(resourcesPath);
        File[] testFiles = resourcesDir.listFiles((dir, name) -> name.startsWith(testFileName + "_") && name.endsWith(".json"));
        assertEquals(1, testFiles.length);
    }

    @Test
    void testWriteDataWithDuplicateCheckAndInsertStrategy() throws JsonProcessingException {
        // 第一次写入数据
        String jsonData = "{\"id\":1,\"name\":\"test\",\"value\":\"test_value\"}";
        Set<String> duplicateFields = new HashSet<>();
        duplicateFields.add("id");

        boolean firstResult = persistenceService.writeData(jsonData, testFileName, true, 
                                                          duplicateFields, DataPersistenceService.DuplicateStrategy.INSERT);
        assertTrue(firstResult);

        // 验证第一次写入后文件数量是否为1
        File resourcesDir = new File(resourcesPath);
        File[] testFilesAfterFirst = resourcesDir.listFiles((dir, name) -> name.startsWith(testFileName + "_") && name.endsWith(".json"));
        assertEquals(1, testFilesAfterFirst.length);

        // 第二次写入相同数据，使用INSERT策略
        boolean secondResult = persistenceService.writeData(jsonData, testFileName, true, 
                                                           duplicateFields, DataPersistenceService.DuplicateStrategy.INSERT);
        assertTrue(secondResult);

        // 验证第二次写入后文件数量是否为2
        File[] testFilesAfterSecond = resourcesDir.listFiles((dir, name) -> name.startsWith(testFileName + "_") && name.endsWith(".json"));
        assertEquals(2, testFilesAfterSecond.length);
    }

    @Test
    void testWriteDataWithDuplicateCheckAndInsertDeleteOldStrategy() throws JsonProcessingException {
        // 第一次写入数据
        String jsonData = "{\"id\":1,\"name\":\"test\",\"value\":\"test_value\"}";
        Set<String> duplicateFields = new HashSet<>();
        duplicateFields.add("id");

        boolean firstResult = persistenceService.writeData(jsonData, testFileName, true, 
                                                          duplicateFields, DataPersistenceService.DuplicateStrategy.INSERT_DELETE_OLD);
        assertTrue(firstResult);

        // 验证文件数量是否为1
        File resourcesDir = new File(resourcesPath);
        File[] testFilesAfterFirst = resourcesDir.listFiles((dir, name) -> name.startsWith(testFileName + "_") && name.endsWith(".json"));
        assertEquals(1, testFilesAfterFirst.length);

        // 修改数据后第二次写入，使用INSERT_DELETE_OLD策略
        String updatedJsonData = "{\"id\":1,\"name\":\"test\",\"value\":\"updated_value\"}";
        boolean secondResult = persistenceService.writeData(updatedJsonData, testFileName, true, 
                                                           duplicateFields, DataPersistenceService.DuplicateStrategy.INSERT_DELETE_OLD);
        assertTrue(secondResult);

        // 验证文件数量是否仍然为1
        File[] testFilesAfterSecond = resourcesDir.listFiles((dir, name) -> name.startsWith(testFileName + "_") && name.endsWith(".json"));
        assertEquals(1, testFilesAfterSecond.length);

        // 验证文件内容是否已更新
        try {
            String content = FileUtils.readFileToString(testFilesAfterSecond[0], StandardCharsets.UTF_8);
            assertEquals(updatedJsonData, content);
        } catch (IOException e) {
            fail("读取测试文件失败: " + e.getMessage());
        }
    }

    @Test
    void testWriteDataWithDuplicateCheckAndUpdateStrategy() throws JsonProcessingException {
        // 第一次写入数据
        String jsonData = "{\"id\":1,\"name\":\"test\",\"value\":\"test_value\"}";
        Set<String> duplicateFields = new HashSet<>();
        duplicateFields.add("id");

        boolean firstResult = persistenceService.writeData(jsonData, testFileName, true, 
                                                          duplicateFields, DataPersistenceService.DuplicateStrategy.UPDATE);
        assertTrue(firstResult);

        // 修改数据后第二次写入，使用UPDATE策略
        String updatedJsonData = "{\"id\":1,\"name\":\"test\",\"value\":\"updated_value\"}";
        boolean secondResult = persistenceService.writeData(updatedJsonData, testFileName, true, 
                                                           duplicateFields, DataPersistenceService.DuplicateStrategy.UPDATE);
        assertTrue(secondResult);

        // 验证文件数量是否为1
        File resourcesDir = new File(resourcesPath);
        File[] testFiles = resourcesDir.listFiles((dir, name) -> name.startsWith(testFileName + "_") && name.endsWith(".json"));
        assertEquals(1, testFiles.length);

        // 验证文件内容是否已更新
        try {
            String content = FileUtils.readFileToString(testFiles[0], StandardCharsets.UTF_8);
            assertEquals(updatedJsonData, content);
        } catch (IOException e) {
            fail("读取测试文件失败: " + e.getMessage());
        }
    }
}