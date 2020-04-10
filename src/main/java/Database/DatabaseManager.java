package Database;


import orffinder.FastaSequence;
import orffinder.ORF;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 DatabaseManager manages the connection to the database as well as inserts, downloads and updates
 */
public class DatabaseManager {
    // connection to SQL database
    private Connection connection;

    /**
     * Constructor of Database manager makes the connection, if no internet gives a pop up
     * @throws SQLException Something went wrong with the server
     */
    public DatabaseManager() throws SQLException {
        try {
            makeConnection();
        } catch (SQLException e) {
            int optionType = JOptionPane.DEFAULT_OPTION;
            int messageType = JOptionPane.PLAIN_MESSAGE;
            ImageIcon icon = new ImageIcon("src/main/resources/eror.gif", "blob");
            int res = JOptionPane.showConfirmDialog(null, "Could'nt connect to database \nPlease check your internet connection", "No internet connection",
                    optionType, messageType, icon);
            e.printStackTrace();
        }
    }

    /**
     * Makes a connection to the database
     * @throws SQLException something went wrong while making the connection
     */
    private void makeConnection() throws SQLException {
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

    /**
     * Inserts the data provided into the MY SQL database
     * @param selected_ORF_list map with the selected orfs as key and their sequence as value
     * @throws SQLException something went wrong during the insert
     */
    public void insert(HashMap<ORF,String> selected_ORF_list) throws SQLException {
        // arraylist with the sequece objects
        ArrayList<FastaSequence> seqlist = new ArrayList<>();
        // amount of rows inserted
        int nRowsInserted = 0;

        // go through all orfs and get their parent sequence
      for(ORF orf : selected_ORF_list.keySet()){
          FastaSequence seq = orf.getParentFastaSequence();
          // if sequence object not yet in seqlist add it to seqlist
          if (!seqlist.contains(seq)){
                seqlist.add(seq);                                                 // todo: sort selected orfs into buckets of Sequence:<orflist>
          }
          }
        // insert all sequences in seqlist
      for (FastaSequence seq : seqlist){
          String Sequencetable_header = seq.header;
          String Sequencetable_filename = seq.getFilename();
          int Sequencetable_orfs_found = seq.completedOrfCount;
          int Sequencetable_length = (int) seq.RealSize;

          PreparedStatement preparedStatement = connection.prepareStatement(
                  "INSERT INTO sequence " +
                          "(header,filename,orfs_found,total_length) " +
                          "VALUES (?, ?, ?, ?);");
          preparedStatement.setString(1, Sequencetable_header);
          preparedStatement.setString(2, Sequencetable_filename); // TODO: 9-4-2020 file gives null ?? heb het even veranderd om verdere foutmeldingen te vinden
          preparedStatement.setInt(3, Sequencetable_orfs_found);
          preparedStatement.setInt(4, Sequencetable_length);
          //add to total of rows inserted
          nRowsInserted += preparedStatement.executeUpdate();
      }
      // insert all orfs into the database with the right parent sequence id
        for(ORF orf : selected_ORF_list.keySet()){

            String ORFtable_ORF_sequence = selected_ORF_list.get(orf);
            int ORFtable_start = orf.getStartPosInSequence();
            int ORFtable_stop = (int) orf.counterEnd;
            // get the latest inserted id in sequence table
            int last_id = Integer.parseInt(getlatestid()); // TODO: 9-4-2020 make this work     // TODO TRY getLatestIdByHeader (new method, pass seq or header&filename)

            PreparedStatement preparedStatement2 = connection.prepareStatement(
                    "INSERT INTO ORF " +
                            "(Sequence_id, start_position, stop_position, ORF_Sequence ) " +
                    "VALUES (?, ?, ?, ?);");
            // prepare teh statement
            preparedStatement2.setInt(1, last_id);
            preparedStatement2.setInt(2, ORFtable_start);
            preparedStatement2.setInt(3, ORFtable_stop);
            preparedStatement2.setString(4, ORFtable_ORF_sequence);
            //execute the update
            nRowsInserted += preparedStatement2.executeUpdate();


        }

        System.out.println(String.format("Inserted %d row(s) of data.", nRowsInserted));
    }

    /**
     * Gets the latest ID inserted into the Sequence table
     * @return the latest ID
     * @throws SQLException problem with getting information from database
     */
    private String getlatestid() throws SQLException {
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
            throw new SQLException("getlatestid: Encountered an error when executing given sql statement", e);
        }
        System.out.println(outputString);
        return outputString;
    }


    /**
     * get the latest ID of a sequence from database based on header and filename
     * @param sequence Fasta Sequence object
     * @return String with the latest ID
     */
    public String getLatestIdByHeader(FastaSequence sequence) throws SQLException {
        return getLatestIdByHeader(sequence.header, sequence.getFilename());
    }

    private String getLatestIdByHeader(String sequenceHeader, String sequenceFilename) throws SQLException {
        String outputString = "";
        try {
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(
                    "select max(id) " +
                    "from Sequence " +
                    "where header like '%'"+sequenceHeader+"'%' " +
                    "and filename like '%'"+sequenceFilename+"'%';");

            while (results.next()) {
                outputString =
                        String.format(
                                "%s",
                                results.getInt(1));

            }
        } catch (SQLException e) {
            throw new SQLException("getLatestIdByHeader: Encountered an error when executing given sql statement", e);
        }
        System.out.println(outputString);
        return outputString;
    }

}

