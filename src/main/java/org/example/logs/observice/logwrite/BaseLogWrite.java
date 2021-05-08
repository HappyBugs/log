package org.example.logs.observice.logwrite;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

/**
 * 日志持久化抽象实现
 *
 * @author 李昆城
 */
@Slf4j
public abstract class BaseLogWrite implements LogWrite {


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
            fileWriter = new FileWriter(file, true);
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
     * @param prod     当前的环境 true：生产环境  false：非生产环境
     * @throws IOException 可能发生异常
     */
    protected void baseWrite(String path, String[] messages, boolean prod) throws IOException {
        //创建文件对象
        File folderPath = new File(path);
        //如果当前目录不存在，且创建失败则纪录错误日志
        if (!folderPath.exists() && !folderPath.mkdirs()) {
            log.error("创建日志收集目录：{} 失败", folderPath.getAbsolutePath());
            return;
        }
        //添加后缀
        path = findFilePath(path, prod);
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
     * 获取文件的详细文件地址
     *
     * @param path 目录地址
     * @return 文件地址
     */
    private String findFilePath(String path, boolean prod) {
        return prod ? path + "/log.txt" : path + "\\log.txt";
    }

}
