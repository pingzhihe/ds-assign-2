package Client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // Send a message to the server once the connection is active
        ctx.writeAndFlush("Hello from Client!");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // Print the message received from the server
        System.out.println("Received from server: " + msg);
        ctx.close(); // Close the connection after receiving the message
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close(); // Close the connection on error
    }
}
