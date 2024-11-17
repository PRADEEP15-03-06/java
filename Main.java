import java.sql.*;
import java.util.Scanner;

class Person {
    private String name;
    private String email;
    private String contactNumber;

    public Person(String name, String email, String contactNumber) {
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    @Override
    public String toString() {
        return "Name: " + name + ", Email: " + email + ", Contact: " + contactNumber;
    }
}

class Alumni extends Person {
    private int id;
    private int graduationYear;
    private String department;

    public Alumni(String name, String email, String contactNumber, int graduationYear, String department) {
        super(name, email, contactNumber);
        this.graduationYear = graduationYear;
        this.department = department;
    }

    public Alumni(int id, String name, String email, String contactNumber, int graduationYear, String department) {
        super(name, email, contactNumber);
        this.id = id;
        this.graduationYear = graduationYear;
        this.department = department;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getGraduationYear() { return graduationYear; }
    public void setGraduationYear(int graduationYear) { this.graduationYear = graduationYear; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + getName() + ", Email: " + getEmail() + ", Contact: " + getContactNumber()
                + ", Graduation Year: " + graduationYear + ", Department: " + department;
    }
}

class DatabaseConnection {
    private Connection connection;

    public DatabaseConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/alumniDB", "root", "1234");
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        }
    }
    public int addAlumni(Alumni alumni) {
        String query = "INSERT INTO Alumni (name, graduationYear, department, email, contactNumber) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, alumni.getName());
            statement.setInt(2, alumni.getGraduationYear());
            statement.setString(3, alumni.getDepartment());
            statement.setString(4, alumni.getEmail());
            statement.setString(5, alumni.getContactNumber());
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    System.out.println("Alumni added successfully with ID: " + generatedId);
                    return generatedId;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error adding alumni: " + e.getMessage());
        }
        return -1; 
    }

    public void viewAlumni(int id) {
        String query = "SELECT * FROM Alumni WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Alumni alumni = new Alumni(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("email"),
                    resultSet.getString("contactNumber"),
                    resultSet.getInt("graduationYear"),
                    resultSet.getString("department")
                );
                System.out.println("Alumni Information: " + alumni);
            } else {
                System.out.println("Alumni not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error viewing alumni: " + e.getMessage());
        }
    }

    // Method to update an alumni record
    public void updateAlumni(int id, String name, String email, String contactNumber, int graduationYear, String department) {
        String query = "UPDATE Alumni SET name = ?, email = ?, contactNumber = ?, graduationYear = ?, department = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, contactNumber);
            statement.setInt(4, graduationYear);
            statement.setString(5, department);
            statement.setInt(6, id);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Alumni record updated successfully.");
            } else {
                System.out.println("Alumni record not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating alumni: " + e.getMessage());
        }
    }

    // Method to delete an alumni record
    public void deleteAlumni(int id) {
        String query = "DELETE FROM Alumni WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Alumni record deleted successfully.");
            } else {
                System.out.println("Alumni record not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting alumni: " + e.getMessage());
        }
    }

    // Method to search alumni by name
    public void searchAlumni(String name) {
        String query = "SELECT * FROM Alumni WHERE name LIKE ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "%" + name + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Alumni alumni = new Alumni(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("email"),
                    resultSet.getString("contactNumber"),
                    resultSet.getInt("graduationYear"),
                    resultSet.getString("department")
                );
                System.out.println(alumni);
            }
        } catch (SQLException e) {
            System.out.println("Error searching alumni: " + e.getMessage());
        }
    }
}

public class Main {
    public static void main(String[] args) {
        DatabaseConnection dbConnection = new DatabaseConnection();
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("Alumni Database Management System");
            System.out.println("1. Add Alumni");
            System.out.println("2. View Alumni");
            System.out.println("3. Update Alumni");
            System.out.println("4. Delete Alumni");
            System.out.println("5. Search Alumni");
            System.out.println("6. Exit");

            try {
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine();  

                switch (choice) {
                    case 1:
                        
                        System.out.print("Enter name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter email: ");
                        String email = scanner.nextLine();
                        System.out.print("Enter contact number: ");
                        String contactNumber = scanner.nextLine();
                        System.out.print("Enter graduation year: ");
                        int graduationYear = scanner.nextInt();
                        scanner.nextLine(); 
                        System.out.print("Enter department: ");
                        String department = scanner.nextLine();

                        Alumni alumni = new Alumni(name, email, contactNumber, graduationYear, department);
                        int newId = dbConnection.addAlumni(alumni); 
                        if (newId != -1) {
                            System.out.println("Alumni added with ID: " + newId);
                        }
                        break;

                    case 2:
                        System.out.print("Enter Alumni ID to view: ");
                        int viewId = scanner.nextInt();
                        dbConnection.viewAlumni(viewId);
                        break;

                    case 3:
                        System.out.print("Enter Alumni ID to update: ");
                        int updateId = scanner.nextInt();
                        scanner.nextLine(); 
                        System.out.print("Enter new name: ");
                        String newName = scanner.nextLine();
                        System.out.print("Enter new email: ");
                        String newEmail = scanner.nextLine();
                        System.out.print("Enter new contact number: ");
                        String newContactNumber = scanner.nextLine();
                        System.out.print("Enter new graduation year: ");
                        int newGraduationYear = scanner.nextInt();
                        scanner.nextLine();  
                        System.out.print("Enter new department: ");
                        String newDepartment = scanner.nextLine();
                        
                        dbConnection.updateAlumni(updateId, newName, newEmail, newContactNumber, newGraduationYear, newDepartment);
                        break;

                    case 4:
                        System.out.print("Enter Alumni ID to delete: ");
                        int deleteId = scanner.nextInt();
                        dbConnection.deleteAlumni(deleteId);
                        break;

                    case 5:
                        System.out.print("Enter Alumni name to search: ");
                        String searchName = scanner.nextLine();
                        dbConnection.searchAlumni(searchName);
                        break;

                    case 6:
                        System.out.println("Exiting the application...");
                        exit = true; 
                        break;

                    default:
                        System.out.println("Invalid option! Please choose again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
    }
}
