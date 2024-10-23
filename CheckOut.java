import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import java.awt.*;
import com.toedter.calendar.JDateChooser; // For date selection
import javax.swing.table.DefaultTableModel;

public class CheckOut extends JFrame {
    private JDateChooser checkOutField; // Changed to JDateChooser for date selection
    private JComboBox<String> ptIdCB; // JComboBox to hold patient IDs
    private JTable table;
    private JScrollPane scrollPane;
    private DefaultTableModel model;

    public CheckOut() {
        setTitle("Manage Check Out Patients");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.decode("#00ACB1"));
        add(panel);

        JButton dashButton = new JButton("Home");
        dashButton.setBounds(10, 10, 80, 25);
        panel.add(dashButton);

        JLabel idLabel = new JLabel("Patient ID:");
        idLabel.setBounds(200, 40, 80, 25);
        panel.add(idLabel);

        ptIdCB = new JComboBox<>();
        ptIdCB.setBounds(300, 40, 160, 25);
        panel.add(ptIdCB);

        JButton fetchButton = new JButton("Fetch");
        fetchButton.setBounds(300, 105, 160, 25);
        panel.add(fetchButton);

        JButton resetButton = new JButton("Reset");
        resetButton.setBounds(200, 105, 80, 25);
        panel.add(resetButton);

        JLabel checkOutLabel = new JLabel("CheckOut Date:");
        checkOutLabel.setBounds(490, 40, 150, 25);
        panel.add(checkOutLabel);

        checkOutField = new JDateChooser(); // Changed to JDateChooser
        checkOutField.setBounds(620, 40, 160, 25);
        panel.add(checkOutField);

        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(620, 80, 80, 25);
        panel.add(submitButton);

        JButton repButton = new JButton("Report");
        repButton.setBounds(620, 145, 80, 25);
        panel.add(repButton);

        // Table setup
        String[] columnNames = {"PatientID", "NAME", "DOB", "PHONE", "ADDRESS", "CHECKIN DATE", "CHECKOUT DATE", "PER-DAY CHARGES"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0); // Empty table model
        table = new JTable(model);
        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 200, 1100, 300);
        scrollPane.setVisible(false); // Initially hidden
        panel.add(scrollPane);

        fetchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fetchPatientDetails();
            }
        });

        dashButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                MainDashboard mdash = new MainDashboard();
                mdash.setVisible(true);
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scrollPane.setVisible(false);
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCheckOutDate();
            }
        });

        repButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reportUpdate reportUpdate = new reportUpdate();
                reportUpdate.setVisible(true);

            }
        });

        add(panel);

        // Populate patient IDs in the JComboBox at startup
        fetchPatientIDs();
    }

    // Method to fetch Patient IDs from the database and add them to the JComboBox
    private void fetchPatientIDs() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT patient_id FROM patients";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            ptIdCB.removeAllItems(); // Clear existing items
            ptIdCB.addItem(""); // Add empty option

            while (rs.next()) {
                String patientID = rs.getString("patient_id");
                ptIdCB.addItem(patientID); // Add each patient ID to the JComboBox
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void fetchPatientDetails() {
        String patientID = (String) ptIdCB.getSelectedItem();

        if (patientID == null || patientID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a Patient ID");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM patients WHERE patient_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, patientID);

            ResultSet rs = stmt.executeQuery();

            // Clear previous table data
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            // Fetch and add data to the table
            if (rs.next()) {
                Object[] row = {
                        rs.getString("patient_id"),
                        rs.getString("name"),
                        rs.getString("dob"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("EnDate"),
                        rs.getString("ExDate"),
                        rs.getString("charges")
                };
                model.addRow(row);
                scrollPane.setVisible(true); // Show the table after data is fetched
            } else {
                JOptionPane.showMessageDialog(this, "Patient not found!");
                scrollPane.setVisible(false); // Hide the table if no data is found
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateCheckOutDate() {
        String patientID = (String) ptIdCB.getSelectedItem();
        java.util.Date checkOutDate = checkOutField.getDate(); // Get date from JDateChooser

        if (patientID == null || patientID.isEmpty() || checkOutDate == null) {
            JOptionPane.showMessageDialog(this, "Please select a Patient ID and CheckOut Date");
            return;
        }

        // Convert java.util.Date to java.sql.Date for the SQL query
        java.sql.Date sqlCheckOutDate = new java.sql.Date(checkOutDate.getTime());

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE patients SET ExDate = ? WHERE patient_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDate(1, sqlCheckOutDate);
            stmt.setString(2, patientID);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "CheckOut Date updated successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Error: Patient not found or update failed.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CheckOut().setVisible(true);
            }
        });
    }
}
