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
        //conn.createTables(); // You should call createTables only once, not every time you create a Login object.
        
        setLayout(new GridBagLayout()); // Set the layout to GridBagLayout
        
        // Set window to open in maximized state
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ensure the application exits when the window is closed
       
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10); // Padding

        // SET UP CONTROLS
        JLabel lblUsername = new JLabel("Username:");
        JLabel lblPassword = new JLabel("Password:");
        JLabel lblStatus = new JLabel(" ", JLabel.CENTER);
        JTextField txtUname = new JTextField(10);
        JPasswordField txtPassword = new JPasswordField();
        JButton btn = new JButton("Submit");
        JButton btnExit = new JButton("Exit");

        lblStatus.setToolTipText("Contact help desk to unlock password");
        
        // ADD OBJECTS TO FRAME
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(lblUsername, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        add(txtUname, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        add(lblPassword, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        add(txtPassword, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
		btn.addActionListener(new ActionListener() {
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


        add(btn, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        btnExit.addActionListener(e -> System.exit(0));
        add(btnExit, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        lblStatus.setForeground(Color.RED); // Set the text color of status messages to red
        add(lblStatus, constraints);


        setVisible(true); // SHOW THE FRAME
    }


	public static void main(String[] args) {

		new Login();
	}
}
