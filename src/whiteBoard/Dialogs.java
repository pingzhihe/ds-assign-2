package whiteBoard;

import javax.swing.*;

public class Dialogs {
    public static String showLoginDialog() {
        while (true) {
            String userName = JOptionPane.showInputDialog(null, "Enter your name:", "Login", JOptionPane.PLAIN_MESSAGE);
            if (userName != null && !userName.trim().isEmpty()) {
                return userName.trim(); //
            } else {
                JOptionPane.showMessageDialog(null, "You must enter a name to continue.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void showAdminWelcomeMessage() {
        JOptionPane.showMessageDialog(null, "Congratulations, you are an administrator!", "Admin Access Granted", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Connection Error", JOptionPane.ERROR_MESSAGE);
    }
    public static void showSererErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Server refused the connect", JOptionPane.ERROR_MESSAGE);
    }
    public static void showKickOutMessage() {
        JOptionPane.showMessageDialog(null, "User Kicked Out");
    }

}