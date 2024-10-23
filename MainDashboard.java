import java.awt.event.*;
import javax.swing.*;

public class MainDashboard extends JFrame {
    public MainDashboard() {
        setTitle("Hospital Management System");
        setSize(1200,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        ImageIcon img = new ImageIcon("src\\mgDash.png");
        JLabel background = new JLabel("",img,JLabel.CENTER);
        background.setBounds(-20,-30,1200,600);
        add(background);

        JButton managePatientsButton = new JButton("Manage Patients");
        managePatientsButton.setBounds(800,200, 160, 25); //470
        panel.add(managePatientsButton);

        JButton manageDoctorsButton = new JButton("Manage Doctors");
        manageDoctorsButton.setBounds(800,240, 160, 25);
        panel.add(manageDoctorsButton);


        JButton checkInButton = new JButton("Check In");
        checkInButton.setBounds(800,280, 160, 25);
        panel.add(checkInButton);


        JButton checkOutButton = new JButton("Check out");
        checkOutButton.setBounds(800,320, 160, 25);
        panel.add(checkOutButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(830, 360,100, 25);
        panel.add(logoutButton);

        managePatientsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ManagePatients managePatients = new ManagePatients();
                managePatients.setVisible(true);
            }
        });

        manageDoctorsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ManageDoctors manageDoctors = new ManageDoctors();
                manageDoctors.setVisible(true);
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the dashboard window
                AdminLogin login = new AdminLogin();
                login.setVisible(true); // Go back to the login page
            }
        });
        checkInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the dashboard window
                CheckIn checkIn = new CheckIn();
                checkIn.setVisible(true);//go to check page
            }
        });
        checkOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the dashboard window
                CheckOut checkOut = new CheckOut();
                checkOut.setVisible(true);//go to check page
            }
        });

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainDashboard dashboard = new MainDashboard();
            dashboard.setVisible(true);
        });
    }
}
