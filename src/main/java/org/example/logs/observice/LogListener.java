package org.example.logs.observice;

import lombok.extern.slf4j.Slf4j;
import org.example.logs.observice.logwrite.impl.RequestLog;
import org.example.logs.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Date;


/**
 * 日志任务实现监听者
 *
 * @author 李昆城
 */
@Slf4j
@Component
public class LogListener implements ApplicationListener<LogEvent> {

    private final RequestLog requestLog;

    @Autowired
    public LogListener(RequestLog requestLog) {
        this.requestLog = requestLog;
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
        //纪录请求日志
        requestLog.write(msg, persistenceAddress);
    }


}
