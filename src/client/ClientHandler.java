package client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import whiteBoard.Dialogs;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private ChannelHandlerContext ctx;
    private String message;
    private ServerMessageReceiver listener;
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.ctx = ctx;  // 保存上下文以便后续使用

        // 构建要发送的消息内容
        String message = "TXT: Hello from Client!\n";

        // 使用ByteBuf来封装字符串数据
        ByteBuf msgBuf = ctx.alloc().buffer();
        msgBuf.writeBytes(message.getBytes(CharsetUtil.UTF_8));

        // 发送消息
        ctx.writeAndFlush(msgBuf);

    }

    public void sendMessage(String msg) {
        if (ctx != null) {
            // 创建一个新的ByteBuf实例来存储要发送的数据
            ByteBuf msgBuf = ctx.alloc().buffer();
            // 将String转换为字节数组，并写入ByteBuf
            msgBuf.writeBytes(msg.getBytes(CharsetUtil.UTF_8));
            // 通过Netty的管道发送数据
            ctx.writeAndFlush(msgBuf);
        }
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
//        System.out.println("Client received: " + buf.toString(CharsetUtil.UTF_8));
        if (buf.readableBytes() < 4) {
            return; // Not enough bytes to read the length field
        }
        buf.markReaderIndex(); // Mark the current reader index
        byte[] typeIndicator = new byte[4];
        buf.readBytes(typeIndicator);
        String type = new String(typeIndicator);
        buf.resetReaderIndex();
        if ("TXT:".equals(type)) {
            buf.skipBytes(4);
            String msgString= buf.toString(CharsetUtil.UTF_8);
            listener.messageReceived(msgString);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close(); // Close the connection on error
        Dialogs.showSererErrorDialog(cause.getMessage());
    }

    public ClientHandler(ServerMessageReceiver listener) {
        this.listener = listener;
    }
}
