import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class reportUpdate extends JFrame {
    static JComboBox<String> ptIdCB; // JComboBox to hold patient IDs
    private JTable table;
    private JScrollPane scrollPane;
    private DefaultTableModel model;
    static JComboBox<String> sortCB;


    public reportUpdate() {
        setTitle("Reports of Patients");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.decode("#00ACB1"));
        add(panel);

        JButton allPButton = new JButton("All Patients");
        allPButton.setBounds(480, 40, 160, 25);
        panel.add(allPButton);

        JLabel idLabel = new JLabel("Patient ID:");
        idLabel.setBounds(300, 10, 160, 25);
        panel.add(idLabel);

        ptIdCB = new JComboBox<>();
        ptIdCB.setBounds(300, 40, 160, 25);
        panel.add(ptIdCB);

        JButton onePButton = new JButton("Fetch");
        onePButton.setBounds(180, 40, 100, 25);
        panel.add(onePButton);

        JButton paidPButton = new JButton("Checked Out Patients");
        paidPButton.setBounds(130, 80, 150, 25);
        panel.add(paidPButton);

        JButton currPButton = new JButton("Current Patients");
        currPButton.setBounds(480, 85, 160, 25);
        panel.add(currPButton);

        JButton mgDashButton = new JButton("Home");
        mgDashButton.setBounds(10, 10, 80, 25);
        panel.add(mgDashButton);

        JButton mgPT = new JButton("Back");
        mgPT.setBounds(10, 50, 80, 25);
        panel.add(mgPT);

        JButton notadPButton = new JButton("Not Admitted");
        notadPButton.setBounds(300, 80, 160, 25);
        panel.add(notadPButton);

        idLabel = new JLabel("SortBy Order:");
        idLabel.setBounds(700, 40, 160, 25);
        panel.add(idLabel);

        sortCB = new JComboBox<>();
        sortCB.setBounds(700, 85, 160, 25);
        panel.add(sortCB);

        sortCB.addItem("patient_id");
        sortCB.addItem("name");
        sortCB.addItem("EnDate");
        sortCB.addItem("Exdate");
        sortCB.addItem("Total_Charge");



        // Table setup
        String[] columnNames = {"PatientID", "NAME", "DOB", "PHONE", "ADDRESS", "CHECK_IN DATE", "CHECK_OUT DATE", "TOTAL CHARGES"};
        model = new DefaultTableModel(columnNames, 0); // Empty table model
        table = new JTable(model);
        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 200, 1100, 300);
        scrollPane.setVisible(false); // Initially hidden
        panel.add(scrollPane);

        // Action Listeners
        allPButton.addActionListener(e -> fetchAllPatients());
        currPButton.addActionListener(e -> fetchCurrentPatients());
        notadPButton.addActionListener(e -> fetchNotAdmittedPatients());

        mgDashButton.addActionListener(e -> {
            dispose();
            MainDashboard mdash = new MainDashboard();
            mdash.setVisible(true);
        });

        onePButton.addActionListener(e -> {
            onePatientFetch();
        });

        paidPButton.addActionListener(e -> {
            paidPatientFetch();
        });

        mgPT.addActionListener(e -> {
            dispose();
            CheckOut checkOut = new CheckOut();
            checkOut.setVisible(true);
        });


        add(panel);

        // Populate patient IDs in the JComboBox at startup
        fetchPatientIDs();
    }
    //sort by order
    static String getOrderBy(){
        String orderByString = "";
        int index = sortCB.getSelectedIndex();
        switch (index){
            case 0: orderByString = "Order By patient_id"; break;
            case 1: orderByString = "Order By name"; break;
            case 2: orderByString = "Order By EnDate"; break;
            case 3: orderByString = "Order By Exdate"; break;
            case 4: orderByString = "Order By charges"; break;
        }
        return orderByString;
    }

    // Method to fetch Patient IDs from the database and add them to the JComboBox
    private void fetchPatientIDs() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT patient_id FROM patients "+reportUpdate.getOrderBy();
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
    //fetch paided pattients
    private void paidPatientFetch() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // SQL query to calculate the total charge and filter where Exdate is not NULL
            String sql = "SELECT patient_id, name, dob, phone, address, EnDate, Exdate, "
                    + "(DATEDIFF(Exdate, EnDate) * charges) AS total_charge "
                    + "FROM patients WHERE Exdate IS NOT NULL " + reportUpdate.getOrderBy();

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            // Clear previous table data
            model.setRowCount(0);

            // Fetch and add data to the table
            while (rs.next()) {
                Object[] row = {
                        rs.getString("patient_id"),
                        rs.getString("name"),
                        rs.getString("dob"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("EnDate"),
                        rs.getString("Exdate"),
                        rs.getString("total_charge") // Fetch the calculated total charge
                };
                model.addRow(row);
            }
            scrollPane.setVisible(true); // Show the table after data is fetched

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    // Fetch all patient details
    private void fetchAllPatients() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM patients "+reportUpdate.getOrderBy();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            // Clear previous table data
            model.setRowCount(0);

            // Fetch and add data to the table
            while (rs.next()) {
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
            }
            scrollPane.setVisible(true); // Show the table after data is fetched

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    //fetchone
    private void onePatientFetch() {
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
                        rs.getDate("EnDate"), // Fetching date as java.sql.Date
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

    // Fetch current patients (where ExDate is NULL)
    private void fetchCurrentPatients() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM patients WHERE EnDate IS NOT NULL AND Exdate IS NULL "+reportUpdate.getOrderBy();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            // Clear previous table data
            model.setRowCount(0);

            // Fetch and add data to the table
            while (rs.next()) {
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
            }
            scrollPane.setVisible(true); // Show the table after data is fetched

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Fetch not admitted patients (where both ExDate and EnDate are NULL)
    private void fetchNotAdmittedPatients() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM patients WHERE EnDate IS NULL "+reportUpdate.getOrderBy();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            // Clear previous table data
            model.setRowCount(0);

            // Fetch and add data to the table
            while (rs.next()) {
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
            }
            scrollPane.setVisible(true); // Show the table after data is fetched

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new reportUpdate().setVisible(true));
    }
}
