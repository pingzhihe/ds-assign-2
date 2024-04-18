package client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private ChannelHandlerContext ctx;
    private String message;
    private MessageListener listener;
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
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("Received from server: " + msg);
        message = (String) msg;
        if (listener != null) {
            listener.onMessageReceived(message);
        }
    }

    public String getMessage() {
        return message;
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close(); // Close the connection on error
    }

    public ClientHandler(MessageListener listener) {
        this.listener = listener;
    }
}
