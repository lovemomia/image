package cn.momia.image.upload;

import cn.momia.image.upload.model.Audio;
import cn.momia.image.upload.model.UploadResult;

import java.io.IOException;

/**
 * Created by hoze on 16/6/14.
 */
public interface AudioUploader {
    UploadResult upload(Audio audio) throws IOException;
}
