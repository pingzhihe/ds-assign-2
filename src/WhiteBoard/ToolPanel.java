package WhiteBoard;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ToolPanel extends JPanel {
    private JButton clearBtn, textBtn, penBtn, lineBtn, ovalBtn, rectangleBtn, circleBtn, eraserBtn, saveBtn;
    private JSlider thicknessSlider;
    private JColorChooser colorChooser;
    private DrawArea drawArea;

    public ToolPanel(DrawArea drawArea) {
        this.drawArea = drawArea;
        setLayout(new FlowLayout());
        initializeButtons();
        setupColorChooser();
        addComponents();
    }

        private void initializeButtons() {
        clearBtn = new JButton("Clear");
        textBtn = new JButton("Text");
        penBtn = new JButton("Pen");
        lineBtn = new JButton("Line");
        ovalBtn = new JButton("Oval");
        rectangleBtn = new JButton("Rectangle");
        circleBtn = new JButton("Circle");
        eraserBtn = new JButton("Eraser");
        saveBtn = new JButton("Save");
    }

    private void setupColorChooser() {
        JButton colorBtn = new JButton("Choose Color");
        colorChooser = new JColorChooser();
        colorBtn.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(null, "Choose a color", Color.black);
            if (newColor != null) {
                drawArea.setCurrentColor(newColor);
            }
        });
        add(colorBtn);
    }

    private void addComponents() {
        add(new JLabel("Thickness:"));
        thicknessSlider = new JSlider(JSlider.HORIZONTAL, 1, 50, 3);
        thicknessSlider.setMajorTickSpacing(10);
        thicknessSlider.setMinorTickSpacing(1);
        thicknessSlider.setPaintTicks(true);
        thicknessSlider.setPaintLabels(true);
        add(thicknessSlider);

        add(clearBtn);
        add(textBtn);
        add(penBtn);
        add(lineBtn);
        add(ovalBtn);
        add(rectangleBtn);
        add(circleBtn);
        add(eraserBtn);
        add(saveBtn);
        setupActions();
    }

    public void setupActions() {
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
            int thickness = thicknessSlider.getValue();
            drawArea.setThickness(thickness);
        });
    }
}
