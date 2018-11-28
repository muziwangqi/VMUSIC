package com.soling.bt.model;

public class FileMessage extends BaseMessage {
    private String fileName;
    private int fileLength;
    private int fileNameLength;
    public FileMessage(){}

    public String getFileName() {
        return fileName;
    }

    public FileMessage setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public int getFileLength() {
        return fileLength;
    }

    public FileMessage setFileLength(int fileLength) {
        this.fileLength = fileLength;
        return this;
    }

    @Override
    public String getMsgContent() {
        return super.getMsgContent();
    }

    public FileMessage setFileNameLength(int fileNameLength) {
        this.fileNameLength = fileNameLength;
        return this;
    }

    @Override
    public String toString() {
        return "FileMessage{"+
                "fileName='"+fileName+'\''+
                ", fileLength="+fileLength+
                ", fileNameLength="+fileNameLength+
                "}";
    }
}
