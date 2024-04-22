package client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private ChannelHandlerContext ctx;
    private String message;
    private ServerMessageReceiver listener;
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        ctx.writeAndFlush("Hello from Client!");
    }

    public void sendMessage(String msg) {
        if (ctx != null) {
            ctx.writeAndFlush(msg);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        listener.messageReceived(msg.toString());
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close(); // Close the connection on error
    }

    public ClientHandler(ServerMessageReceiver listener) {
        this.listener = listener;
    }
}
