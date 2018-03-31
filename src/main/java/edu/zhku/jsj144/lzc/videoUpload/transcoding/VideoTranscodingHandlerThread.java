package edu.zhku.jsj144.lzc.videoUpload.transcoding;

import java.io.File;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import edu.zhku.jsj144.lzc.video.util.uploadUtil.Info;

public class VideoTranscodingHandlerThread extends Thread {

	private static Queue<Info> q = new LinkedBlockingQueue<Info>();
	private String basePath;
	private String execPath = "D:\\Users\\ele\\Downloads\\ffmpeg_\\bin\\ffmpeg.exe ";
	
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
				String videoPath = basePath + "/" + videoInfo.getUid() + "/" + videoInfo.getVid();
				
				new File(videoPath + "_").mkdir();
				
				System.out.println(execPath + "-i " + videoPath + " -acodec copy -vcodec copy " + videoPath + "_/" + "video.mp4");
				System.out.println(execPath + "-i " + videoPath + "_/" + "video.mp4" + " -c copy -bsf:v h264_mp4toannexb " + videoPath + "_/" + "output.ts");
				System.out.println(execPath + "-i " + videoPath + "_/" + "output.ts" + " -c copy -map 0 -f segment -segment_list " + 
								videoPath + "_/" + "playlist.m3u8 -segment_time 10 " + videoPath + "_/" + "output%03d.ts");

				Runtime.getRuntime().exec(
						execPath + "-i " + videoPath + " -acodec copy -vcodec copy " + videoPath + "_/" + "video.mp4");
				Runtime.getRuntime().exec(
						execPath + "-i " + videoPath + "_/" + "video.mp4" + " -c copy -bsf:v h264_mp4toannexb " + videoPath + "_/" + "output.ts");
				Runtime.getRuntime().exec(
						execPath + "-i " + videoPath + "_/" + "output.ts" + " -c copy -map 0 -f segment -segment_list " +
								videoPath + "_/" + "playlist.m3u8 -segment_time 10 " + videoPath + "_/" + "output%03d.ts");
				
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
