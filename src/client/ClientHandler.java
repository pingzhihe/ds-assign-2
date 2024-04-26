package client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import whiteBoard.Dialogs;

import java.nio.charset.StandardCharsets;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private ChannelHandlerContext ctx;
    private String message;
    private ServerMessageReceiver listener;
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.ctx = ctx;  // Save the context for future use

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
            // Create a new ByteBuf instance to store the data to be sent
            ByteBuf msgBuf = ctx.alloc().buffer();
            // Convert the string to a byte array and write it to the ByteBuf
            msgBuf.writeBytes(msg.getBytes(CharsetUtil.UTF_8));

            ctx.writeAndFlush(msgBuf); //The ByteBuf will be released automatically by the Netty framework
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
            buf.release();  // Release the buffer
        }
        else if ("IMG:".equals(type)) {
            buf.skipBytes(4);
            byte[] img = new byte[buf.readableBytes()];
            buf.readBytes(img);
            listener.imgReceived(img);
            buf.release();
        }
    }
    public void sendImg(byte[] img) {
        ByteBuf buffer = Unpooled.buffer(4 + img.length);
        buffer.writeBytes("IMG:".getBytes(StandardCharsets.UTF_8));
        buffer.writeBytes(img);
        ctx.writeAndFlush(buffer);
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
