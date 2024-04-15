package Client;

import WhiteBoard.Whiteboard;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientApp {
    private Client client;
    private Whiteboard whiteboard;

    public ClientApp(String host, int port) {
        // 初始化网络客户端
        client = new Client(host, port);

        // 初始化 GUI
        whiteboard = new Whiteboard();
    }

    public void start() {
        try {
            client.connect();
            SwingUtilities.invokeLater(() -> {
                whiteboard.setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unable to connect to server: " + e.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
            // After connect error, close the application
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        // 主函数中启动客户端应用
        ClientApp app = new ClientApp("localhost", 8080);
        app.start();
    }
}
