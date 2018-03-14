package edu.zhku.jsj144.lzc.videoUpload.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import edu.zhku.jsj144.lzc.videoUpload.service.entity.UploadProgressInfo;

@Path("videos")
@Produces(MediaType.APPLICATION_JSON)
public interface UploadInfoService {

	public boolean isPreparedToUpload(String id) throws Exception;

	public void setUploadFinished(String id) throws Exception;
	
	public void setUploadProgress(String vid, int progress);
	
	@GET
	@Path("/{vid}/uploadprogress")
	public UploadProgressInfo getUploadProgress(@PathParam("vid") String vid);
}
