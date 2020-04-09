package orffinder;


import helpers.MaskFactory;

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
 * ORFFinder reads through a textfile (assumed format: nucleotide FASTA) to find ORFS
 */
public class ORFFinder {

    // constants
    private static final byte HEADER = 62;    //  00 11 11 10  // Header prefix >
    private static final byte CR = 13;        //  00 00 11 01  // Carriage Return
    private static final byte LF = 10;        //  00 00 10 10  // Line Feed
    private static final byte A = 65;         //  01 00 00 01
    private static final byte T = 84;         //  01 01 01 00
    private static final byte C = 67;         //  01 00 00 11
    private static final byte G = 71;         //  01 00 01 11
    private static final byte N = 78;         //  01 00 11 10
    private static final long ATG = (A) | (T << 8) | (G << 16);       // 00 00 00 00 01 00 01 11 01 01 01 00 01 00 00 01
    private static final long TAG = (T) | (A << 8) | (G << 16);       // 00 00 00 00 01 00 01 11 01 00 00 01 01 01 01 00
    private static final long TAA = (T) | (A << 8) | (A << 16);       // 00 00 00 00 01 00 00 01 01 00 00 01 01 01 01 00
    private static final long TGA = (T) | (G << 8) | (A << 16);       // 00 00 00 00 01 00 00 01 01 00 01 11 01 01 01 00
    private static final long CRLF_CHECK_1 = (CR << 8) | (LF << 16); // 00 00 00 00 00 00 10 10 00 00 11 01 00 00 00 00  // x + #13 + #10
    private static final long CRLF_CHECK_2 = (CR << 16);             // 00 00 00 00 00 00 11 01 00 00 00 00 00 00 00 00  // x + y + #13
    private static final int MASK_3 = 0x00FFFFFF;
    private static final long MASK_5 = 0xFFFFFFFFFFL;

    static String filename_RELATIVE_TEMP = "src/test/resources/data/Glennie the platypus.fa";
    private final ArrayList<FastaSequence> fastaSequences = new ArrayList<FastaSequence>(100);
    private File file;
    private RandomAccessFile mainRAFile;
    private FileChannel mainFileChannel;
    private MappedByteBuffer mainBuffer;

    public static void main(String[] args) {

    }

    public ORFFinder(File file) throws IOException {
        file = file;
        mainRAFile = new RandomAccessFile(file, "r");
        mainFileChannel = mainRAFile.getChannel();
        //  mainBuffer = new RandomAccessFile(file, "r").getChannel().map(FileChannel.MapMode.READ_ONLY, 0, mainFileChannel.size()); // as oneliner
        mainBuffer = mainFileChannel.map(FileChannel.MapMode.READ_ONLY, 0, mainFileChannel.size());
        mainBuffer.order(ByteOrder.LITTLE_ENDIAN);                      // SHOOT ME IN THE FOOT (took ~xxxhours to figure out)

    }

    public void setFile(File file) {
        this.file = file;
    }


    /**
     * Algorithm by Eric Langedijk
     * ASCII TABLE (for reference)
     * using ordinal values as keys maybe? later?: ATG 658471, TAG 846571, TAA 846565, TGA 847165 (changed use of hashmaps to arraylist for now)
     * chars of interest and their ASCII values: (65, A) (84, T) (67, C) (71, G) (62, >)
     */
    public void findOrfs() {
        findOrfs(String.valueOf(file));
    }

