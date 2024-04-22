package client;
import whiteBoard.WhiteBoardEventListener;
import whiteBoard.Whiteboard;

public class ClientApp implements WhiteBoardEventListener, MessageReceiver{
    private Client client;
    private ClientHandler clientHandler;
    private Whiteboard whiteboard;

    public ClientApp(String host, int port) {

        // Initialize the client and clientHandler
        client = new Client(host, port);
        clientHandler = new ClientHandler(this);
        whiteboard = new Whiteboard(this);
    }

    @Override
    public void onDraw(String message) {
        clientHandler.sendMessage(message);
    }


    public void startConnection()throws InterruptedException {
        try {
            client.connect(clientHandler);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void shutdown() {
        client.shutdown();
    }

    @Override
    public void messageReceived(String message) {
        System.out.println("ClientApp: message Received: " + message);
        if (message.equals("Manager")){
            whiteboard.manaagerMode();
            whiteboard.start();

        }
        else if (message.equals("Normal")){
            whiteboard.start();
        }
        if (message.startsWith("wb")){
            whiteboard.praseMessage(message);
        }
    }

    public static void main(String[]args){
        ClientApp clientApp = new ClientApp("localhost", 8080);
        try{
            clientApp.startConnection();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
