package server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static final ChannelGroup allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public void channelActive(ChannelHandlerContext ctx) {
        // 当新客户端连接时，将其 Channel 添加到 ChannelGroup 中以便后续广播
        allChannels.add(ctx.channel());
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("Ok I think we are connected you sent to me: " + msg);
        ctx.write("Ok I think we are connected you sent to me: " + msg); // 将接收到的消息写给发送者，而不冲刷出站消息
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush(); // 将之前接收到的信息冲刷到远程节点
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close(); // 出错时，关闭channel
    }

    public String praseMessage(String message) {
        return message;
    }
}
