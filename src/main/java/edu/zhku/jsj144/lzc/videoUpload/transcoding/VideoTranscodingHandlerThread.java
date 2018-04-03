package edu.zhku.jsj144.lzc.videoUpload.transcoding;

import java.io.File;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import edu.zhku.jsj144.lzc.video.util.uploadUtil.Info;

public class VideoTranscodingHandlerThread extends Thread {

	private static Queue<Info> q = new LinkedBlockingQueue<Info>();
	private String basePath;

	public VideoTranscodingHandlerThread(String basePath) {
		this.basePath = basePath;
	}

	public static void addTask(Info info) {
		try {
			((LinkedBlockingQueue<Info>) q).put(info);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				Info videoInfo = ((LinkedBlockingQueue<Info>) q).take();
				Runtime.getRuntime().exec("sh /usr/local/tomcat/video.sh " + videoInfo.getUid() + " " + videoInfo.getVid());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
