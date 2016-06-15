package cn.momia.image.upload.model;

import java.io.InputStream;

/**
 * Created by hoze on 16/6/8.
 */
public class Audio {
    private String fileName;
    private InputStream fileStream;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public InputStream getFileStream() {
        return fileStream;
    }

    public void setFileStream(InputStream fileStream) {
        this.fileStream = fileStream;
    }
}
