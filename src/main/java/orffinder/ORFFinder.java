package orffinder;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

// try -Xms512M -Xmx512M in VM options
// add -ea for enable assertions in VM options

/**
 * ORFFinder reads through a textfile (assumed format: nucleotide FASTA, ansi coded) to find ORFs
 * Open Reading Frame objects are made on each startcodon (ATG) within a FastaSequence object
 * FastaSequences are kept track of in a local field.
 */
public class ORFFinder {

    static String filename_RELATIVE_TEMP = "src/test/resources/data/Glennie the platypus.fa";
    private final ArrayList<FastaSequence> fastaSequences = new ArrayList<FastaSequence>(100);
    private File file;
    private RandomAccessFile mainRAFile;
    private FileChannel mainFileChannel;
    private MappedByteBuffer mainBuffer;

    public static void main(String[] args) {
        ORFFinder orff = new ORFFinder();
        orff.printConstants();
    }

    public ORFFinder()  {
    }

    /**
     * set the filename currently in use and create a RandomAccesFile with buffer to read
     * @param file
     * @throws IOException
     */
    public void setFile(File file) throws IOException {
        this.file = file;
        mainRAFile = new RandomAccessFile(file, "r");
        mainFileChannel = mainRAFile.getChannel();
        mainBuffer = mainFileChannel.map(FileChannel.MapMode.READ_ONLY, 0, mainFileChannel.size());
        mainBuffer.order(ByteOrder.LITTLE_ENDIAN);      // the byte order has to be this way for the masks to work
    }


    /**
     * Algorithm by Eric Langedijk
     * Finds orfs in an ansi coded fasta file in a rapid fashion. There's no abundance of errorchecking, so data musn't
     * be invalid. orfs will still be predicted even in invalid data.
     * ASCII TABLE (for reference)
     * using ordinal values as keys maybe? later?: ATG 658471, TAG 846571, TAA 846565, TGA 847165 (changed use of hashmaps to arraylist for now)
     * chars of interest and their ASCII values: (65, A) (84, T) (67, C) (71, G) (62, >)
     */
    public void findOrfs() {

        // TIME LOGGING
        long startTime = System.nanoTime();

        // variables
        StringBuilder currHeader;
        byte b_byteAtPointer;
        long currentCodonLong;
        int position;
        int charCounter;
        int currentTextLine;

        // initialise some variables
        FastaSequence currentFastaSequence = null;
        position = 0;
        charCounter = 0;
        currentTextLine = 0;

        MappedByteBuffer buffer =  mainBuffer;

        final long lastValidDNACharacterPos = buffer.capacity() - 8;

        int delta;  // the amount of characters a codon can be in (5 in windows, 4 in unix, mac not included)
        boolean UNIXLinefeeds = true;
        // check which type of linefeed the file contains, if it contains CR assume all are CRLF
        while (position < lastValidDNACharacterPos) {
            b_byteAtPointer = buffer.get();
            if (b_byteAtPointer == NucByteConstants.CR) {
                System.out.println("Encountered {CR} character, assuming all lines end with CRLF! - WINDOWS FILE");
                UNIXLinefeeds = false;
            }
            if (b_byteAtPointer == NucByteConstants.LF) {
                break;
            }
            position++;
        }

        if (UNIXLinefeeds) {
            delta = 3;      // for reading [current] + [T LF G] for example
        } else {
            delta = 4;      // for reading [current] + [CR LF T G] for example
        }
        final boolean isUnix = UNIXLinefeeds;

        // reset pos to 0 before reading again
        buffer.rewind();
        position = 0;
        int p_pointerPos = 0;

        while (position < lastValidDNACharacterPos) {

            b_byteAtPointer = buffer.get(p_pointerPos);

            switch (b_byteAtPointer) {
                // end of line

                case NucByteConstants.LF:
                    currentTextLine++;

                    break;

                // header line start (>) marks start of new sequence object
                case NucByteConstants.HEADER: // >
                    // if sequence object was made, end it here at the start of a new header
                    if (currentFastaSequence != null) {
                        currentFastaSequence.EndPos = position;          // TODO: 6-4-2020 make private? use setter?
                        currentFastaSequence.RealSize = charCounter + 1;
                        //currentFastaSequence.getStatistics();
                    }
                    // build the string of the new header (thanks java for not being nice with string concat)
                    currHeader = new StringBuilder();
                    buffer.position(p_pointerPos);
                    while (position <= lastValidDNACharacterPos && b_byteAtPointer != NucByteConstants.LF) {
                        b_byteAtPointer = buffer.get();
                        if (b_byteAtPointer != NucByteConstants.CR) {
                            currHeader.append((char) b_byteAtPointer);
                        }
                        p_pointerPos++;
                        position++;

                    } //end while headerbuilder

                    currentTextLine++;
                    currentFastaSequence = new FastaSequence(this, file.getName(), currHeader.toString(), currentTextLine, position);

                    currHeader = null;
                    fastaSequences.add(currentFastaSequence);
                    charCounter = 0;
                    continue; // do not increment position but continue


                    // check for orf start, if it starts,
                case NucByteConstants.A:
                    assert currentFastaSequence != null : "NO DATA AT ALL";

                    // put byes 0,1,2 of buffer.getInt into currentCodon
                    // (read 4 bytes from here but only use first 3 )
                    currentCodonLong = buffer.getInt(p_pointerPos) & NucByteConstants.MASK_3;


                    if (currentCodonLong == NucByteConstants.ATG) {
                        currentFastaSequence.addNewORF(position, charCounter, charCounter % 3);

                    } else {
                        // if 0,1,3 bytes wasn't enough, check 5 bytes briefly too
                        currentCodonLong = compress(buffer.getLong(p_pointerPos), isUnix);

                        if (currentCodonLong == NucByteConstants.ATG) {
                            currentFastaSequence.addNewORF(position, charCounter, charCounter % 3);
                        }
                    }

                    break;

                // check if orf ends, then send a signal to the current sequence to end all orfs in the current frame
                case NucByteConstants.T:
                    assert currentFastaSequence != null : "NO DATA AT ALL ";

                    currentCodonLong = buffer.getInt(position) & NucByteConstants.MASK_3;
                    if (currentCodonLong == NucByteConstants.TAG || currentCodonLong == NucByteConstants.TAA || currentCodonLong == NucByteConstants.TGA) {
                        currentFastaSequence.updateORFs(position + 2, charCounter + 2, charCounter % 3);
                    } else {
                        currentCodonLong = compress(buffer.getLong(p_pointerPos), isUnix);
                        if (currentCodonLong == NucByteConstants.TAG || currentCodonLong == NucByteConstants.TAA || currentCodonLong == NucByteConstants.TGA) {
                            currentFastaSequence.updateORFs(position + delta, charCounter + delta, charCounter % 3);
                        }
                    }

                    break;

            } // end switch (b_byteAtPointer)

            if (b_byteAtPointer >= 40) {
                charCounter++;  // count chars valued higher than A, assume in {A,T,C,G,N}
            }
            p_pointerPos++;
            position++;

        } // end while loop that reads over file

        // round up the last sequence made (if any were made)
        if (currentFastaSequence != null) {
            if (currentFastaSequence.EndPos == 0) {
                currentFastaSequence.EndPos = lastValidDNACharacterPos;
                currentFastaSequence.RealSize = charCounter + 1;
            }
        }
        //currentFastaSequence.getStatistics();


        // Print logged time
        logTime(startTime, 4);


    }

