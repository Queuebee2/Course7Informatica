package orffinder;

public class ORF {

    private long offset;         // offset in file
    public long endpos;         // endpos in file
    private long counterStart;   // relative counter to dna
    public long counterEnd;     // relative counter to dna without the fucking linefeeds

    // currently testing
    public Sequence parentSequence;
    private int ID;
    static int orfsmade = 0;

public ORF(long position, long charCounter, Sequence parent) {

    offset = position;
    counterStart = charCounter;

    // currently testing (speed)
    parentSequence = parent;
    ID = orfsmade++;
}
    
    public long getSize() {
        return counterEnd-counterStart;
    }

    public int getOffset() {
        return (int) offset;
    }

    public long getCounterStart() {
        return counterStart;
    }

    public long getEndpos() {
        return endpos;
    }

    public int getID(){
        return ID;
    }
}


// raise LITTLE_ENDIAN