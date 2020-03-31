package ORFF_GUI;
public class ORFBaby extends ORF {

    static int totalBabyORFs = 0;

    private Sequence parentSequence;
    private int id;
    private int startpos;
    private int length;
    private int endpos;

    public ORFBaby(int startpos, Sequence parent) {
        this.id = totalBabyORFs++;
        this.startpos = startpos;


        /**
         * bugfix: initial length 3 -> 2, because each ORF wil be 'fed' the last 'G' of 'ATG' directly after
         * initiation
         */
        this.length = 2;

        this.parentSequence = parent;
    }

    public boolean feed(boolean stopcodon) {                // todo maybe this can return a mature ORF on stopcodon instead
        this.length++;
        if (stopcodon && length % 3 == 0) {
            endpos = startpos + length;
            return false;
        }
        return true;
    }

    public ORF mature() {
        ORF matureOrf = new ORF(startpos, length, endpos);
        return matureOrf;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ORF=");
        sb.append(id);
        sb.append("START=").append(startpos);
        sb.append("END=").append(endpos);
        sb.append("SEQID=").append(parentSequence.getSequenceID());
        return sb.toString();
    }
}
