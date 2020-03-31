import com.sun.source.tree.LiteralTree;

import java.util.ArrayList;
import java.util.Iterator;

public class Sequence implements Iterable<ORF> {


    static int totalCompletedORFs = 0;

    ArrayList<ORF> activeORFMap;
    ArrayList<ORF> completedORFMap;
    int sequenceID;
    boolean isActive; // is the baby active or not
    private int longestORF = 0;
    private int totalORFLength = 0;
    private int shortestORF = 100000000;

    public Sequence(int sequenceID) {
        this.sequenceID = sequenceID;
        activeORFMap = new ArrayList<ORF>();
        completedORFMap = new ArrayList<ORF>();
    }

    public static void addTotalCompletedORFCount(int amount) {// todo rename
        totalCompletedORFs += amount; // todo rename

    }

    public static int getTotalCompletedCount() {// todo rename
        return totalCompletedORFs; // todo rename
    }

    public void addNewORF(ORF orf) {
        activeORFMap.add(orf);

    }

    public void feedActiveORFs(int c, boolean isStopcodon) {
        // System.out.println("feeding  " + activeORFMap.size());  // TODO DEBUGPRINT

        ArrayList inactives = new ArrayList(activeORFMap.size());

        for (ORF orf : activeORFMap) {
            isActive = orf.feed(c, isStopcodon);
            if (!isActive) {
                inactives.add(orf);
                if (!(orf.getLength() <= Settings.MINIMAL_ORF_LENGTH)) {
                    // if orf.Length  is not too small, add it
                    int orfLength = orf.getLength();
                    totalORFLength += orfLength;
                    if (orfLength > longestORF) {
                        longestORF = orfLength;
                    }
                    if (orfLength < shortestORF) {
                        shortestORF = orfLength;
                    }
                    completedORFMap.add(orf);
                } else {
                    // orfs that are too small are disposed of
                    orf = null;
                }

            }

        }


        // remove inactives (cant do in other loop, derp)
        for (Object orf : inactives) {
            // System.out.println(orfBaby);  //
            activeORFMap.remove(orf);
        }

    }

    public int getSequenceID() {
        return sequenceID;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Sequence{");
        sb.append("sequenceID=").append(sequenceID);
        sb.append(", totalORFLength=").append(totalORFLength);
        sb.append(", averageORFLength=").append(getAverageORFLength());
        sb.append(", longestORF=").append(longestORF);
        sb.append(", shortestORF=").append(shortestORF);
        sb.append(", completedORFs=").append(getCompletedORFCount());
        sb.append('}');
        return sb.toString();
    }

    public int getShortestORFLength() {
        return shortestORF;
    }

    public int getLongestORFLength() {
        return longestORF;
    }

    public int getAverageORFLength() {
        return totalORFLength / completedORFMap.size();
    }

    public int getCompletedORFCount() {
        return completedORFMap.size();

    }

    @Override
    public Iterator<ORF> iterator() {
        return completedORFMap.iterator();
    }
}
