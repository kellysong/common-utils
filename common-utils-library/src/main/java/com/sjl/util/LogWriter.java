package com.sjl.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 本地日志工具类
 *
 * @author Kelly
 */
public class LogWriter {

    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;

    static private String TAG = "CabinetLog";

    private static boolean isDebug = true;

    private static final long SINGLE_FILE_SIZE = 10 * 1024 * 1024;

    private static int LOG_MAX_SAVE_DAY = 10;

    private static final int CHUNK_SIZE = 4000;
    private static final String FILE_SUFFIX = ".txt";

    private static Object obj = new Object();

    static File logFile;
    static File logDir;

    private static int stackTraceIndex = 4;
    private static int saveDay = LOG_MAX_SAVE_DAY;

    /**
     * 初始化日志
     *
     * @param tag     过滤tag
     * @param context 上下文
     */
    public static void init(String tag, Context context) {
        init(true, tag, context);
    }

    /**
     * 初始化日志
     *
     * @param debug   是否打开日志
     * @param tag     过滤tag
     * @param context 上下文
     */
    public static void init(boolean debug, String tag, Context context) {
        isDebug = debug;
        if (!TextUtils.isEmpty(tag)) {
            TAG = tag;
        }
        String s = DateUtils.formatDateTime(new Date(), DateUtils.DATE_FORMAT_4);
        logFile = new File(Environment.getExternalStorageDirectory() + File.separator + context.getPackageName() + File.separator + "logs", s + FILE_SUFFIX);
        logDir = logFile.getParentFile();
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        checkLogFile(true);
    }


    public static void setStackTraceIndex(int stackTraceIndex) {
        LogWriter.stackTraceIndex = stackTraceIndex;
    }

    /**
     * 设置文件保留天数
     *
     * @param saveDay
     */
    public static void setSaveDay(int saveDay) {
        if (saveDay <= 0) {
            return;
        }
        LogWriter.saveDay = saveDay;
    }

    public static void v(String msg) {
        if (isDebug) {
            String log = createLog(msg);
            Log.v(TAG, log);
            writeLog(VERBOSE, log);
        }
    }


    public static void v(String msg, Throwable tr) {
        if (isDebug) {
            String log = createLog(msg);
            Log.v(TAG, log, tr);
            writeLog(VERBOSE, log + '\n' + Log.getStackTraceString(tr));
        }
    }

    public static void d(String msg) {
        if (isDebug) {
            String log = createLog(msg);
            Log.d(TAG, log);
            writeLog(DEBUG, log);
        }
    }

    public static void d(String msg, Throwable tr) {
        if (isDebug) {
            String log = createLog(msg);
            Log.d(TAG, log, tr);
            writeLog(DEBUG, log + '\n' + Log.getStackTraceString(tr));
        }
    }

    public static void i(String msg) {
        if (isDebug) {
            String log = createLog(msg);
            Log.i(TAG, log);
            writeLog(INFO, log);
        }
    }

    public static void i(String msg, Throwable tr) {
        if (isDebug) {
            String log = createLog(msg);
            Log.i(TAG, log, tr);
            writeLog(INFO, log + '\n' + Log.getStackTraceString(tr));
        }
    }

    public static void w(String msg) {
        if (isDebug) {
            String log = createLog(msg);
            Log.w(TAG, log);
            writeLog(WARN, log);
        }
    }

    public static void w(String msg, Throwable tr) {
        if (isDebug) {
            String log = createLog(msg);
            Log.w(TAG, log, tr);
            writeLog(WARN, log + '\n' + Log.getStackTraceString(tr));
        }
    }


    public static void e(String msg) {
        if (isDebug) {
            String log = createLog(msg);
            Log.e(TAG, log);
            writeLog(ERROR, log);
        }
    }


    public static void e(String msg, Throwable tr) {
        if (isDebug) {
            String log = createLog(msg);
            Log.e(TAG, log, tr);
            writeLog(ERROR, log + '\n' + Log.getStackTraceString(tr));
        }
    }

    /**
     * 写日志到文件
     *
     * @param logLevel
     * @param content
     */
    private static void writeLog(int logLevel, String content) {
        if (content == null) {
            return;
        }
        synchronized (obj) {
            long start = System.currentTimeMillis();
            try {
                boolean b = checkLogFile(false);
                if (!b) {
                    return;
                }
                //分块输出，避免不完整和显示过长
                byte[] bytes = content.getBytes();
                int length = bytes.length;
                if (length <= CHUNK_SIZE) {
                    writeLogToFile(logLevel, content);
                    return;
                }
                for (int i = 0; i < length; i += CHUNK_SIZE) {
                    int count = Math.min(length - i, CHUNK_SIZE);
                    if (i != 0) {
                        writeLogToFile(logLevel, "    " + new String(bytes, i, count));
                    } else {
                        writeLogToFile(logLevel, new String(bytes, i, count));
                    }

                }
            } catch (Exception e) {

            } finally {
//                System.out.println("写日志耗时:" + (System.currentTimeMillis() - start) + "ms");
            }
        }
    }

