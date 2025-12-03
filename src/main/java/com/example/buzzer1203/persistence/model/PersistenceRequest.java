package com.example.buzzer1203.persistence.model;

import java.util.List;

/**
 * 数据持久化请求模型类
 */
public class PersistenceRequest {

    // JSON格式的数据
    private String jsonData;

    // 指定文件名
    private String fileName;

    // 是否校验数据重复
    private boolean checkDuplicate;

    // 判断重复所校验的字段集合
    private List<String> checkFields;

    // 重复后的动作
    private DuplicateAction duplicateAction;

    /**
     * 重复数据处理动作枚举
     */
    public enum DuplicateAction {
        // 只做新增
        INSERT_ONLY,
        // 新增同时删除旧的数据
        INSERT_AND_DELETE_OLD,
        // 修改
        UPDATE,
        // 不做处理
        NO_ACTION
    }

    // 构造函数
    public PersistenceRequest() {
    }

    public PersistenceRequest(String jsonData, String fileName, boolean checkDuplicate, List<String> checkFields, DuplicateAction duplicateAction) {
        this.jsonData = jsonData;
        this.fileName = fileName;
        this.checkDuplicate = checkDuplicate;
        this.checkFields = checkFields;
        this.duplicateAction = duplicateAction;
    }

    // Getter和Setter方法
    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isCheckDuplicate() {
        return checkDuplicate;
    }

    public void setCheckDuplicate(boolean checkDuplicate) {
        this.checkDuplicate = checkDuplicate;
    }

    public List<String> getCheckFields() {
        return checkFields;
    }

    public void setCheckFields(List<String> checkFields) {
        this.checkFields = checkFields;
    }

    public DuplicateAction getDuplicateAction() {
        return duplicateAction;
    }

    public void setDuplicateAction(DuplicateAction duplicateAction) {
        this.duplicateAction = duplicateAction;
    }

    @Override
    public String toString() {
        return "PersistenceRequest{" +
                "jsonData='" + jsonData + '\'' +
                ", fileName='" + fileName + '\'' +
                ", checkDuplicate=" + checkDuplicate +
                ", checkFields=" + checkFields +
                ", duplicateAction=" + duplicateAction +
                '}';
    }
}
