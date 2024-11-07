package com.zhu.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Component
@ConfigurationProperties(prefix = "qiniuyun")
public class QiNiuUtils {

    private String accessKey;

    private String secretKey;

    private String bucket;

    @Value("${qiniuyun.qiniu-image-domain}")
    private String QINIU_IMAGE_DOMAIN;

    @Value("${qiniuyun.expire-in-seconds}")
    private long expireSeconds;

    public Auth getAuth(){
        return Auth.create(accessKey,secretKey);
    }

    /*
        上传文件到存储空间
     */
    public String upload(InputStream inputStream,String fileName,String type){
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.huadongZheJiang2());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = getAuth();
        String upToken = auth.uploadToken(bucket);
        String url = null;
        String prefix = getPrefix(type);
        try {
            Response response = uploadManager.put(inputStream, prefix+fileName, upToken,null,null);
            url = QINIU_IMAGE_DOMAIN + JSONObject.parseObject(response.bodyString()).get("key");
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
            //将上传完的图片的外链返回出去
            return url;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.toString());
        }
        return url;
    }

    public String getPrefix(String type){
        if(type.equals("video")){
            return "video/";
        }else{
            return "videoPic/";
        }
    }

    /*
        查看七牛云存储空间中的所有文件
     */
    public void cat(){
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region0());
        Auth auth = getAuth();
        BucketManager manager = new BucketManager(auth, cfg);
        //要查看文件名的前缀
        String prefix = "Bicon";
        int limit = 1000;
        String delimiter = "";
        BucketManager.FileListIterator fileListIterator = manager.createFileListIterator(bucket, prefix, limit, delimiter);
        while (fileListIterator.hasNext()) {
            //处理获取的file list结果
            FileInfo[] items = fileListIterator.next();
            List<FileInfo> fileInfos = Arrays.asList(items);
            List<String> collect = fileInfos.stream().map(fileInfo -> {
                return QINIU_IMAGE_DOMAIN+fileInfo.key;
            }).collect(Collectors.toList());
            //将所有符合前缀的文件进行显示
            System.out.println(collect);
        }
    }

    /*
        获取文件访问地址
     */
    public String fileUrl(String fileName) throws UnsupportedEncodingException {
        String encodedFileName = URLEncoder.encode(fileName, "utf-8");
        String publicUrl = String.format("%s/%s", QINIU_IMAGE_DOMAIN, encodedFileName);
        Auth auth = getAuth();
        long expireInSeconds = expireSeconds;
        if(-1 == expireInSeconds){
            return auth.privateDownloadUrl(publicUrl);
        }
        return auth.privateDownloadUrl(publicUrl, expireInSeconds);
    }

}