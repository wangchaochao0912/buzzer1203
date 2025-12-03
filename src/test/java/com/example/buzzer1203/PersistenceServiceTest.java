package com.example.buzzer1203;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PersistenceServiceTest {

    @Autowired
    private PersistenceService persistenceService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    private String resourcesPath;
    private String testFileName = "test_data";

    @BeforeEach
    void setUp() throws IOException {
        // 获取resources目录路径
        resourcesPath = Objects.requireNonNull(
                getClass().getClassLoader().getResource("")).getPath();

        // 清除所有测试文件
        File resourcesDir = new File(resourcesPath);
        if (resourcesDir.exists() && resourcesDir.isDirectory()) {
            File[] files = resourcesDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().startsWith(testFileName)) {
                        FileUtils.deleteQuietly(file);
                    }
                }
            }
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        // 清除所有测试文件
        File resourcesDir = new File(resourcesPath);
        if (resourcesDir.exists() && resourcesDir.isDirectory()) {
            File[] files = resourcesDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().startsWith(testFileName)) {
                        FileUtils.deleteQuietly(file);
                    }
                }
            }
        }
    }

    @Test
    void testWriteDataWithoutDuplicateCheck() throws IOException, InterruptedException {
        // 准备测试数据
        String jsonData = "{\"id\":1,\"name\":\"test1\"}";
        Set<String> duplicateFields = new HashSet<>(Arrays.asList("id"));

        // 调用写入方法
        persistenceService.writeData(jsonData, testFileName, false, duplicateFields, PersistenceService.DuplicateAction.ONLY_INSERT);

        // 验证文件是否创建
        String currentDate = dateFormat.format(new Date());
        String newFileName = testFileName + "_" + currentDate + ".json";
        File newFile = new File(resourcesPath + File.separator + newFileName);
        assertTrue(newFile.exists());

        // 验证文件内容是否正确
        String fileContent = FileUtils.readFileToString(newFile, StandardCharsets.UTF_8);
        List<Map<String, Object>> dataList = objectMapper.readValue(fileContent, List.class);
        assertEquals(1, dataList.size());
        assertEquals(1, dataList.get(0).get("id"));
        assertEquals("test1", dataList.get(0).get("name"));
    }

    @Test
    void testWriteDataWithDuplicateCheckAndOnlyInsert() throws IOException, InterruptedException {
        // 准备测试数据
        String jsonData1 = "{\"id\":1,\"name\":\"test1\"}";
        String jsonData2 = "{\"id\":1,\"name\":\"test2\"}";
        Set<String> duplicateFields = new HashSet<>(Arrays.asList("id"));

        // 第一次写入数据
        persistenceService.writeData(jsonData1, testFileName, true, duplicateFields, PersistenceService.DuplicateAction.ONLY_INSERT);

        // 第二次写入相同ID的数据（只做新增）
        persistenceService.writeData(jsonData2, testFileName, true, duplicateFields, PersistenceService.DuplicateAction.ONLY_INSERT);

        // 验证文件内容是否包含两条数据
        String currentDate = dateFormat.format(new Date());
        String newFileName = testFileName + "_" + currentDate + ".json";
        File newFile = new File(resourcesPath + File.separator + newFileName);
        assertTrue(newFile.exists());

        String fileContent = FileUtils.readFileToString(newFile, StandardCharsets.UTF_8);
        List<Map<String, Object>> dataList = objectMapper.readValue(fileContent, List.class);
        assertEquals(2, dataList.size());
    }

    @Test
    void testWriteDataWithDuplicateCheckAndUpdate() throws IOException, InterruptedException {
        // 准备测试数据
        String jsonData1 = "{\"id\":1,\"name\":\"test1\"}";
        String jsonData2 = "{\"id\":1,\"name\":\"test2\"}";
        Set<String> duplicateFields = new HashSet<>(Arrays.asList("id"));

        // 第一次写入数据
        persistenceService.writeData(jsonData1, testFileName, true, duplicateFields, PersistenceService.DuplicateAction.UPDATE);

        // 第二次写入相同ID的数据（修改）
        persistenceService.writeData(jsonData2, testFileName, true, duplicateFields, PersistenceService.DuplicateAction.UPDATE);

        // 验证文件内容是否只有一条数据且已更新
        String currentDate = dateFormat.format(new Date());
        String newFileName = testFileName + "_" + currentDate + ".json";
        File newFile = new File(resourcesPath + File.separator + newFileName);
        assertTrue(newFile.exists());

        String fileContent = FileUtils.readFileToString(newFile, StandardCharsets.UTF_8);
        List<Map<String, Object>> dataList = objectMapper.readValue(fileContent, List.class);
        assertEquals(1, dataList.size());
        assertEquals("test2", dataList.get(0).get("name"));
    }
}
