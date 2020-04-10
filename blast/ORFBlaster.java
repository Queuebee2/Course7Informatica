package blast;

import orffinder.ORF;
import org.biojava.nbio.core.sequence.io.util.IOUtils;
import org.biojava.nbio.ws.alignment.qblast.*;

import javax.swing.*;
import java.io.*;
import java.util.List;


/** todo SEPARATE to different thread (run blasts in background)
 *  - [ ] method to blasts ORFs
 *  - [ ] methods (elsewhere) to store blast results ( maybe a custom blastresult object )
 *  * [ ] (optional) implement independenlty --> A JFrame (extend) with some methods
 */


public  class  ORFBlaster  {


    boolean ORFBlasterMade = false;


    private static final String BLAST_OUTPUT_FILE = "blastOutput.xml";      // todo filenotfound error if subdirectory ( now outputs to project folder )

    NCBIQBlastService service = new NCBIQBlastService();
    NCBIQBlastAlignmentProperties queryProperties = new NCBIQBlastAlignmentProperties();
    NCBIQBlastOutputProperties outputProperties = new NCBIQBlastOutputProperties();


    public ORFBlaster()  {
        this("MJ.Lambers@student.han.nl");      // todo deprecate this
    }

    public ORFBlaster(String email)  {

        // todo implement email gui or something
        service.setEmail(email);
        /**
         * important todo/think about: singleton. Never should there ever be 2 blasts running at once
         */

    }

    

    public void blastORFselection(List<ORF> orfs, String algorithm, String database) {


        StringBuilder fastaString = new StringBuilder();
        for ( ORF orf : orfs ) {
            fastaString.append(orf.toFastaFormat());
        }

        blast(fastaString.toString(), algorithm, database);

    }

    /**
     * less generic, hardcoded blast-db combo methods
     * got this from here https://github.com/swappyk/biojava/blob/master/biojava-ws/src/main/java/demo/NCBIQBlastServiceDemo.java
     */
    public void blast(String query, String algorithm, String database) {

        queryProperties.setBlastProgram(BlastProgramEnum.valueOf(algorithm));   //todo een beetje kan hier fout gaan als niet altijd comobox
        queryProperties.setBlastDatabase(database);                             //todo vanalles kan hier fout gaan bij verkeerde input

        outputProperties.setOutputFormat(BlastOutputFormatEnum.XML);

        String rid = null;
        FileWriter writer = null;
        BufferedReader reader = null;

        try {
            // send blast request and save request id
            rid = service.sendAlignmentRequest(query, queryProperties);

            while (!service.isReady(rid)) {
                System.out.println("Waiting for results. Sleeping for 25 seconds");
                Thread.sleep(25000);
            }

            // read results when they are ready
            InputStream in = service.getAlignmentResults(rid, outputProperties);
            reader = new BufferedReader(new InputStreamReader(in));

            File f = new File(BLAST_OUTPUT_FILE);
            System.out.println("Saving query results in file " + f.getAbsolutePath());
            writer = new FileWriter(f);

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line + System.getProperty("line.separator"));
            }
        } catch (Exception e) {
            int optionType = JOptionPane.DEFAULT_OPTION; // YES+NO+CANCEL
            int messageType = JOptionPane.PLAIN_MESSAGE; // no standard icon
            ImageIcon icon = new ImageIcon("src/main/resources/idkwhy.gif", "blob");
            int res = JOptionPane.showConfirmDialog(null, "Could'nt connect to BLAST \nWe don't know why\nError Message : 'Unable to retrieve request ID'", "No connection possible",
                    optionType, messageType, icon);
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            IOUtils.close(writer);
            IOUtils.close(reader);
            service.sendDeleteRequest(rid);
        }

    }

}
