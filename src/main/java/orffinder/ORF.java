package orffinder;

public class ORF {

    private long offset;         // offset in file
    public long endpos;         // endpos in file
    private long counterStart;   // relative counter to dna
    public long counterEnd;     // relative counter to dna without the fucking linefeeds

public ORF(long position, long charCounter) {
    offset = position;
    counterStart = charCounter;
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

}


// raise LITTLE_ENDIAN