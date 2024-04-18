package server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;

import java.io.IOException;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static final ChannelGroup allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public void channelActive(ChannelHandlerContext ctx) {
        // 当新客户端连接时，将其 Channel 添加到 ChannelGroup 中以便后续广播
        allChannels.add(ctx.channel());
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("Ok I think we are connected you sent to me: " + msg);
        for (Channel channel: allChannels) {
            if (channel != ctx.channel()) {
                channel.writeAndFlush(msg);
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush(); // 将之前接收到的信息冲刷到远程节点
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof IOException && cause.getMessage().contains("Connection reset")) {
            // 客户端连接被重置
            System.out.println("Client connection was reset!");
        } else {
            cause.printStackTrace(); // 打印其他类型的错误堆栈
        }
        ctx.close(); // 无论发生什么类型的异常，都关闭连接
    }

    public String praseMessage(String message) {
        return message;
    }
}
