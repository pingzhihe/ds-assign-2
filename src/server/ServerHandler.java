package server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static final ChannelGroup allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static boolean hasManager = false;
    private static final AttributeKey<String> USER_NAME = AttributeKey.valueOf("USER_NAME");
    private static final AttributeKey<Boolean> MANAGER = AttributeKey.valueOf("MANAGER");

    private HashMap<String, String> chatHistory = new HashMap<>();
    public void channelActive(ChannelHandlerContext ctx) {

        allChannels.add(ctx.channel());
        String role = !hasManager ? "Manager" : "Normal";
        String message = "TXT:" + role + "\n";
        if (!hasManager) {
            hasManager = true;
            ctx.channel().attr(MANAGER).set(true);
        } else {
            ctx.channel().attr(MANAGER).set(false);
        }

        ByteBuf msgBuf = ctx.alloc().buffer();
        msgBuf.writeBytes(message.getBytes(CharsetUtil.UTF_8));
        ctx.writeAndFlush(msgBuf);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        if (buf.readableBytes() < 4) {
            return; // Not enough bytes to read the type indicator
        }

        buf.markReaderIndex(); // Mark the current reader index
        byte[] typeIndicator = new byte[4];
        buf.readBytes(typeIndicator);
        String type = new String(typeIndicator, StandardCharsets.UTF_8);
        buf.resetReaderIndex();

        if ("TXT:".equals(type)) {
            buf.skipBytes(4); // Skip the type prefix
            String msgString = buf.toString(CharsetUtil.UTF_8);
            processMessage(ctx, msgString.trim());
        }
        else if("IMG:".equals(type)){
            buf.skipBytes(4);
            byte[] img = new byte[buf.readableBytes()];
            buf.readBytes(img);
            processImageData(img);
        }
    }

    private void processMessage(ChannelHandlerContext ctx, String msg) {
        if (msg.startsWith("wb")) {
            for (Channel channel : allChannels) {
                if (channel != ctx.channel()) {
                    sendMessage(channel, msg + "\n");
                }
            }
        } else if (msg.startsWith("chat")) {
            String[] parts = msg.split(":", 2);  // Split only into two parts
            if (parts.length > 1) {
                String message = parts[1].trim();
                String username = ctx.channel().attr(USER_NAME).get();
                chatHistory.put(username, message);
                for (Channel channel : allChannels) {
                    for (String user : chatHistory.keySet()) {
                        sendMessage(channel, "chat " + user + ": " + chatHistory.get(user) + "\n");
                    }
                }
            }

        } else if (msg.startsWith("UserName")) {
            System.out.println("sfjsfjshfieuf");
            String[] parts = msg.split(":", 2);
            if (parts.length > 1) {
                String username = parts[1].trim();
                ctx.channel().attr(USER_NAME).set(username);
                sendNewUserToManager(ctx.channel().attr(USER_NAME).get());
            }
        } else if (msg.startsWith("Delete")) {
            String[] parts = msg.split(":", 2);
            if (parts.length > 1) {
                String usernameToDelete = parts[1].trim();
                deleteChannel(usernameToDelete);
            }
        } else {
            System.out.println("Server received: " + msg);
        }
    }

    private void processImageData(byte[] imageData) {
        System.out.println("Received image data");
        for (Channel channel : allChannels) {
            if (Boolean.FALSE.equals(channel.attr(MANAGER).get())) {
                ByteBuf imgBuf = channel.alloc().buffer();
                imgBuf.writeBytes("IMG:".getBytes(StandardCharsets.UTF_8));
                imgBuf.writeBytes(imageData);
                channel.writeAndFlush(imgBuf);
            }
        }
    }

    private void sendMessage(Channel channel, String message) {
        message = "TXT:" + message;
        ByteBuf msgBuf = channel.alloc().buffer();
        msgBuf.writeBytes(message.getBytes(CharsetUtil.UTF_8));
        channel.writeAndFlush(msgBuf);
    }

    private void deleteChannel(String usernameToDelete) {
        for (Channel channel : allChannels) {
            String username = channel.attr(USER_NAME).get();
            if (username != null && username.equalsIgnoreCase(usernameToDelete)) {
                sendMessage(channel, "KickedOut");
                channel.close();  // Close the channel associated with the username
                System.out.println("Disconnected user: " + username);
                break;  // Assuming only one channel per username, break after found
            }
        }
    }

    private void sendNewUserToManager(String newUser) {
        if (newUser == null) return; //
        for (Channel channel : allChannels) {
            if (Boolean.TRUE.equals(channel.attr(MANAGER).get())) {
                newUser = "TXT:NewUser: " + newUser + "\n";
                ByteBuf msgBuf = channel.alloc().buffer();
                msgBuf.writeBytes(newUser.getBytes(CharsetUtil.UTF_8));
                channel.writeAndFlush(msgBuf);
                System.out.println("Sent new user to manager: " + newUser);
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
            // Client connection reset
            System.out.println("Client connection was reset!");
        } else {
            cause.printStackTrace(); // Print other types of error stacks
        }
        ctx.close(); // No matter what type of exception occurs, close the connection
    }

}
