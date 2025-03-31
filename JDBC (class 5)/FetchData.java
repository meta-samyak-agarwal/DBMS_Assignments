package testing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.*;

class FetchData {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/mydatabase";
        String username = "root";
        String password = "root";
        String filePath = "C:\\Users\\Samyak\\Desktop\\testing\\testing\\queries";

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();

            // Reading queries from the file
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String query;

            while ((query = br.readLine()) != null) {
                query = query.trim();

                if (query.isEmpty() || query.startsWith("--")) {
                    // Skip empty lines or comments
                    continue;
                }

                try {
                    // Handle batch insert specifically
                    if (query.toUpperCase().startsWith("INSERT")) {
                        PreparedStatement ps = connection.prepareStatement("INSERT INTO images (image_id, image_url, product_id) VALUES (?, ?, ?)");

                        for (int i = 1; i <= 5; i++) {
                            ps.setInt(1, 111 + i); // Example IDs: 101, 102, ...
                            ps.setString(2, "url" + i); // Example URLs: url1, url2, ...
                            ps.setInt(3, 1); // Example product ID: 1
                            ps.addBatch();
                        }

                        int[] rowsInserted = ps.executeBatch();
                        System.out.println("Batch insert completed. Rows inserted: " + rowsInserted.length);
                        ps.close();
                    } else if (query.toUpperCase().startsWith("SELECT")) {
                        // Handle SELECT statements
                        ResultSet resultSet = statement.executeQuery(query);
                        while (resultSet.next()) {
                            PrintStream out = System.out;
                            out.println(resultSet.getString(1) + " -> " + resultSet.getString(2) + " -> " + resultSet.getString(3));
                        }
                        resultSet.close();
                    } else {
                        // Handle other queries (UPDATE, DELETE)
                        int rowsAffected = statement.executeUpdate(query);
                        System.out.println("Rows affected: " + rowsAffected);
                    }
                } catch (SQLException ex) {
                    System.out.println("Error executing query: " + query);
                    System.out.println("SQL exception: " + ex);
                }
            }

            // Close resources
            br.close();
            statement.close();
            connection.close();

        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found: " + e);
        } catch (SQLException e) {
            System.out.println("SQL exception occurred: " + e);
        } catch (IOException e) {
            System.out.println("File read error: " + e);
        }
    }
}
