# ITM 411 Ticket Management System

## About the Project
This project, developed for ITM 411 by Mitansh Chaudhari, is a Java-based ticket management system. It leverages a Java Swing GUI for a user-friendly experience and JDBC for efficient database interactions. The system supports multiple user roles, each with tailored functionalities to cater to different aspects of ticket management.

### Functionality
- **Login System**: Secure authentication mechanism for users with role-based access control.
- **Ticket Management**: Ability for users to create new support tickets and for admins to assign, update, or delete them as needed.
- **User Interaction**: Intuitive interface for users to interact with the system, including options to refresh data views and search for specific tickets.
- **Dynamic Data Representation**: Real-time table updates to reflect the current state of tickets in the system.

### Files Description

#### `Dao.java` (Data Access Object)
This class acts as the intermediary between the application and the database. It manages all database transactions, including:
- Connecting to the database.
- Performing CRUD (Create, Read, Update, Delete) operations on tickets.
- Handling user-related data for authentication and user role management.

#### `Login.java`
This file contains the code for the login interface, including:
- A form for users to enter their login credentials.
- Verification against database records to authenticate users.
- Redirecting users to the main ticket interface upon successful login.

#### `Tickets.java`
The core of the application's user interface, providing:
- A table view for admins and regular users to view tickets, with admin users having additional controls.
- Button-based actions for creating new tickets, refreshing the view, and searching for tickets by ID.
- Customizable GUI components with event-driven functionalities for an interactive user experience.

## Usage Guide

Users can log in as an admin or regular user to access different functionalities. Admins have full control over the tickets, including the ability to assign tickets to departments, update ticket statuses, and delete tickets. Regular users can create tickets and update the description of their tickets.

For a walkthrough of the functionalities and user interfaces, please refer to the 'Getting Started' and 'Testing' sections of this README.

### Built With
- Java
- Java Swing for GUI
- JDBC for database interactions

## Testing

### Admin and User Credentials
For testing the application, use the following credentials:

#### Admin:
- **Username**: admin **Password**: 123
- **Username**: mit **Password**: admin

#### Regular User:
- **Username**: chaudhari **Password**: mc123
- **Username**: mit_chau **Password**: mit123

Please use these credentials to log in to the system with respective user roles and test the application functionalities.


