package edu.zhku.jsj144.lzc.videoUpload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import edu.zhku.jsj144.lzc.video.util.uploadUtil.Info;
import edu.zhku.jsj144.lzc.videoUpload.service.UploadInfoService;
import edu.zhku.jsj144.lzc.videoUpload.transcoding.VideoTranscodingHandlerThread;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class UploadHandler extends ChannelInboundHandlerAdapter {

	private Info info;
	private long readedSize = 0;
	private FileOutputStream ofs;
	private UploadInfoService uploadInfoService;

	public UploadHandler(Info info, String basePath, UploadInfoService uploadInfoService) {
		this.info = info;
		this.uploadInfoService = uploadInfoService;
		try {
			File file = new File(basePath + "/" + info.getUid() + "/" + info.getVid());
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if (file.exists()) {
				readedSize = file.length();
			}
			ofs = new FileOutputStream(file, true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = (ByteBuf) msg;
		readedSize += buf.readableBytes();
		System.out.println(info.getTotalsize() + "   " + readedSize + "   " + buf.readableBytes());
		if (buf.isReadable()) {
			byte[] bytes = new byte[buf.readableBytes()];
			buf.readBytes(bytes);
			ofs.write(bytes);
		}


		// 上传完成
		if (readedSize >= info.getTotalsize()) {
			ctx.pipeline().remove(this);
			ofs.close();
			ctx.close();
			
			// 添加转码任务
			VideoTranscodingHandlerThread.addTask(info);
		}
		buf.release();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// Close the connection when an exception is raised.
		cause.printStackTrace();
		ctx.close();
	}
	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		ctx.pipeline().remove(this);
		ofs.close();
		ctx.close();
        super.channelInactive(ctx);
    }
	
}
