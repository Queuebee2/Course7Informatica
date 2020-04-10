package orffinder;

/**
 * Maskfactory has some static helper methods for ORFFinder compression methods to extract
 * certain bytes from  a long
 */
public class MaskFactory
{
    public static byte GetByte_0(long u)
    {
        return (byte) (u & 0xFF);
    }

    public static byte GetByte_1(long u)
    {
        return (byte) ((u & 0xFF00) >> 8);
    }

    public static byte GetByte_2(long u)
    {
        return (byte) ((u & 0xFF0000) >> 16);
    }

}