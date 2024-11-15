package whiteBoard;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Whiteboard extends JFrame {
    private DrawArea drawArea;
    private ToolPanel toolsPanel;

    private ManagerPanel managerPanel;
    private ChatArea chatArea;
    private boolean isManager = false;

    private String filePath;


    private WhiteBoardEventListener listener;

    public Whiteboard(WhiteBoardEventListener listener) {

        super("Whiteboard");
        this.listener = listener;
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                listener.onWhiteBoardClose();
                System.exit(0);
            }
        });
        initializeComponents();
        setupLayout();
        setupActions();
        filePath = "";
    }

    private void initializeComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        chatArea = new ChatArea();
        drawArea = new DrawArea();
        toolsPanel = new ToolPanel(drawArea, isManager);
        managerPanel = new ManagerPanel();
        chatArea.setPreferredSize(new Dimension(200,getHeight()));
        managerPanel.setPreferredSize(new Dimension(200, getHeight()));
        drawArea.setPreferredSize(new Dimension(800, 600));
        getContentPane().add(drawArea, BorderLayout.CENTER);
    }

    private void setupLayout() {
        getContentPane().add(chatArea, BorderLayout.EAST);
        getContentPane().add(toolsPanel, BorderLayout.NORTH);
        getContentPane().add(managerPanel, BorderLayout.WEST);
    }

    private void setupActions() {
        toolsPanel.setupActions();
        drawArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                String text = drawArea.handleMousePressed(e.getX(), e.getY());
                if (drawArea.getState().equals("text")&& text != ""){
                    listener.onWhiteBoardMsg("wb " + drawArea.getState()+" " + e.getX() + " " + e.getY() +" " + text + "\n");
                }
            }
            public void mouseReleased(MouseEvent e) {
                drawArea.handleMouseReleased(e.getX(), e.getY());
                if (!drawArea.getState().equals("free_draw") && !drawArea.getState().equals("text") &&
                        !drawArea.getState().equals("eraser")) {
                    listener.onWhiteBoardMsg(generateMessage(drawArea) + " " + e.getX() + " " + e.getY() + "\n");
                    drawArea.handleMouseReleased(e.getX(), e.getY());
                }
            }
        });

        drawArea.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (drawArea.getState().equals("free_draw") || drawArea.getState().equals("eraser")) {
                    listener.onWhiteBoardMsg(generateMessage(drawArea) + " " + e.getX() + " " + e.getY()+ "\n");
                    drawArea.handleMouseDragged(e.getX(), e.getY());
                }
            }
        });

        chatArea.getSendButton().addActionListener(e -> {
            String message = chatArea.getChatInput().getText();
            chatArea.getChatInput().setText("");
            listener.onWhiteBoardMsg("chat: " + message + "\n");
        });

        managerPanel.getKickOutButton().addActionListener(e -> {
            String userName = managerPanel.kickOutSelectedUsers();
            if (!userName.equals(" ")) {
                listener.onWhiteBoardMsg("Delete: " + userName + "\n");
            }
        });

        toolsPanel.getSaveAsBtn().addActionListener(e -> {
            filePath = toolsPanel.saveFile();
            System.out.println(filePath);
        });

        toolsPanel.getLoadBtn().addActionListener(e -> {
            try {
                BufferedImage img = toolsPanel.loadFile();
                byte[] imgByte = bufferedImageToByteArray(img, "PNG");
                listener.onImg(imgByte);

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        toolsPanel.getSaveBtn().addActionListener(e -> {
            if (filePath.equals("")) {
                filePath = toolsPanel.saveFile();
            }
            else{
                toolsPanel.quickSave(filePath);
            }
        });

        toolsPanel.getNewBtn().addActionListener(e -> {
            if (filePath.equals("")) {
                toolsPanel.saveFile();
            }
            else{
                toolsPanel.quickSave(filePath);
            }
            drawArea.clear();
            listener.onWhiteBoardMsg("clear\n");
            filePath = "";
        });
    }


    /**
     * Converts a BufferedImage to a byte array.
     * @param image The BufferedImage to convert.
     * @param formatName The format name (e.g., "PNG", "JPEG").
     * @return A byte array representing the image.
     * @throws IOException If an error occurs during writing.
     */
    public byte[] bufferedImageToByteArray(BufferedImage image, String formatName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, formatName, baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();
        return imageInByte;
    }

    //**
    // * Converts a byte array to a BufferedImage.
    // * @param imageInByte The byte array to convert.
    // * @return A BufferedImage representing the image.
    // * @throws IOException If an error occurs during reading.
    // */

    public void receiveImg(byte[] img){
        try {
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(img));
            drawArea.loadBufferImage(bufferedImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    public String generateMessage(DrawArea d1) {
        return "wb " + d1.getState() + " " + d1.getColor()+ " " + d1.getThickness() + " " + d1.getOldX() + " " + d1.getOldY();
    }

    public void parseMessage(String message){
        String[] messages = message.split("\n");
        for (String msg : messages) {
            String trimmedMsg= msg.substring(3);
            drawArea.parseMessage(trimmedMsg);
        }
    }

    public void managerMode(){
        setSize(1200, 600);
        toolsPanel.setManager();
        managerPanel.getKickOutButton().setVisible(true);

    }

    public void addUser(String newUser){
        managerPanel.addUser(newUser);
    }

    public String getUserList(){
        return managerPanel.getUserList();
    }

    public void shunDown(){
        System.exit(0);
    }
    public void receiveChat(String message){
        chatArea.getChatArea().append(message + "\n");
    }

    public void clear() {
        drawArea.clear();
    }

    public byte[] getImg(){
        try {
            return bufferedImageToByteArray(drawArea.getImageData(), "PNG");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void shutDown(){
        System.exit(0);
    }
    public void removeUser(String removedUser){
        managerPanel.removeUser(removedUser);
    }

}

