package whiteBoard;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ToolPanel extends JPanel {
    private JButton clearBtn, textBtn, penBtn, eraserBtn, saveAsBtn, loadBtn,saveBtn;
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
        saveAsBtn = new JButton("Save_As");
        loadBtn = new JButton("Load");
        saveBtn = new JButton("Save");
        String[] shapes = {"Line", "Oval", "Rectangle", "Circle"};
        shapeSelector = new JComboBox<>(shapes);
        saveAsBtn.setVisible(false);
        loadBtn.setVisible(false);
        saveBtn.setVisible(false);
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
        add(saveAsBtn);
        add(loadBtn);
        add(saveBtn);

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

        thicknessSlider.addChangeListener(e -> {
            int thickness = thicknessSlider.getValue();
            drawArea.setThickness(thickness);
        });
    }
    public JButton getSaveAsBtn(){
        return saveAsBtn;
    }

    public JButton getLoadBtn(){
        return loadBtn;
    }

    public JButton getSaveBtn(){
        return saveBtn;
    }

    public String saveFile() {
        try{
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Specify a file to save");
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                ImageIO.write(drawArea.getImage(), "PNG", new File(fileToSave.getAbsolutePath() + ".png"));
                return fileToSave.getAbsolutePath() + ".png";
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public void saveAs(String filePath) {
        try {
            ImageIO.write(drawArea.getImage(), "PNG", new File(filePath));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public BufferedImage loadFile() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image files", "jpg", "png"));
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            BufferedImage loadedImage = ImageIO.read(file);
            drawArea.loadImage(file);
            return loadedImage;
        }
        return null;
    }

    public void setManager(){
        saveAsBtn.setVisible(true);
        loadBtn.setVisible(true);
        saveBtn.setVisible(true);
    }

}
