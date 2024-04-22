package whiteBoard;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ToolPanel extends JPanel {
    private JButton clearBtn, textBtn, penBtn, eraserBtn, saveBtn, loadBtn;
    private JSlider thicknessSlider;
    private JComboBox<String> shapeSelector;
    private JColorChooser colorChooser;
    private DrawArea drawArea;

    public ToolPanel(DrawArea drawArea, boolean isManager) {
        this.drawArea = drawArea;
        setLayout(new FlowLayout());
        initializeButtons(isManager);
        System.out.println(isManager);
        setupColorChooser();
        addComponents();
    }

        private void initializeButtons(boolean isManager) {
        clearBtn = new JButton("Clear");
        textBtn = new JButton("Text");
        penBtn = new JButton("Pen");
        eraserBtn = new JButton("Eraser");
        saveBtn = new JButton("Save");
        loadBtn = new JButton("Load");
        String[] shapes = {"Line", "Oval", "Rectangle", "Circle"};
        shapeSelector = new JComboBox<>(shapes);
        saveBtn.setVisible(false);
        loadBtn.setVisible(false);
    }

    private void setupColorChooser() {
        JButton colorBtn = new JButton("Color");
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
        add(eraserBtn);
        add(shapeSelector);
        add(saveBtn);
        add(loadBtn);

        setupActions();
    }

    public void setupActions() {
        clearBtn.addActionListener(e -> drawArea.clear());
        textBtn.addActionListener(e -> drawArea.setState("text"));
        penBtn.addActionListener(e -> drawArea.setState("free_draw"));
        eraserBtn.addActionListener(e -> drawArea.setState("eraser"));
        shapeSelector.addActionListener(e -> {
            String selectedShape = (String) shapeSelector.getSelectedItem();
            drawArea.setState(selectedShape);
        });
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

        loadBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image files", "jpg", "png"));
            int option = fileChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                drawArea.loadImage(file);
            }
        });

        thicknessSlider.addChangeListener(e -> {
            int thickness = thicknessSlider.getValue();
            drawArea.setThickness(thickness);
        });
    }

    public void setManager(){
        saveBtn.setVisible(true);
        loadBtn.setVisible(true);
    }

}
