package org.example.logs.observice.logwrite;

/**
 * 持久化写入文本对象
 *
 * @author 李昆城
 */
public interface LogWrite {


    /**
     * 持久化写入
     *
     * @param msg  需要写入的内容
     * @param path 持久化地址
     */
    void write(String[] msg, String path);
}
