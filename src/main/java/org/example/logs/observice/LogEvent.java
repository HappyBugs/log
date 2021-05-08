package org.example.logs.observice;

import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * 持久化日志参数传递对象
 *
 * @author 李昆城
 */
@Setter
public class LogEvent extends ApplicationEvent {

    /**
     * 目标任务对象
     */
    private Object source;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public LogEvent(Object source) {
        super(source);
        this.source = source;
    }


    /**
     * 执行方法的目标地址
     */
    private String targetAddress;
    /**
     * 参数信息
     */
    private String param;
    /**
     * 日志返回值
     */
    private String result;
    /**
     * 错误消息
     */
    private String errorMsg;
    /**
     * 错误位置
     */
    private String errorLocation;
    /**
     * 开始时间
     */
    private Long startTime;
    /**
     * 截止时间
     */
    private Long endTime;
    /**
     * 持久化地址，不传递则使用默认值
     */
    private String persistenceAddress;


    public LogEvent targetAddress(String targetAddress) {
        this.setTargetAddress(targetAddress);
        return this;
    }

    public LogEvent param(String param) {
        this.setParam(param);
        return this;
    }

    public LogEvent result(String result) {
        this.setResult(result);
        return this;
    }

    public LogEvent errorMsg(String errorMsg) {
        this.setErrorMsg(errorMsg);
        return this;
    }

    public LogEvent errorLocation(String errorLocation) {
        this.setErrorLocation(errorLocation);
        return this;
    }

    public LogEvent startTime(Long startTime) {
        this.setStartTime(startTime);
        return this;
    }

    public LogEvent endTime(Long endTime) {
        this.setEndTime(endTime);
        return this;
    }

    public LogEvent persistenceAddress(String persistenceAddress) {
        this.setPersistenceAddress(persistenceAddress);
        return this;
    }


    @Override
    public Object getSource() {
        return source;
    }

    public String getTargetAddress() {
        return "目标方法："+targetAddress;
    }

    public String getParam() {
        return "参数信息："+param;
    }

    public String getResult() {
        return "返回值："+result;
    }

    public String getErrorMsg() {
        return "错误消息："+errorMsg;
    }

    public String getErrorLocation() {
        return "部分错误地址："+errorLocation;
    }

    public Long getStartTime() {
        return startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public String getPersistenceAddress() {
        return persistenceAddress;
    }
}
