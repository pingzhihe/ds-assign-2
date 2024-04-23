package whiteBoard;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;

public class ManagerPanel extends JPanel {
    private JTable userTable;
    private JButton kickOutButton;
    private DefaultTableModel tableModel;

    public ManagerPanel() {
        setLayout(new BorderLayout());
        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        // 设置表格模型，包括用户名和复选框
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Select");
        tableModel.addColumn("Username");
        userTable = new JTable(tableModel) {
            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Boolean.class; // 第一列为复选框
                    case 1:
                        return String.class; // Second row is username
                    default:
                        return Object.class;
                }
            }
        };

        kickOutButton = new JButton("Kick Out");
    }

    private void setupLayout() {
        add(new JScrollPane(userTable), BorderLayout.CENTER);
        add(kickOutButton, BorderLayout.SOUTH);
    }


    String kickOutSelectedUsers() {
        Vector<Vector> dataVector = tableModel.getDataVector();
        if (dataVector.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No users to kick out.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return " ";
        }

        boolean atLeastOneSelected = false;
        for (int i = dataVector.size() - 1; i >= 0; i--) {
            if ((Boolean) dataVector.get(i).get(0)) { // 检查复选框是否被选中
                atLeastOneSelected = true;
                String username = (String) dataVector.get(i).get(1);
                System.out.println("User kicked out: " + username); // 打印信息要在移除前进行
                tableModel.removeRow(i); // 移除选中的行
                // 这里可以添加更多逻辑，比如通知服务器踢出用户
                return username;

            }
        }

        if (!atLeastOneSelected) {
            JOptionPane.showMessageDialog(this, "No user selected to kick out.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
        return " ";
    }
    public void addUser(String username) {
        tableModel.addRow(new Object[]{false, username});
    }

    public JButton getKickOutButton() {
        return kickOutButton;
    }



}
