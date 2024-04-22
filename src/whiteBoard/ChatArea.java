package whiteBoard;

import javax.swing.*;
import java.awt.*;


public class ChatArea extends JPanel {
    private JTextArea chatArea;
    private JTextField chatInput;
    private JButton sendButton;

    public ChatArea() {
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScroll = new JScrollPane(chatArea);

        chatInput = new JTextField(30);
        sendButton = new JButton("Send");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(chatInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(chatScroll, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

    }
    public JButton getSendButton() {
        return sendButton;
    }

    public JTextField getChatInput() {
        return chatInput;
    }

    public JTextArea getChatArea() {
        return chatArea;
    }


}
