package Database;

import java.sql.*;
import java.util.Properties;

public class Readtable {

    public static void main (String[] args)  throws Exception
    {
        //Host: hannl-hlo-bioinformatica-mysqlsrv.mysql.database.azure.com
        //Database: owe7_pg# (# vervangen door jouw projectgroepnr)
        //User: owe7_pg#@hannl-hlo-bioinformatica-mysqlsrv
        //Passw: blaat1234
        //Host: 3306
        // Initialize connection variables.
        String host = "hannl-hlo-bioinformatica-mysqlsrv.mysql.database.azure.com";
        String database = "owe7_pg1";
        String user = "owe7_pg1@hannl-hlo-bioinformatica-mysqlsrv";
        String password = "password1";
        int Host = 3306;

        Connection connection = null;

        // Initialize connection object
        try
        {
            String url = String.format("jdbc:mariadb://%s/%s", host, database);

            // Set connection properties.
            Properties properties = new Properties();
            properties.setProperty("user", user);
            properties.setProperty("password", password);
            properties.setProperty("useSSL", "true");
            properties.setProperty("verifyServerCertificate", "true");
            properties.setProperty("requireSSL", "false");

            // get connection
            connection = DriverManager.getConnection(url, properties);
        }
        catch (SQLException e)
        {
            throw new SQLException("Failed to create connection to database", e);
        }
        if (connection != null)
        {
            System.out.println("Successfully created connection to database.");

            // Perform some SQL queries over the connection.
            try
            {

                Statement statement = connection.createStatement();
                ResultSet results = statement.executeQuery("SELECT * from test;");
                while (results.next())
                {
                    String outputString =
                            String.format(
                                    "Data row = (%s, %s)",
                                    results.getString(1),
                                    results.getString(2));

                    System.out.println(outputString);
                }
            }
            catch (SQLException e)
            {
                throw new SQLException("Encountered an error when executing given sql statement", e);
            }
        }
        else {
            System.out.println("Failed to create connection to database.");
        }
        System.out.println("Execution finished.");
    }
}
