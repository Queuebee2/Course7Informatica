package helpers;

import orffinder.ORFFinder;
import orffinder.Sequence;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Reader {
    private static File file;
    private static ORFFinder orfFinder;

    public File FileChooser() {
        System.out.println("Reader.FileChooser() fired!");               // todo-debugprint
        JFileChooser jfc = new JFileChooser(".");
        jfc.setDialogTitle("Choose a directory to save your file: ");
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int returnValue = jfc.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            if (jfc.getSelectedFile().isFile()) {
                file = jfc.getSelectedFile();
            }
        }

        try {
            orfFinder = new ORFFinder(file);

        } catch (IOException e) {

            // todo popup
            e.printStackTrace();
        }

        orfFinder.findOrfs();
        orfFinder.getallOrfs();
        return file;
    }

    public ArrayList<Sequence> getSeq_list() {

        return orfFinder.getInfoForVisualisation();
    }

}
