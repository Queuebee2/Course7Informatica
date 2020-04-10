package orffinder;

public class ORF {

    private long offset;         // offset in file
    public long endpos;         // endpos in file
    private long counterStart;   // relative counter to dna
    public long counterEnd;     // relative counter to dna without the fucking linefeeds

    // currently testing
    private FastaSequence parentFastaSequence;
    private int ID;
    static int orfsmade = 0;

//    public ORF(String mockery) {
//        if (mockery.equals("mock")) {
//            offset = 10;
//            endpos = 23;
//            counterStart = 100;
//            counterEnd = 113;
//            parentFastaSequence = new FastaSequence("mock");
//        } else {
//            throw new IllegalArgumentException("mock ORF failed");
//        }
//
//    }

public ORF(int position, int charCounter, FastaSequence parent) {

    offset = position;
    counterStart = charCounter;

    // currently testing (speed)
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
     * @return int: absolute byte-offset in ansii fasta file
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
     * @return int: absolute byte-end in ansii fasta file
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