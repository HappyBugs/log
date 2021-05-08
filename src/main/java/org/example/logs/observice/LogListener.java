package org.example.logs.observice;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.logs.LogConfig;
import org.example.logs.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

/**
 * 日志任务实现监听者
 *
 * @author 李昆城
 */
@Slf4j
@Component
public class LogListener implements ApplicationListener<LogEvent> {

    private final LogConfig logConfig;
    private final DateUtils dateUtils;
    private final Environment environment;

    private boolean prod;

    @Autowired
    public LogListener(LogConfig logConfig, DateUtils dateUtils, Environment environment) {
        this.logConfig = logConfig;
        this.dateUtils = dateUtils;
        this.environment = environment;
    }

    public void init() {
        String property = environment.getProperty("spring.profiles.active");
        prod = "prod".equals(property);
    }

    /**
     * 事件监听器，当监听到事件执行的代码
     *
     * @param event 事件对象
     */
    @Override
    public void onApplicationEvent(LogEvent event) {
        String targetAddress = event.getTargetAddress();
        String param = event.getParam();
        String result = event.getResult();
        String errorMsg = event.getErrorMsg();
        String errorLocation = event.getErrorLocation();
        Long startTime = event.getStartTime();
        Long endTime = event.getEndTime();
        String persistenceAddress = event.getPersistenceAddress();
        //开始时间、截止时间
        String start = DateUtils.dateToString(new Date(startTime), DateUtils.NOW) + "  " + startTime;
        String end = DateUtils.dateToString(new Date(endTime), DateUtils.NOW) + "  " + endTime;
        //运行时间
        long runTime = endTime - startTime;
        String runTimeString = "执行时间（毫秒）：" + runTime;
        //持久化消息
        String[] msg = new String[]{start, targetAddress, param, result, errorMsg, errorLocation, runTimeString, end};
        try {
            write(findDirectoryPath(persistenceAddress), msg);
        } catch (IOException e) {
            log.error("持久化日志失败：{}", e.getMessage());
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
     * 获取文件的详细文件地址
     *
     * @param path 目录地址
     * @return 文件地址
     */
    private String findFilePath(String path) {
        return prod ? path + "/log.txt" : path + "\\log.txt";
    }

    /**
     * 写入内容
     *
     * @param msg  需要打印的消息数组
     * @param file 文件
     */
    private void writeContent(String[] msg, File file) {
        //如果没有内容，则直接返回
        if (Objects.isNull(msg) || msg.length < 1) {
            return;
        }
        BufferedWriter bufferedWriter = null;
        FileWriter fileWriter;
        try {
            //是否追加写入。否则将先清文本再写入
            boolean append = true;
            fileWriter = new FileWriter(file, append);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("<----------Start---------->");
            //换行
            bufferedWriter.newLine();
            for (String str : msg) {
                bufferedWriter.write(str);
                bufferedWriter.newLine();
            }
            bufferedWriter.write("<----------End------------>");
            bufferedWriter.newLine();
            bufferedWriter.newLine();
        } catch (IOException e) {
            log.error("向日志中写入数据发生错误：{}", e.getMessage());
        } finally {
            try {
                if (Objects.nonNull(bufferedWriter)) {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
            } catch (IOException e) {
                log.error("关闭输出流发生错误：{}", e.getMessage());
            }
        }
    }


    /**
     * 写入文件
     *
     * @param path     文件保存地址
     * @param messages 内容信息
     * @throws IOException 可能发生异常
     */
    private void write(String path, String[] messages) throws IOException {
        //创建文件对象
        File folderPath = new File(path);
        //如果当前目录不存在，且创建失败则纪录错误日志
        if (!folderPath.exists() && !folderPath.mkdirs()) {
            log.error("创建日志收集目录：{} 失败", folderPath.getAbsolutePath());
            return;
        }
        //添加后缀
        path = findFilePath(path);
        File filePath = new File(path);
        if (!filePath.exists() && !filePath.createNewFile()) {
            log.error("创建日志收集文件：{} 失败：", folderPath.getAbsolutePath());
            return;
        }
        //将写入变为单线程。防止重复操作
        synchronized (this) {
            writeContent(messages, filePath);
        }
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
