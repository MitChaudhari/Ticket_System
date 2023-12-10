package javaapplication1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dao {
	// instance fields
	static Connection connect = null;
	Statement statement = null;

	// constructor
	public Dao() {
	  
	}
 
	public Connection getConnection() {
		// Setup the connection with the DB
		try {
			connect = DriverManager
					.getConnection("jdbc:mysql://www.papademas.net:3307/tickets?autoReconnect=true&useSSL=false"
							+ "&user=fp411&password=411");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connect;
	}

	// CRUD implementation

	public void createTables() {
		// variables for SQL Query table creations
	    final String createTicketsTable = "CREATE TABLE mchau_tickets(ticket_id INT AUTO_INCREMENT PRIMARY KEY, ticket_issuer VARCHAR(30), department VARCHAR(30), ticket_description VARCHAR(200), open_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, status ENUM('Open', 'In Progress', 'Closed') DEFAULT 'Open')";
	    final String createUsersTable = "CREATE TABLE mchau_users(uid INT AUTO_INCREMENT PRIMARY KEY, uname VARCHAR(30), upass VARCHAR(30), admin TINYINT(1))";
	    
		try {

			// execute queries to create tables

			statement = getConnection().createStatement();

			statement.executeUpdate(createTicketsTable);
			statement.executeUpdate(createUsersTable);
			System.out.println("Created tables in given database...");

			// end create table
			// close connection/statement object
			statement.close();
			connect.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// add users to user table
		addUsers();
	}

	public void addUsers() {
		// add list of users from userlist.csv file to users table

		// variables for SQL Query inserts
		String sql;

		Statement statement;
		BufferedReader br;
		List<List<String>> array = new ArrayList<>(); // list to hold (rows & cols)

		// read data from file
		try {
			br = new BufferedReader(new FileReader(new File("./userlist.csv")));

			String line;
			while ((line = br.readLine()) != null) {
				array.add(Arrays.asList(line.split(",")));
			}
		} catch (Exception e) {
			System.out.println("There was a problem loading the file");
		}

		try {

			// Setup the connection with the DB

			statement = getConnection().createStatement();

			// create loop to grab each array index containing a list of values
			// and PASS (insert) that data into your User table
		    for (List<String> rowData : array) {
		        sql = "insert into mchau_users(uname, upass, admin) " + 
		              "values('" + rowData.get(0) + "', '" + rowData.get(1) + "', " + rowData.get(2) + ");";
		        statement.executeUpdate(sql);
		    }
		    
			System.out.println("Inserts completed in the given database...");

			// close statement object
			statement.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public int insertRecords(String ticketIssuer, String ticketDesc, String department, String status) {
	    int id = 0;
	    try {
	        statement = getConnection().createStatement();
	        String sql = "Insert into mchau_tickets (ticket_issuer, ticket_description, department, status) values('" + ticketIssuer + "', '" + ticketDesc + "', '" + department + "', '" + status + "')";
	        statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);

	        // retrieve ticket id number newly auto generated upon record insertion
	        ResultSet resultSet = statement.getGeneratedKeys();
	        if (resultSet.next()) {
	            // retrieve first field in table
	            id = resultSet.getInt(1);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return id;
	}

	public ResultSet readRecords(boolean isAdmin, String username) {
	    ResultSet results = null;
	    try {
	        if (isAdmin) {
	            String query = "SELECT * FROM mchau_tickets";
	            statement = connect.createStatement();
	            results = statement.executeQuery(query);
	        } else {
	            String query = "SELECT * FROM mchau_tickets WHERE ticket_issuer = ?";
	            PreparedStatement pstmt = connect.prepareStatement(query);
	            pstmt.setString(1, username); // Set the username for the query
	            results = pstmt.executeQuery();
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return results;
	}

	
	// Method to update records
	public boolean updateRecord(int ticketId, String ticketDescription, String department, String status) {
	    try {
	        // Start building the SQL statement
	        StringBuilder sqlBuilder = new StringBuilder("UPDATE mchau_tickets SET ");
	        ArrayList<Object> params = new ArrayList<>();
	        
	        // Check and append description if not null
	        if (ticketDescription != null) {
	            sqlBuilder.append("ticket_description = ?, ");
	            params.add(ticketDescription);
	        }
	        
	        // Check and append department if not null
	        if (department != null) {
	            sqlBuilder.append("department = ?, ");
	            params.add(department);
	        }
	        
	        // Check and append status if not null
	        if (status != null) {
	            sqlBuilder.append("status = ?, ");
	            params.add(status);
	        }
	        
	        // Remove the last comma and space
	        if (params.size() > 0) {
	            sqlBuilder.setLength(sqlBuilder.length() - 2);
	        }
	        
	        // Finish the SQL statement
	        sqlBuilder.append(" WHERE ticket_id = ?;");
	        params.add(ticketId);
	        
	        try (PreparedStatement stmt = getConnection().prepareStatement(sqlBuilder.toString())) {
	            // Set the parameters for the SQL statement
	            for (int i = 0; i < params.size(); i++) {
	                stmt.setObject(i + 1, params.get(i));
	            }
	            
	            int affectedRows = stmt.executeUpdate();
	            return affectedRows > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
    // Method to delete records
    public boolean deleteRecord(int ticketId) {
        try {
            String sql = "DELETE FROM mchau_tickets WHERE ticket_id = ?";
            try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
                stmt.setInt(1, ticketId);
                
                int affectedRows = stmt.executeUpdate();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to assign a ticket to a department and update its status to 'In Progress'
    public boolean assignTicket(int ticketId, String department) {
        return updateRecord(ticketId, null, department, "In Progress");
    }
    
    // Method to search Ticket By Id
    public ResultSet searchTicketById(int ticketId) throws SQLException {
        String query = "SELECT * FROM mchau_tickets WHERE ticket_id = ?";
        PreparedStatement pstmt = getConnection().prepareStatement(query);
        pstmt.setInt(1, ticketId);
        return pstmt.executeQuery();
    }
    // Method to update Ticket Description
    public boolean updateTicketDescription(int ticketId, String newDescription) {
        // SQL query to update ticket description
        String sql = "UPDATE mchau_tickets SET ticket_description = ? WHERE ticket_id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, newDescription);
            pstmt.setInt(2, ticketId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    

}
