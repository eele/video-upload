package edu.zhku.jsj144.lzc.videoUpload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import edu.zhku.jsj144.lzc.videoUpload.object.Info;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class UploadHandler extends ChannelInboundHandlerAdapter {

	private Info info;
	private long readedSize = 0;
	private FileOutputStream ofs;

	public UploadHandler(Info info) {
		this.info = info;
		
		try {
			File file = new File(info.getFilepath());
			ofs = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {
		ByteBuf buf = (ByteBuf) msg;
		readedSize += buf.readableBytes();
		if (buf.isReadable()) {
			byte[] bytes = new byte[buf.readableBytes()];
			buf.readBytes(bytes);
			ofs.write(bytes);
		}

		System.out.println(info.getTotalsize() + "   " + readedSize);

		if (readedSize >= info.getTotalsize()) {
			ctx.pipeline().remove(this);
			ofs.close();
			ctx.close();
		}
		buf.release();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// Close the connection when an exception is raised.
		cause.printStackTrace();
		ctx.close();
	}
	
}
