package whiteBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import client.MessageListener;

public class Whiteboard extends JFrame implements MessageListener{
    private DrawArea drawArea;
    private ToolPanel toolsPanel;
    private NetWorkManager netWorkManager;

    public Whiteboard() {
        super("Whiteboard");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                netWorkManager.shutdown();
                System.exit(0);
            }
        });

        netWorkManager = new NetWorkManager("localhost", 8080,  this);
        try{
            netWorkManager.startConnection();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        initializeComponents();
        setupLayout();
        setupActions();
    }

    private void initializeComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);
        drawArea = new DrawArea();
        toolsPanel = new ToolPanel(drawArea);
    }

    private void setupLayout() {
        getContentPane().add(toolsPanel, BorderLayout.NORTH);
        getContentPane().add(drawArea, BorderLayout.CENTER);
    }

    private void setupActions() {
        toolsPanel.setupActions();
        drawArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                drawArea.handleMousePressed(e.getX(), e.getY());
                if (drawArea.getState().equals("text")) {
                    netWorkManager.sendMessage(drawArea.getState() + " " + e.getX() + " " + e.getY());
                }
            }
            public void mouseReleased(MouseEvent e) {
                drawArea.handleMouseReleased(e.getX(), e.getY());
                if (!drawArea.getState().equals("free_draw") && !drawArea.getState().equals("text") && !drawArea.getState().equals("eraser")) {
                    netWorkManager.sendMessage(drawArea.getState() + " " + e.getX() + " " + e.getY() + " " + drawArea.getOldX() + " " + drawArea.getOldY());
                }
            }
        });
        drawArea.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (drawArea.getState().equals("free_draw")) {
                    netWorkManager.sendMessage(drawArea.getState() + " " + e.getX() + " " + e.getY()+ " " + drawArea.getOldX() + " " + drawArea.getOldY());
                    drawArea.handleMouseDragged(e.getX(), e.getY());
                }
            }
        });

    }

    @Override
    public void onMessageReceived(String message) {
        SwingUtilities.invokeLater(() -> {
            if (drawArea != null) {
                drawArea.praseMessage(message);
            }
        });
    }
    public void start() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    public static void main(String[] args) {
        Whiteboard whiteboard = new Whiteboard();
        whiteboard.start();
    }

}


