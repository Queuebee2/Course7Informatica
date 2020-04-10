package ORFFinderTests;

import orffinder.NucByteConstants;

public class TestNucByteConstants {

    public static void main(String[] args) {
        assert NucByteConstants.HEADER == 62;
        assert NucByteConstants.CR == 13;
        assert NucByteConstants.LF == 10;
        assert NucByteConstants.A == 65;
        assert NucByteConstants.T == 84;
        assert NucByteConstants.C == 67;
        assert NucByteConstants.G == 71;
        assert NucByteConstants.ATG == 4674625;
        assert NucByteConstants.TAG == 4669780;
        assert NucByteConstants.TAA == 4276564;
        assert NucByteConstants.TGA == 4278100;
        assert NucByteConstants.CRLF_CHECK_1 == 658688;
        assert NucByteConstants.CRLF_CHECK_2 == 851968;
        assert NucByteConstants.MASK_3 == 16777215;
        assert NucByteConstants.MASK_5 == 1099511627775L;
        System.out.println("constants tested");
    }

}
