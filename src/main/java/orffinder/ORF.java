package orffinder;

public class ORF {

    private long offset;            // offset in file (absolute)
    public long endpos;             // endpos in file (absolute)
    private long counterStart;      // relative counter to dna
    public long counterEnd;         // relative counter to dna without the fucking linefeeds
    private FastaSequence parentFastaSequence;
    private int ID;
    static int orfsmade = 0;        // tracker of how many orfs have been made in total and to use as ID


public ORF(int position, int charCounter, FastaSequence parent) {
    offset = position;
    counterStart = charCounter;
    parentFastaSequence = parent;
    ID = orfsmade++;
}

    public int getLength() {
        return getSize();
    }

    public int getSize() {
        return (int) (counterEnd-counterStart);
    }

    /**
     *
     * @return int: absolute position of the start of this orf in the fasta file assuming it is ansii encoded
     */
    public int getOffset() {
        return (int) offset;
    }

    public int getStartPosInFile() {
        return getOffset();
    }

    public int getEndPosInFile() {
        return (int) getEndpos();
    }

    public int getStartPosInSequence() {
        return (int) counterStart;
    }

    public int getEndPosInSequence() {
        return (int) counterEnd;
    }

    @Deprecated
    public long getCounterStart() {
        return counterStart;
    }

    /**
     *
     * @return int: absolute position of the end of this orf in the fasta file assuming it is ansii encoded
     */
    public int getEndpos() {
        return (int) endpos;
    }

    public int getID(){
        return ID;
    }

    public FastaSequence getParentFastaSequence() {
        return parentFastaSequence;
    }

    /**
     * finds the corresponding ORF sequence in the linked fasta file and creates a (no newline) fasta formatted
     * fasta entry with
     *  - filename
     *  - startposition (relative to sequence)
     *  - endposition (relative also)
     *  - the frame relative to the start of the sequence
     *
     * @example:
     *      > ORF:filename.fa:100:262:1
     *      ATGACATCGATCGATCGATGATACTAGCTAGCTAGCATGCATCGATGCATAGACTAGCATGTCATAGAG....
     *
     * @return fastaString
     */
    public String toFastaFormat() {
        StringBuilder fastaBuilder = new StringBuilder();

        fastaBuilder.append("> ORF:").append(getParentFastaSequence().getFilename()+":");

        fastaBuilder.append(counterStart+":"+counterEnd).append(":"+ (getLength() % 3) + "\n"); // pos and frame!
        String ORFDNASequnceString = getParentFastaSequence().getORFFinder().getOrf(this);
        fastaBuilder.append(ORFDNASequnceString).append("\n");

        return fastaBuilder.toString();
    }
}


// raise LITTLE_ENDIAN