    public void findOrfs(String filename) {

        // TIME LOGGING
        long startTime = System.nanoTime();

        // variables
        StringBuilder currHeader;
        byte b_byteAtPointer;
        long currentCodonLong;
        int position;
        int charCounter;
        int currentTextLine;

        // initialise some
        FastaSequence currentFastaSequence = null;
        position = 0;
        charCounter = 0;
        currentTextLine = 0;
        int delta;

        //debug stuff
        int orfsCounted = 0;


            MappedByteBuffer buffer =  mainBuffer;

            final long lastValidDNACharacterPos = buffer.capacity() - 8;

            boolean UNIXLinefeeds = true;
            // check which type of linefeed the file contains, if it contains CR assume all are CRLF
            while (position < lastValidDNACharacterPos) {
                b_byteAtPointer = buffer.get();
                if (b_byteAtPointer == CR) {
                    System.out.println("Encountered {CR} character, assuming all lines end with CRLF! - WINDOWS FILE");
                    UNIXLinefeeds = false;
                }
                if (b_byteAtPointer == LF) {
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
                // if c = #13 and c + 1 <> #10 then raise FuckingLinuxException

                b_byteAtPointer = buffer.get(p_pointerPos);

                switch (b_byteAtPointer) {
                    // end of line

                    case LF:
                        currentTextLine++;

                        break;

                    // header line start (>) marks start of new sequence object
                    case HEADER: // >
                        // if sequence object was made, end it here at the start of a new header
                        if (currentFastaSequence != null) {
                            currentFastaSequence.EndPos = position;          // TODO: 6-4-2020 make private? use setter?
                            currentFastaSequence.RealSize = charCounter + 1;
                            //currentFastaSequence.getStatistics();
                        }
                        // build the string of the new header (thanks java for not being nice with string concat)
                        currHeader = new StringBuilder();
                        buffer.position(p_pointerPos);
                        while (position <= lastValidDNACharacterPos && b_byteAtPointer != LF) {
                            b_byteAtPointer = buffer.get();
                            if (b_byteAtPointer != CR) {
                                currHeader.append((char) b_byteAtPointer);
                            }
                            p_pointerPos++;
                            position++;

                        } //end while headerbuilder

                        currentTextLine++;
                        currentFastaSequence = new FastaSequence(filename, currHeader.toString(), currentTextLine, position);

                        currHeader = null;
                        fastaSequences.add(currentFastaSequence);
                        charCounter = 0;
                        continue; // do not increment position but continue


                        // check orf start
                    case A:
                        assert currentFastaSequence != null : "NO FUCKING DNA";

                        // put byes 0,1,2 of buffer.getInt into currentCodon
                        // (read 4 bytes from here but only use first 3 )
                        currentCodonLong = buffer.getInt(p_pointerPos) & MASK_3;


                        if (currentCodonLong == ATG) {
                            currentFastaSequence.addNewORF(position, charCounter, charCounter % 3);

                        } else {
                            // if 0,1,3 bytes wasn't enough, check 5 bytes briefly too
                            currentCodonLong = compress(buffer.getLong(p_pointerPos), isUnix);

                            if (currentCodonLong == ATG) {
                                currentFastaSequence.addNewORF(position, charCounter, charCounter % 3);
                            }
                        }

                        break;

                    // check if orf ends
                    case T:
                        assert currentFastaSequence != null : "NO FUCKING DNA";

                        currentCodonLong = buffer.getInt(position) & MASK_3;

                        if (currentCodonLong == TAG || currentCodonLong == TAA || currentCodonLong == TGA) {
                            currentFastaSequence.updateORFs(position + 2, charCounter + 2, charCounter % 3);

                        } else {
                            currentCodonLong = compress(buffer.getLong(p_pointerPos), isUnix);
                            if (currentCodonLong == TAG || currentCodonLong == TAA || currentCodonLong == TGA) {
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

    public void printStats() {
        for (FastaSequence seq : fastaSequences) {
            seq.getStatistics();
        }
    }

    private static long compress(long i, boolean isUnix) {
        if (isUnix) {
            return compressUnix(i);
        } else {
            return compressWindows(i);
        }
    }

    private static long compressWindows(long i) {
        if (MaskFactory.GetByte_1(i) == LF) {
            return CRLFCompress1(i);
        } else if (MaskFactory.GetByte_2(i) == LF) {
            return CRLFCompress2(i);
        } else {
            return 0;
        }
    }


    private static long CRLFCompress1(long i) {
        return (i & 0xFF) | ((i & 0xFFFF000000L) >> 16);
    }


    private static long CRLFCompress2(long i) {
        // compress from a + b + CR + LF + c
        return (i & 0xFFFF) | ((i & 0xFF00000000L) >> 16);

    }

    private static long compressUnix(long i) {
        if (MaskFactory.GetByte_1(i) == LF) {
            return LFCompress1(i);
        } else if (MaskFactory.GetByte_2(i) == LF) {
            return LFCompress2(i);
        } else {
            return 0;
        }
    }


    private static long LFCompress1(long i) {
        return (i & 0xFF) | ((i & 0xFFFF0000) >> 8);
    }


    private static long LFCompress2(long i) {
        return (i & 0xFF) | ((i & 0xFF000000) >> 8);
    }



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


    public String getOrf(ORF orf) {

        int c;
        StringBuilder dna = new StringBuilder();

        int endpos = (int) orf.getEndpos();
        if (endpos + 1 < mainBuffer.capacity()) {
            for (int i = orf.getOffset(); i < endpos; i++) {
                // todo change endpos in ORF
                c = mainBuffer.get(i);
                if (c >= A) { // skip whitechars
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


    public ArrayList<FastaSequence> getInfoForVisualisation() {

        return new ArrayList<>(fastaSequences);
    }




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

    private void logPlanckTime(double startTime) {
        long endTime = System.nanoTime();
        double duration = (endTime - startTime);
        BigDecimal planckSeconds = BigDecimal.valueOf((539124760000000000000000000000000000000000000.0 * duration * 0.000000001));
        System.out.println("THAT ONLY TOOK " + planckSeconds.toPlainString() + " planckSeconds!");
    }

    private void printConstants() {
        StringBuilder constants = new StringBuilder();
        constants.append("A=").append(A);
        constants.append("\nT=").append(T);
        constants.append("\nC=").append(C);
        constants.append("\nG=").append(G);
        constants.append("\nATG=").append(ATG);
        constants.append("\nTAG=").append(TAG);
        constants.append("\nTAA=").append(TAA);
        constants.append("\nTGA=").append(TGA);
        constants.append("\nCRLF_CHECK_1=").append(CRLF_CHECK_1);
        constants.append("\nCRLF_CHECK_2=").append(CRLF_CHECK_2);
        constants.append("\nCRLF_CHECK_1BINARY=").append(Long.toBinaryString(CRLF_CHECK_1));
        constants.append("\nCRLF_CHECK_2BINARY=").append(Long.toBinaryString(CRLF_CHECK_2));
        constants.append("\nMASK_3=").append(MASK_3);
        constants.append("\nMASK_5=").append(MASK_5);
        constants.append("\nMASK_3BINARY=").append(Long.toBinaryString(MASK_3));
        constants.append("\nMASK_5BINARY=").append(Long.toBinaryString(MASK_5));

        System.out.println(constants.toString());

    }
}






// raise Exception.Create('hell') || Exception.Create('LITTLE_ENDIAN');