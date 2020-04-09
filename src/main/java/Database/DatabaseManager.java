package Database;


import java.sql.*;
import java.util.Properties;

/**
 *
 * Todo : everything
 * plan for now : implement Java DB? (??) -- embedded derby (only needs JDK, no server)
 *      stuff needed for plan:
 *          - A DB setup script
 *          - methods to query db
 */
public class DatabaseManager {
    private Connection connection;

    public DatabaseManager() throws SQLException {
        makeConnection();
    }

    public void makeConnection() throws SQLException {
        connection = null;

        // Initialize connection object
        try {
            String host = "hannl-hlo-bioinformatica-mysqlsrv.mysql.database.azure.com";
            String database = "owe7_pg1";
            String url = String.format("jdbc:mariadb://%s/%s", host, database);

            // Set connection properties.
            Properties properties = new Properties();
            String user = "owe7_pg1@hannl-hlo-bioinformatica-mysqlsrv";
            properties.setProperty("user", user);
            String password = "password1";
            properties.setProperty("password", password);
            properties.setProperty("useSSL", "true");
            properties.setProperty("verifyServerCertificate", "true");
            properties.setProperty("requireSSL", "false");

            // get connection
            connection = DriverManager.getConnection(url, properties);
        } catch (SQLException e) {
            throw new SQLException("Failed to create connection to database", e);
        }
        if (connection != null) {
            System.out.println("Successfully created connection to database.");
        }
        else{
            System.out.println("Failed to create connection to database.");
        }
    }
    public void insert() throws SQLException {
        int nRowsInserted = 0;
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO test (name, quantity) VALUES (?, ?);");
        preparedStatement.setString(1, "banana");
        preparedStatement.setInt(2, 150);
        nRowsInserted += preparedStatement.executeUpdate();

        preparedStatement.setString(1, "orange");
        preparedStatement.setInt(2, 154);
        nRowsInserted += preparedStatement.executeUpdate();

        preparedStatement.setString(1, "apple");
        preparedStatement.setInt(2, 100);
        nRowsInserted += preparedStatement.executeUpdate();
        System.out.println(String.format("Inserted %d row(s) of data.", nRowsInserted));
    }
    public void download() throws SQLException {
        try {

            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT * from test;");
            while (results.next()) {
                String outputString =
                        String.format(
                                "Data row = (%s, %s)",
                                results.getString(1),
                                results.getString(2));

                System.out.println(outputString);
            }
        } catch (SQLException e) {
            throw new SQLException("Encountered an error when executing given sql statement", e);
        }
        System.out.println("Execution finished.");
    }
    public void update() throws SQLException {
        try
        {
            // Modify some data in table.
            int nRowsUpdated = 0;
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE test SET quantity = ? WHERE name = ?;");
            preparedStatement.setInt(1, 200);
            preparedStatement.setString(2, "banana");
            nRowsUpdated += preparedStatement.executeUpdate();
            System.out.println(String.format("Updated %d row(s) of data.", nRowsUpdated));

            // NOTE No need to commit all changes to database, as auto-commit is enabled by default.
        }
        catch (SQLException e)
        {
            throw new SQLException("Encountered an error when executing given sql statement.", e);
        }
        System.out.println("Execution finished.");
    }
}

