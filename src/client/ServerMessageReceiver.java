package client;

// The callback interface aims for event driven programming
// Acting as a mediator between the client handler and main entry of the client application.
public interface ServerMessageReceiver {
    void messageReceived(String message);

    void imgReceived(byte[] img);
}
