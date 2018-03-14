package edu.zhku.jsj144.lzc.videoUpload.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;

import edu.zhku.jsj144.lzc.videoUpload.service.UploadInfoService;
import edu.zhku.jsj144.lzc.videoUpload.service.entity.UploadProgressInfo;

public class UploadInfoServiceImpl implements UploadInfoService {

	private JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
	private Client dynamicClient = clientFactory.createClient("http://localhost:8080/video/service/r?wsdl");

	private static Map<String, Integer> uploadMap = new HashMap<String, Integer>(); // vid与视频上传进度的映射

	@Override
	public boolean isPreparedToUpload(String id) throws Exception {
		boolean flag = (boolean) dynamicClient.invoke("isPreparedToUpload", id)[0];
		if (flag) {
			uploadMap.put(id, 0);
		}
		return flag;
	}

	@Override
	public void setUploadFinished(String id) throws Exception {
		uploadMap.remove(id);
		dynamicClient.invoke("setUploadFinished", id);
	}

	@Override
	public void setUploadProgress(String vid, int progress) {
		uploadMap.put(vid, progress);
	}

	@Override
	public UploadProgressInfo getUploadProgress(String vid) {
		Integer p = uploadMap.get(vid);
		UploadProgressInfo info = new UploadProgressInfo();
		info.setVid(vid);
		if (p == null) {
			info.setProgress(-1);
			return info;
		} else {
			info.setProgress(p);
			return info;
		}
	}

}
