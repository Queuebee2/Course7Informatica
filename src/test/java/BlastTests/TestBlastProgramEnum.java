package BlastTests;

import org.biojava.nbio.ws.alignment.qblast.BlastProgramEnum;

public class TestBlastProgramEnum {

    public static void main(String[] args) {


        try {
            System.out.println(BlastProgramEnum.valueOf("blast"));
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.out.println(BlastProgramEnum.valueOf("blastp"));
        }

    }
}
