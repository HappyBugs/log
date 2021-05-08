package org.example.logs.observice.logwrite.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.logs.LogConfig;
import org.example.logs.observice.logwrite.BaseLogWrite;
import org.example.logs.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * 请求日志
 *
 * @author 李昆城
 */
@Slf4j
@Component
public class RequestLog extends BaseLogWrite {


    private final LogConfig logConfig;
    private final DateUtils dateUtils;
    private final Environment environment;

    @Autowired
    public RequestLog(LogConfig logConfig, DateUtils dateUtils, Environment environment) {
        this.logConfig = logConfig;
        this.dateUtils = dateUtils;
        this.environment = environment;
    }

    private boolean prod;

    @PostConstruct
    public void init() {
        String property = environment.getProperty("spring.profiles.active");
        prod = "prod".equals(property);
    }

    /**
     * 写入请求的持久化信息
     *
     * @param msg  需要写入的内容
     * @param path 持久化地址
     */
    @Override
    public void write(String[] msg, String path) {
        try {
            super.baseWrite(findDirectoryPath(path), msg, prod);
        } catch (IOException e) {
            log.error("写入日志失败：{}", e.getMessage());
        }
    }

    /**
     * 获取持久化地址。如果prod是true。则表示是linux系统。那么文件路径为/ 否则为\\
     *
     * @param persistenceAddress 持久化地址
     * @return 判断之后的持久化地址。
     */
    private String findDirectoryPath(String persistenceAddress) {
        persistenceAddress = StringUtils.isNoneBlank(persistenceAddress) ? persistenceAddress : logConfig.getParams().get(LogConfig.LogConfigKey.path);
        return prod ? persistenceAddress + "/" + dateFolder() : persistenceAddress + "\\" + dateFolder();
    }


    /**
     * 根据日期进行创建文件夹
     */
    public String dateFolder() {
        int year = dateUtils.getYear();
        int month = dateUtils.getMonth();
        int day = dateUtils.getDay();
        return prod ? year + "/" + month + "/" + day : year + "\\" + month + "\\" + day;
    }
}
