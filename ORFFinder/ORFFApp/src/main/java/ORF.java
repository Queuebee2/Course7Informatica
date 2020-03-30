public class ORF {

    static int totalBabyORFs = 0;
    private Sequence parentSequence;
    private int id;
    private int startpos;
    private int length;
    private int endpos;
    private String dnaSequence;
    private StringBuilder dnaBuilder;


    public ORF(int startpos, Sequence parent) {
        this.id = totalBabyORFs++;
        this.startpos = startpos;




        /**
         * bugfix: initial length 3 -> 2, because each ORF wil be 'fed' the last 'G' of 'ATG' directly after
         * initiation
         */
        // this.length = 2;
        this.dnaBuilder = new StringBuilder("AT");

        this.parentSequence = parent;
    }

    public boolean feed(int c, boolean stopcodon) {                // todo maybe this can return a mature ORF on stopcodon instead
        // this.length++;
        dnaBuilder.append((char) c);
        if (stopcodon && dnaBuilder.length() % 3 == 0) {
            endpos = startpos + length;
            dnaSequence = dnaBuilder.toString();
            dnaBuilder = null;
            return false;
        }
        return true;
    }

    public String getDnaSequence() {
        return dnaSequence;
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
