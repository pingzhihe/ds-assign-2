package whiteBoard;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class DrawArea extends JComponent {
    private Image image;
    private Graphics2D g2;
    private int oldX, oldY;
    private Color currentColor = Color.BLACK;  // Default color
    private int thickness = 3;  // Default thickness

    private String state = "free_draw";  // Default state

    public DrawArea() {
        setDoubleBuffered(false);
    }

    public String handleMousePressed(int x, int y){
        oldX = x;
        oldY = y;
        if (state.equals("text")) {
            String text = JOptionPane.showInputDialog("Input Text:");
            if (text != null) {
                g2.drawString(text, oldX, oldY);
                repaint();
                return text;
            }
        }
        return " ";
    }

    public void handleMouseReleased(int x, int y){
        g2.setPaint(currentColor);
        g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        if (!state.equals("free_draw") && !state.equals("text")) {
            drawShape(oldX, oldY, x, y, state);
        }
        repaint();
    }

    public void handleMouseDragged(int x, int y){
        if (state.equals("free_draw")) {
            g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setPaint(currentColor);
            g2.drawLine(oldX, oldY, x, y);
            oldX = x;
            oldY = y;
            repaint();
        }

        else if (state.equals("eraser")) {
            g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setPaint(Color.white);
            g2.drawLine(oldX, oldY, x, y);
            oldX = x;
            oldY = y;
            repaint();
        }
    }

    protected void paintComponent(Graphics g) {
        if (image == null) {
            image = createImage(getSize().width, getSize().height);
            g2 = (Graphics2D) image.getGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            clear();
        }
        g.drawImage(image, 0, 0, null);
    }

    public void clear() {
        g2.setPaint(Color.white);
        g2.fillRect(0, 0, getSize().width, getSize().height);
        g2.setPaint(currentColor);
        repaint();
    }

    public void setCurrentColor(Color color) {
        currentColor = color;
        g2.setPaint(currentColor);
    }


    public void drawShape(int x1, int y1, int x2, int y2, String shape) {
        int x = Math.min(x1, x2);
        int y = Math.min(y1, y2);
        int width = Math.abs(x1 - x2);
        int height = Math.abs(y1 - y2);

        switch (shape) {
            case "Line":
                g2.drawLine(x1, y1, x2, y2);
                break;
            case "Oval":
                g2.drawOval(x, y, width, height);
                break;
            case "Rectangle":
                g2.drawRect(x, y, width, height);
                break;
            case "Circle":
                int diameter = Math.max(width, height);
                g2.drawOval(x, y, diameter, diameter);
                break;
            default:
                break;
        }
    }

    public void setState(String state){
        this.state = state;
    }

    public void setThickness(int thickness) {
        if (g2 != null) {
            this.thickness = thickness;
            g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        }
    }
    public RenderedImage getImage()  {
        return (RenderedImage) image;
    }

    public String getState(){
        return this.state;
    }
    public int getOldX(){
        return this.oldX;
    }
    public int getOldY(){
        return this.oldY;
    }
    public int getColor(){
        return this.currentColor.getRGB();
    }
    public int getThickness(){
        return this.thickness;
    }

    public void drawWithMsg(String state, int rgb, int thickness, int x1, int y1, int x2, int y2){
        g2.setPaint(new Color(rgb));
        g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        if (state.equals("free_draw")) {
            g2.drawLine(x1, y1, x2, y2);
        } else if (state.equals("eraser")) {
            g2.setPaint(Color.white);
            g2.drawLine(x1, y1, x2, y2);
        }
        else {
            drawShape(x1, y1, x2, y2, state);
        }
        repaint();
    }
    public void textWithMsg(String text, int x, int y){
        g2.drawString(text, x, y);
        repaint();
    }

    public void parseMessage(String message){
        String[] msg = message.split(" ");
        String state = msg[0];
        if (state.equals("text")){
            int x = Integer.parseInt(msg[1]);
            int y = Integer.parseInt(msg[2]);
            if (msg.length == 3){
                textWithMsg("", x, y);
                return;
            }
            String[] subMsg = Arrays.copyOfRange(msg, 3, msg.length);
            String text = String.join(" ", subMsg);
            textWithMsg(text, x, y);
            return;
        }
        int rgb = Integer.parseInt(msg[1]);
        int thickness = Integer.parseInt(msg[2]);
        int x1 = Integer.parseInt(msg[3]);
        int y1 = Integer.parseInt(msg[4]);
        int x2 = Integer.parseInt(msg[5]);
        int y2 = Integer.parseInt(msg[6]);
        drawWithMsg(state, rgb, thickness, x1, y1, x2, y2);
    }

    public void loadImage(File file) {
        try {
            BufferedImage loadedImage = ImageIO.read(file); // Read the image
            if (loadedImage != null) {
                image = loadedImage;
                g2 = (Graphics2D) image.getGraphics(); // Update the Graphics2D object
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(currentColor);
                g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Adjust the size of the component according to the size of the image
                setPreferredSize(new Dimension(image.getWidth(this), image.getHeight(this)));
                revalidate(); // Notify the layout system that the size of the component may change
                repaint();
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Image could not be read", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void loadBufferImage(BufferedImage img){
        image = img;
        g2 = (Graphics2D) image.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(currentColor);
        g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        setPreferredSize(new Dimension(image.getWidth(this), image.getHeight(this)));
        repaint();
    }

    public BufferedImage getImageData(){
        return (BufferedImage) image;
    }
}