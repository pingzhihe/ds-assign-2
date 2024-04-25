package client;

public interface ServerMessageReceiver {
    void messageReceived(String message);

    void imgReceived(byte[] img);
}
