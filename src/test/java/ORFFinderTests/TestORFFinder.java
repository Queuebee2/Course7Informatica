package ORFFinderTests;

import orffinder.FastaSequence;
import orffinder.ORF;
import orffinder.ORFFinder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TestORFFinder {

    public static void main(String[] args) {
        try {
            File testfile = new File("src/test/resources/data/Glennie_the_platypus.fa");
            ORFFinder orfFinder = new ORFFinder();
            orfFinder.setFile(testfile);
            orfFinder.findOrfs();//
            orfFinder.printStats();
            orfFinder.getallOrfs();
            ArrayList<FastaSequence> bob = orfFinder.getFastaSequences();

            StringBuilder fasta = new StringBuilder();
            for ( FastaSequence sequence  : bob   ) {

                int count = 0;
                for ( ORF orf : sequence ) {
                    fasta.append(orf.toFastaFormat());
                    count++;
                    if (count > 10) {
                        break;
                    }
                }
            }
            System.out.println(fasta.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
