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
        String host = "localhost";  // Default host address
        int port = 8070;            // Default port number

        if (args.length > 0) {
            host = args[0];   // If there are arguments, the first argument is the host address
        }
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]); // The second argument is the port number, which needs to be converted to an integer
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number provided. Using default port 8070.");
            }
        }
        new Server(host, port).start();
    }
}
