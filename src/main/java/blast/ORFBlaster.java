package blast;

import javax.management.InstanceAlreadyExistsException;

public  class  ORFBlaster  {


    boolean ORFBlasterMade = false;

    public ORFBlaster() throws InstanceAlreadyExistsException {

        if (!ORFBlasterMade) {

        } else {
            throw new InstanceAlreadyExistsException("Only one ORFBlaster should exist at any time");
        }
    }



}
