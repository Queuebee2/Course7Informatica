import com.sun.source.tree.LiteralTree;

import java.util.ArrayList;
import java.util.Iterator;

public class Sequence implements Iterable<ORF> {


    static int totalCompletedORFs = 0;

    ArrayList<ORF> activeORFMap;
    ArrayList<ORF> CompletedORFMap;
    int sequenceID;
    boolean isActive; // is the baby active or not

    public Sequence(int sequenceID) {
        this.sequenceID = sequenceID;
        activeORFMap = new ArrayList<ORF>();
        CompletedORFMap = new ArrayList<ORF>();
    }

    public static void addTotalCompletedORFCount(int amount) {// todo rename
        totalCompletedORFs += amount; // todo rename

    }

    public static int getTotalCompletedCount() {// todo rename
        return totalCompletedORFs; // todo rename
    }

    public void addORFBaby(ORF orf) {
        activeORFMap.add(orf);

    }

    public void feedActiveORFBabies(int c, boolean isStopcodon) {
        // System.out.println("feeding  " + activeORFMap.size());  // TODO DEBUGPRINT

        ArrayList inactives = new ArrayList(activeORFMap.size());

        for (ORF orf : activeORFMap) {
            isActive = orf.feed(c, isStopcodon);
            if (!isActive) {
                inactives.add(orf);
                CompletedORFMap.add(orf);
            }

        }

        // remove inactives (cant do in other loop, derp)
        for (Object orfBaby : inactives) {
            // System.out.println(orfBaby);  //
            activeORFMap.remove(orfBaby);
        }

    }

    public int getSequenceID() {
        return sequenceID;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Sequence{");
        sb.append("ActiveORFMap=").append(activeORFMap.size());
        sb.append(", CompletedORFMap=").append(CompletedORFMap.size());
        sb.append(", sequenceID=").append(sequenceID);
        sb.append('}');
        return sb.toString();
    }

    public int getCompletedORFCount() {
        return CompletedORFMap.size();
    }

    @Override
    public Iterator<ORF> iterator() {
        return CompletedORFMap.iterator();
    }
}
