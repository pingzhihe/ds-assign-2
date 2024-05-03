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
        // Set up the table model, including username and checkbox
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Select");
        tableModel.addColumn("Username");
        userTable = new JTable(tableModel) {
            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Boolean.class; // The first column is a checkbox
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
        kickOutButton.setVisible(false);
    }


    String kickOutSelectedUsers() {
        Vector<Vector> dataVector = tableModel.getDataVector();
        if (dataVector.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No users to kick out.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return " ";
        }

        boolean atLeastOneSelected = false;
        for (int i = dataVector.size() - 1; i >= 0; i--) {
            if ((Boolean) dataVector.get(i).get(0)) { // Check if the checkbox is selected
                atLeastOneSelected = true;
                String username = (String) dataVector.get(i).get(1);
                System.out.println("User kicked out: " + username);  // Print the information before removing
                tableModel.removeRow(i); // Remove the selected row
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

    public void removeUser(String username) {
        Vector<Vector> dataVector = tableModel.getDataVector();
        for (int i = dataVector.size() - 1; i >= 0; i--) {
            if (dataVector.get(i).get(1).equals(username)) {
                tableModel.removeRow(i);
            }
        }
    }

    public String getUserList(){
        Vector<Vector> dataVector = tableModel.getDataVector();
        StringBuilder userList = new StringBuilder();
        for (int i = 0; i < dataVector.size(); i++) {
            userList.append(dataVector.get(i).get(1)).append(",");
    }
        System.out.println(userList);
        return userList.toString();
    }

}
