package orfgui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.ArrayList;

public class SharedListSelectionHandler implements ListSelectionListener {
    private ArrayList<Integer> indexlist;

    public void valueChanged(ListSelectionEvent e) {
        indexlist = new ArrayList<Integer>();
        System.out.println("i got input");
        ListSelectionModel lsm = (ListSelectionModel) e.getSource();

        int firstIndex = e.getFirstIndex();
        int lastIndex = e.getLastIndex();
        boolean isAdjusting = e.getValueIsAdjusting();

        if (lsm.isSelectionEmpty()) {
        } else {
            if (!isAdjusting) {
                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();
                for (int i = minIndex; i <= maxIndex; i++) {
                    if (lsm.isSelectedIndex(i)) {
                        indexlist.add(i);
                        System.out.println(i);
                    }
                }
            }
        }
    }

    public ArrayList<Integer> getIndexlist() {
        return indexlist;
    }
}


