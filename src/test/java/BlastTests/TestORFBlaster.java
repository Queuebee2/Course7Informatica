package BlastTests;

import blast.ORFBlaster;
import orffinder.FastaSequence;
import orffinder.ORF;
import orffinder.ORFFinder;
import org.jmock.Mockery;

public class TestORFBlaster {

    private static final Mockery context = new Mockery();

    // TODO FIX ILLEGALARGUMENTEXCEPTION :(
    public static void main(String[] args) {
        // set up
        final ORFFinder finder = context.mock(ORFFinder.class);
        //context.setDefaultResultForType(ORFFinder.class, "AAAAAAAAATTACHGGG");

        final ORFBlaster orfBlaster = new ORFBlaster();
        FastaSequence sequence = new FastaSequence(finder, "test filename", "test header", 0, 0);
        ORF orf = new ORF(14, 0, sequence);

        // expectations
//        context.checking(new Expectations() {{
//            oneOf (ORFFinder.class).getOrf(ORF.class); will(returnValue("ATACGAGGCAGTACT"));}
//
//        });
        // String fastaString = orf.toFastaFormat();
        //orfBlaster.blastn(fastaString);


    }
//    private static final String SEQUENCE = "ATTATAAACGACATAATCGATCGATGCATGTAATATATATAGCTAGCTAGCAGATGCTAGTCGACGATGATGA";
//
//    public static void main(String[] args) {
//
//        Mockery mocky = new Mockery();
//        mocky.checking(new ExpectationBuilder() {
//            @Override
//            public void buildExpectations(Action action, ExpectationCollector expectationCollector) {
//
//            }
//        });
//        ORFBlaster orfBlaster = new ORFBlaster();
//        ORFFinder finder = new ORFFinder(mocky);
//        FastaSequence = new FastaSequence()
//        ORF = new ORF();
//        orfBlaster.blastn(SEQUENCE);
//        }

    }

