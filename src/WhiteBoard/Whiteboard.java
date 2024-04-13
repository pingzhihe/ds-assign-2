package WhiteBoard;

import javax.swing.*;
import java.awt.*;




public class Whiteboard extends JFrame {
    private DrawArea drawArea;
    private JButton clearBtn, textBtn, penBtn, lineBtn, ovalBtn, rectangleBtn, circleBtn;
    private JColorChooser colorChooser;

    public Whiteboard() {
        super("Whiteboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);

        JPanel toolsPanel = new JPanel();
        toolsPanel.setLayout(new FlowLayout());

        // Shape buttons
        lineBtn = new JButton("Line");
        ovalBtn = new JButton("Oval");
        rectangleBtn = new JButton("Rectangle");
        circleBtn = new JButton("Circle");
        toolsPanel.add(lineBtn);
        toolsPanel.add(ovalBtn);
        toolsPanel.add(rectangleBtn);
        toolsPanel.add(circleBtn);

        // Color picker
        JButton colorBtn = new JButton("Choose Color");
        toolsPanel.add(colorBtn);
        colorChooser = new JColorChooser();
        colorBtn.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(null, "Choose a color", Color.black);
            if (newColor != null) {
                drawArea.setCurrentColor(newColor);
            }
        });

        // Pen button for free drawing
        penBtn = new JButton("Pen");
        toolsPanel.add(penBtn);

        textBtn = new JButton("Text");
        toolsPanel.add(textBtn);

        clearBtn = new JButton("Clear");
        toolsPanel.add(clearBtn);

        getContentPane().add(toolsPanel, BorderLayout.NORTH);

        drawArea = new DrawArea();
        getContentPane().add(drawArea, BorderLayout.CENTER);

        setupActions();
    }

    private void setupActions() {
        clearBtn.addActionListener(e -> drawArea.clear());
        textBtn.addActionListener(e -> drawArea.setTextMode(true));
        penBtn.addActionListener(e -> drawArea.setFreeDrawMode(true));
        lineBtn.addActionListener(e -> drawArea.setShape("Line"));
        ovalBtn.addActionListener(e -> drawArea.setShape("Oval"));
        rectangleBtn.addActionListener(e -> drawArea.setShape("Rectangle"));
        circleBtn.addActionListener(e -> drawArea.setShape("Circle"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Whiteboard().setVisible(true));
    }
}