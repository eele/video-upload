package edu.zhku.jsj144.lzc.videoUpload.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface UploadInfoService {

    @WebMethod(operationName = "deleteVideoFile")
    public void deleteVideoFile(@WebParam(name = "vid") String vid);

	public void checkToken(String token) throws Exception;

}
