import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ManagePatients extends JFrame {
    private JTextField patientIdField, nameField, dobField, patientPhField, EnDField, ExDField;
    private JTextField addField;  // Declare at class level
    private JRadioButton maleBT, femaleBT;
    private ButtonGroup group;
    private JTable table;
    private JScrollPane scrollPane;
    private DefaultTableModel model;
    private JPanel panel;

    public ManagePatients() {
        setTitle("Manage Patients");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.decode("#00ACB1"));
        add(panel);

        ImageIcon img = new ImageIcon("src\\mgPT.png");
        JLabel background = new JLabel("", img, JLabel.CENTER);
        background.setBounds(60, 30, 1200, 600);
        add(background);

        JLabel idLabel = new JLabel("Patient ID:");
        idLabel.setBounds(10, 10, 80, 25);
        panel.add(idLabel);

        patientIdField = new JTextField();
        patientIdField.setBounds(100, 10, 160, 25);
        panel.add(patientIdField);

        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setBounds(10, 170, 80, 25);
        panel.add(genderLabel);

        maleBT = new JRadioButton("Male");
        femaleBT = new JRadioButton("Female");
        maleBT.setBounds(100, 170, 150, 25);
        femaleBT.setBounds(260, 170, 150, 25);

        // Initialize ButtonGroup
        group = new ButtonGroup();
        group.add(maleBT);
        group.add(femaleBT);
        panel.add(maleBT);
        panel.add(femaleBT);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(10, 50, 80, 25);
        panel.add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(100, 50, 160, 25);
        panel.add(nameField);

        JLabel dobLabel = new JLabel("DoB:");
        dobLabel.setBounds(10, 90, 80, 25); //10,70,80,25
        panel.add(dobLabel);

        dobField = new JTextField();
        dobField.setBounds(100, 90, 160, 25); //100,70,160,25 (450, 70, 160, 25)
        panel.add(dobField);

        JLabel phLabel = new JLabel("Phone:");
        phLabel.setBounds(10, 130, 80, 25); //10,10,80,25
        panel.add(phLabel);

        patientPhField = new JTextField();
        patientPhField.setBounds(100, 130, 160, 25); //100,10,160,25
        panel.add(patientPhField);
        //second portion

        JLabel addLabel = new JLabel("Address:");
        addLabel.setBounds(300, 130, 160, 25); //10.10.40,80,25
        panel.add(addLabel);

        addField = new JTextField();
        addField.setBounds(390, 10, 160, 145); //100,40,160,25
        panel.add(addField);


        JButton addButton = new JButton("Add Patient");
        addButton.setBounds(10, 210, 160, 25);
        panel.add(addButton);

        JButton resButton = new JButton("Reset");
        resButton.setBounds(200, 210, 160, 25);
        panel.add(resButton);

        JButton viewButton = new JButton("View Patient");
        viewButton.setBounds(390, 210, 160, 25);
        panel.add(viewButton);

        JButton mgDashButton = new JButton("Home");
        mgDashButton.setBounds(800, 40, 80, 25);
        panel.add(mgDashButton);

        String[] columnNames = {"PatientID", "NAME", "DOB", "PHONE", "ADDRESS", "GENDER"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0); // Empty table model
        table = new JTable(model);
        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 250, 1100, 300);
       // table.setBounds(20, 260, 1100, 295);
        scrollPane.setVisible(false); // Initially hidden
        panel.add(scrollPane);

        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    fetchPatientDetails();
                }catch(Exception ex)
                {}
            }
        });

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addPatient();
            }
        });

        mgDashButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                MainDashboard mgDash = new MainDashboard();
                mgDash.setVisible(true);
            }
        });
        resButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetFields();

            }
        });

        add(panel);
    }
    
    private void addPatient() {
        String id = patientIdField.getText();
        String name = nameField.getText();
        String dob = dobField.getText();
        String address = addField.getText();
        String phone = patientPhField.getText();
        String gender = maleBT.isSelected() ? "Male" : femaleBT.isSelected() ? "Female" : null;

        if (gender == null) {
            JOptionPane.showMessageDialog(this, "Please select gender.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO patients (patient_id, name, dob, address, phone, gender) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            stmt.setString(2, name);
            stmt.setString(3, dob);
            stmt.setString(4, address);
            stmt.setString(5, phone);
            stmt.setString(6, gender);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Patient added successfully!");
                resetFields();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void resetFields() {
        patientIdField.setText("");
        nameField.setText("");
        dobField.setText("");
        patientPhField.setText("");
        addField.setText("");
        group.clearSelection(); // Reset the gender selection
    }


    private void fetchPatientDetails() throws SQLException {

        String patientID = patientIdField.getText();

        // If patientID is null or empty, fetch all patients
        if (patientID == null || patientID.isEmpty()) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "SELECT * FROM patients"; // No WHERE clause, fetches all patients
                PreparedStatement stmt = conn.prepareStatement(sql);

                ResultSet rs = stmt.executeQuery();

                // Clear previous table data
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0);

                // Fetch and add data to the table
                while (rs.next()) {
                    Object[] row = {
                            rs.getString("patient_id"),
                            rs.getString("name"),
                            rs.getString("dob"),
                            rs.getString("phone"),
                            rs.getString("address"),
                            rs.getString("gender"),
                    };
                    model.addRow(row);
                }

                // Display the table if data is found, otherwise hide it
                if (model.getRowCount() > 0) {
                    scrollPane.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "No patients found!");
                    scrollPane.setVisible(false);
                }
            }
        } else {
            // Fetch specific patient details if patientID is provided
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
                            rs.getString("gender"),
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
    }
    public static void main(String[] args) {
        new ManagePatients().setVisible(true);
    }
}
