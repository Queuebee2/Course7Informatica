package Database;


import orffinder.FastaSequence;
import orffinder.ORF;

import java.sql.*;
import java.util.ArrayList;
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


    /**
     * TODO translate to java
     *  make hashmap
     *  for orf in selectedOrfs.keys():
     *      if orf.parent in hashmap.keys():
     *         arraylist = hashmap.get(parent)
     *         arraylist.add(orf)
     *      else
     *         make new arralist
     *         arraylist.add(orf)
     *         hashmap.put(orf.parent, arraylist)
     */

    /**
     *
     * @param selected_ORF_list
     * @throws SQLException
     */
    public void insert(HashMap<ORF,String> selected_ORF_list) throws SQLException {

        Selected_ORFs = selected_ORF_list;
        ArrayList<FastaSequence> seqlist = new ArrayList<>();
        int nRowsInserted = 0;
        System.out.println("amount of orfs selected: " + Selected_ORFs.size());

      for(ORF orf : Selected_ORFs.keySet()){
          FastaSequence seq = orf.getParentFastaSequence();           //todo ? problem: if multiple sequences, what do we do?
          if (!seqlist.contains(seq)){
                seqlist.add(seq);                                                 // todo: sort selected orfs into buckets of Sequence:<orflist>
          }
          }

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
          nRowsInserted += preparedStatement.executeUpdate();
      }
        for(ORF orf : Selected_ORFs.keySet()){
            // all id are auto increment
            String ORFtable_ORF_sequence = Selected_ORFs.get(orf);
            int ORFtable_start = orf.getStartPosInSequence();
            int ORFtable_stop = (int) orf.counterEnd;


            int last_id = Integer.parseInt(getlatestid()); // TODO: 9-4-2020 make this work     // TODO TRY getLatestIdByHeader (new method, pass seq or header&filename)

            PreparedStatement preparedStatement2 = connection.prepareStatement(
                    "INSERT INTO ORF " +
                            "(Sequence_id, start_position, stop_position, ORF_Sequence ) " +
                    "VALUES (?, ?, ?, ?);");

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
            System.out.println(String.format("Updated %d row(s) of data.", nRowsUpdated));      // todo 12 ORFS -> 16 rows???  4 -> 8

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
            throw new SQLException("getlatestid: Encountered an error when executing given sql statement", e);
        }
        System.out.println(outputString);
        return outputString;
    }


    /**
     * get the latest ID of a sequence from database based on header and filename
     * @param sequence
     * @return
     */
    public String getLatestIdByHeader(FastaSequence sequence) throws SQLException {
        return getLatestIdByHeader(sequence.header, sequence.getFilename());
    }

    public String getLatestIdByHeader(String sequenceHeader, String sequenceFilename) throws SQLException {
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

