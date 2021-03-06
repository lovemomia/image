package cn.momia.image.web.ctrl;

import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.image.upload.AudioUploader;
import cn.momia.image.upload.model.Audio;
import cn.momia.image.upload.model.Image;
import cn.momia.image.upload.model.UploadResult;
import cn.momia.image.upload.ImageUploader;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/upload")
public class UploadController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

    @Autowired private ImageUploader imageUploader;
    @Autowired private AudioUploader audioUploader;

    @RequestMapping(value = "/image", method = { RequestMethod.POST })
    public MomiaHttpResponse uploadImage(HttpServletRequest request) {
        try {
            Image image = parseImage(request);
            UploadResult result = imageUploader.upload(image);

            return MomiaHttpResponse.SUCCESS(buildResponseData(result));
        } catch (Exception e) {
            LOGGER.error("fail to upload image file", e);
            return MomiaHttpResponse.FAILED;
        }
    }

    private Image parseImage(HttpServletRequest request) throws IOException, FileUploadException {
        Image image = new Image();
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        List items = upload.parseRequest(request);
        Iterator it = items.iterator();
        while (it.hasNext()) {
            FileItem item = (FileItem) it.next();
            if (!item.isFormField()) {
                image.setFileName(item.getName());
                image.setFileStream(item.getInputStream());
            } else {
                String CutName = item.getFieldName();
                if ("cut".equals(CutName)) {
                    boolean cut = Boolean.parseBoolean(item.getString());
                    image.setCut(cut);
                }
            }
        }

        return image;
    }

    private JSONObject buildResponseData(UploadResult result) {
        JSONObject data = new JSONObject();

        data.put("path", result.getPath());
        data.put("width", result.getWidth());
        data.put("height", result.getHeight());

        return data;
    }

    @RequestMapping(value = "/audio", method = { RequestMethod.POST })
    public MomiaHttpResponse uploadAudio(HttpServletRequest request) {
        try {
            Audio audio = parseAudio(request);
            UploadResult result = audioUploader.upload(audio);

            return MomiaHttpResponse.SUCCESS(buildResponseData(result));
        } catch (Exception e) {
            LOGGER.error("fail to upload audio file", e);
            return MomiaHttpResponse.FAILED;
        }
    }

    private Audio parseAudio(HttpServletRequest request) throws IOException, FileUploadException {
        Audio audio = new Audio();
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        List items = upload.parseRequest(request);
        Iterator it = items.iterator();
        while (it.hasNext()) {
            FileItem item = (FileItem) it.next();
            if (!item.isFormField()) {
                audio.setFileName(item.getName());
                audio.setFileStream(item.getInputStream());
            }
        }

        return audio;
    }
}