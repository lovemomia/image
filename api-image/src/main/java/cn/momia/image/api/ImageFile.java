package cn.momia.image.api;

import org.apache.commons.lang3.StringUtils;

public class ImageFile {
    private static String domain;

    public void setDomain(String domain) {
        ImageFile.domain = domain;
    }

    public static String smallUrl(String path) {
        return url(path, "s");
    }

    public static String middleUrl(String path) {
        return url(path, "m");
    }

    public static String largeUrl(String path) {
        return url(path, "l");
    }

    public static String url(String path) {
        return url(path, "");
    }

    public static String url(String path, String size) {
        if (StringUtils.isBlank(path)) return "";
        if (path.startsWith("http://")) return path;
        if (!path.startsWith("/")) path = "/" + path;

        if (StringUtils.isBlank(size)) return domain + path;

        int indexOfDot = path.lastIndexOf(".");
        if (indexOfDot > 0) return domain + path.substring(0, indexOfDot) + "_" + size + path.substring(indexOfDot);
        return domain + path + "_" + size;
    }
}
