package blast;

import orffinder.ORF;
import org.biojava.nbio.core.sequence.io.util.IOUtils;
import org.biojava.nbio.ws.alignment.qblast.*;

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

    public void blastORFselection(List<ORF> orfs) {

//
//        props.setBlastProgram(BlastProgramEnum.blastp);
//        blastService.
    }

    /**
     * less generic, hardcoded blast-db combo methods
     * got this from here https://github.com/swappyk/biojava/blob/master/biojava-ws/src/main/java/demo/NCBIQBlastServiceDemo.java
     */
    public void blastn(String query) {

        queryProperties.setBlastProgram(BlastProgramEnum.blastn);
        queryProperties.setBlastDatabase("nt");

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
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            IOUtils.close(writer);
            IOUtils.close(reader);
            service.sendDeleteRequest(rid);
        }

    }

}
