package edu.zhku.jsj144.lzc.videoUpload;

import java.io.File;
import java.io.IOException;
import edu.zhku.jsj144.lzc.videoUpload.object.Info;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.channel.ChannelHandlerContext;

public class FileInfoClientHandler extends SimpleChannelInboundHandler<Info> {
	
	private String localFilepath = "/Users/apple/Documents/a.mp4";

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Info msg) throws Exception {
		System.err.println("c send file");
		
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
		System.err.println("c FileInfo");
	}

}
