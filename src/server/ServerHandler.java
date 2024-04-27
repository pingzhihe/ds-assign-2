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
    private static final AttributeKey<String> USER_NAME = AttributeKey.valueOf("USER_NAME");
    private static final AttributeKey<Boolean> MANAGER = AttributeKey.valueOf("MANAGER");

    private static boolean isManager = false;

    private HashMap<String, String> chatHistory = new HashMap<>();
    public void channelActive(ChannelHandlerContext ctx) {

        allChannels.add(ctx.channel());
        String message = "TXT:You have been connected!" + "\n";

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
            buf.release();
        }
        else if("IMG:".equals(type)){
            buf.skipBytes(4);
            byte[] img = new byte[buf.readableBytes()];
            buf.readBytes(img);
            processImageData(img);
            buf.release();
        }
    }

    private void processMessage(ChannelHandlerContext ctx, String msg) {
//        System.out.println("Manager: "+ isManager);
        if (msg.startsWith("manager")) {
            if (!isManager){
                isManager = true;
                ctx.channel().attr(MANAGER).set(true);
                sendMessage(ctx.channel(), "Manager\n");
            }
            // Only one manager is allowed
            else{
                sendMessage(ctx.channel(), "ManagerError\n");
            }
        }
        else if (msg.startsWith("normal")) {
            if (isManager){
                sendMessage(ctx.channel(), "Normal\n");
                ctx.channel().attr(MANAGER).set(false);
            }
            else {
                // No manager is present
                sendMessage(ctx.channel(), "NormalError\n");
            }
        }

        else if (msg.startsWith("wb")) {
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

        }
        else if (msg.startsWith("UserName")) {
            String[] parts = msg.split(":", 2);
            if (parts.length > 1) {
                String username = parts[1].trim();
                ctx.channel().attr(USER_NAME).set(username);
                if (Boolean.FALSE.equals(ctx.channel().attr(MANAGER).get())) {
                    sendNewUserToManager(ctx.channel().attr(USER_NAME).get());
                }
            }
        }
        else if (msg.startsWith("Delete")) {
            String[] parts = msg.split(":", 2);
            if (parts.length > 1) {
                String usernameToDelete = parts[1].trim();
                deleteChannel(usernameToDelete);
            }
        }else if (msg.startsWith("clear")){
            for (Channel channel : allChannels) {
                if (Boolean.FALSE.equals(channel.attr(MANAGER).get())) {
                    sendMessage(channel, "clear\n");
                }
            }
        }
        else if (msg.startsWith("reject")){
            System.out.println("Rejecting user: " + ctx.channel().attr(USER_NAME).get());
            String[] parts = msg.split(":", 2);
            if (parts.length > 1) {
                String usernameToDelete = parts[1].trim();
                deleteChannel(usernameToDelete);
            }
        }

        else if (msg.startsWith("approve")) {
            String[] parts = msg.split(":", 2);
            if (parts.length > 1) {
                String username = parts[1].trim();
                System.out.println("Approving user: " + username);
                for (Channel channel : allChannels) {
                    if (channel.attr(USER_NAME).get().equals(username)) {
                        sendMessage(channel, "Approved\n");
                    }
                }
            }
        }
        else if (msg.startsWith("shutdown")) {
            for (Channel channel : allChannels) {
                if (Boolean.FALSE.equals(channel.attr(MANAGER).get())) {
                    sendMessage(channel, "shutdown\n");
                }
            }
        }

        else {
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

        for (Channel channel : allChannels) {
            if (Boolean.TRUE.equals(channel.attr(MANAGER).get())) {
                sendMessage(channel, "Removed: " + usernameToDelete);
            }
        }
    }

    private void sendNewUserToManager(String newUser) {
        if (newUser == null) return;
        for (Channel channel : allChannels) {
            if (Boolean.TRUE.equals(channel.attr(MANAGER).get())) {
                sendMessage(channel, "NewUser: " + newUser);
                System.out.println("Sent new user to manager: " + newUser);
            }
        }
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush(); // Flush the buffer
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
