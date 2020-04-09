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
            ORFFinder orfFinder = new ORFFinder(testfile);
            orfFinder.findOrfs();//
            orfFinder.printStats();
            orfFinder.getallOrfs();
            ArrayList<FastaSequence> bob = orfFinder.getInfoForVisualisation();


            for ( FastaSequence sequence  : bob   ) {
                int count = 0;
                for ( ORF orf : sequence ) {
                    System.out.println(orfFinder.getOrf(orf));
                    count++;
                    if (count > 10) {
                        break;
                    }
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
