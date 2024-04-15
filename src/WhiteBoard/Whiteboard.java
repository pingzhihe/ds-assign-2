package WhiteBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Whiteboard extends JFrame {
    private DrawArea drawArea;
    private ToolPanel toolsPanel;

    public Whiteboard() {
        super("Whiteboard");
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
            }
            public void mouseReleased(MouseEvent e) {
                drawArea.handleMouseReleased(e.getX(), e.getY());
            }
        });
        drawArea.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                drawArea.handleMouseDragged(e.getX(), e.getY());
            }
        });
    }
    public void start() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Whiteboard().setVisible(true));
    }
}


