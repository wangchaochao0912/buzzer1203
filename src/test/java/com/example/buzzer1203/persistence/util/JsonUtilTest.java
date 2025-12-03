package com.example.buzzer1203.persistence.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JSON工具类单元测试
 */
public class JsonUtilTest {

    @Test
    public void testValidJson() {
        String validJson = "{\"id\": 1, \"name\": \"test\", \"value\": \"test_value\"}";
        assertTrue(JsonUtil.isValidJson(validJson), "Valid JSON should be accepted");
    }

    @Test
    public void testInvalidJsonMissingClosingBrace() {
        String invalidJson = "{\"id\": 1, \"name\": \"test\", \"value\": \"test_value\"";
        assertFalse(JsonUtil.isValidJson(invalidJson), "Invalid JSON (missing closing brace) should be rejected");
    }

    @Test
    public void testInvalidJsonExtraComma() {
        String invalidJson = "{\"id\": 1, \"name\": \"test\", \"value\": \"test_value\",";
        assertFalse(JsonUtil.isValidJson(invalidJson), "Invalid JSON (extra comma) should be rejected");
    }

    @Test
    public void testInvalidJsonInvalidSyntax() {
        String invalidJson = "{\"id\": 1, \"name\": \"test\", \"value\": \"test_value\"";
        assertFalse(JsonUtil.isValidJson(invalidJson), "Invalid JSON (invalid syntax) should be rejected");
    }

    @Test
    public void testNullJson() {
        assertFalse(JsonUtil.isValidJson(null), "Null JSON should be rejected");
    }

    @Test
    public void testEmptyJson() {
        assertFalse(JsonUtil.isValidJson(""), "Empty JSON should be rejected");
    }

    @Test
    public void testWhitespaceJson() {
        assertFalse(JsonUtil.isValidJson("   "), "Whitespace JSON should be rejected");
    }
}
