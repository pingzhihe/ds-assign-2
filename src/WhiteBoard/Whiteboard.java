package WhiteBoard;

import javax.swing.*;
import java.awt.*;

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
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Whiteboard().setVisible(true));
    }
}


