package cn.momia.image.upload;

import java.io.InputStream;

public class Image {
    private String fileName;
    private InputStream fileStream;
    private boolean cut = true;

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

    public boolean isCut() {
        return cut;
    }

    public void setCut(boolean cut) {
        this.cut = cut;
    }
}
