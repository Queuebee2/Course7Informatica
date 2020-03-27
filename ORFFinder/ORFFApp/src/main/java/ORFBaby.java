/**
 * describes the contents of a baby open-reading-frame, which is not finsihed
 */

public class ORFBaby extends ORF {

    int startpos;
    int length;
    int endpos;

    public ORFBaby(int startpos) {
        this.startpos = startpos;
        this.length = 3;
    }

    public boolean feed(boolean stopcodon) {

        this.length++;
        if (stopcodon && length % 3 == 0) {
            endpos = startpos+length;
            return false;
        }
        return true;
    }

    public ORF mature() {
        ORF matureOrf = new ORF(startpos, length, endpos);
        return matureOrf;
    }
}
