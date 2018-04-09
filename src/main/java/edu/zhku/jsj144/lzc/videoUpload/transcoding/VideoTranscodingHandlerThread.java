package edu.zhku.jsj144.lzc.videoUpload.transcoding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import edu.zhku.jsj144.lzc.video.util.uploadUtil.Info;

public class VideoTranscodingHandlerThread extends Thread {

	private static Queue<Info> q = new LinkedBlockingQueue<Info>();
	private String transcodingScript = "sh ./video.sh";

	public VideoTranscodingHandlerThread() throws IOException {
        Properties properties = new Properties();
        properties.load(
                VideoTranscodingHandlerThread.class.getClassLoader().getResourceAsStream("server.properties"));
        transcodingScript = properties.getProperty("transcoding_script");
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
				Process proc = Runtime.getRuntime().exec(transcodingScript + " " + videoInfo.getUid() + " " + videoInfo.getVid());
				StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERR");
				StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUT");
				errorGobbler.start();
				outputGobbler.start();
				proc.waitFor();
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
