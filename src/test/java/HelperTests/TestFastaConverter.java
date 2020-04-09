package HelperTests;

import helpers.FastaConverter;
import orffinder.FastaSequence;

// todo test setting file somewhere randomly in pc, already existing files, non-text files...
// ... overwrite mode? ... god so many things to account for and we haven't even scraped the surface
public class TestFastaConverter {

    public static void main(String[] args) {

        FastaConverter fc = new FastaConverter();

        FastaSequence seq = new FastaSequence("sequence 1", 0, 0);
        seq.EndPos = 100;
        seq.RealSize= 90;

    }
}
