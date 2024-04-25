package client;
import whiteBoard.WhiteBoardEventListener;
import whiteBoard.Whiteboard;
import whiteBoard.Dialogs;

import java.net.ConnectException;

public class ClientApp implements WhiteBoardEventListener, ServerMessageReceiver {
    private Client client;
    private ClientHandler clientHandler;
    private Whiteboard whiteboard;

    private String userName;

    public ClientApp(String host, int port) {

        // Initialize the client and clientHandler
        client = new Client(host, port);
        clientHandler = new ClientHandler(this);
        whiteboard = new Whiteboard(this);
        userName = Dialogs.showLoginDialog();
    }

    @Override
    public void onDraw(String message) {
        clientHandler.sendMessage("TXT:" + message);
    }

    public void onImg(byte[] img){
        clientHandler.sendImg(img);
    }

    public void startConnection() {
        try {
            client.connect(clientHandler);
        } catch (ConnectException | InterruptedException e) {

            // 捕获特定的 ConnectException
            Dialogs.showErrorDialog("Cannot connect to server at " +
                    client.getHost() + ":" + client.getPort() + ". Please ensure the server is running.");

            System.exit(1);
        }

    }
    public void shutdown() {
        client.shutdown();
    }


    @Override
    public void messageReceived(String message) {
        message = message.trim();
        if (message.equals("Manager")){
            System.out.println(userName);
            clientHandler.sendMessage("TXT:UserName: "+ userName + "\n");
            whiteboard.managerMode();
            Dialogs.showAdminWelcomeMessage();
            whiteboard.start();
        }
        else if (message.equals("Normal")){
            clientHandler.sendMessage("TXT:UserName: "+ userName + "\n");
            whiteboard.start();
        }

        else if (message.startsWith("wb")){
            whiteboard.parseMessage(message);
        }
        else if (message.startsWith("NewUser")){
            String newUser = message.split(":")[1].trim();
            whiteboard.addUser(newUser);
            System.out.println("Received new user: " + newUser);
        }
        else if (message.equals("KickedOut")){
            Dialogs.showKickOutMessage();
            whiteboard.shunDown();
        } else if (message.startsWith("chat")) {
            System.out.println(message);
            String chat = message.substring(5);
            whiteboard.receiveChat(chat);

        } else if (message.startsWith("clear")){
            whiteboard.clear();
        }
        else {
            System.out.println("Client received: " + message);
        }
    }
    @Override
    public void imgReceived(byte[] img){
        System.out.println("Received image data");
        whiteboard.receiveImg(img);
    }

    public static void main(String[]args){
        ClientApp clientApp = new ClientApp("localhost", 8080);
        clientApp.startConnection();
    }
}
