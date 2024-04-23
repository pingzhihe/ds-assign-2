package server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;

import java.io.IOException;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static final ChannelGroup allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static boolean hasManager = false;
    private static final AttributeKey<String> USER_NAME = AttributeKey.valueOf("USER_NAME");
    private static final AttributeKey<Boolean> MANAGER = AttributeKey.valueOf("MANAGER");
    public void channelActive(ChannelHandlerContext ctx) {
        allChannels.add(ctx.channel());
        if (!hasManager) {
            hasManager = true;
            ctx.writeAndFlush("Manager\n");
            ctx.channel().attr(MANAGER).set(true);
        }
        else{
            ctx.channel().attr(MANAGER).set(false);
            ctx.writeAndFlush("Normal\n");
        }
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String msgString = msg.toString();
        if (msgString.startsWith("wb")) {
            for (Channel channel : allChannels) {
                if (channel != ctx.channel()) {
                    channel.writeAndFlush(msgString + "\n");
                }
            }
        }
        else if (msgString.startsWith("chat")){
            System.out.println(msgString);
        }
        else if (msgString.startsWith("UserName")){
            String username = msgString.split(":")[1].trim();
            ctx.channel().attr(USER_NAME).set(username);
            System.out.println("Server received: " + msgString);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush(); // 将之前接收到的信息冲刷到远程节点
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof IOException && cause.getMessage().contains("Connection reset")) {
            // Client connection reset
            System.out.println("Client connection was reset!");
        } else {
            cause.printStackTrace(); // Print other types of error stacks
        }
        ctx.close(); // No matter what type of exception occurs, close the connection
    }

}
