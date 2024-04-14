package WhiteBoard;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;


public class Whiteboard extends JFrame {
    private DrawArea drawArea;
    private JButton clearBtn, textBtn, penBtn, lineBtn, ovalBtn, rectangleBtn, circleBtn;
    private JButton eraserBtn, saveBtn;
    private JColorChooser colorChooser;

    public Whiteboard() {
        super("Whiteboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);

        JPanel toolsPanel = new JPanel();
        toolsPanel.setLayout(new FlowLayout());
        JSlider thicknessSlider = new JSlider(JSlider.HORIZONTAL, 1, 50, 3);
        thicknessSlider.setMajorTickSpacing(10);
        thicknessSlider.setMinorTickSpacing(1);
        thicknessSlider.setPaintTicks(true);
        thicknessSlider.setPaintLabels(true);

        toolsPanel.add(new JLabel("Thickness:"));
        toolsPanel.add(thicknessSlider);

        // Shape buttons
        lineBtn = new JButton("Line");
        ovalBtn = new JButton("Oval");
        rectangleBtn = new JButton("Rectangle");
        circleBtn = new JButton("Circle");
        eraserBtn = new JButton("Eraser");
        saveBtn = new JButton("Save");
        toolsPanel.add(saveBtn);
        toolsPanel.add(lineBtn);
        toolsPanel.add(ovalBtn);
        toolsPanel.add(rectangleBtn);
        toolsPanel.add(circleBtn);
        toolsPanel.add(eraserBtn);


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

        setupActions(thicknessSlider);
    }

    private void setupActions(JSlider thicknessSlider) {
        clearBtn.addActionListener(e -> drawArea.clear());
        textBtn.addActionListener(e -> drawArea.setTextMode(true));
        penBtn.addActionListener(e -> drawArea.setPenMode(true));
        lineBtn.addActionListener(e -> drawArea.setShape("Line"));
        ovalBtn.addActionListener(e -> drawArea.setShape("Oval"));
        rectangleBtn.addActionListener(e -> drawArea.setShape("Rectangle"));
        circleBtn.addActionListener(e -> drawArea.setShape("Circle"));
        eraserBtn.addActionListener(e -> drawArea.setEraserMode(true));
        saveBtn.addActionListener(e -> {
            try {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Specify a file to save");
                int userSelection = fileChooser.showSaveDialog(this);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    ImageIO.write(drawArea.getImage(), "PNG", new File(fileToSave.getAbsolutePath() + ".png"));
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        thicknessSlider.addChangeListener(e -> {
            int thickness = ((JSlider) e.getSource()).getValue();
            drawArea.setThickness(thickness);
            if (drawArea.isEraserMode()) {  // 检查是否激活了橡皮擦模式
                drawArea.setEraserCursor(thickness);
            }
        });


    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Whiteboard().setVisible(true));
    }
}