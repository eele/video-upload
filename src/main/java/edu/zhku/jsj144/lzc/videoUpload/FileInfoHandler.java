package edu.zhku.jsj144.lzc.videoUpload;

import java.io.File;
import java.io.IOException;

import edu.zhku.jsj144.lzc.videoUpload.object.Info;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class FileInfoHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {
		System.err.println("s FileInfo " + msg);
		Info info;
		File file = new File(((Info) msg).getFilepath());
		if (file.exists()) {
			info = new Info("UNFINISHED");
			info.setFinishedSize(file.length());
		} else {
			info = new Info("NEW");
			info.setFinishedSize(0);
		}
		ctx.pipeline().addLast(new UploadHandler((Info) msg));
		ctx.writeAndFlush(info);
		ctx.pipeline().remove(this);
		ctx.pipeline().remove("encoder");
		ctx.pipeline().remove("decoder");
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// Close the connection when an exception is raised.
		cause.printStackTrace();
		ctx.close();
	}
}
