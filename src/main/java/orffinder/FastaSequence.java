package orffinder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * FastaSequence (named this way because of possible collision with the biojava Sequence object)
 * holds information about the position, size, header and filename of sequences in a fasta file.
 * When orfs are found it also keeps a list of ORF objects that can be turned into fasta
 */
public class FastaSequence implements Iterable<ORF>, Serializable {


    public final String header;
    private String fastaFilename;
    private int SequenceID;
    private static int totalSequencesMade = 0;
    final long lineNumber;
    final long offset;      // in file
    long EndPos;
    public long RealSize;
    private ORFFinder finder;

    private  ArrayList<ORF> ORFList = new ArrayList<ORF>();
    private  ArrayList<ORF>[] ORFTrackers =  new ArrayList[3];
    public int completedOrfCount = ORFList.size();


    public FastaSequence(ORFFinder finder, String filename, String currHeader, int currentTextLine, int position) {
        this.SequenceID = totalSequencesMade++;
        this.fastaFilename = filename;
        this.finder = finder;
        header = currHeader;
        lineNumber = currentTextLine;
        offset = position; // in file
        ORFTrackers[0]= new ArrayList<ORF>();
        ORFTrackers[1]= new ArrayList<ORF>();
        ORFTrackers[2]= new ArrayList<ORF>();
    }

    /**
     * start orf because a start codon has been encountered
     * @param position      current absolute position in file from reader on which the orf starts
     * @param charCounter   character counter, counting from start of sequence
     * @param frame
     */
    public void addNewORF(int position, int charCounter, int frame) {
        ORFTrackers[frame].add(new ORF(position, charCounter, this));
    }

    /**
     * TODO : add customizable minimum length
     * end all the orfs in the current frame, because a stop codon has been encountered
     * @param endPos        current absolute position in  file from reader on which the orf ends (-1)
     * @param charCounter   character counter, counting fromstart of sequence
     * @param frame         current frame
     */
    public void updateORFs(long endPos, long charCounter, int frame) {
        assert (frame >= 0 && frame <= 2) : "frame CANT BE < 0 or > 2";        // asserts are off in compiled product
        ArrayList<ORF> tracker = ORFTrackers[frame];        // get the list corresponding to the frame
        if (tracker.size() > 0) {
            for ( ORF orf : tracker ) {
                orf.endpos = endPos + 1;                    // account for last character
                orf.counterEnd = charCounter;
            }
            ORFList.addAll(tracker);
            tracker.clear();
        }
    }

    /**
     * @return The size of the sequence, by length in nucleotides
     */
    public long getRealSize() {
        return RealSize;
    }

    public ORFFinder getORFFinder() {
        return finder;
    }

    public String getFilename() {
        return fastaFilename;
    }


    /**
     *
     * @return the ORFs in this sequence in an object to be iterated over.
     */
    @Override
    public Iterator<ORF> iterator() {
        return ORFList.iterator();
    }

    public ArrayList<String[]> makeTable_list(){
        ArrayList<String[]> tablelist = new ArrayList<String[]>();
        int orfIdMaker = 0;
        for(ORF orf:ORFList){
            String[] orfvalue = new String[5];
            orfvalue[0] = String.valueOf(SequenceID);
            orfvalue[1] = String.valueOf(orf.getStartPosInFile());
            orfvalue[2] = String.valueOf(orf.counterEnd);
            orfvalue[3] = String.valueOf(orf.getSize());
            orfvalue[4] = String.valueOf(orf.getID());
            tablelist.add(orfvalue);
        }
        return tablelist;
    }

    /**
     * Print sequence statistics including statistics for first 10 ORFs
     * mostly for debug purposes but can be used for statistics if expanded.
     * @return
     */
    public String getStatistics() {
        long totalOrfLength = 0;
        long averageOrfLength = 0;
        long shortestOrfLength= 0;
        long longestOrfLength= 0;
         completedOrfCount = 0;
        int incompleteOrfCount = 0;

        long size;

        for (ArrayList<ORF> tracker : ORFTrackers) {
            incompleteOrfCount += tracker.size();
        }

        for (ORF  orf: ORFList ) {
            completedOrfCount++;
            size =  orf.getSize();
           totalOrfLength += size;

            if (orf.getEndpos() != 0 && size < shortestOrfLength) {
                shortestOrfLength = size;
            }
            if (size > longestOrfLength) {
                longestOrfLength = size;
            }
        }
        averageOrfLength = totalOrfLength / completedOrfCount;

        StringBuilder statistics = new StringBuilder("DNA header=");
        statistics.append(header);
        statistics.append("{ID=").append(SequenceID);
        statistics.append(" StartPos=").append(offset);
        statistics.append(" EndPos=").append(EndPos);
        statistics.append(" CalculatedSize=").append(EndPos - offset);
        statistics.append(" RealSize=").append(RealSize);


        for (int i = 0; i < 10; i++) {
            ORF o = ORFList.get(i);
            statistics.append("\n\torf startpos=").append(o.getOffset());
            statistics.append(" endpos=").append(o.endpos);
            statistics.append(" RealSize=").append(o.getSize());
            statistics.append(" StartCounter=").append(o.getEndPosInSequence());
            statistics.append(" EndCounter=").append(o.counterEnd);
            statistics.append(" ID: ").append(o.getID());
            statistics.append(" PARENT: ").append(o.getParentFastaSequence());

        }

        statistics.append(" totalOrfLength=").append(totalOrfLength);
        statistics.append(" averageOrfLength=").append(averageOrfLength);
        statistics.append(" longestOrfLength=").append(longestOrfLength);
        statistics.append(" shortestOrfLength=").append(shortestOrfLength);
        statistics.append(" incompleteOrfCount=").append(incompleteOrfCount);
        statistics.append(" completedOrfCount=").append(completedOrfCount);
        statistics.append("}");

        System.out.println(statistics.toString());
        return statistics.toString();

    }
}