    // print some statistics
    public void printStats() {
        for (FastaSequence seq : fastaSequences) {
            seq.getStatistics();
        }
    }

    /**
     * Compression methods to speed up parsing and ignore linefeeds
     * @param i the integer to compress
     * @param isUnix whether to account for unix line feeds
     * @return long compressed to 3 usable bytes
     */
    private static long compress(long i, boolean isUnix) {
        if (isUnix) {
            return compressUnix(i);
        } else {
            return compressWindows(i);
        }
    }

    /**
     * remove linefeed from long at bitlevel using masks from NubyteConstants
     * @param i
     * @return
     */
    private static long compressWindows(long i) {
        if (MaskFactory.GetByte_1(i) == NucByteConstants.LF) {
            return CRLFCompress1(i);
        } else if (MaskFactory.GetByte_2(i) == NucByteConstants.LF) {
            return CRLFCompress2(i);
        } else {
            return 0;
        }
    }


    private static long CRLFCompress1(long i) {
        // compress from a + CR + LF b + + c TO a + b + c
        return (i & 0xFF) | ((i & 0xFFFF000000L) >> 16);
    }

    private static long CRLFCompress2(long i) {
        // compress from a + b + CR + LF + c TO a + b + c
        return (i & 0xFFFF) | ((i & 0xFF00000000L) >> 16);
    }

    private static long compressUnix(long i) {
        if (MaskFactory.GetByte_1(i) == NucByteConstants.LF) {
            return LFCompress1(i);
        } else if (MaskFactory.GetByte_2(i) == NucByteConstants.LF) {
            return LFCompress2(i);
        } else {
            return 0;
        }
    }


    private static long LFCompress1(long i) {
        // compress from a + CR + LF b + + c TO a + b + c
        return (i & 0xFF) | ((i & 0xFFFF0000) >> 8);
    }


