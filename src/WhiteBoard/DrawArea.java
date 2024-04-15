package WhiteBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

public class DrawArea extends JComponent {
    private Image image;
    private Graphics2D g2;
    private int currentX, currentY, oldX, oldY;
    private Color currentColor = Color.BLACK;  // Default color
    private boolean textMode = false, freeDrawMode = false, eraseMode = false;
    private String currentShape = "Line"; //  Default shape

    public DrawArea() {
        setDoubleBuffered(false);
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                oldX = e.getX();
                oldY = e.getY();
                if (textMode) {
                    String text = JOptionPane.showInputDialog("Input Text:");
                    if (text != null) {
                        g2.drawString(text, oldX, oldY);
                        repaint();
                    }
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (!textMode) {
                    drawShape(oldX, oldY, e.getX(), e.getY());
                }
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (freeDrawMode && !textMode) {
                    currentX = e.getX();
                    currentY = e.getY();
                    if (g2 != null) {
                        g2.drawLine(oldX, oldY, currentX, currentY);
                        repaint();
                        oldX = currentX;
                        oldY = currentY;
                    }
                }
            }
        });
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

    public void setShape(String shape) {
        // Set the shape to draw and ensure freeDrawMode and textMode are disabled when a shape is selected
        freeDrawMode = false;
        textMode = false;
        currentShape = shape;
    }

    private void drawShape(int x1, int y1, int x2, int y2) {
        if (g2 == null) return;

        int x = Math.min(x1, x2);
        int y = Math.min(y1, y2);
        int width = Math.abs(x1 - x2);
        int height = Math.abs(y1 - y2);

        switch (currentShape) {
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

    public void setPenMode(boolean mode) {
        freeDrawMode = mode;
        eraseMode = false;
        textMode = false;
        if (mode) {
            g2.setPaint(currentColor);
        }
    }

    public void setEraserMode(boolean mode) {
        freeDrawMode =mode;
        eraseMode = mode;
        textMode = false;
        if (mode) {
            g2.setPaint(Color.WHITE);
        }
    }

    public void setTextMode(boolean mode) {
        textMode = mode;
        // Disable free drawing and shape drawing when text mode is enabled
        if (mode) {
            freeDrawMode = false;
        }
    }
    public void setThickness(int thickness) {
        if (g2 != null) {
            g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        }
    }
    public void setEraserCursor(int size) {
        // 创建光标图像，边框固定为1像素
        BufferedImage cursorImg = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = cursorImg.createGraphics();

        // 黑色边框
        graphics.setColor(Color.BLACK);
        graphics.drawRect(0, 0, size - 1, size - 1); // 绘制1像素的黑色边框

            // 白色填充中间区域
        graphics.setColor(Color.WHITE);
        graphics.fillRect(1, 1, size - 2, size - 2);

        graphics.dispose();

        // 设置自定义光标，光标的热点位于中心
        Cursor customCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(size / 2, size / 2), "eraser cursor");
        setCursor(customCursor);
    }



    public RenderedImage getImage() {
        return (RenderedImage) image;
    }

    public boolean isEraserMode() {
        return eraseMode;
    }

}