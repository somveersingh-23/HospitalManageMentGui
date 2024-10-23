import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class AdminLogin extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public AdminLogin() {
        setTitle("Admin Login");
        setSize(1200,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(360,200, 80, 25);
        panel.add(userLabel);

        ImageIcon img = new ImageIcon("src\\login2.png");
        JLabel background = new JLabel("",img,JLabel.CENTER);
        background.setBounds(-70,-170,1200,600);
        add(background);

        ImageIcon img2 = new ImageIcon("src\\loginS.png");
        JLabel background2 = new JLabel("",img2,JLabel.CENTER);
        background2.setBounds(290,0,1200,600);
        add(background2);

        ImageIcon img3 = new ImageIcon("src\\loginl.png");
        JLabel background3 = new JLabel("",img3,JLabel.CENTER);
        background3.setBounds(-400,0,1200,600);
        add(background3);

        usernameField = new JTextField();
        usernameField.setBounds(470,200, 160, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(360, 240, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(470,240, 160, 25);
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(470, 280, 160, 25);
        panel.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                authenticateAdmin();
            }
        });

        add(panel);
    }

    private void authenticateAdmin() {
       // JOptionPane.showMessageDialog(this,"Hello :)"); //this is Written by GUlathi Sir
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login Successful 🚀");
                dispose(); // Close login window
                MainDashboard dashboard = new MainDashboard();
                dashboard.setVisible(true); // Open main dashboard
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials ⚠");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminLogin login = new AdminLogin();
            login.setVisible(true);
        });

    }
}
