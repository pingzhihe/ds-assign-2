package whiteBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
public class Whiteboard extends JFrame {
    private DrawArea drawArea;
    private ToolPanel toolsPanel;

    private ManagerPanel managerPanel;
    private ChatArea chatArea;
    private boolean isManager = false;



    private WhiteBoardEventListener listener;

    public Whiteboard(WhiteBoardEventListener listener) {

        super("Whiteboard");
        this.listener = listener;
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        initializeComponents();
        setupLayout();
        setupActions();
    }

    private void initializeComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        chatArea = new ChatArea();
        drawArea = new DrawArea();
        toolsPanel = new ToolPanel(drawArea, isManager);
        managerPanel = new ManagerPanel();
        chatArea.setPreferredSize(new Dimension(200,getHeight()));
        managerPanel.setPreferredSize(new Dimension(200, getHeight()));
        drawArea.setPreferredSize(new Dimension(800, 600));
        getContentPane().add(drawArea, BorderLayout.CENTER);
    }

    private void setupLayout() {
        getContentPane().add(chatArea, BorderLayout.EAST);
        getContentPane().add(toolsPanel, BorderLayout.NORTH);
    }

    private void setupActions() {
        toolsPanel.setupActions();
        drawArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                drawArea.handleMousePressed(e.getX(), e.getY());
                if (drawArea.getState().equals("text")) {
                }
            }
            public void mouseReleased(MouseEvent e) {
                drawArea.handleMouseReleased(e.getX(), e.getY());
                if (!drawArea.getState().equals("free_draw") && !drawArea.getState().equals("text") &&
                        !drawArea.getState().equals("eraser")) {
                    listener.onDraw(generateMessage(drawArea) + " " + e.getX() + " " + e.getY() + "\n");
                    drawArea.handleMouseReleased(e.getX(), e.getY());
                }
            }
        });

        drawArea.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (drawArea.getState().equals("free_draw") || drawArea.getState().equals("eraser")) {
                    listener.onDraw(generateMessage(drawArea) + " " + e.getX() + " " + e.getY()+ "\n");
                    drawArea.handleMouseDragged(e.getX(), e.getY());
                }
            }
        });

        chatArea.getSendButton().addActionListener(e -> {
            String message = chatArea.getChatInput().getText();
            chatArea.getChatInput().setText("");
            listener.onDraw("chat: " + message + "\n");
        });

        managerPanel.getKickOutButton().addActionListener(e -> {
            String userName = managerPanel.kickOutSelectedUsers();
            if (!userName.equals(" ")) {
                listener.onDraw("Delete: " + userName + "\n");
            }
        });

    }
    public void start() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    public String generateMessage(DrawArea d1) {
        return "wb " + d1.getState() + " " + d1.getColor()+ " " + d1.getThickness() + " " + d1.getOldX() + " " + d1.getOldY();
    }

    public void parseMessage(String message){
        String[] messages = message.split("\n");
        for (String msg : messages) {
            String trimmedMsg= msg.substring(3);
            drawArea.parseMessage(trimmedMsg);
        }
    }
    public void managerMode(){
        setSize(1200, 600);
        toolsPanel.setManager();
        getContentPane().add(managerPanel, BorderLayout.WEST);
    }

    public void addUser(String newUser){
        managerPanel.addUser(newUser);
    }
    public void shunDown(){
        System.exit(0);
    }
    public void receiveChat(String message){
        chatArea.getChatArea().append(message + "\n");
    }
}

