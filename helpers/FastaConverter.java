package helpers;

public class FastaConverter {

    private String filename;

    public FastaConverter() {
        this("\\data\\Fasta");
    }

    public FastaConverter(String filename) {
        filename = filename;
    }


    public void orfToFasta() {
        orfToFasta(filename);
    }

    public void orfToFasta(String filename) {

    }

}
