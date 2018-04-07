package edu.zhku.jsj144.lzc.videoUpload;

import java.io.File;

import edu.zhku.jsj144.lzc.video.util.uploadUtil.Info;
import edu.zhku.jsj144.lzc.videoUpload.service.UploadInfoService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class FileInfoHandler extends ChannelInboundHandlerAdapter {
	
	private String basePath;
	private UploadInfoService uploadInfoService;

	public FileInfoHandler(String basePath, UploadInfoService uploadInfoService) {
		this.basePath = basePath;
		this.uploadInfoService = uploadInfoService;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		Info info;
		try {
            uploadInfoService.checkToken(((Info) msg).getToken());
            // Token验证有效
            File file = new File(basePath + "/" + ((Info) msg).getUid() + "/" + ((Info) msg).getVid());
            if (file.exists()) {
                info = new Info("UNFINISHED");
                if (file.length() == ((Info) msg).getTotalsize()) {
                    throw new IllegalStateException();
                }
                info.setFinishedSize(file.length());
            } else {
                info = new Info("NEW");
                info.setFinishedSize(0);
            }
            info.setVid(((Info) msg).getVid());
            ctx.pipeline().addLast(new UploadHandler((Info) msg, basePath, uploadInfoService));
            ctx.writeAndFlush(info);
            ctx.pipeline().remove(this);
            ctx.pipeline().remove("encoder");
            ctx.pipeline().remove("decoder");
        } catch (Exception e) {
            // Token验证无效
            info = new Info("INVALID");
            ctx.writeAndFlush(info);
            ctx.close();
        }
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// Close the connection when an exception is raised.
		cause.printStackTrace();
		ctx.close();
	}
}
