import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ManageDoctors extends JFrame {
    private JTextField doctorIdField, nameField, phoneField, addrField;
    private JComboBox specializationCB;
    private JTable table;
    private JScrollPane scrollPane;
    private DefaultTableModel model;
    private JPanel panel;

    public ManageDoctors() {
        setTitle("Manage Doctors");
        setSize(1200,600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.decode("#00ACB1"));
        add(panel);

        // Adding background images
        ImageIcon img = new ImageIcon("src\\mgDoc.png");
        JLabel background = new JLabel("", img, JLabel.CENTER);
        background.setBounds(300,20,1200,600);
        add(background);

        // Doctor ID
        JLabel idLabel = new JLabel("Doctor ID:");
        idLabel.setBounds(10, 10, 80, 25);
        panel.add(idLabel);

        doctorIdField = new JTextField();
        doctorIdField.setBounds(100, 10, 160, 25);
        panel.add(doctorIdField);

        // Name
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(10, 40, 80, 25);
        panel.add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(100, 40, 160, 25);
        panel.add(nameField);

        // Specialization
        JLabel specializationLabel = new JLabel("Specialization:");
        specializationLabel.setBounds(10, 70, 100, 25);
        panel.add(specializationLabel);

        specializationCB = new JComboBox();
        specializationCB.addItem("Audiologist");
        specializationCB.addItem("Cardiologist");
        specializationCB.addItem("Dentist");
        specializationCB.addItem("Dermatologist");
        specializationCB.addItem("Hepatologist");
        specializationCB.addItem("Nephrologist");
        specializationCB.addItem("Neurologist");
        specializationCB.addItem("Oncologist");
        specializationCB.addItem("Ophthalmologist");
        specializationCB.addItem("Pediatrician");

        specializationCB.setBounds(100, 70, 160, 25);
        panel.add(specializationCB);

        // Phone and Address fields
        JLabel phLabel = new JLabel("Phone:");
        phLabel.setBounds(300, 10, 80, 25);
        panel.add(phLabel);

        phoneField = new JTextField();
        phoneField.setBounds(370, 10, 160, 25);
        panel.add(phoneField);

        JLabel addLabel = new JLabel("Address:");
        addLabel.setBounds(300, 40, 80, 25);
        panel.add(addLabel);

        addrField = new JTextField();
        addrField.setBounds(370, 40, 160, 150);
        panel.add(addrField);

        // Buttons
        JButton addButton = new JButton("Add Doctor");
        addButton.setBounds(100, 110, 160, 25);
        panel.add(addButton);

        JButton viewButton = new JButton("View Doctor");
        viewButton.setBounds(100, 140, 160, 25);
        panel.add(viewButton);

        JButton resetButton = new JButton("Reset");
        resetButton.setBounds(600, 105, 80, 25);
        panel.add(resetButton);

        JButton mgDashButton = new JButton("Home");
        mgDashButton.setBounds(600, 40, 80, 25);
        panel.add(mgDashButton);

        // Add action listeners
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addDoctor();
            }
        });

        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    viewDoctor();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        mgDashButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                MainDashboard mgDash = new MainDashboard();
                mgDash.setVisible(true);
            }
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doctorIdField.setText("");
                nameField.setText("");
                phoneField.setText("");
                addrField.setText("");

            }
        });

        // Adding the panel
        add(panel);

        // Table for displaying doctor data
        model = new DefaultTableModel();
        model.addColumn("Doctor ID");
        model.addColumn("Name");
        model.addColumn("Specialization");
        model.addColumn("Phone");
        model.addColumn("Address");

        table = new JTable(model);
        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 200, 680, 200);
        panel.add(scrollPane);

    }

    // Method to add a doctor
    private void addDoctor() {
        String id = doctorIdField.getText();
        String name = nameField.getText();
        String specialization = specializationCB.getSelectedItem().toString();
        String phone = phoneField.getText();
        String address = addrField.getText();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO doctors (doctor_id, name, specialization, phone, address) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            stmt.setString(2, name);
            stmt.setString(3, specialization);
            stmt.setString(4, phone);
            stmt.setString(5, address);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Doctor added successfully!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Method to view doctor details
    private void viewDoctor() throws SQLException {
        String doctorID = doctorIdField.getText();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = doctorID.isEmpty() ? "SELECT * FROM doctors" : "SELECT * FROM doctors WHERE doctor_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            if (!doctorID.isEmpty()) {
                stmt.setString(1, doctorID);
            }

            ResultSet rs = stmt.executeQuery();
            model.setRowCount(0); // Clear the table before displaying new data

            while (rs.next()) {
                Object[] row = {
                        rs.getString("doctor_id"),
                        rs.getString("name"),
                        rs.getString("specialization"),
                        rs.getString("phone"),
                        rs.getString("address")
                };
                model.addRow(row);
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No doctor found!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ManageDoctors().setVisible(true);
            }
        });
    }
}
