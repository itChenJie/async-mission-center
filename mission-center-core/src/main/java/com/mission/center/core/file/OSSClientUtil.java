package com.mission.center.core.file;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyun.oss.model.VoidResult;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mission.center.constants.Constants;
import com.mission.center.error.ServiceException;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

@Slf4j
public class OSSClientUtil {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret ;
    //空间
    public  final String bucketName;
    //apk的空间
    public final String apkBucketName ;
    //文件存储目录
    private String filedir;
    private OSSClient ossClient;
    // 附件最大限制 单位kb
    private Integer maxSizekb;

    public OSSClientUtil(String endpoint,String accessKeyId, String accessKeySecret,String bucketName,
                         String apkBucketName,String filedir,Integer maxSizekb){
        this.endpoint = endpoint;
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.bucketName = bucketName;
        this.apkBucketName = apkBucketName;
        this.filedir = filedir;
        this.maxSizekb = maxSizekb == null ? (4096*4096):maxSizekb;
        ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
    }
    

    /**
     * 销毁
     */
    @PreDestroy
    public void destory() {
        ossClient.shutdown();
    }

    /**
     * 获得url链接
     *
     * @param key
     * @return
     */
    public String getTimeBarUrl(String key) {
        // 设置URL过期时间为10年  3600l* 1000*24*365*10
        Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10);
        // 生成URL
        URL url = ossClient.generatePresignedUrl(bucketName, key, expiration);
        if (url != null) {
            return url.toString();
        }
        return null;
    }

    /**
     * 上传到OSS服务器  如果同名文件会覆盖服务器上的
     *
     * @param instream 文件流
     * @param fileName 文件名称 包括后缀名
     * @return 出错返回"" ,唯一MD5数字签名
     */
    public String uploadFileByStream(InputStream instream, String fileName) {
        String ret = "";
        try {
            //创建上传Object的Metadata
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(instream.available());
            objectMetadata.setCacheControl("no-cache");
            objectMetadata.setHeader("Pragma", "no-cache");
            objectMetadata.setContentType("application/vnd.ms-excel");
            objectMetadata.setContentDisposition("inline;filename=" + fileName);
            //上传文件
            PutObjectResult putResult = ossClient.putObject(bucketName, dir(filedir) + fileName, instream, objectMetadata);
            ret = putResult.getETag();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (instream != null) {
                    instream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 附件上传
     * @return url
     */
    public String uploadByFile(String path){
        File file = new File(path);
        if (!file.exists()){
            throw new ServiceException("文件不存在！");
        }

        try(InputStream instream =new FileInputStream(file)) {
           String fileName = FileUtil.getName(path);
           String uploadPath = dir(filedir)+fileName;
           PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uploadPath, instream);
           ossClient.putObject(putObjectRequest);
           return uploadPath;
        }catch (OSSException oe){
            log.error("oss 文件上传失败！",oe);
            throw new ServiceException("oss 文件上传失败！");
        } catch (IOException e) {
            log.error("oss 文件获取失败！",e);
            throw new ServiceException("文件获取失败！");
        }
    }


    /**
     * 获得url链接
     *
     * @param key
     * @return
     */
    public String getUrl(String key) {
        // 设置URL过期时间为1天
        Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24);
        // 生成URL
        URL url = ossClient.generatePresignedUrl(bucketName, key, expiration);
        if (url != null) {
            return url.toString();
        }
        return null;
    }
    public String dir(String filedir){
        if (StringUtils.isNotBlank(filedir)){
            return filedir+ Constants.SLASH_SEPARATOR;
        }
        return DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN)+Constants.SLASH_SEPARATOR;
    }

    public void deleteObject(String key){
        VoidResult result = ossClient.deleteObject(bucketName, key);
        Assert.isFalse(result.getResponse()==null||!result.getResponse().isSuccessful()
                ,()->new ServiceException("oss 文件删除失败！"));
    }

}