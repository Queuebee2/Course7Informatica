package ORFF_GUI;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.List;

public class Reader {
    private static File file;
    private static ORFFinder orf;

    static File FileChooser() {
        System.out.println("i got here");
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Choose a directory to save your file: ");
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int returnValue = jfc.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            if (jfc.getSelectedFile().isFile()) {
                file = jfc.getSelectedFile();
            }
        }
        orf = new ORFFinder();
        orf.readAndFindORFs(String.valueOf(file));

        return file;
    }
    static List<Integer> getLengths() {
        int largest = 0;
        List<Integer> listoflength = orf.getInfoForVisualisation();

        return listoflength;
    }

}
