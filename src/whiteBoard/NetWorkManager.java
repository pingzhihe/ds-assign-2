package whiteBoard;
import client.Client;
import client.ClientHandler;
public class NetWorkManager {
    private Client client;
    private ClientHandler clientHandler;

    public NetWorkManager(String host, int port) {
        // 初始化中介者
        // 初始化网络客户端
        client = new Client(host, port);
        clientHandler = new ClientHandler();
    }

    public void startConnection()throws InterruptedException {
        try {
            client.connect(clientHandler);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw e;
        }
    }
    public void sendMessage(int x, int y) {
        String message = "Coordinates: " + x + "," + y;
        clientHandler.sendMessage(message);
    }
}
