package Controller;

import Model.History;
import Model.Logs;
import Model.Product;
import Model.User;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import java.sql.SQLException;
import java.io.File;


public class SQLite {
    public int DEBUG_MODE = 0;
    private Connection conn;
    String driverURL;
    public MemoryTimeout timeoutTracker = MemoryTimeout.getInstance();
    
    public SQLite() {
        // Resolve relative path to project root where database.db is located
        File dbFile = new File(System.getProperty("user.dir"), "database.db"); 
        driverURL = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            }

    public void connect() {
        try {
            conn = DriverManager.getConnection(driverURL);
        } catch (SQLException e) {
            System.err.println("An error occurred while processing your request. Please try again.");
        }
    }

    public void close() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return conn;
    }
    
    public void createNewDatabase() {
        try (Connection conn = DriverManager.getConnection(driverURL)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("Database database.db created.");
            }
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void createHistoryTable() {
        String sql = "CREATE TABLE IF NOT EXISTS history (\n"
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + " username TEXT NOT NULL,\n"
            + " name TEXT NOT NULL,\n"
            + " stock INTEGER DEFAULT 0,\n"
            + " timestamp TEXT NOT NULL\n"
            + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table history in database.db created.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void createLogsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS logs (\n"
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + " event TEXT NOT NULL,\n"
            + " username TEXT NOT NULL,\n"
            + " desc TEXT NOT NULL,\n"
            + " timestamp TEXT NOT NULL\n"
            + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table logs in database.db created.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
     
    public void createProductTable() {
        String sql = "CREATE TABLE IF NOT EXISTS product (\n"
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + " name TEXT NOT NULL UNIQUE,\n"
            + " stock INTEGER DEFAULT 0,\n"
            + " price REAL DEFAULT 0.00\n"
            + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table product in database.db created.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
     
    public void createUserTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (\n"
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + " username TEXT NOT NULL UNIQUE,\n"
            + " password TEXT NOT NULL,\n"
            + " role INTEGER DEFAULT 2,\n"
            + " locked INTEGER DEFAULT 0\n"
            + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table users in database.db created.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void dropHistoryTable() {
        String sql = "DROP TABLE IF EXISTS history;";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table history in database.db dropped.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void dropLogsTable() {
        String sql = "DROP TABLE IF EXISTS logs;";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table logs in database.db dropped.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void dropProductTable() {
        String sql = "DROP TABLE IF EXISTS product;";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table product in database.db dropped.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void dropUserTable() {
        String sql = "DROP TABLE IF EXISTS users;";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table users in database.db dropped.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void addHistory(String username, String name, int stock, String timestamp) {
        String sql = "INSERT INTO history(username,name,stock,timestamp) VALUES('" + username + "','" + name + "','" + stock + "','" + timestamp + "')";
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()){
            stmt.execute(sql);
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void addLogs(String event, String username, String desc, String timestamp) {
        String sql = "INSERT INTO logs(event, username, desc, timestamp) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(driverURL);
        java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
            pstmt.setString(1, event);
            pstmt.setString(2, username);
            pstmt.setString(3, desc);
            pstmt.setString(4, timestamp);
            
            pstmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace(); // show full error, not just print
        }   
    }
    
    public boolean addProduct(String name, int stock, double price) {
    String sql = "INSERT INTO product(name, stock, price) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(driverURL);
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, stock);
            pstmt.setDouble(3, price);
            pstmt.executeUpdate();
            return true;

        } catch (Exception ex) {
            System.err.println("Failed to add product: " + ex.getMessage());
            return false;
        }
    }

    public boolean editProduct(String originalName, String newName, int stock, double price) {
        String sql = "UPDATE product SET name = ?, stock = ?, price = ? WHERE name = ?";

        try (Connection conn = DriverManager.getConnection(driverURL);
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newName);
            pstmt.setInt(2, stock);
            pstmt.setDouble(3, price);
            pstmt.setString(4, originalName);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (Exception ex) {
            System.err.println("Failed to edit product: " + ex.getMessage());
            return false;
        }
    }

    public boolean deleteProduct(String name) {
        String sql = "DELETE FROM product WHERE name = ?";

        try (Connection conn = DriverManager.getConnection(driverURL);
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception ex) {
            System.err.println("Failed to delete product: " + ex.getMessage());
            return false;
        }
    }

    
//    public void addUser(String username, String password) {
//        String sql = "INSERT INTO users(username,password) VALUES('" + username + "','" + password + "')";
//        
//        try (Connection conn = DriverManager.getConnection(driverURL);
//            Statement stmt = conn.createStatement()){
//            stmt.execute(sql);
//            
////      PREPARED STATEMENT EXAMPLE
////      String sql = "INSERT INTO users(username,password) VALUES(?,?)";
////      PreparedStatement pstmt = conn.prepareStatement(sql)) {
////      pstmt.setString(1, username);
////      pstmt.setString(2, password);
////      pstmt.executeUpdate();
//        } catch (Exception ex) {
//            System.out.print(ex);
//        }
//    }
    
    
    public ArrayList<History> getHistory(){
        String sql = "SELECT id, username, name, stock, timestamp FROM history";
        ArrayList<History> histories = new ArrayList<History>();
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                histories.add(new History(rs.getInt("id"),
                                   rs.getString("username"),
                                   rs.getString("name"),
                                   rs.getInt("stock"),
                                   rs.getString("timestamp")));
            }
        } catch (Exception ex) {
            System.out.print(ex);
        }
        return histories;
    }
    
    public ArrayList<Logs> getLogs(){
        String sql = "SELECT id, event, username, desc, timestamp FROM logs";
        ArrayList<Logs> logs = new ArrayList<Logs>();
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                logs.add(new Logs(rs.getInt("id"),
                                   rs.getString("event"),
                                   rs.getString("username"),
                                   rs.getString("desc"),
                                   rs.getString("timestamp")));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return logs;
    }
    
    public ArrayList<Product> getProduct(){
        String sql = "SELECT id, name, stock, price FROM product";
        ArrayList<Product> products = new ArrayList<Product>();
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                products.add(new Product(rs.getInt("id"),
                                   rs.getString("name"),
                                   rs.getInt("stock"),
                                   rs.getFloat("price")));
            }
        } catch (Exception ex) {
            System.out.print(ex);
        }
        return products;
    }
    
    public ArrayList<User> getUsers(){
        String sql = "SELECT id, username, password, role, locked FROM users";
        ArrayList<User> users = new ArrayList<User>();
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                users.add(new User(rs.getInt("id"),
                                   rs.getString("username"),
                                   rs.getString("password"),
                                   rs.getInt("role"),
                                   rs.getInt("locked")));
            }
        } catch (Exception ex) {}
        return users;
    }

    public boolean addUser(String username, String rawPassword, int role) {
        // Hash password before storing
        String hashedPassword = HashPassword.hashPassword(rawPassword);

        String sql = "INSERT INTO users(username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(driverURL);
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setInt(3, role);
            pstmt.executeUpdate();
            return true;

        } catch (Exception ex) {
            System.out.print(ex);
            return false;
        }
    }


    public String validatePassword(String password, String username) {
        List<String> errors = new ArrayList<>();

        if (password.length() < 8 || password.length() > 64) {
            errors.add("• Password must be between 8 and 64 characters.");
        }

        if (password.toLowerCase().contains(username.toLowerCase())) {
            errors.add("• Password should not contain your username.");
        }

        boolean hasLower = false;
        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }

        if (!hasLower) errors.add("• Must contain at least one lowercase letter.");
        if (!hasUpper) errors.add("• Must contain at least one uppercase letter.");
        if (!hasDigit) errors.add("• Must contain at least one number.");
        if (!hasSpecial) errors.add("• Must contain at least one special character.");

        if (errors.isEmpty()) return null;
        return String.join("\n", errors);
    }

    public boolean registerUser(String username, String password, String confirmPassword) {
        if (username == null || password == null || confirmPassword == null) {
            JOptionPane.showMessageDialog(null, "Fields cannot be null.");
            return false;
        }

        username = username.trim();
        password = password.trim();
        confirmPassword = confirmPassword.trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(null, "All fields are required.");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(null, "Passwords do not match.");
            return false;
        }

        if (usernameExists(username)) {
            JOptionPane.showMessageDialog(null, "Username already exists.");
            return false;
        }

        String passwordValidationMessage = validatePassword(password, username);
        if (passwordValidationMessage != null) {
            JOptionPane.showMessageDialog(null, "Registration failed: " + passwordValidationMessage);
            return false;
        }

        String hashedPassword = HashPassword.hashPassword(password);
        addUser(username, hashedPassword, 2); // Default role
        return true;
    }
    

    public void removeUser(String username) {
        // issue: injection risk
        // String sql = "DELETE FROM users WHERE username='" + username + "';";
        String sql = "DELETE FROM users WHERE username=?";

        try (Connection conn = DriverManager.getConnection(driverURL);
            
            //Statement stmt = conn.createStatement()) {
            //stmt.execute(sql);
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
            pstmt.setString(1, username);
            pstmt.executeUpdate();
            
            System.out.println("User " + username + " has been deleted.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }

    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(driverURL);
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;  // returns true if username already exists
            }
        } catch (Exception ex) {
            System.out.print(ex);
        }
        return false;
    }
    
    public Product getProduct(String name){
        // issue: injection risk
        // String sql = "SELECT name, stock, price FROM product WHERE name='" + name + "';";
        String sql = "SELECT name, stock, price FROM product WHERE name = ?";
        Product product = null;
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            //Statement stmt = conn.createStatement();
            //ResultSet rs = stmt.executeQuery(sql)){
       
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    product = new Product(
                        rs.getString("name"),
                        rs.getInt("stock"),
                        rs.getFloat("price")
                    );
                }
            }
            
        } catch (Exception ex) {
            System.out.print(ex);
        }
        return product;
    }
    
    public User validateLogin(String username, String passwordPlaintext) {

        if (timeoutTracker.isLocked(username)) {
            JOptionPane.showMessageDialog(null, "Account locked due to too many failed login attempts. Please try again later.");
            return null;
        } // login failed due to rate limiting

        String sql = "SELECT id, username, password, role, locked FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(driverURL);
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                String hashedInput = HashPassword.hashPassword(passwordPlaintext);

                if (storedHash.equals(hashedInput)) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        storedHash,
                        rs.getInt("role"),
                        rs.getInt("locked")
                    );
                }
            }
        } catch (Exception ex) {
            System.out.print(ex);
        }

        return null; // login failed
    }

    public boolean productExists(String name) {
        String sql = "SELECT 1 FROM product WHERE name = ? LIMIT 1";
        try (Connection conn = DriverManager.getConnection(driverURL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // true if product exists
        } catch (Exception ex) {
            System.out.println("Error checking product existence: " + ex);
            return false;
        }
    }
    
}