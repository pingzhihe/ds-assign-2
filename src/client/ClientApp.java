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

    boolean isManager = false;

    public ClientApp(String host, int port) {

        // Initialize the client and clientHandler
        client = new Client(host, port);
        clientHandler = new ClientHandler(this);
        whiteboard = new Whiteboard(this);
        userName = Dialogs.showLoginDialog();
        System.out.println("Client started at host: " + host + ", at port :" + port);
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
            Dialogs.showWaitingMessage();
        }
        else if (message.equals("ManagerError")) {
            Dialogs.showErrorDialog("Only one manager is allowed in the session.");
            System.exit(1);
        }

        else if (message.equals("NormalError")) {
            Dialogs.showErrorDialog("No manager is present in the session.");
            System.exit(1);
        }
        else if (message.startsWith("wb")){
            whiteboard.parseMessage(message);
        }
        else if (message.startsWith("NewUser")){

            String newUser = message.split(":")[1].trim();
            String command = Dialogs.showJoinMessage(newUser);

            if (command.equals("approve")){
                clientHandler.sendMessage("TXT:approve:" + newUser + "\n");
                clientHandler.sendImg(whiteboard.getImg());
                whiteboard.addUser(newUser);
                System.out.println("Received new user: " + newUser);
            }
            else{
                clientHandler.sendMessage("TXT:reject:" + newUser + "\n");
            }
        }

        else if (message.equals("Approved")){
            whiteboard.start();
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

        else if (message.startsWith("You have been connected!")){
            if (isManager){
                System.out.println("Client received: " + message);
                clientHandler.sendMessage("TXT:manager\n");
            }
            else{
                clientHandler.sendMessage("TXT:normal\n");
            }
        }
        else if (message.startsWith("shutdown")){
            Dialogs.showManagerShutDown();
            whiteboard.shunDown();
        }
        else if (message.startsWith("Removed")){
            String removedUser = message.split(":")[1].trim();
            whiteboard.removeUser(removedUser);
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

    @Override
    public void onWhiteBoardClose(){
        if (isManager){
            clientHandler.sendMessage("TXT:shutdown\n");
        }
        else{
            clientHandler.sendMessage("TXT:Delete:" + userName + "\n");
        }
    }

    public static void main(String[]args){
        if (args.length < 1) {
            System.err.println("Usage: java ClientApp [create|join] [host] [port]");
            System.exit(1);
        }
        String mode = args[0];  // Expected "create" or "join"
        String host = args.length > 1 ? args[1] : "localhost";  // Default to localhost if not specified
        int port = args.length > 2 ? Integer.parseInt(args[2]) : 8070;  // Default to port 8080 if not specified

        if (!mode.equals("create") && !mode.equals("join")) {
            System.err.println("Invalid mode. Use 'create' or 'join'.");
            System.exit(1);
        }

        ClientApp clientApp = new ClientApp(host, port);
        if (mode.equals("create")) {
            clientApp.isManager = true;
            System.out.println("Creating a new whiteboard session...");
        }
        else{
            System.out.println("Joining an existing whiteboard session...");
        }

        clientApp.startConnection();

    }
}
