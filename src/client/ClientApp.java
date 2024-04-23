package client;
import whiteBoard.WhiteBoardEventListener;
import whiteBoard.Whiteboard;
import whiteBoard.LoginDialog;
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
        userName = LoginDialog.showLoginDialog();
    }

    @Override
    public void onDraw(String message) {
        clientHandler.sendMessage(message);
    }


    public void startConnection() {
        try {
            client.connect(clientHandler);
        } catch (ConnectException | InterruptedException e) {

            // 捕获特定的 ConnectException
            LoginDialog.showErrorDialog("Cannot connect to server at " +
                    client.getHost() + ":" + client.getPort() + ". Please ensure the server is running.");

            System.exit(1);
        }

    }


    public void shutdown() {
        client.shutdown();
    }


    @Override
    public void messageReceived(String message) {
        System.out.println("ClientApp: message Received: " + message);
        if (message.equals("Manager")){
            clientHandler.sendMessage("UserName: "+ userName + "\n");
            whiteboard.managerMode();
            LoginDialog.showAdminWelcomeMessage();
            whiteboard.start();

        }
        else if (message.equals("Normal")){
            clientHandler.sendMessage("UserName: "+ userName + "\n");
            whiteboard.start();
        }

        if (message.startsWith("wb")){
            whiteboard.parseMessage(message);
        }
    }


    public static void main(String[]args){
        ClientApp clientApp = new ClientApp("localhost", 8080);
        clientApp.startConnection();
    }
}
