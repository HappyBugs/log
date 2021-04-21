package org.example.logs.observice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 日志任务实现监听者
 *
 * @author 李昆城
 */
@Slf4j
@Component
public class LogListener implements ApplicationListener<LogEvent> {

    /**
     * 事件监听器，当监听到事件执行的代码
     *
     * @param event 事件对象
     */
    @Override
    public void onApplicationEvent(LogEvent event) {
        log.info("监听到一个日志请求，开始持久化 {}", event);
    }
}
