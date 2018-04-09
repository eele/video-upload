package edu.zhku.jsj144.lzc.videoUpload;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharingFilter;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;

import edu.zhku.jsj144.lzc.videoUpload.service.UploadInfoService;
import edu.zhku.jsj144.lzc.videoUpload.service.impl.UploadInfoServiceImpl;
import edu.zhku.jsj144.lzc.videoUpload.transcoding.VideoTranscodingHandlerThread;
import io.netty.bootstrap.ServerBootstrap;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import javax.xml.ws.Endpoint;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * VideoUploads any incoming data.
 */
public class VideoUploadServer {

	private int port;
	private String basePath;
	private static UploadInfoService uploadInfoService;

    static {
        try {
            uploadInfoService = new UploadInfoServiceImpl();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public VideoUploadServer(int port, String basePath) {
		this.port = port;
		this.basePath = basePath;
	}

	public void run() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast("decoder",
									new ObjectDecoder(ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
							ch.pipeline().addLast("encoder", new ObjectEncoder());
							ch.pipeline().addLast("handler", new FileInfoHandler(basePath, uploadInfoService));
						}

					});

			// Bind and start to accept incoming connections.
			ChannelFuture f = b.bind(port).sync();

			// Wait until the server socket is closed.
			// In this example, this does not happen, but you can do that to gracefully
			// shut down your server.
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
	    properties.load(
	            VideoUploadServer.class.getClassLoader().getResourceAsStream("server.properties"));

		int port = 8086;
		if (properties.getProperty("port") != null) {
			port = Integer.parseInt(properties.getProperty("port"));
		}

		String videoDir = properties.getProperty("videodir");
		if (videoDir == null) {
			videoDir = "./videofiles";
		}

		// 发布Web服务
        Endpoint.publish("http://localhost:8088/video/service/p", uploadInfoService);

		// 启动视频转码线程
		new VideoTranscodingHandlerThread().start();

		// 启动上传服务
		new VideoUploadServer(port, videoDir).run();
	}
}