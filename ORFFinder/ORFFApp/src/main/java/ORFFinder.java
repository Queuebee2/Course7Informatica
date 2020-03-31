import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

// try -Xms512M -Xmx512M in VM options

/**
 * ORFFinder reads through a textfile (assumed format: nucleotide FASTA) to find ORFS
 */
public class ORFFinder {

    static String filename_RELATIVE_TEMP = "data/DNA.txt";
    private final ArrayList<Sequence> sequences = new ArrayList<Sequence>(100);

    /**
     * constructor
     */
    public ORFFinder() {


    }

    /**
     * main (for testing...)
     */
    public static void main(String[] args) {
        // test ORFFinder
        ORFFinder orfFinder = new ORFFinder();
        // read the default input
        orfFinder.readAndFindORFs();

        orfFinder.seeDNA();

    }

    /**
     * ASCII TABLE (for reference)
     * using ordinal values as keys maybe? later?: ATG 658471, TAG 846571, TAA 846565, TGA 847165 (changed use of hashmaps to arraylist for now)
     * chars of interest and their ASCII values: (65, A) (84, T) (67, C) (71, G) (62, >)
     */
    public void readAndFindORFs() {
        readAndFindORFs(filename_RELATIVE_TEMP);
    }

    public void readAndFindORFs(String filename) {
        long startTime = System.nanoTime();

        int[] last = new int[3];
        int trackedATGs = 0;
        int currentSequenceID = 0;
        int currentPos = 0;
        Sequence currentSequence = null;
        boolean isStopCodon;

        int c;

        try (BufferedReader reader = Files.newBufferedReader(
                Path.of(filename))) {

            while ((c = reader.read()) != -1) {
                // iterate over character (int) in the file...
                switch (c) {
                    case (62):  // ASCII 62 == >
                        double dnaStartTime = System.nanoTime();
                        String header = reader.readLine(); // Save the header
                        currentSequenceID++;
                        currentPos = 0;
                        if (currentSequence != null) {
                            // testing

                            System.out.print(currentSequence);
                            System.out.println(header); // TODO REMOVE ??
                            long dnaEndTime = System.nanoTime();
                            double duration = (dnaEndTime - dnaStartTime);
                            BigDecimal planckseconds = BigDecimal.valueOf((539124760000000000000000000000000000000000000.0 * duration * 0.000000001));
                            System.out.println(". ERIC, WOW, THAT ONLY TOOK " + planckseconds.toPlainString() + " planckseconds!!!!!!!!!!!!!!!");
                            int totalThisSequence = currentSequence.getCompletedORFCount();
                            Sequence.addTotalCompletedORFCount(totalThisSequence);
                        } else {
                            System.out.println(header);
                        }
                        currentSequence = new Sequence(currentSequenceID);
                        sequences.add(currentSequence);
                        break;
                    case (65):  // ASCII 65 == A
                    case (67):  // ASCII 67 == C
                    case (71):  // ASCII 71 == G
                    case (84):  // ASCII 84 == T            // TODO enhancement add 'U' ?
                        currentPos++;
                        last[0] = last[1];
                        last[1] = last[2];
                        last[2] = c;
                        switch (Integer.toString(last[0]) + last[1] + last[2]) {
                            case "846571":  // TAG
                            case "846565":  // TAA
                            case "847165":  // TGA
                                // System.out.println("is stop codon true");  // TODO DEBUGPRINT
                                isStopCodon = true;
                                break;
                            case "658471":  // ATG
                                trackedATGs++;
                                // continue to default
                                ORF orf = new ORF(currentPos, currentSequence);
                                currentSequence.addNewORF(orf);
                            default:
                                // System.out.println("is stop codon false");   // TODO DEBUGPRINT
                                isStopCodon = false;
                                break;
                        }
                        currentSequence.feedActiveORFs(c, isStopCodon);

                        break;
                    default:
                        //dostuff
                        break;
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(currentSequence);  // last sequence

        System.out.println("found " + trackedATGs + " occurences of ATG");
        System.out.println("of which " + Sequence.getTotalCompletedCount() + " Complete ORFs");
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        double planckseconds = 5.39124760e44 * duration;
        System.out.println("Duration : " + planckseconds + " planckseconds");
        System.out.println("Duration : " + duration + " nanoseconds");
        long mseconds = duration / 1000000;
        System.out.println("Duration : " + mseconds + " milliseconds");
        long seconds = mseconds / 1000;
        System.out.println("Duration : " + seconds + " seconds");
    }

    public void seeDNA() {
        for (Sequence sequence : sequences) {
            for (ORF orf : sequence) {
                System.out.println(orf);
                System.out.println(orf.getDnaSequence());
            }
        }
    }

    public void testReadAndCountSpeed(int amount) {
        for (int i = 0; i < amount + 1; i++) {
            long startTime = System.nanoTime();
            long counts = readandcount(i * 10);
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);
            System.out.print("with looping " + counts + " times per char, counting, I counted " + counts + " times  |  ");
            System.out.println("Duration : " + duration / 1000000 + " milliseconds");
        }
    }

    public long readandcount(int counts) {

        long countable = 0;

        try (BufferedReader reader = Files.newBufferedReader(Path.of(filename_RELATIVE_TEMP))) {
            int c;
            while ((c = reader.read()) != -1) {

                for (int i = 0; i < counts + 1; i++) {
                    countable++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return countable;

    }

}


// raise Exception.Create('hell');
