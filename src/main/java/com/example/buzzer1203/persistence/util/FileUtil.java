package com.example.buzzer1203.persistence.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 文件工具类，用于文件操作
 */
public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 根据基础文件名查找所有日期下的同名文件
     * @param directory 目录路径
     * @param baseFileName 基础文件名
     * @return 文件列表
     */
    public static List<File> findFilesByBaseName(String directory, String baseFileName) {
        List<File> files = new ArrayList<>();
        File dir = new File(directory);

        if (!dir.exists() || !dir.isDirectory()) {
            logger.warn("Directory does not exist or is not a directory: {}", directory);
            return files;
        }

        // 匹配模式：baseFileName_yyyyMMdd.json
        String pattern = baseFileName + "_\\d{8}\\.json";
        Pattern regexPattern = Pattern.compile(pattern);

        File[] allFiles = dir.listFiles();
        if (allFiles != null) {
            for (File file : allFiles) {
                if (file.isFile() && regexPattern.matcher(file.getName()).matches()) {
                    files.add(file);
                }
            }
        }

        logger.info("Found {} files matching pattern '{}' in directory '{}'", files.size(), pattern, directory);
        return files;
    }

    /**
     * 读取文件内容
     * @param filePath 文件路径
     * @return 文件内容
     * @throws IOException IO异常
     */
    public static String readFileContent(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return new String(Files.readAllBytes(path));
    }

    /**
     * 写入文件内容
     * @param filePath 文件路径
     * @param content 文件内容
     * @throws IOException IO异常
     */
    public static void writeFileContent(String filePath, String content) throws IOException {
        Path path = Paths.get(filePath);
        Files.write(path, content.getBytes());
    }

    /**
     * 检查文件是否存在
     * @param filePath 文件路径
     * @return 是否存在
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    /**
     * 创建目录
     * @param directoryPath 目录路径
     * @return 是否创建成功
     */
    public static boolean createDirectory(String directoryPath) {
        Path path = Paths.get(directoryPath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                logger.info("Directory created: {}", directoryPath);
                return true;
            } catch (IOException e) {
                logger.error("Failed to create directory: {}", directoryPath, e);
                return false;
            }
        }
        return true;
    }

    /**
     * 删除文件
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public static boolean deleteFile(String filePath) {
        Path path = Paths.get(filePath);
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            logger.error("Failed to delete file: {}", filePath, e);
            return false;
        }
    }
}
