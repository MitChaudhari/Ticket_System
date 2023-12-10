package javaapplication1;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

@SuppressWarnings("serial")
public class Login extends JFrame {
    // Nested RoundedPanel class
    class RoundedPanel extends JPanel {
        private final int radius;
        private final Color backgroundColor;

        public RoundedPanel(int radius, Color backgroundColor) {
            this.radius = radius;
            this.backgroundColor = backgroundColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(backgroundColor);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2d.dispose();
        }
    }

	Dao conn;

	public Login() {
		super("IIT HELP DESK LOGIN");
		conn = new Dao();
		// conn.createTables(); // Call this method once to create the tables.

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

				// Base dark gradient colors
				Color baseDark = new Color(30, 30, 30); // almost black
				Color midDark = new Color(50, 34, 60); // dark purple
				Color highlight = new Color(90, 60, 90); // subtle purple

				// Additional colors for depth
				Color depthStart = new Color(30, 30, 60, 100); // translucent blue for special touch
				Color depthEnd = new Color(60, 30, 30, 100); // translucent red for special touch

				// Create a gradient from top to bottom
				GradientPaint gpVertical = new GradientPaint(0, 0, baseDark, 0, height, midDark);
				g2d.setPaint(gpVertical);
				g2d.fillRect(0, 0, width, height);

				// Add a horizontal gradient for visual interest
				GradientPaint gpHorizontal = new GradientPaint(0, 0, new Color(0, 0, 0, 0), width, 0, highlight);
				g2d.setPaint(gpHorizontal);
				g2d.fillRect(0, 0, width, height);

				// Add a gradient from corner to corner for depth
				GradientPaint gpDiagonal = new GradientPaint(0, 0, depthStart, width, height, depthEnd);
				g2d.setPaint(gpDiagonal);
				g2d.fillRect(0, 0, width, height);
			}
		};

		setContentPane(backgroundPanel);
		backgroundPanel.setLayout(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(20, 50, 20, 50);

        // Card-like panel for the login form using RoundedPanel
        RoundedPanel loginPanel = new RoundedPanel(20, new Color(0, 0, 0, 150));
        loginPanel.setLayout(new GridBagLayout());

		// Title
		JLabel lblTitle = new JLabel("Welcome to IIT Help Desk!");
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
		lblTitle.setForeground(Color.WHITE);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.anchor = GridBagConstraints.CENTER;
		loginPanel.add(lblTitle, constraints);

		// Subtitle with word-wrap
		JLabel lblSubtitle = new JLabel("Login to create or manage support tickets.");
		lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblSubtitle.setForeground(Color.LIGHT_GRAY);
		constraints.gridy = 1;
		constraints.gridwidth = 2;
		constraints.anchor = GridBagConstraints.CENTER;
		loginPanel.add(lblSubtitle, constraints);

		// Custom Username box using JTextField with Rounded Corners and Placeholder Logic
		class PlaceholderRoundedTextField extends JTextField {
		    private boolean showingPlaceholder;
		    private boolean hasTyped = false;
		    private Shape shape;

		    public PlaceholderRoundedTextField(String placeholder, int size) {
		        super(size);
		        this.showingPlaceholder = true;
		        setText(placeholder);
		        setForeground(Color.GRAY);
		        setOpaque(false);

		        this.addKeyListener(new KeyAdapter() {
		            @Override
		            public void keyTyped(KeyEvent e) {
		                if (showingPlaceholder) {
		                    setText("");
		                    setForeground(Color.BLACK);
		                    showingPlaceholder = false;
		                }
		                hasTyped = true;
		            }
		        });

		        this.addFocusListener(new FocusAdapter() {
		            @Override
		            public void focusGained(FocusEvent e) {
		                if (showingPlaceholder && !hasTyped) {
		                    setForeground(Color.GRAY);
		                } else {
		                    setForeground(Color.BLACK);
		                }
		            }

		            @Override
		            public void focusLost(FocusEvent e) {
		                hasTyped = false;
		                if (getText().isEmpty()) {
		                    setText(placeholder);
		                    setForeground(Color.GRAY);
		                    showingPlaceholder = true;
		                }
		            }
		        });
		    }

		    @Override
		    protected void paintComponent(Graphics g) {
		        g.setColor(getBackground());
		        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
		        super.paintComponent(g);
		    }

		    @Override
		    protected void paintBorder(Graphics g) {
		        g.setColor(getForeground());
		        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
		    }

		    @Override
		    public boolean contains(int x, int y) {
		        if (shape == null || !shape.getBounds().equals(getBounds())) {
		            shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
		        }
		        return shape.contains(x, y);
		    }

		    @Override
		    public String getText() {
		        return showingPlaceholder ? "" : super.getText();
		    }
		}
		// Username field behavior
		PlaceholderRoundedTextField txtUname = new PlaceholderRoundedTextField("Username", 15);
		constraints.ipadx = 50; // Decrease width padding to shorten field
		constraints.ipady = 10; // Increase height padding for a taller field
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 2; // Ensure the text field doesn't fill the entire grid width
		constraints.fill = GridBagConstraints.NONE; // Do not let it stretch horizontally
		loginPanel.add(txtUname, constraints);
		
		
		// Custom Password box using JTextField with Rounded Corners and Placeholder Logic
		class PlaceholderRoundedPasswordField extends JPasswordField {
		    private boolean showingPlaceholder;
		    private boolean hasTyped = false;
		    private Shape shape;

		    public PlaceholderRoundedPasswordField(String placeholder, int size) {
		        super(size);
		        this.showingPlaceholder = true;
		        setText(placeholder);
		        setForeground(Color.GRAY);
		        setEchoChar((char) 0);
		        setOpaque(false);

		        this.addKeyListener(new KeyAdapter() {
		            @Override
		            public void keyTyped(KeyEvent e) {
		                if (showingPlaceholder) {
		                    setText("");
		                    setForeground(Color.BLACK);
		                    setEchoChar('•');
		                    showingPlaceholder = false;
		                }
		                hasTyped = true;
		            }
		        });

		        this.addFocusListener(new FocusAdapter() {
		            @Override
		            public void focusGained(FocusEvent e) {
		                if (showingPlaceholder && !hasTyped) {
		                    setForeground(Color.GRAY);
		                    setEchoChar((char) 0);
		                } else {
		                    setForeground(Color.BLACK);
		                    if (!showingPlaceholder) {
		                        setEchoChar('•');
		                    }
		                }
		            }

		            @Override
		            public void focusLost(FocusEvent e) {
		                hasTyped = false;
		                if (getPassword().length == 0) {
		                    setText(placeholder);
		                    setForeground(Color.GRAY);
		                    setEchoChar((char) 0);
		                    showingPlaceholder = true;
		                }
		            }
		        });
		    }

		    @Override
		    protected void paintComponent(Graphics g) {
		        g.setColor(getBackground());
		        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
		        super.paintComponent(g);
		    }

		    @Override
		    protected void paintBorder(Graphics g) {
		        g.setColor(getForeground());
		        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
		    }

		    @Override
		    public boolean contains(int x, int y) {
		        if (shape == null || !shape.getBounds().equals(getBounds())) {
		            shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
		        }
		        return shape.contains(x, y);
		    }


		    @Override
		    public String getText() {
		        return showingPlaceholder ? "" : String.valueOf(super.getPassword());
		    }
		}
		
		// Password field behavior
		PlaceholderRoundedPasswordField txtPassword = new PlaceholderRoundedPasswordField("Password", 15);
		constraints.gridy = 3;
		loginPanel.add(txtPassword, constraints);

		// Login button with rounded corners and gradient
		JButton btnLogin = new JButton("Login") {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				GradientPaint gp = new GradientPaint(0, 0, new Color(255, 140, 0), getWidth(), getHeight(),
						new Color(255, 165, 0), true);
				g2d.setPaint(gp);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
				super.paintComponent(g);
			}
		};

		btnLogin.setOpaque(false);
		btnLogin.setContentAreaFilled(false);
		btnLogin.setBorderPainted(false);
		btnLogin.setForeground(Color.WHITE);
		btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
		constraints.ipadx = 30; // Decrease width padding to shorten button
		constraints.gridy = 4;
		constraints.anchor = GridBagConstraints.CENTER;
		loginPanel.add(btnLogin, constraints);

		// Status label
		JLabel lblStatus = new JLabel(" ", JLabel.CENTER);
		lblStatus.setForeground(Color.RED);
		constraints.gridy = 5;
		constraints.gridwidth = 2;
		loginPanel.add(lblStatus, constraints);

		// Add the login panel to the background panel
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.CENTER;
		backgroundPanel.add(loginPanel, constraints);

		// Action listener for the login button
		btnLogin.addActionListener(new ActionListener() {
		    int count = 0; // count agent

		    @Override
		    public void actionPerformed(ActionEvent e) {
		        boolean admin = false;
		        String username = ""; // Initialize username
		        count = count + 1;

		        // verify credentials of user
		        String query = "SELECT * FROM mchau_users WHERE uname = ? and upass = ?;";
		        try (PreparedStatement stmt = conn.getConnection().prepareStatement(query)) {
		            stmt.setString(1, txtUname.getText().trim());
		            stmt.setString(2, new String(txtPassword.getPassword()));
		            ResultSet rs = stmt.executeQuery();
		            if (rs.next()) {
		                admin = rs.getBoolean("admin"); // get table column value
		                username = rs.getString("uname"); // get user name
		                new Tickets(admin, username); // open Tickets file / GUI interface, passing the admin status and username
		                setVisible(false); // HIDE THE FRAME
		                dispose(); // CLOSE OUT THE WINDOW
		            } else {
		                lblStatus.setText("Try again! " + (3 - count) + " / 3 attempt(s) left");
		                if (count >= 3) {
		                    btnLogin.setEnabled(false); // Disable the login button after 3 attempts
		                }
		            }
		        } catch (SQLException ex) {
		            ex.printStackTrace();
		            lblStatus.setText("Error: Unable to connect to database.");
		        }
		    }
		});

		setVisible(true); // Show the frame
	}

	// A custom border class for rounded corners
	class RoundedBorder extends AbstractBorder {
		private int radius;

		RoundedBorder(int radius) {
			this.radius = radius;
		}

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
		}
	}

	public static void main(String[] args) {

		new Login();
	}
}
