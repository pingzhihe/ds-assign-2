package client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private ChannelHandlerContext ctx;
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        // Send a message to the server once the connection is active
        ctx.writeAndFlush("Hello from Client!");
    }

    public void sendMessage(String message) {
        if (ctx != null) {
            ctx.writeAndFlush(message);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close(); // Close the connection on error
    }

}
