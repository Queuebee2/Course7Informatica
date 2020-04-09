package Database;


import orffinder.ORF;

import java.sql.*;
import java.util.HashMap;
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
    private HashMap<ORF,String> Selected_ORFs;

    public DatabaseManager(HashMap selected_ORF_list) throws SQLException {
        Selected_ORFs = selected_ORF_list;
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
        for(ORF orf : Selected_ORFs.keySet()){
            // all id are auto increment
            String ORFtable_ORF_sequence = Selected_ORFs.get(orf);
            int ORFtable_start = (int) orf.getCounterStart();
            int ORFtable_stop = (int) orf.counterEnd;

            String Sequencetable_header = orf.parentFastaSequence.header;
            String Sequencetable_filename = orf.parentFastaSequence.filename;
            int Sequencetable_orfs_found = orf.parentFastaSequence.completedOrfCount;
            int Sequencetable_length = (int) orf.parentFastaSequence.RealSize;

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO sequence (header,filename,orfs_found,total_length) VALUES (?, ?, ?, ?);");
            preparedStatement.setString(1, Sequencetable_header);
            preparedStatement.setString(2, "wtf"); // TODO: 9-4-2020 file gives null ?? heb het even veranderd om verdere foutmeldingen te vinden 
            preparedStatement.setInt(3, Sequencetable_orfs_found);
            preparedStatement.setInt(4, Sequencetable_length);
            nRowsInserted += preparedStatement.executeUpdate();

            int last_id = Integer.parseInt(getlatestid()); // TODO: 9-4-2020 make this work 

            PreparedStatement preparedStatement2 = connection.prepareStatement("INSERT INTO ORF (Sequence_id, start_position, stop_position, ORF_Sequence ) VALUES (?, ?, ?, ?);");
            preparedStatement2.setInt(1, last_id);
            preparedStatement2.setInt(2, ORFtable_start);
            preparedStatement2.setInt(3, ORFtable_stop);
            preparedStatement2.setString(4, ORFtable_ORF_sequence);
            nRowsInserted += preparedStatement2.executeUpdate();


        }

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
    public String getlatestid() throws SQLException {
        String outputString = "";
        try {

            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT max(id) FROM sequence ");
            while (results.next()) {
                outputString =
                        String.format(
                                "%s",
                                results.getInt(1));

            }
        } catch (SQLException e) {
            throw new SQLException("Encountered an error when executing given sql statement", e);
        }
        System.out.println(outputString);
        return outputString;
    }
}

