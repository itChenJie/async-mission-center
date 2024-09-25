package com.mission.center.core.file;

import cn.hutool.core.lang.Assert;
import com.mission.center.constants.Constants;
import com.mission.center.error.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Slf4j
@Component
public class FileService {
    @Value("${file.root-path}")
    private String rootPath;
    @Autowired
    private OSSClientUtil ossClientUtil;

    /**
     * 文件上传
     * @param path
     * @return
     */
    public String upload(String path){
        Assert.notBlank(path,()->new ServiceException("文件路径不能为空！"));
        String ossKay = ossClientUtil.uploadByFile(rootPath + Constants.SLASH_SEPARATOR+ path);
        return ossKay;
    }

    /**
     * 根据key 删除oss 文件
     * @param path
     */
    public void deleteOssFile(String path){
        ossClientUtil.deleteObject(path);
    }

    /**
     * 删除临时文件
     * @param path
     */
    public void deleteTempFile(String path){
        File file = new File(path);
        if(file.exists()){
            file.delete();
            return;
        }
        log.error("文件不存在！:{}",path);
    }

    public String getRootPath() {
        return rootPath;
    }

    /**
     * 创建或替换文件 不存在创建 存在删除再创建
     * @param filePath
     */
    public void createOrReplaceFile(String filePath) {
        File file = new File(filePath);
        try {
            if (file.exists()) {
                // 如果文件存在，先删除
                Assert.isFalse(!file.delete(),()->new ServiceException("任务文件已经存在，并且文件清空失败！" + filePath));
            }
            file.createNewFile();
        } catch (IOException e) {
            log.info("文件创建失败！",e);
            throw new ServiceException("文件创建失败！" + filePath);
        }
    }
}
