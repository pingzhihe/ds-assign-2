package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;

public class Server {
    private final String host;
    private int port;

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
        ResourceLeakDetector.setLevel(Level.PARANOID);
    }

    public void start() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.config().setOption(ChannelOption.TCP_NODELAY, true);
                            ch.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4),
                                    new LengthFieldPrepender(4),
                                    new ServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {

        String host = args.length > 1 ? args[0] : "localhost";
        int port = args.length > 2 ? Integer.parseInt(args[1]) : 8070;
        new Server(host, port).start();
    }
}
