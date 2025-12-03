package com.example.buzzer1203.persistence.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JSON工具类，用于JSON数据的解析和转换
 */
public class JsonUtil {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将JSON字符串解析为Map
     * @param json JSON字符串
     * @return Map对象
     */
    public static Map<String, Object> parseJsonToMap(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse JSON to Map: {}", json, e);
            return null;
        }
    }

    /**
     * 将JSON数组字符串解析为List<Map>
     * @param json JSON数组字符串
     * @return List<Map>对象
     */
    public static List<Map<String, Object>> parseJsonArrayToList(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse JSON array to List: {}", json, e);
            return null;
        }
    }

    /**
     * 从文件中读取JSON数组并解析为List<Map>
     * @param filePath 文件路径
     * @return List<Map>对象
     */
    public static List<Map<String, Object>> parseJsonArrayFromFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists() || !file.isFile()) {
                logger.warn("File does not exist or is not a file: {}", filePath);
                return new ArrayList<>();
            }

            String content = FileUtil.readFileContent(filePath);
            if (content == null || content.trim().isEmpty()) {
                return new ArrayList<>();
            }

            // 尝试解析为JSON数组
            try {
                return objectMapper.readValue(content, new TypeReference<List<Map<String, Object>>>() {});
            } catch (JsonProcessingException e) {
                // 如果不是数组，尝试解析为单个对象
                try {
                    Map<String, Object> singleObject = objectMapper.readValue(content, new TypeReference<Map<String, Object>>() {});
                    List<Map<String, Object>> list = new ArrayList<>();
                    list.add(singleObject);
                    return list;
                } catch (JsonProcessingException ex) {
                    logger.error("Failed to parse content from file as JSON array or object: {}", filePath, ex);
                    return new ArrayList<>();
                }
            }
        } catch (IOException e) {
            logger.error("Failed to read file content: {}", filePath, e);
            return new ArrayList<>();
        }
    }

    /**
     * 将对象转换为JSON字符串
     * @param object 对象
     * @return JSON字符串
     */
    public static String convertToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Failed to convert object to JSON", e);
            return null;
        }
    }

    /**
     * 格式化JSON字符串
     * @param json JSON字符串
     * @return 格式化后的JSON字符串
     */
    public static String formatJson(String json) {
        try {
            Object object = objectMapper.readValue(json, Object.class);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Failed to format JSON: {}", json, e);
            return json;
        }
    }

    /**
     * 验证JSON字符串是否有效
     * @param json JSON字符串
     * @return 是否有效
     */
    public static boolean isValidJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            logger.error("JSON string is null or empty");
            return false;
        }
        try {
            objectMapper.readValue(json, Object.class);
            return true;
        } catch (JsonProcessingException e) {
            logger.error("Invalid JSON: {}", json, e);
            return false;
        }
    }
}
