package ORFF_GUI;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class Reader {
    private static File file;

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
        ORFFinder.read(file);
        return file;
    }

}
