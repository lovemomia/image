package cn.momia.image.upload.impl;

import cn.momia.common.webapp.config.Configuration;
import cn.momia.image.upload.model.Audio;
import cn.momia.image.upload.model.UploadResult;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hoze on 16/6/14.
 */
public class LocalAudioUploaderImpl extends AbstractAudioUploader {

    private final Logger log = LoggerFactory.getLogger(LocalAudioUploaderImpl.class);

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public UploadResult upload(Audio audio) throws IOException {

        UploadResult result = new UploadResult();

        byte[] audioBytes = IOUtils.toByteArray(audio.getFileStream());
        String relativePath = getAudioRelativePath(audioBytes,audio);
        String fullPath = getAudioFullPath(relativePath);

        File outputFile = new File(fullPath);
        prepareOutputDir(outputFile);

        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
        outputStream.write(audioBytes);
        IOUtils.closeQuietly(outputStream);

        result.setPath(File.separator + relativePath);
        result.setWidth(0);
        result.setHeight(0);

        return result;
    }

    private String getAudioRelativePath(byte[] audioBytes,Audio audio) {
        String date = DATE_FORMAT.format(new Date());
        String audioKey = DigestUtils.md5Hex(audioBytes);

        String fileName = audio.getFileName();
        String ext = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();


        return StringUtils.join(new String[]{date, audioKey}, File.separator) + ext;
    }

    private String getAudioFullPath(String relativePath) {
        return StringUtils.join(new String[] { Configuration.getString("Image.Upload.Local.AudioDir"), relativePath }, File.separator);
    }

    private void prepareOutputDir(File outputFile) {
        File parent = outputFile.getParentFile();
        if (!parent.exists()) {
            synchronized (this) {
                if (!parent.exists()) parent.mkdirs();
            }
        }
    }
}
