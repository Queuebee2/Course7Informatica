package ORFF_GUI;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;

public class ORFvis extends JFrame {
    private JFrame mainFrame;
    private JLabel headerLabel;
    private JLabel statusLabel;
    private JPanel controlPanel;
    private JScrollPane displayfile;
    private JTextField pathToFile;
    private JTextArea textofFile;

    public ORFvis() {
        prepareGUI();
    }

    public static void main(String[] args) {
        new SplashScreenDemo();
        ORFvis swingMenuDemo = new ORFvis();
        swingMenuDemo.showFile();
        swingMenuDemo.showMenuDemo();



    }
    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }
    private void prepareGUI() {
        setLookAndFeel();
        mainFrame = new JFrame("ORFF_GUI");
        mainFrame.setSize(1000, 1000);
        mainFrame.setLayout(null);


        headerLabel = new JLabel("", JLabel.CENTER);
        statusLabel = new JLabel("", JLabel.CENTER);
        statusLabel.setSize(350, 100);

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        controlPanel = new JPanel();
        controlPanel.setBounds(1, 1, 1000, 1000);
        controlPanel.setLayout(null);
        //mainFrame.add(headerLabel);
        mainFrame.add(controlPanel);
        //mainFrame.add(statusLabel);
        mainFrame.setVisible(true);
    }

    private void showFile() {
        // creation of file display with scrolling bar
        Border blackline = BorderFactory.createLineBorder(Color.black);
        textofFile = new JTextArea(200, 100);
        displayfile = new JScrollPane(textofFile);
        pathToFile = new JTextField("Path/of/File");
        pathToFile.setBounds(5, 5, 970, 25);
        displayfile.setBounds(5, 30, 970, 200);
        displayfile.setBorder(blackline);
        displayfile.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        displayfile.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        textofFile.setText("Display of file content");
        controlPanel.add(pathToFile);
        controlPanel.add(displayfile);
    }

    private void ORFvisualisatie(){
        Border blackline = BorderFactory.createLineBorder(Color.black);
        JPanel visScreen = new VisualisatiePane();
        visScreen.setBackground(Color.white);
        visScreen.setBounds(5,240,970,685);
        //JScrollPane displayORF = new JScrollPane(visScreen);
        //displayORF.setBorder(blackline);
        //displayORF.setBounds(5,240,970,685);
        //displayORF.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //displayORF.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        controlPanel.add(visScreen);

        // TODO: 31-3-2020 make visualisation 
    }
    private void showMenuDemo() {
        //create a menu bar
        final JMenuBar menuBar = new JMenuBar();

        //create menus
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Blast");
        JMenu databaseMenu = new JMenu("Database");
        final JMenu linkMenu = new JMenu("if anything else");

        //create menu items
        JMenuItem New = new JMenuItem("New ORF search");
        New.setMnemonic(KeyEvent.VK_N);
        New.setActionCommand("New");

        JMenuItem Export = new JMenuItem("Export");
        Export.setActionCommand("Export");

        JMenuItem Exit = new JMenuItem("Exit");
        Exit.setActionCommand("Exit");

        JMenuItem selectBlastn = new JMenuItem("BLASTn");
        selectBlastn.setActionCommand("BLASTn");

        JMenuItem selectBlastt = new JMenuItem("BLASTx");
        selectBlastt.setActionCommand("BLASTx");

        JMenuItem selectTBlastX = new JMenuItem("tBLASTx");
        selectTBlastX.setActionCommand("tBLASTx");

        JMenuItem Upload = new JMenuItem("Upload");
        Upload.setActionCommand("Upload");

        JMenuItem Download = new JMenuItem("Download");
        Download.setActionCommand("Download");

        MenuItemListener menuItemListener = new MenuItemListener();


        New.addActionListener(menuItemListener);
        Export.addActionListener(menuItemListener);
        Exit.addActionListener(menuItemListener);
        selectBlastn.addActionListener(menuItemListener);
        selectBlastt.addActionListener(menuItemListener);
        selectTBlastX.addActionListener(menuItemListener);
        Upload.addActionListener(menuItemListener);
        Download.addActionListener(menuItemListener);

        final JCheckBoxMenuItem showWindowMenu = new JCheckBoxMenuItem("Hide file", false);
        showWindowMenu.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {

                if (showWindowMenu.getState()) {
                    controlPanel.remove(displayfile);
                    mainFrame.repaint();
                } else {
                    controlPanel.add(displayfile);
                    mainFrame.repaint();

                }
            }
        });
        final JCheckBoxMenuItem showLinksMenu = new JCheckBoxMenuItem(
                "Show Translation", true);
        showLinksMenu.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {

                if (showLinksMenu.getState()) {
                    menuBar.remove(linkMenu);
                    mainFrame.repaint();
                } else {
                    menuBar.add(linkMenu);
                    mainFrame.repaint();
                }
            }
        });
        //add menu items to menus
        fileMenu.add(New);
        fileMenu.add(Export);
        fileMenu.addSeparator();
        fileMenu.add(showWindowMenu);
        fileMenu.addSeparator();
        fileMenu.add(showLinksMenu);
        fileMenu.addSeparator();
        fileMenu.add(Exit);

        editMenu.add(selectBlastn);
        editMenu.add(selectBlastt);
        editMenu.add(selectTBlastX);

        databaseMenu.add(Upload);
        databaseMenu.add(Download);
        //add menu to menubar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(databaseMenu);

        //add menubar to the frame
        mainFrame.setJMenuBar(menuBar);
        mainFrame.setVisible(true);
    }

    private void FileDisplayer(File file) throws IOException {

        BufferedReader input = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(
                                file)));
        textofFile.read(input, "READING FILE :)");
    }
    class MenuItemListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // TODO: 30-3-2020 make it like so that blast can be called with Blast(blastn or whaterver you choose)
            switch (e.getActionCommand()){

                case "New":
                    File file = Reader.FileChooser();
                    pathToFile.setText(String.valueOf(file));
                    try {
                        FileDisplayer(file);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    ORFvisualisatie();
                case "Export":
                    System.out.println("hellooo");
                    break;
                case "BLASTn":
                    System.out.println("1");
                    break;
                case "BLASTx":
                    System.out.println("2");
                    break;
                case "tBLASTx":
                    System.out.println("3");
                    break;
                case "Exit":
                    System.out.println("you wanna exit :(");
                    break;
                case "Upload":
                    System.out.println("uploading");
                    break;
                case "download":
                    System.out.println("downloading");
                    break;
            }
            statusLabel.setText(e.getActionCommand() + " JMenuItem clicked.");

        }
    }
}
