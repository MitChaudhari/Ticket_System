package javaapplication1;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;

@SuppressWarnings("serial")
public class Login extends JFrame {

    Dao conn;

    public Login() {
        super("IIT HELP DESK LOGIN");
        conn = new Dao();
        // conn.createTables(); // You should call createTables only once.

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set up a gradient background panel
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();
                Color color1 = new Color(106, 116, 145);
                Color color2 = new Color(58, 63, 70);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, height, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, width, height);
            }
        };
        setContentPane(backgroundPanel);
        backgroundPanel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        // Card-like panel for the login form
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setBackground(new Color(0, 0, 0, 80)); // Semi-transparent
        loginPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        // Adjust constraints for the components inside the login panel
        constraints.gridx = 0;
        constraints.gridy = 0;
        loginPanel.add(new JLabel("Username:"), constraints);

        JTextField txtUname = new JTextField(15);
        constraints.gridx = 1;
        constraints.gridy = 0;
        loginPanel.add(txtUname, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        loginPanel.add(new JLabel("Password:"), constraints);

        JPasswordField txtPassword = new JPasswordField(15);
        constraints.gridx = 1;
        constraints.gridy = 1;
        loginPanel.add(txtPassword, constraints);

        JButton btnLogin = new JButton("Login");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        loginPanel.add(btnLogin, constraints);

        JCheckBox rememberMe = new JCheckBox("Remember me");
        constraints.gridx = 0;
        constraints.gridy = 3;
        loginPanel.add(rememberMe, constraints);

        JLabel lblForgotPassword = new JLabel("Forgot Password?");
        constraints.gridx = 1;
        constraints.gridy = 3;
        loginPanel.add(lblForgotPassword, constraints);

        JLabel lblStatus = new JLabel(" ", JLabel.CENTER);
        lblStatus.setForeground(Color.RED);
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        loginPanel.add(lblStatus, constraints);

        // Add the login panel to the background panel
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        backgroundPanel.add(loginPanel, constraints);
        btnLogin.addActionListener(new ActionListener() {
			int count = 0; // count agent

			@Override
			public void actionPerformed(ActionEvent e) {
				boolean admin = false;
				count = count + 1;
				// verify credentials of user (MAKE SURE TO CHANGE TO YOUR TABLE NAME BELOW)

				String query = "SELECT * FROM mchau_users WHERE uname = ? and upass = ?;";
				try (PreparedStatement stmt = conn.getConnection().prepareStatement(query)) {
					stmt.setString(1, txtUname.getText());
					stmt.setString(2, new String(txtPassword.getPassword()));
					ResultSet rs = stmt.executeQuery();
					if (rs.next()) {
						admin = rs.getBoolean("admin"); // get table column value
						new Tickets(admin); //open Tickets file / GUI interface
						setVisible(false); // HIDE THE FRAME
						dispose(); // CLOSE OUT THE WINDOW
					} else
						lblStatus.setText("Try again! " + (3 - count) + " / 3 attempt(s) left");
				} catch (SQLException ex) {
					ex.printStackTrace();
					lblStatus.setText("Error: Unable to connect to database.");
				}
 			 
			}
		});


        add(btnLogin);

        setVisible(true); // SHOW THE FRAME
    }

	public static void main(String[] args) {

		new Login();
	}
}
