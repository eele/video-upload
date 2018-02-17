package edu.zhku.jsj144.lzc.videoUpload;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		// TODO Auto-generated method stub
		ch.pipeline().addLast("decoder",
				new ObjectDecoder(ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
		ch.pipeline().addLast("encoder", new ObjectEncoder());
		ch.pipeline().addLast("handler", new FileInfoClientHandler());
	}

}
