package com.sjl.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

/**
 * 文件操作工具类
 *
 * @author songjiali
 * @version 1.0.0
 * @filename FileUtils.java
 * @time 2018/10/22 17:25
 * @copyright(C) 2018 song
 */
public class FileUtils {

    /**
     * 删除指定路径下的文件目录及文件
     *
     * @param path     文件路径
     * @param selfFlag true删除本身目录及下面所有目录及文件，false本身目录及下面所有目录的文件
     */
    public static void deleteFiles(String path, boolean selfFlag) {
        File file = new File(path);
        // 1级文件刪除
        if (!file.isDirectory()) {
            file.delete();
        } else if (file.isDirectory()) {
            // 2级文件列表
            String[] filelist = file.list();
            for (int j = 0; j < filelist.length; j++) {
                File filessFile = new File(path + File.separator + filelist[j]);
                if (!filessFile.isDirectory()) {
                    filessFile.delete();
                } else if (filessFile.isDirectory()) {
                    // 递归删除文件
                    deleteFiles(path + File.separator + filelist[j], selfFlag);
                }
            }
            if (selfFlag) {
                file.delete();
            }

        }
    }

    /**
     * 常规文件拷贝
     *
     * @param in
     * @param target
     * @throws IOException
     */
    public static void fileCopy(InputStream in, File target) throws IOException {
        OutputStream out = null;
        try {
            out = new FileOutputStream(target);
            byte buffer[] = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            LogUtils.i("file copy success!");
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * 使用NIO进行快速的文件拷贝
     *
     * @param source 源文件
     * @param target 目标文件
     * @throws IOException
     */
    public static void fileCopy(File source, File target) throws IOException {
        FileChannel inChannel = new FileInputStream(source).getChannel();
        FileChannel outChannel = new FileOutputStream(target).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
            int maxCount = (64 * 1024 * 1024) - (32 * 1024);
            long size = inChannel.size();
            long position = 0;
            while (position < size) {
                position += inChannel.transferTo(position, maxCount, outChannel);
            }
            LogUtils.i("file copy success!");
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }

    /**
     * 写文件到sd卡
     *
     * @param in       输入流
     * @param fileName 文件名（绝对路径）
     */
    public static void writeFileToSD(InputStream in, String fileName) {
        OutputStream os = null;
        File file = null;
        try {
            file = new File(fileName);
            File fileParent = file.getParentFile();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            os = new FileOutputStream(file);
            byte buffer[] = new byte[1024 * 4];
            int temp = 0;
            while ((temp = in.read(buffer)) != -1) {
                os.write(buffer, 0, temp);
            }
            in.close();
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开指定文件夹
     *
     * @param context 上下文
     * @param path    路径
     */
    public static void openAssignFolder(Context context, String path) {
        File file = new File(path);
        if (null == file || !file.exists()) {
            return;
        }

        Uri fromFile;
        if (Build.VERSION.SDK_INT >= 24) {
            fromFile = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file.getParentFile());
        } else {
            fromFile = Uri.fromFile(file.getParentFile());
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setDataAndType(fromFile, "file/*");
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }


}
