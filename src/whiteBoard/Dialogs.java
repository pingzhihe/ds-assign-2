package whiteBoard;

import javax.swing.*;

public class Dialogs {
    public static String showLoginDialog() {
        while (true) {
            String userName = JOptionPane.showInputDialog(null, "Enter your name:", "Login", JOptionPane.PLAIN_MESSAGE);
            // check if null is returned (indicating user clicked cancel or closed the dialog)
            if (userName == null) {
                JOptionPane.showMessageDialog(null, "No input received, application will exit.", "Exiting", JOptionPane.WARNING_MESSAGE);
                System.exit(0);  // Close the application if user cancels login
            } else if (!userName.trim().isEmpty()) {
                return userName.trim();  // Return the trimmed username
            } else {
                // If user did not enter any content but clicked OK, show an error message and re-show the input box
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
        JOptionPane.showMessageDialog(null, "You have been kicked out!");
    }

    public static String showJoinMessage(String userName){
        Object [] options = {"approve", "reject"};
        int response = JOptionPane.showOptionDialog(null, userName + " wants to join the whiteboard.", "Join Request",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (response == JOptionPane.YES_OPTION){
            return "approve";
        }
        else {
            return "reject";
        }
    }
    public static void showWaitingMessage(){
        JOptionPane.showMessageDialog(null, "Waiting for approval from the manager.");
    }

    public static void showManagerShutDown(){
        JOptionPane.showMessageDialog(null, "Manager has shut down the session. Exiting application.");
    }


}