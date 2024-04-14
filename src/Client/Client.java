package Client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;


public class Client {
    private final String host;
    private final int port;
    private Channel channel;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new StringDecoder(), new StringEncoder(), new ClientHandler());
                        }
                    });

            ChannelFuture future = bootstrap.connect(host, port).sync();
            channel = future.channel();
        } catch (InterruptedException e) {
            group.shutdownGracefully();
            throw e;
        }
    }

    // 方法来发送消息
    public void sendMessage(String message) {
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(message);
        }
    }
}
