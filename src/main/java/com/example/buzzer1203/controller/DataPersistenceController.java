package com.example.buzzer1203.controller;

import com.example.buzzer1203.persistence.model.PersistenceRequest;
import com.example.buzzer1203.persistence.service.DataPersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据持久化控制器，用于测试数据持久化服务
 */
@RestController
@RequestMapping("/api/persistence")
public class DataPersistenceController {

    @Autowired
    private DataPersistenceService dataPersistenceService;

    /**
     * 持久化数据
     * @param request 持久化请求
     * @return 操作结果
     */
    @PostMapping("/save")
    public String persistData(@RequestBody PersistenceRequest request) {
        try {
            boolean result = dataPersistenceService.persistData(request);
            if (result) {
                return "Data persisted successfully";
            } else {
                return "Failed to persist data";
            }
        } catch (Exception e) {
            return "Error persisting data: " + e.getMessage();
        }
    }
}
