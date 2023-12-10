package javaapplication1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class Tickets extends JFrame implements ActionListener {

	// class level member objects
	Dao dao = new Dao(); // for CRUD operations
	Boolean chkIfAdmin = null;
	private JTable table;
	private String username;

	// Sub menu item objects for all Main menu item objects
	JMenuItem mnuItemExit;
	JMenuItem mnuItemUpdate;
	JMenuItem mnuItemDelete;
	JMenuItem mnuItemOpenTicket;
	JMenuItem mnuItemViewTicket;

	public Tickets(Boolean isAdmin, String username) {
		chkIfAdmin = isAdmin;
		this.username = username;
		prepareGUI();

	}

	private void prepareGUI() {
		// Maximize the window
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set a gradient background panel
		setContentPane(new JPanel() {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				int width = getWidth();
				int height = getHeight();
				Color color1 = new Color(133, 224, 255);
				Color color2 = new Color(255, 102, 178);
				GradientPaint gp = new GradientPaint(0, 0, color1, 0, height, color2);
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, width, height);
			}
		});

		getContentPane().setLayout(new GridBagLayout());

		// Panel for buttons
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.setOpaque(false); // Make the panel transparent

		// Add buttons to the button panel
		JButton btnLogout = new JButton(new ImageIcon(getClass().getResource("/icons/logout.png")));
		JButton btnRefresh = new JButton(new ImageIcon(getClass().getResource("/icons/refresh.png")));
		JButton btnNewTicket = new JButton(new ImageIcon(getClass().getResource("/icons/add.png")));
		JButton btnSearch = new JButton(new ImageIcon(getClass().getResource("/icons/search.png")));

		// Set button actions
		btnLogout.addActionListener(e -> {
			// Close Tickets.java and open Login.java
			this.dispose();
			new Login().setVisible(true);
		});
		btnRefresh.addActionListener(e -> populateTable());
		btnNewTicket.addActionListener(e -> createNewTicket());
		btnSearch.addActionListener(e -> searchTicket());

		// Add buttons to the panel
		buttonPanel.add(btnLogout);
		buttonPanel.add(btnRefresh);
		buttonPanel.add(btnNewTicket);
		buttonPanel.add(btnSearch);

		GridBagConstraints gbc_buttons = new GridBagConstraints();
		gbc_buttons.gridx = 0;
		gbc_buttons.gridy = 0;
		gbc_buttons.gridwidth = 1;
		gbc_buttons.anchor = GridBagConstraints.NORTHWEST;
		gbc_buttons.insets = new Insets(10, 10, 10, 10);
		getContentPane().add(buttonPanel, gbc_buttons);

		// Define table columns based on user type
		String[] adminColumns = { "Ticket ID", "Ticket Issuer", "Department", "Description", "Status" };
		String[] userColumns = { "Ticket ID", "Description", "Status" };

		// Initialize table with the correct columns based on user role
		DefaultTableModel model = new DefaultTableModel(new Object[][] {}, chkIfAdmin ? adminColumns : userColumns) {
			@Override
			public boolean isCellEditable(int row, int column) {
				// No cells can be edited directly in the table
				return false;
			}
		};
		table = new JTable(model) {
			// Override JTable's changeSelection method to simulate row hover effect
			public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
				super.changeSelection(rowIndex, columnIndex, true, false);
			}
		};
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Ensure only one row can be selected at a time
		JScrollPane sp = new JScrollPane(table);
		table.setFillsViewportHeight(true);

		// Format table
		table.setGridColor(Color.GRAY);
		table.setShowVerticalLines(true);
		table.setFont(new Font("SansSerif", Font.PLAIN, 18));
		table.setRowHeight(30);

		JPanel panel = new JPanel(new BorderLayout()) {
		    @Override
		    protected void paintComponent(Graphics g) {
		        super.paintComponent(g); // Call superclass method first
		        Graphics2D g2d = (Graphics2D) g.create();
		        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		        // Set the background color
		        g2d.setColor(new Color(255, 255, 255, 200)); // Semi-transparent white

		        // Filling the rounded rectangle (background)
		        int offset = 5; // Offset for the border to ensure it is visible within the panel bounds
		        int arcSize = 30; // Adjust this to change how rounded the corners are
		        g2d.fillRoundRect(offset, offset, getWidth() - 1 - offset * 2, getHeight() - 1 - offset * 2, arcSize, arcSize);

		        // Optionally, draw the border with a different color
		        g2d.setColor(Color.WHITE);
		        g2d.drawRoundRect(offset, offset, getWidth() - 1 - offset * 2, getHeight() - 1 - offset * 2, arcSize, arcSize);

		        g2d.dispose();
		    }
		};

		// Set the panel to be transparent to allow the gradient to show through
		panel.setOpaque(false);
		panel.add(sp, BorderLayout.CENTER);

		// Set a smaller preferred size for the panel if desired
		panel.setPreferredSize(new Dimension(600, 400)); // You might adjust the size as needed


		// Add the table panel below the button panel
		GridBagConstraints gbc_table = new GridBagConstraints();
		gbc_table.gridx = 0;
		gbc_table.gridy = 1; // This row is below the button panel
		gbc_table.gridwidth = gbc_table.gridheight = 1;
		gbc_table.fill = GridBagConstraints.BOTH;
		gbc_table.insets = new Insets(10, 10, 10, 10);
		getContentPane().add(panel, gbc_table);

		// Mouse listener for hover effect and row selection
		table.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
				if (row > -1) {
					// Set the selection directly in the table model
					table.getSelectionModel().setSelectionInterval(row, row);
					// Repaint the table to reflect the new row highlight
					table.repaint();
				} else {
					// Clear selection when not hovering over a row
					table.clearSelection();
				}
			}
		});

		// Mouse listener for admin and regular user actions
		table.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent evt) {
		        int row = table.rowAtPoint(evt.getPoint());
		        if (row >= 0) {
		            Object ticketId = table.getModel().getValueAt(row, 0); // Assuming first column is Ticket ID

		            if (chkIfAdmin) {
		                // Admin options
		                displayOptions(ticketId);
		            } else {
		                // Regular user option to update description
		                int confirm = JOptionPane.showConfirmDialog(null, "Would you like to update the description for Ticket ID " + ticketId + "?", "Update Description", JOptionPane.YES_NO_OPTION);
		                if (confirm == JOptionPane.YES_OPTION) {
		                    updateDescription(ticketId);
		                }
		            }
		        }
		    }
		});
		
		setVisible(true); // Refresh the frame
		populateTable(); // Populate the table with data
	}

	
	private void populateTable() {
		try {
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			ResultSet rs = dao.readRecords(chkIfAdmin, username); // Use username instead of userId

			model.setRowCount(0); // Clear the existing data

			while (rs.next()) {
				if (chkIfAdmin) {
					// Admins see all columns
					model.addRow(new Object[] { rs.getInt("ticket_id"), rs.getString("ticket_issuer"),
							rs.getString("department"), rs.getString("ticket_description"), rs.getString("status") });
				} else {
					// Regular users see fewer columns
					model.addRow(new Object[] { rs.getInt("ticket_id"), rs.getString("ticket_description"),
							rs.getString("status") });
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle any errors that occur during the database read operation
		}
	}

	private void displayOptions(Object ticketId) {
		// Present options to the user
		String[] options = { "Assign Department", "Update Status", "Delete Ticket" };
		String selectedOption = (String) JOptionPane.showInputDialog(null,
				"Choose action for Ticket ID " + ticketId + ":", "Ticket Actions", JOptionPane.QUESTION_MESSAGE, null,
				options, options[0]);

		// Handle user selection
		if (selectedOption != null) { // Check if a selection was made
			switch (selectedOption) {
			case "Assign Department":
				assignDepartment(ticketId);
				break;
			case "Update Status":
				updateStatus(ticketId);
				break;
			case "Delete Ticket":
				deleteTicket(ticketId);
				break;
			}
		}
	}

	private void assignDepartment(Object ticketId) {
		String[] departments = { "CS", "ITM", "Biology", "Chemistry" };
		String department = (String) JOptionPane.showInputDialog(null, "Select the department to assign:",
				"Assign Department", JOptionPane.QUESTION_MESSAGE, null, departments, departments[0]);

		if (department != null) {
			// Update the database with the selected department and change status to "In
			// Progress"
			boolean success = dao.assignTicket((Integer) ticketId, department);
			if (success) {
				JOptionPane.showMessageDialog(null, "Department assigned successfully.");
				populateTable(); // Refresh table to show changes
			} else {
				JOptionPane.showMessageDialog(null, "Error assigning department.");
			}
		}
	}

	private void updateStatus(Object ticketId) {
		String[] statuses = { "Open", "In Progress", "Closed" };
		String status = (String) JOptionPane.showInputDialog(null, "Select the new status:", "Update Status",
				JOptionPane.QUESTION_MESSAGE, null, statuses, statuses[0]);

		if (status != null) {
			// Update the database with the new status
			boolean success = dao.updateRecord((Integer) ticketId, null, null, status);
			if (success) {
				JOptionPane.showMessageDialog(null, "Status updated successfully.");
				populateTable(); // Refresh table to show changes
			} else {
				JOptionPane.showMessageDialog(null, "Error updating status.");
			}
		}
	}

	private void deleteTicket(Object ticketId) {
		int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete ticket ID " + ticketId + "?",
				"Delete Ticket", JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			// Delete the ticket from the database
			boolean success = dao.deleteRecord((Integer) ticketId);
			if (success) {
				JOptionPane.showMessageDialog(null, "Ticket deleted successfully.");
				populateTable(); // Refresh table to show changes
			} else {
				JOptionPane.showMessageDialog(null, "Error deleting ticket.");
			}
		}
	}

	private void createNewTicket() {
	    // Prompt for ticket description using JOptionPane for input dialog
	    String ticketDescription = JOptionPane.showInputDialog(this, "Enter ticket description:");

	    // Check if the ticket description is not null and not empty
	    if (ticketDescription != null && !ticketDescription.trim().isEmpty()) {
	        // Assign values for ticket fields
	        String ticketIssuer = this.username; // Assuming 'username' is a class member that stores the logged-in user's username
	        String department = "Unassigned";
	        String status = "Open";

	        // Insert the new ticket record into the database
	        int ticketId = dao.insertRecords(ticketIssuer, ticketDescription, department, status);

	        // If the ticketId is positive, the insertion was successful
	        if (ticketId > 0) {
	            JOptionPane.showMessageDialog(this, "Ticket created successfully! Ticket ID is: " + ticketId);
	            populateTable(); // Refresh the table to show the new ticket
	        } else {
	            // Handle the case where insertRecords returns 0 or negative value, which means insertion failed
	            JOptionPane.showMessageDialog(this, "Failed to create a new ticket. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    } else {
	        // If the user canceled the dialog or entered an empty description, show a warning
	        JOptionPane.showMessageDialog(this, "Ticket description cannot be empty.", "Warning", JOptionPane.WARNING_MESSAGE);
	    }
	}

	private void searchTicket() {
	    // Prompt the user to enter a ticket number
	    String ticketIdStr = JOptionPane.showInputDialog(this, "Enter the ticket ID to search:");

	    // Check if the user entered a ticket number
	    if (ticketIdStr != null && !ticketIdStr.trim().isEmpty()) {
	        try {
	            int ticketId = Integer.parseInt(ticketIdStr.trim());

	            // Fetch the ticket from the database
	            ResultSet rs = dao.searchTicketById(ticketId);

	            // Clear the existing table data
	            DefaultTableModel model = (DefaultTableModel) table.getModel();
	            model.setRowCount(0);

	            // Add the fetched ticket to the table if it exists
	            if (rs != null && rs.next()) {
	                if (chkIfAdmin) {
	                    model.addRow(new Object[]{
	                        rs.getInt("ticket_id"), 
	                        rs.getString("ticket_issuer"),
	                        rs.getString("department"), 
	                        rs.getString("ticket_description"), 
	                        rs.getString("status")
	                    });
	                } else {
	                    model.addRow(new Object[]{
	                        rs.getInt("ticket_id"), 
	                        rs.getString("ticket_description"), 
	                        rs.getString("status")
	                    });
	                }
	            } else {
	                JOptionPane.showMessageDialog(this, "No ticket found with ID: " + ticketId, "Search Result", JOptionPane.INFORMATION_MESSAGE);
	            }
	        } catch (NumberFormatException e) {
	            JOptionPane.showMessageDialog(this, "Please enter a valid ticket ID.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
	        } catch (SQLException e) {
	            e.printStackTrace();
	            JOptionPane.showMessageDialog(this, "Error occurred while searching for the ticket.", "Database Error", JOptionPane.ERROR_MESSAGE);
	        }
	    } else if (ticketIdStr != null) {
	        // If the user did not enter a number, show a warning
	        JOptionPane.showMessageDialog(this, "Ticket ID cannot be empty.", "Warning", JOptionPane.WARNING_MESSAGE);
	    }
	}
	
	private void updateDescription(Object ticketId) {
	    String newDescription = JOptionPane.showInputDialog(this, "Enter new description for Ticket ID " + ticketId + ":");

	    // Check if the user entered a new description
	    if (newDescription != null && !newDescription.trim().isEmpty()) {
	        // Call the Dao method to update the ticket description
	        boolean success = dao.updateTicketDescription((Integer) ticketId, newDescription);
	        
	        if (success) {
	            JOptionPane.showMessageDialog(this, "Description updated successfully.");
	            populateTable(); // Refresh the table to show the updated description
	        } else {
	            JOptionPane.showMessageDialog(this, "Error updating description.", "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    } else if (newDescription != null) {
	        // If the user did not enter a description, show a warning
	        JOptionPane.showMessageDialog(this, "Description cannot be empty.", "Warning", JOptionPane.WARNING_MESSAGE);
	    }
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
