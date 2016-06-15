package cn.momia.image.upload;

import cn.momia.image.upload.model.Image;
import cn.momia.image.upload.model.UploadResult;

import java.io.IOException;

public interface ImageUploader {
    UploadResult upload(Image image) throws IOException;
}
