package orffinder;

import java.util.ArrayList;
import java.util.Iterator;

public class FastaSequence implements Iterable<ORF> {


    public final String header;
    public String filename;
    final int SequenceID;
    private static int IDIncrement = 0;
    final long lineNumber;
    final long offset;      // in file
    public long EndPos;
    public long RealSize;
    public int completedOrfCount;
    private ArrayList<String[]> tablelist;

    private  ArrayList<ORF> ORFList = new ArrayList<ORF>();
    private  ArrayList<ORF>[] ORFTrackers =  new ArrayList[3];
//final ArrayList<ArrayList<ORF>> ORFTrackers = new ArrayList < ArrayList < ORF >> (3); // if above doesnt work

    public FastaSequence(String filename, String currHeader, int currentTextLine, int position) {
        SequenceID = IDIncrement++;
        header = currHeader;
        lineNumber = currentTextLine;
        offset = position; // in file
        ORFTrackers[0]= new ArrayList<ORF>();
        ORFTrackers[1]= new ArrayList<ORF>();
        ORFTrackers[2]= new ArrayList<ORF>();
    }

    public void addNewORF(long position, long charCounter, int modulo) {
        //ORFTrackers.get(modulo).add(new ORF(position, charCounter)); // for nested arraylist

        ORFTrackers[modulo].add(new ORF(position, charCounter, this));
    }

    public void updateORFs(long endPos, long charCounter, int modulo) {
        //ArrayList<ORF> tracker = ORFTrackers.get(modulo); // for nested arraylist
        assert (modulo >= 0 && modulo <= 2) : "MODULO CANT BE < 0 or > 2";
        ArrayList<ORF> tracker = ORFTrackers[modulo];
        if (tracker.size() > 0) {
            for ( ORF orf : tracker ) {
                orf.endpos = endPos;
                orf.counterEnd = charCounter;

            }
            ORFList.addAll(tracker);
            tracker.clear();
        }
    }

    public long getRealSize() {
        return RealSize;
    }

    @Override
    public Iterator<ORF> iterator() {
        return ORFList.iterator();
    }
    public ArrayList<String[]> makeTable_list(){
        tablelist = new ArrayList<String[]>();
        int orfIdMaker = 0;
        for(ORF orf:ORFList){
            String[] orfvalue = new String[5];
            orfvalue[0] = String.valueOf(SequenceID);
            orfvalue[1] = String.valueOf(orf.getCounterStart());
            orfvalue[2] = String.valueOf(orf.counterEnd);
            orfvalue[3] = String.valueOf(orf.getSize());
            orfvalue[4] = String.valueOf(orf.getID());
            tablelist.add(orfvalue);
        }
        return tablelist;
    }
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
            statistics.append(" StartCounter=").append(o.getCounterStart());
            statistics.append(" EndCounter=").append(o.counterEnd);
            statistics.append(" ID: ").append(o.getID());
            statistics.append(" PARENT: ").append(o.parentFastaSequence);

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

