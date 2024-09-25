package com.mission.center.core.confing;

import com.mission.center.core.file.OSSClientUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfing {

    @Bean
    public OSSClientUtil ossClientUtil(@Value("${oss.endpoint}")String endpoint,@Value("${oss.accessKeyId}")String accessKeyId,
                                       @Value("${oss.accessKeySecret}")String accessKeySecret, @Value("${oss.bucketName}")String bucketName,
                                       @Value("${oss.apkBucketName}")String apkBucketName,@Value("${oss.filedir}")String filedir){
        OSSClientUtil ossClientUtil = new OSSClientUtil(endpoint,accessKeyId,accessKeySecret,bucketName,
                apkBucketName,filedir,null);
        return ossClientUtil;
    }
}
