package edu.zhku.jsj144.lzc.videoUpload.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;

import edu.zhku.jsj144.lzc.videoUpload.service.UploadInfoService;
import edu.zhku.jsj144.lzc.videoUpload.service.entity.UploadProgressInfo;

public class UploadInfoServiceImpl implements UploadInfoService {

	private JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
	private Client dynamicClient = clientFactory.createClient("http://localhost:8080/video/service/p?wsdl");

	@Override
	public void checkToken(String token) throws Exception {
		dynamicClient.invoke("checkToken", token);
	}

}
