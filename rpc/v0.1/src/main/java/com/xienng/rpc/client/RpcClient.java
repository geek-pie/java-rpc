package com.xienng.rpc.client;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;

import com.xienng.rpc.serializer.MethodInvoker;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class RpcClient {
	private String host;

	private int port;

	public RpcClient(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public Object sendCommand(Class clazz, Method method, Object[] args) {

		MethodInvoker invoker = new MethodInvoker(method.getName(), args, clazz);
		final ClientHandler clientHandler = new ClientHandler(invoker);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(workerGroup);
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.SO_KEEPALIVE, true);
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new ObjectDecoder(1024 * 1024,
							ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
					ch.pipeline().addLast(new ObjectEncoder());
					ch.pipeline().addLast(clientHandler);

				}
			});

			// Start the client.
			ChannelFuture f = b.connect(new InetSocketAddress(host, port)).sync();
			// Wait until the connection is closed. 当一个任务完成的时候会继续执行。
        f.channel().closeFuture().sync();
			return clientHandler.getResponse();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			workerGroup.shutdownGracefully();
		}
		return null;

	}

}
