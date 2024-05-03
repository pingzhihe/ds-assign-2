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
        System.out.println("Server started on " + host + ":" + port);
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

            ChannelFuture f = b.bind(host, port).sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String host = "localhost";  // 默认主机地址
        int port = 8070;            // 默认端口

        if (args.length > 0) {
            host = args[0];  // 如果存在参数，则第一个参数为主机地址
        }
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);  // 第二个参数为端口，需要转换为整数
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number provided. Using default port 8070.");
            }
        }
        new Server(host, port).start();
    }
}
