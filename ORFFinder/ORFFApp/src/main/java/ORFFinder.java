import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

// try -Xms512M -Xmx512M in VM options
public class ORFFinder {


    static String filename_RELATIVE_TEMP = "data/DNA.txt";

    /**
     * constructor
     */
    public ORFFinder() {
        read();

    }

    /**
     * main (for testing...)
     */
    public static void main(String[] args) {
        new ORFFinder();
    }

    /**
     * ASCII TABLE
     * Dec  Char                           Dec  Char     Dec  Char     Dec  Char
     * ---------                           ---------     ---------     ----------
     *   0  NUL (null)                      32  SPACE     64  @         96  `
     *   1  SOH (start of heading)          33  !         65  A         97  a
     *   2  STX (start of text)             34  "         66  B         98  b
     *   3  ETX (end of text)               35  #         67  C         99  c
     *   4  EOT (end of transmission)       36  $         68  D        100  d
     *   5  ENQ (enquiry)                   37  %         69  E        101  e
     *   6  ACK (acknowledge)               38  &         70  F        102  f
     *   7  BEL (bell)                      39  '         71  G        103  g
     *   8  BS  (backspace)                 40  (         72  H        104  h
     *   9  TAB (horizontal tab)            41  )         73  I        105  i
     *  10  LF  (NL line feed, new line)    42  *         74  J        106  j
     *  11  VT  (vertical tab)              43  +         75  K        107  k
     *  12  FF  (NP form feed, new page)    44  ,         76  L        108  l
     *  13  CR  (carriage return)           45  -         77  M        109  m
     *  14  SO  (shift out)                 46  .         78  N        110  n
     *  15  SI  (shift in)                  47  /         79  O        111  o
     *  16  DLE (data link escape)          48  0         80  P        112  p
     *  17  DC1 (device control 1)          49  1         81  Q        113  q
     *  18  DC2 (device control 2)          50  2         82  R        114  r
     *  19  DC3 (device control 3)          51  3         83  S        115  s
     *  20  DC4 (device control 4)          52  4         84  T        116  t
     *  21  NAK (negative acknowledge)      53  5         85  U        117  u
     *  22  SYN (synchronous idle)          54  6         86  V        118  v
     *  23  ETB (end of trans. block)       55  7         87  W        119  w
     *  24  CAN (cancel)                    56  8         88  X        120  x
     *  25  EM  (end of medium)             57  9         89  Y        121  y
     *  26  SUB (substitute)                58  :         90  Z        122  z
     *  27  ESC (escape)                    59  ;         91  [        123  {
     *  28  FS  (file separator)            60  <         92  \        124  |
     *  29  GS  (group separator)           61  =         93  ]        125  }
     *  30  RS  (record separator)          62  >         94  ^        126  ~
     *  31  US  (unit separator)            63  ?         95  _        127  DEL
     *
     * using ordinal values as keys maybe? later?: ATG 658471, TAG 846571, TAA 846565, TGA 847165 (changed use of hashmaps to arraylist for now)
     * chars of interest: (65, A) (84, T) (67, C) (71, G)
     */
    public void read() {

        long startTime = System.nanoTime();

        int[] last = new int[3];
        int trackedATGs = 0;
        int currentSequenceID = 0;
        int currentPos = 0;
        Sequence currentSequence = null;
        boolean isStopCodon;

        try (BufferedReader reader = Files.newBufferedReader(Path.of(filename_RELATIVE_TEMP))) {
            int c;
            while ((c = reader.read()) != -1) {

                // header 62 = >
                // assume all files start with this

                // chars of interest: (65, A) (84, T) (67, C) (71)

                switch (c) {
                    case (62):  // >
                        double dnaStartTime = System.nanoTime();
                        String header = reader.readLine(); // catch header and skip this header TODO confirm? CONFIRMED!
                        currentSequenceID++;
                        currentPos = 0;
                        if (currentSequence != null) {
                            // testing

                            System.out.print(currentSequence);
                            System.out.println(header); // TODO REMOVE ??
                            long dnaEndTime = System.nanoTime();
                            double duration = (dnaEndTime - dnaStartTime);
                            BigDecimal planckseconds = BigDecimal.valueOf((539124760000000000000000000000000000000000000.0 * duration * 0.000000001));
                            System.out.println(". ERIC, WOW, THAT ONLY TOOK " + planckseconds.toPlainString() + " planckseconds!!!!!!!!!!!!!!!");
                            int totalThisSequence = currentSequence.getCompletedORFCount();
                            Sequence.addTotalCompletedORFCount(totalThisSequence);
                        } else {
                            System.out.println(header);
                        }
                        currentSequence = new Sequence(currentSequenceID);
                        System.out.println("created " + currentSequenceID);
                        break;
                    case (65):  // A
                    case (67):  // C
                    case (71):  // G
                    case (84):  // T
                        currentPos++;
                        last[0] = last[1];
                        last[1] = last[2];
                        last[2] = c;
                        switch (Integer.toString(last[0]) + last[1] + last[2]) {
                            case "846571":  // TAG
                            case "846565":  // TAA
                            case "847165":  // TGA
                                // System.out.println("is stop codon true");  // TODO DEBUGPRINT
                                isStopCodon = true;
                                break;
                            case "658471":  // ATG replaces // if (last[0] == 65 && last[1] == 84 && last[2] == 71) {}
                                trackedATGs++;
                                // continue to default
                                ORFBaby orfBaby = new ORFBaby(currentPos, currentSequence);
                                currentSequence.addORFBaby(orfBaby);
                            default:
                                // System.out.println("is stop codon false");   // TODO DEBUGPRINT
                                isStopCodon = false;
                                break;
                        }
                        currentSequence.feedActiveORFBabies(isStopCodon);

                        break;
                    default:
                        //dostuff
                        break;
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(currentSequence);  // last sequence

        System.out.println("found " + trackedATGs + " occurences of ATG");
        System.out.println("of which " + Sequence.getTotalCompletedCount() + " Complete ORFs");
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        double planckseconds = 5.39124760e44 * duration;
        System.out.println("Duration : " + planckseconds + " planckseconds");
        System.out.println("Duration : " + duration + " nanoseconds");
        long mseconds = duration / 1000000;
        System.out.println("Duration : " + mseconds + " milliseconds");
        long seconds = mseconds / 1000;
        System.out.println("Duration : " + seconds + " seconds");
    }


    public void testReadAndCountSpeed(int amount) {
        for (int i = 0; i < amount + 1; i++) {
            long startTime = System.nanoTime();
            long counts = readandcount(i * 10);
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);
            System.out.print("with looping " + counts + " times per char, counting, I counted " + counts + " times  |  ");
            System.out.println("Duration : " + duration / 1000000 + " milliseconds");
        }
    }

    public long readandcount(int counts) {

        long countable = 0;

        try (BufferedReader reader = Files.newBufferedReader(Path.of(filename_RELATIVE_TEMP))) {
            int c;
            while ((c = reader.read()) != -1) {

                for (int i = 0; i < counts + 1; i++) {
                    countable++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return countable;

    }

}


// raise Exception.Create('hell');