    private static long LFCompress2(long i) {
        // compress from a + b + CR + LF + c TO a + b + c
        return (i & 0xFF) | ((i & 0xFF000000) >> 8);
    }


    /**
     * mostly a test method, to see how long it takes to get all orf sequences and count orfs.
     */
    public void getallOrfs() {
        long startTime = System.nanoTime();

        System.out.println("getting all ORFS...");
        String orfString;
        int orfsFound = 0;
        for (FastaSequence seq : fastaSequences
        ) {
            for (ORF orf : seq
            ) {
                orfString = getOrf(orf);
                orfsFound++;
            }
        }
        logTime(startTime, 4);
        System.out.println("got " + orfsFound + " orf Strings");
    }


    /**
     * fetch the nucleotide sequence of an ORF from the file it originates from
     * @param orf
     * @return
     */
    public String getOrf(ORF orf) {

        int c;
        StringBuilder dna = new StringBuilder();

        int endpos = orf.getEndpos() + 1;
        if (endpos + 1 < mainBuffer.capacity()) {
            for (int i = orf.getOffset(); i < endpos; i++) {
                // todo change endpos in ORF
                c = mainBuffer.get(i);
                if (c >= NucByteConstants.A) { // skip whitechars
                    dna.append((char) c);
                }

            }
            return dna.toString();
        }
        // todo exception
        System.out.println("dna impossible length");
        System.out.println("buffer cap: " +mainBuffer.capacity());
        System.out.println("endpos: " +endpos);
        return "";

    }


    public ArrayList<FastaSequence> getSequences() {
        return getFastaSequences();
    }
    public ArrayList<FastaSequence> getFastaSequences() {
        return fastaSequences;
    }

    /**
     * time logger mostly for debug purposes
     * @param startTime
     * @param verbose       level of verbosity
     */
    private void logTime(long startTime, int verbose) {
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        switch (verbose) {
            case 5: // nanoseconds only
                System.out.println("Duration : " + duration + " nanoSeconds");
                break;
            case 4:
                BigDecimal planckSeconds = BigDecimal.valueOf((539124760000000000000000000000000000000000000.0 * duration * 0.000000001));
                System.out.println("Duration : " + planckSeconds + " planckSeconds");
            case 3:
                System.out.println("Duration : " + duration + " nanoSeconds");
            case 2:
                long mseconds = duration / 1000000;
                System.out.println("Duration : " + mseconds + " milliSeconds");
            case 1:
                long seconds = duration / 1000000000;
                System.out.println("Duration : " + seconds + " seconds");
            case 0:
            default:
                break;
        }

    }

    /**
     * funny time logger for estimated planck time
     * @param startTime
     */
    private void logPlanckTime(double startTime) {
        long endTime = System.nanoTime();
        double duration = (endTime - startTime);
        BigDecimal planckSeconds = BigDecimal.valueOf((539124760000000000000000000000000000000000000.0 * duration * 0.000000001));
        System.out.println("THAT ONLY TOOK " + planckSeconds.toPlainString() + " planckSeconds!");
    }

    /**
     * test to check all constants manually some in binary string some as integer.
     */
    private void printConstants() {
        StringBuilder constants = new StringBuilder();
        constants.append("A=").append(NucByteConstants.A);
        constants.append("\nT=").append(NucByteConstants.T);
        constants.append("\nC=").append(NucByteConstants.C);
        constants.append("\nG=").append(NucByteConstants.G);
        constants.append("\nATG=").append(NucByteConstants.ATG);
        constants.append("\nTAG=").append(NucByteConstants.TAG);
        constants.append("\nTAA=").append(NucByteConstants.TAA);
        constants.append("\nTGA=").append(NucByteConstants.TGA);
        constants.append("\nCRLF_CHECK_1=").append(NucByteConstants.CRLF_CHECK_1);
        constants.append("\nCRLF_CHECK_2=").append(NucByteConstants.CRLF_CHECK_2);
        constants.append("\nCRLF_CHECK_1BINARY=").append(Long.toBinaryString(NucByteConstants.CRLF_CHECK_1));
        constants.append("\nCRLF_CHECK_2BINARY=").append(Long.toBinaryString(NucByteConstants.CRLF_CHECK_2));
        constants.append("\nMASK_3=").append(NucByteConstants.MASK_3);
        constants.append("\nMASK_5=").append(NucByteConstants.MASK_5);
        constants.append("\nMASK_3BINARY=").append(Long.toBinaryString(NucByteConstants.MASK_3));
        constants.append("\nMASK_5BINARY=").append(Long.toBinaryString(NucByteConstants.MASK_5));

        System.out.println(constants.toString());

    }

}






// raise Exception.Create('hell') || Exception.Create('LITTLE_ENDIAN');