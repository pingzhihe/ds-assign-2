package whiteBoard;
import client.Client;
import client.ClientHandler;
import client.MessageListener;

public class NetWorkManager {
    private Client client;
    private ClientHandler clientHandler;

    public NetWorkManager(String host, int port, MessageListener listener) {
        // 初始化中介者
        // 初始化网络客户端
        client = new Client(host, port);
        clientHandler = new ClientHandler(listener);
    }

    public void startConnection()throws InterruptedException {
        try {
            client.connect(clientHandler);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw e;
        }
    }
    public void sendMessage(String msg) {
        String message = msg;
        clientHandler.sendMessage(message);
    }
    public void shutdown() {
        client.shutdown();
    }

}
