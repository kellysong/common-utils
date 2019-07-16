package com.sjl.util;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * base64与字节、文件之间转换工具类
 *
 * @author songjiali
 * @version 1.0.0
 * @filename Base64ConvertUtils.java
 * @time 2018/10/22 17:25
 * @copyright(C) 2018 song
 */
public class Base64ConvertUtils {
    /**
     * base64字符串转byte[]
     *
     * @param data
     * @return
     * @throws IOException
     */
    public static byte[] base64ToByte(String data) throws IOException {
        byte[] apacheBytes = Base64.decode(data, Base64.DEFAULT);
        return apacheBytes;
    }

    /**
     * byte[]转base64
     *
     * @param data
     * @return
     */
    public static String byteToBase64(byte[] data) {
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    /**
     * 将文件转成base64 字符串
     *
     * @param path 文件路径
     * @return
     * @throws Exception
     */
    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return byteToBase64(buffer);
    }

    /**
     * 将base64字符解码并保存为文件
     *
     * @param base64Code
     * @param targetPath
     * @throws Exception
     */
    public static void decoderBase64File(String base64Code, String targetPath) throws Exception {
        byte[] buffer = base64ToByte(base64Code);
        FileOutputStream out = new FileOutputStream(targetPath);
        out.write(buffer);
        out.flush();
        out.close();
    }

    /**
     * 将base64字符保存文本文件
     *
     * @param base64Code
     * @param targetPath
     * @throws Exception
     */
    public static void toFile(String base64Code, String targetPath) throws Exception {
        byte[] buffer = base64Code.getBytes();
        FileOutputStream out = new FileOutputStream(targetPath);
        out.write(buffer);
        out.flush();
        out.close();
    }
}
