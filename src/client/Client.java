package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.ResourceLeakDetector;

import java.net.ConnectException;


public class Client{
    private final String host;
    private final int port;
    private Channel channel;
    private EventLoopGroup group;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        this.group = new NioEventLoopGroup();
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
    }


    public void connect(ClientHandler handler) throws InterruptedException, ConnectException {
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.config().setOption(ChannelOption.TCP_NODELAY, true);
                            ch.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4),
                                    new LengthFieldPrepender(4),
                                    handler);
                        }
                    });

            ChannelFuture future = bootstrap.connect(host, port).sync();
            channel = future.channel();
            if (!future.isSuccess()) {
                throw future.cause(); // If the connection fails, throw an exception
            }

        } catch (Throwable e) {
            group.shutdownGracefully();
            if (e instanceof ConnectException) {
                throw (ConnectException) e; // Throw the ConnectException for the caller to handle
            } else if (e instanceof InterruptedException) {
                throw (InterruptedException) e; // Throw the InterruptedException for the caller to handle
            } else {
                throw new RuntimeException("Connection failed due to an unexpected error", e);
            }
        }
    }

    public void shutdown(){
        if (channel != null) {
            channel.close().awaitUninterruptibly();
        }
        group.shutdownGracefully();
    }

    public String getHost() {
        return host;
    }
    public int getPort() {
        return port;
    }
}
