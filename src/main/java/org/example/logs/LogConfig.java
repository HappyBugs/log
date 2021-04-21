package org.example.logs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 日志配置类
 *
 * @author 李昆城
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "log")
public class LogConfig {

    /**
     * 配置列表
     */
    private Map<LogConfigKey, String> params;

    @PostConstruct
    public void init() {
        boolean defaultPath = false;
        //如果没有初始化
        if (Objects.isNull(params)) {
            //初始化容器
            params = new LinkedHashMap<>();
            //设置默认地址
            defaultPath();
            //采用默认地址
            defaultPath = true;
        }
        //如果未配置地址
        if (!params.containsKey(LogConfigKey.path)) {
            defaultPath();
            //采用默认地址
            defaultPath = true;
        }
        log.info("日志收集持久化地址定义类型：{}", defaultPath ? "默认" : "自定义");
        log.info("日志收集持久化地址：{}", params.get(LogConfigKey.path));
    }

    /**
     * 加载默认的持久化地址
     */
    private void defaultPath() {
        //默认的持久化地址
        String defaultPath = "E:\\logs";
        params.put(LogConfigKey.path, defaultPath);
    }

    /**
     * 日志配置key值
     *
     * @author 李昆城
     */
    public enum LogConfigKey {
        /**
         * 持久化地址
         */
        path
    }


}
