package BlastTests;

import blast.ORFBlaster;
import orffinder.FastaSequence;
import orffinder.ORF;
import orffinder.ORFFinder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TestORFBlaster {

    public static void main(String[] args) {

        File testfile = new File("src/test/resources/data/Glennie_the_platypus.fa");
        ORFFinder orfFinder = new ORFFinder();
        try {
            orfFinder.setFile(testfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        ORFBlaster blaster = new ORFBlaster();
        blaster.blast(fasta.toString(), "blastn", "nt");
    }

    }

