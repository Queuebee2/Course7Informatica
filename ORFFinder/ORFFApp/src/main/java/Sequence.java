import java.util.ArrayList;

public class Sequence {


    ArrayList<ORFBaby> ActiveORFBabyMap;
    ArrayList<ORF> CompletedORFMap;
    int sequenceID;
    boolean isActive; // is the baby active or not

    public Sequence(int sequenceID) {
        this.sequenceID = sequenceID;
        ActiveORFBabyMap = new  ArrayList<ORFBaby>();
        CompletedORFMap = new ArrayList<ORF>();
    }

    public void addORFBaby(ORFBaby orfBaby) {
        ActiveORFBabyMap.add(orfBaby);

    }

    public void feedActiveORFBabies(boolean isStopcodon) {
        // System.out.println("feeding  " + ActiveORFBabyMap.size());  // TODO DEBUGPRINT

        ArrayList inactives = new ArrayList(ActiveORFBabyMap.size());

        for (Object orfBaby : ActiveORFBabyMap) {
            isActive = ((ORFBaby) orfBaby).feed(isStopcodon);
            if (!isActive) {
                inactives.add(orfBaby);
                ORF matureORF = ((ORFBaby) orfBaby).mature();
                CompletedORFMap.add(matureORF);
            }

        }

        // remove inactives (cant do in other loop, derp)
        for (Object orfBaby : inactives) {

            ActiveORFBabyMap.remove(orfBaby);
        }

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Sequence{");
        sb.append("ActiveORFMap=").append(ActiveORFBabyMap.size());
        sb.append(", CompletedORFMap=").append(CompletedORFMap.size());
        sb.append(", sequenceID=").append(sequenceID);
        sb.append('}');
        return sb.toString();
    }
}