    private static void writeLogToFile(int logLevel, String content) {
        FileWriter fileWriter = null;
        try {
            String level = "--";
            switch (logLevel) {
                case VERBOSE:
                    level = "V";
                    break;
                case DEBUG:
                    level = "D";
                    break;
                case INFO:
                    level = "I";
                    break;
                case WARN:
                    level = "W";
                    break;
                case ERROR:
                    level = "E";
                    break;
                default:
                    break;
            }
            StringBuilder sb = new StringBuilder();
            String formatDate =  DateUtils.formatDateTime(new Date(), DateUtils.DATE_FORMAT_5);
            sb.append(formatDate).append("/").append(android.os.Process.myPid()).append("/").append(level).append(":").append(content).append('\n');
            if (!formatDate.contains(logFile.getName())) {
                reset();
            }
            fileWriter = new FileWriter(logFile, true);
            fileWriter.append(sb.toString());
            fileWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (Exception e1) { /* fail silently */ }
            }
        }
    }

    private static void reset() {
        String s = DateUtils.formatDateTime(new Date(), DateUtils.DATE_FORMAT_4);
        logDir = logFile.getParentFile();
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        synchronized (obj) {
            logFile = new File(logDir, s + FILE_SUFFIX);
        }

    }

    /**
     * 创建日志描述，定位信息
     *
     * @param msg
     * @return
     */
    private static String createLog(String msg) {
        StringBuilder builder = new StringBuilder();
        try {
            Thread thread = Thread.currentThread();
            StackTraceElement[] stackTrace = thread.getStackTrace();
            String className = stackTrace[stackTraceIndex].getFileName();
            String methodName = stackTrace[stackTraceIndex].getMethodName();
            int lineNumber = stackTrace[stackTraceIndex].getLineNumber();
            builder.append(methodName);
            builder.append("(").append(className).append(":").append(lineNumber).append(")");
            builder.append(msg);
            builder.append("  ---->").append("Thread:").append(thread.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    private static boolean checkLogFile(boolean first) {
        if (logFile == null) {
            return false;
        }
        if (!first) {
            if (logFile.length() > SINGLE_FILE_SIZE) {
                String formatDate = DateUtils.formatDateTime(new Date(), DateUtils.DATE_FORMAT_4);
                String segmentTime = DateUtils.formatDateTime(new Date(), DateUtils.DATE_FORMAT_7);
                String newFileName = new StringBuffer().append(formatDate).append("_").append(
                        segmentTime).append(FILE_SUFFIX).toString();
                File parentFile = logFile.getParentFile();
                logFile = new File(parentFile, newFileName);
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                } else {
                    deleteLogFile(parentFile);
                }

            }
        } else {
            File parentFile = logFile.getParentFile();
            deleteLogFile(parentFile);
        }
        return true;
    }

    /**
     * 删除冗余文件
     *
     * @param parentFile
     */
    private static void deleteLogFile(File parentFile) {

        File[] files = parentFile.listFiles(new MyFilenameFilter());
        if (files == null || files.length == 0) {
            return;
        }
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    static class MyFilenameFilter implements FilenameFilter {
        String deleteDate;

        public MyFilenameFilter() {
            deleteDate = new SimpleDateFormat(DateUtils.DATE_FORMAT_4).format(getDateBefore());
        }

        @Override
        public boolean accept(File dir, String name) {
            final SimpleDateFormat format = new SimpleDateFormat(DateUtils.DATE_FORMAT_4);
            Date d1, d2;
            try {
                String date;
                int index = name.indexOf("_");
                if (index > 0) {
                    date = name.substring(0, index);
                } else {
                    date = name;
                }
                d1 = format.parse(deleteDate);
                d2 = format.parse(date);
                boolean after = d2.before(d1);
                return after;
            } catch (Exception e) {
                return false;
            }
        }

        private Date getDateBefore() {
            Date date = new Date();
            Calendar now = Calendar.getInstance();
            now.setTime(date);
            now.set(Calendar.DATE, now.get(Calendar.DATE) - saveDay);
            return now.getTime();
        }
    }

    public static File getLogDir() {
        return logDir;
    }
}