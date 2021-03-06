package cn.momia.image.upload.impl;

import cn.momia.common.webapp.config.Configuration;
import cn.momia.image.upload.model.Image;
import cn.momia.image.upload.model.UploadResult;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class OssImageUploaderImpl extends AbstractImageUploader {
    private OSSClient ossClient;
    private String bucketName;

    public void init() {
        String AccessId = Configuration.getString("Oss.AccessId");
        String AccessKey = Configuration.getString("Oss.AccessKey");
        String EndPoint = Configuration.getString("Oss.EndPoint");
        ossClient = new OSSClient(EndPoint, AccessId, AccessKey);

        bucketName = Configuration.getString("Oss.BucketName");
        if (!ossClient.doesBucketExist(bucketName)) {
            throw new RuntimeException("bucket: " + bucketName + " does not exist");
        }
    }

    public UploadResult upload(Image image) throws IOException {
        byte[] imageBytes = IOUtils.toByteArray(image.getFileStream());

        String fileName = DigestUtils.md5Hex(imageBytes) + ".jpg"; //上传到OSS的文件名
        uploadFile(fileName, new ByteArrayInputStream(imageBytes), imageBytes.length);

        UploadResult result = new UploadResult(); //返回结果
        result.setPath(fileName);

        BufferedImage savedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
        result.setWidth(savedImage.getWidth());
        result.setHeight(savedImage.getHeight());

        return result;
    }

    // 上传文件
    private void uploadFile(String fileName, InputStream stream, int length) {
        ObjectMetadata objectMeta = new ObjectMetadata();
        objectMeta.setContentLength(length);
        // 可以在metadata中标记文件类型
        objectMeta.setContentType("image/jpeg");

        ossClient.putObject(bucketName, fileName, stream, objectMeta);
    }
}