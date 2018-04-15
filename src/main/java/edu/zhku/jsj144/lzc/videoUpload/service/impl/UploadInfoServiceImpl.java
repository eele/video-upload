package edu.zhku.jsj144.lzc.videoUpload.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;

import edu.zhku.jsj144.lzc.videoUpload.service.UploadInfoService;
import edu.zhku.jsj144.lzc.videoUpload.service.entity.UploadProgressInfo;

import javax.jws.WebService;

@WebService(
        endpointInterface = "edu.zhku.jsj144.lzc.videoUpload.service.UploadInfoService",
        targetNamespace="http://service.videoUpload.lzc.jsj144.zhku.edu/",
        serviceName = "UploadInfoService")
public class UploadInfoServiceImpl implements UploadInfoService {

	private JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
	private Client dynamicClient;
	private String videodir;

    public UploadInfoServiceImpl() throws IOException {
        Properties properties = new Properties();
        properties.load(
                UploadInfoServiceImpl.class.getClassLoader().getResourceAsStream("server.properties"));
        videodir = properties.getProperty("videodir");
        String url = properties.getProperty("info_service_wsdurl");
        if (url == null) {
            url = "http://localhost:8080/video/service/p?wsdl";
        }
        dynamicClient = clientFactory.createClient(url);
    }

    @Override
    public void deleteVideoFile(String vid) {
        File file = new File(videodir + "/" + vid.substring(0, 32) + "/" + vid + "_");
        delDir(file);
    }

    @Override
	public void checkToken(String token) throws Exception {
		dynamicClient.invoke("checkToken", token);
	}

    @Override
    public void setUploadFinished(String vid) throws Exception {
        dynamicClient.invoke("setUploadFinished", vid);
    }

    private void delDir(File f) {
        // 判断是否是一个目录, 不是的话跳过, 直接删除; 如果是一个目录, 先将其内容清空.
        if(f.isDirectory()) {
            // 获取子文件/目录
            File[] subFiles = f.listFiles();
            // 遍历该目录
            for (File subFile : subFiles) {
                // 递归调用删除该文件: 如果这是一个空目录或文件, 一次递归就可删除. 如果这是一个非空目录, 多次
                // 递归清空其内容后再删除
                delDir(subFile);
            }
        }
        // 删除空目录或文件
        f.delete();
    }

}
