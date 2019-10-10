package com.littlec.sdk.utils;

import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.helpers.LogLog;

import java.io.IOException;

/**
 * @Type com.littlec.sdk.utils
 * @User zhangguoqiong
 * @Desc
 * @Date 2018/4/2
 * @Version
 */


public class LogConfigurator {
    private Level rootLevel = Level.DEBUG;
    private Priority priority = Priority.DEBUG;
    private String filePattern = "%d - [%p::%c::%C] - %m%n";
    private String logCatPattern = "%m%n";
    private String fileName = "android-log4j.log";
    private int maxBackupSize = 5;
    private long maxFileSize = 524288L;
    private boolean immediateFlush = true;
    private boolean useLogCatAppender = true;
    private boolean useFileAppender = true;
    private boolean resetConfiguration = true;
    private boolean internalDebugging = false;

    public LogConfigurator(){ }

    public LogConfigurator(String fileName){
        setFileName(fileName);
    }

    public LogConfigurator(String fileName, Level rootLevel){
        this(fileName);
        setRootLevel(rootLevel);
    }

    public LogConfigurator(String fileName, Level rootLevel, String filePattern){
        this(fileName);
        setRootLevel(rootLevel);
        setFilePattern(filePattern);
    }

    public LogConfigurator(String fileName, int maxBackupSize, long maxFileSize, String filePattern, Level rootLevel){
        this(fileName,rootLevel,filePattern);
        setMaxBackupSize(maxBackupSize);
        setMaxFileSize(maxFileSize);
    }

    public void configure(){
        Logger root = Logger.getRootLogger();
        if (isResetConfiguration()){
            LogManager.getLoggerRepository().resetConfiguration();
        }
        LogLog.setInternalDebugging(isInternalDebugging());

        try{
            if (isUseFileAppender()){
                configureFileAppender();
            }
        }catch (Exception e){
            useLogCatAppender=true;
        }

        root.setLevel(getRootLevel());
    }

    public void setLevel(String loggerName, Level level){
        Logger.getLogger(loggerName).setLevel(level);
    }

    private void configureFileAppender(){
        Logger root = Logger.getRootLogger();
        Layout fileLayout = new PatternLayout(getFilePattern());
        RollingFileAppender rollingFileAppender;
        try{
            rollingFileAppender = new RollingFileAppender(fileLayout,getFileName());
        }catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException("Exception configuring log system",e);
        }
        rollingFileAppender.setMaxBackupIndex(getMaxBackupSize());
        rollingFileAppender.setMaximumFileSize(getMaxFileSize());
        rollingFileAppender.setImmediateFlush(isImmediateFlush());
        rollingFileAppender.setThreshold(priority);

        root.addAppender(rollingFileAppender);
    }



    public Level getRootLevel() {
        return rootLevel;
    }

    public void setRootLevel(Level rootLevel) {
        this.rootLevel = rootLevel;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getFilePattern() {
        return filePattern;
    }

    public void setFilePattern(String filePattern) {
        this.filePattern = filePattern;
    }

    public String getLogCatPattern() {
        return logCatPattern;
    }

    public void setLogCatPattern(String logCatPattern) {
        this.logCatPattern = logCatPattern;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getMaxBackupSize() {
        return maxBackupSize;
    }

    public void setMaxBackupSize(int maxBackupSize) {
        this.maxBackupSize = maxBackupSize;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public boolean isImmediateFlush() {
        return immediateFlush;
    }

    public void setImmediateFlush(boolean immediateFlush) {
        this.immediateFlush = immediateFlush;
    }


    public boolean isUseFileAppender() {
        return useFileAppender;
    }

    public void setUseFileAppender(boolean useFileAppender) {
        this.useFileAppender = useFileAppender;
    }

    public boolean isResetConfiguration() {
        return resetConfiguration;
    }

    public void setResetConfiguration(boolean resetConfiguration) {
        this.resetConfiguration = resetConfiguration;
    }

    public boolean isInternalDebugging() {
        return internalDebugging;
    }

    public void setInternalDebugging(boolean internalDebugging) {
        this.internalDebugging = internalDebugging;
    }

}
