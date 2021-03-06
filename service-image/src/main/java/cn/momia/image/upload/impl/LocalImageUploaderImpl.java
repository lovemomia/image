package cn.momia.image.upload.impl;

import cn.momia.common.webapp.config.Configuration;
import cn.momia.image.upload.model.Image;
import cn.momia.image.upload.model.UploadResult;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LocalImageUploaderImpl extends AbstractImageUploader {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public UploadResult upload(Image image) throws IOException {
        UploadResult result = new UploadResult();

        byte[] imageBytes = IOUtils.toByteArray(image.getFileStream());

        String relativePath = getImageRelativePath(imageBytes);
        String fullPath = getImageFullPath(relativePath);

        File outputFile = new File(fullPath);
        prepareOutputDir(outputFile);

        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
        outputStream.write(imageBytes);
        IOUtils.closeQuietly(outputStream);

        BufferedImage savedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));

        result.setPath(File.separator + relativePath);
        result.setWidth(savedImage.getWidth());
        result.setHeight(savedImage.getHeight());

        if (image.isCut()) {
            String largeUrl = resizeImage(fullPath, 800, 800, getCutImagesUrl(fullPath, 1));
            String file240180_url = cutImage(largeUrl, 240, 180, getCutImagesUrl(fullPath, 2));
            cutImage(file240180_url, 90, 90, getCutImagesUrl(fullPath, 3));
        }

        return result;
    }

    private String getImageRelativePath(byte[] imageBytes) {
        String date = DATE_FORMAT.format(new Date());
        String imageKey = DigestUtils.md5Hex(imageBytes);

        return StringUtils.join(new String[] { date, imageKey }, File.separator) + ".jpg";
    }

    private String getImageFullPath(String relativePath) {
        return StringUtils.join(new String[] { Configuration.getString("Image.Upload.Local.Dir"), relativePath }, File.separator);
    }

    private void prepareOutputDir(File outputFile) {
        File parent = outputFile.getParentFile();
        if (!parent.exists()) {
            synchronized (this) {
                if (!parent.exists()) parent.mkdirs();
            }
        }
    }

    private String resizeImage(String url, int width, int height, String to_url) throws IOException {
        File file = new File(url);

        BufferedImage image = ImageIO.read(file);
        Thumbnails.Builder<BufferedImage> builder = null;

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        if (width >= imageWidth && height >= imageHeight) {
            builder = Thumbnails.of(image).size(imageWidth, imageHeight);
        } else {
            if ((float) width / height > (float) imageWidth / imageHeight) {
                builder = Thumbnails.of(image).size((int) ((float) imageWidth / imageHeight * height), height).outputQuality(0.9);
            } else {
                builder = Thumbnails.of(image).size(width, (int) ((float) imageHeight / imageWidth * width)).outputQuality(0.9);
            }
        }
        builder.outputFormat("jpg").toFile(to_url);

        return to_url + ".jpg";
    }

    /**
     * 压缩至指定图片尺寸（例如：width:800;height:600），保持图片不变形，多余部分裁剪掉
     *
     * @param url
     * @param width
     * @param height
     * @throws Exception
     */
    private String cutImage(String url, int width, int height, String to_url) throws IOException {
        File file = new File(url);

        BufferedImage image = ImageIO.read(file);
        Thumbnails.Builder<BufferedImage> builder = null;

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        if (width >= imageWidth && height >= imageHeight) {
            builder = Thumbnails.of(image).size(imageWidth, imageHeight);
        } else {
            if ((float) height / width != (float) imageWidth / imageHeight) {
                if (imageWidth > imageHeight) {
                    image = Thumbnails.of(url).height(height).outputQuality(0.9).asBufferedImage();
                } else {
                    image = Thumbnails.of(url).width(width).outputQuality(0.9).asBufferedImage();
                }
                builder = Thumbnails.of(image).sourceRegion(Positions.CENTER, width, height).size(width, height).outputQuality(0.9);
            } else {
                builder = Thumbnails.of(image).size(width, height).outputQuality(0.9);
            }
        }
        builder.outputFormat("jpg").toFile(to_url);

        return to_url + ".jpg";
    }

    /**
     * 获取压缩裁剪图片的路径
     *
     * @param url
     * @param mark
     * @return
     */
    private String getCutImagesUrl(String url, int mark) {
        if (mark == 1) return url.replace(".jpg", "_l");
        else if (mark == 2) return url.replace(".jpg", "_m");
        else if (mark == 3) return url.replace(".jpg", "_s");
        else return url;
    }
}