package BlastTests;

import blast.ORFBlaster;

public class TestORFBlaster {

    private static final String SEQUENCE = "ATTATAAACGACATAATCGATCGATGCATGTAATATATATAGCTAGCTAGCAGATGCTAGTCGACGATGATGA";

    public static void main(String[] args) {

        ORFBlaster orfBlaster = new ORFBlaster();
        orfBlaster.blastn(SEQUENCE);
        }

    }

