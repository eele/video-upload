package edu.zhku.jsj144.lzc.videoUpload;

import java.io.File;
import java.io.IOException;

import edu.zhku.jsj144.lzc.video.util.uploadUtil.Info;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.channel.ChannelHandlerContext;

public class FileInfoClientHandler extends SimpleChannelInboundHandler<Info> {
	
	private String localFilepath = "D:\\Users\\ele\\Downloads\\S8TBL29I2.flv";

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Info msg) {
		ctx.pipeline().remove(this);
		ctx.pipeline().remove("encoder");
		ctx.pipeline().remove("decoder");
		ctx.pipeline().addLast("streamer", new ChunkedWriteHandler());
		ctx.pipeline().addLast("handler", new UploadClientHandler((Info) msg, localFilepath));
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws IOException {
		Info info = new Info("FILEINFO");
		info.setTotalsize(new File(localFilepath).length());
		ctx.writeAndFlush(info);
	}

}
