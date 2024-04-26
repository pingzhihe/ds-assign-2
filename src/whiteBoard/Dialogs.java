package whiteBoard;

import javax.swing.*;

public class Dialogs {
    public static String showLoginDialog() {
        while (true) {
            String userName = JOptionPane.showInputDialog(null, "Enter your name:", "Login", JOptionPane.PLAIN_MESSAGE);
            // 检查是否返回null（表示用户点击取消或关闭对话框）
            if (userName == null) {
                JOptionPane.showMessageDialog(null, "No input received, application will exit.", "Exiting", JOptionPane.WARNING_MESSAGE);
                System.exit(0);  // 用户取消输入或关闭窗口时退出程序
            } else if (!userName.trim().isEmpty()) {
                return userName.trim();  // 返回有效的非空用户名
            } else {
                // 如果用户没有输入任何内容但点击了OK，显示错误消息并重新显示输入框
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

    public static void showManagerError(){
        JOptionPane.showMessageDialog(null, "Only one manager is allowed at the session");

    }

}