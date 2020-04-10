package orffinder;

/**
 * Holds constants used in ORFFinder to evaluate values of characters or character sets
 */
public class NucByteConstants {
    // constants
    public static final byte HEADER = 62;                              //  00 11 11 10  // Header prefix >
    public static final byte CR = 13;                                  //  00 00 11 01  // Carriage Return
    public static final long CRLF_CHECK_2 = (CR << 16);                //  00 00 00 00 00 00 11 01 00 00 00 00 00 00 00 00  // x + y + #13
    public static final byte LF = 10;                                  //  00 00 10 10  // Line Feed
    public static final long CRLF_CHECK_1 = (CR << 8) | (LF << 16);    //  00 00 00 00 00 00 10 10 00 00 11 01 00 00 00 00  // x + #13 + #10
    public static final byte A = 65;                                   //  01 00 00 01
    public static final byte T = 84;                                   //  01 01 01 00
    public static final long TAA = (T) | (A << 8) | (A << 16);         //  00 00 00 00 01 00 00 01 01 00 00 01 01 01 01 00
    public static final byte C = 67;                                   //  01 00 00 11
    public static final byte G = 71;                                   //  01 00 01 11
    public static final long TGA = (T) | (G << 8) | (A << 16);         //  00 00 00 00 01 00 00 01 01 00 01 11 01 01 01 00
    public static final long TAG = (T) | (A << 8) | (G << 16);         //  00 00 00 00 01 00 01 11 01 00 00 01 01 01 01 00
    public static final long ATG = (A) | (T << 8) | (G << 16);         //  00 00 00 00 01 00 01 11 01 01 01 00 01 00 00 01
    public static final byte N = 78;                                   //  01 00 11 10
    public static final int  MASK_3 = 0x00FFFFFF;                      //  00 00 00 00 11 11 11 11 11 11 11 11 11 11 11 11
    public static final long MASK_5 = 0xFFFFFFFFFFL;                   //  11 11 11 11 11 11 11 11 11 11 11 11 11 11 11 11 11 11 11 11


}
