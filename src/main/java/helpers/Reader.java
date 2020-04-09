package helpers;

import javax.swing.*;
import java.io.File;


public class Reader {
    private static File file;

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
        return file;
    }

}
