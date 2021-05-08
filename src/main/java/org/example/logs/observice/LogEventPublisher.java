package org.example.logs.observice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 日志收集器
 *
 * @author 李昆城
 */
@Slf4j
@Component
public class LogEventPublisher implements ApplicationEventPublisherAware {


    /**
     * 任务列表，无锁的线程安全并发队列
     */
    private final Queue<LogEvent> taskList = new ConcurrentLinkedQueue<>();


    /**
     * 设置当前的事件发布器
     *
     * @param applicationEventPublisher 发送器
     */
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * 事件发布器
     */
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 新开一个线程发布一个配送任务
     *
     * @param logEvent 配送任务参数对象
     */
    public void publishEvent(LogEvent logEvent) {
        if (Objects.isNull(logEvent)) {
            return;
        }
        //添加一个任务，如果添加成功，则返回true，否则队列已满
        boolean offer = taskList.offer(logEvent);
        if (!offer) {
            log.warn("队列已满，该任务无法添加持久化任务");
        }
        //获取到最旧的一个任务
        LogEvent task = taskList.poll();
        //如果不是空，则推送一个任务。
        if (Objects.nonNull(task)) {
            applicationEventPublisher.publishEvent(logEvent);
        }
    }

}
