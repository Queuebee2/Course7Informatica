package orfgui;
import helpers.Reader;
import orffinder.ORF;
import orffinder.ORFFinder;
import orffinder.Sequence;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class ORFVisualiser extends JFrame {
    private JFrame mainFrame;
    private JLabel headerLabel;
    private JLabel statusLabel;
    private JPanel controlPanel;
    private JScrollPane displayfile;
    private ORFFinder orfFinder;
    private HashMap<Integer,ORF> ORFlist;
    private JTable selected_table;
    private JTextField pathToFile;
    private JTextArea textofFile;
    private JLabel jLabelEmptyHolderImage;
    private ArrayList<Sequence> list;
    private ArrayList<Rectangle> reclist;
    private JTable table;
    private ListSelectionModel listSelectionModel;

    Color black= new Color(43, 43, 43);
    Color lighter_black= new Color(60, 63, 65);
    Color DarkBlue= new Color(47, 79, 79);
    Color Blue= new Color( 30,200,255);
    Image img = Toolkit.getDefaultToolkit().getImage("src/main/resources/DNA-512.png");

    public ORFVisualiser() {


        new SplashScreenDemo();
        prepareGUI();
        HolderImage();
        showFile();
        showMenuDemo();

    }

    public static void main(String[] args) {

        ORFVisualiser swingMenuDemo = new ORFVisualiser();




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
        mainFrame = new JFrame("orfgui");
        mainFrame.setSize(1000, 1000);
        mainFrame.setLayout(new BorderLayout(1,1));
        mainFrame.getContentPane().setBackground(black);
        mainFrame.setIconImage(img);
        headerLabel = new JLabel("", JLabel.CENTER);
        statusLabel = new JLabel("", JLabel.CENTER);
        statusLabel.setSize(350, 100);

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        controlPanel = new JPanel();
        controlPanel.setBounds(1, 1, 950, 930);
        controlPanel.setLayout(new BorderLayout(10,10));
        controlPanel.setBackground(black);
        //mainFrame.add(headerLabel);
        mainFrame.add(controlPanel,BorderLayout.CENTER);
        //mainFrame.add(statusLabel);
        controlPanel.setVisible(true);
        mainFrame.setVisible(true);
    }

    private void showFile() {
        // creation of file display with scrolling bar
        Border blackline = BorderFactory.createLineBorder(Blue);
        Font titel = new Font("arial",Font.BOLD,16);
        Font text = new Font("arial",Font.PLAIN,12);
        textofFile = new JTextArea(200, 100);
        textofFile.setBackground(lighter_black);
        textofFile.setForeground(Color.white);
        textofFile.setFont(text);

        displayfile = new JScrollPane(textofFile);
        pathToFile = new JTextField("Path/of/File");
        pathToFile.setBackground(Blue);
        pathToFile.setForeground(Color.white);
        pathToFile.setEditable(false);
        pathToFile.setFont(titel);
        pathToFile.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Blue));
        //pathToFile.setBounds(5, 5, 970, 25);
        pathToFile.setPreferredSize(new Dimension(970,25));
        //displayfile.setBounds(5, 30, 970, 200);

        textofFile.setPreferredSize(new Dimension(940,100));

        displayfile.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Blue));
        //displayfile.setBorder(blackline);
        displayfile.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        displayfile.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        displayfile.setPreferredSize(new Dimension(940,100));

        textofFile.setText("Display of file content");

        controlPanel.add(pathToFile, BorderLayout.NORTH);
        controlPanel.add(displayfile,BorderLayout.CENTER);
    }

    private ArrayList<Rectangle> makeRec() {
        System.out.println("im making more rectangles");

        reclist = new ArrayList<Rectangle>();
        int firstline = 10;
        for (Sequence sequence : list) {
             firstline = firstline + 30;
             for (ORF orf : sequence) {
                 Random rand = new Random();
                 int start = (int) orf.getCounterStart();
                 int size = (int) orf.getSize();
                 Color randomColor = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
                 Rectangle rec = new Rectangle(10 + start, firstline + 5, size, 10, randomColor);
                 reclist.add(rec);
            }
         }
         System.out.println(reclist);
        return reclist;
    }
    private void ORFvisualisatie(){
        Border blackline = BorderFactory.createLineBorder(Blue);
        JPanel visScreen = new VisualisatiePane(list,reclist);
        visScreen.setBackground(lighter_black);
        //visScreen.setBounds(5,240,970,685);

        System.out.println("got lengths" + list.size());
        int largest = 0;
        for (Sequence sequence : list){
            int Length = (int) sequence.getRealSize();
            if (Length > largest) {
                largest = Length;
                System.out.println(largest);
                }
            }
        System.out.println("largest"+ largest);
        visScreen.setPreferredSize(new Dimension(largest,685));

        JScrollPane displayORF = new JScrollPane(visScreen);
        displayORF.setBorder(blackline);
        //displayORF.setBounds(5,240,970,685);
        displayORF.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        displayORF.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        controlPanel.add(displayORF,BorderLayout.SOUTH);

        // TODO: 31-3-2020 make visualisation 
    }

    private void ORFtable() {
        DefaultTableModel tableModel = null;
        ArrayList<String[]> table_list = new ArrayList<String[]>();
        if (table == null) {
            String[] columnNames = {"Sequence ID", "Start", "End", "Length", "ID", "parent Sequence"};
            tableModel = new DefaultTableModel(columnNames, 0);
        }
        else{
                tableModel = (DefaultTableModel) table.getModel();
                int rows = selected_table.getRowCount();
                tableModel.fireTableRowsDeleted(0, rows - 1);
            }

            for (Sequence sequence : list) {
                table_list = sequence.makeTable_list();
                for (String[] string : table_list) {

                    //System.out.println(Arrays.toString(string));
                    tableModel.addRow(string);
                }
                table = new JTable(tableModel);
                table.setCellSelectionEnabled(true);
                table.setModel(tableModel);
                table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                tableModel.fireTableDataChanged();
                table.repaint();
                //adding it to JScrollPane
                JScrollPane sp = new JScrollPane(table);
                //sp.repaint();
                sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                sp.setViewportView(table);
                listSelectionModel = table.getSelectionModel();
                listSelectionModel.addListSelectionListener(
                        new SharedListSelectionHandler()
                );
                controlPanel.add(sp,BorderLayout.SOUTH);
            }
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
    private void MakeORFlist(){
        ORFlist =  new HashMap<>();
        for(Sequence sequence : list){
            for(ORF orf : sequence){
               int ID =  orf.getID();
               ORFlist.put(ID,orf);
            }
        }
    }
    private void HolderImage(){
        ImageIcon image = new ImageIcon("src/main/resources/hatebed.gif");
        jLabelEmptyHolderImage =new JLabel("",image,JLabel.CENTER);
        controlPanel.add(jLabelEmptyHolderImage,BorderLayout.SOUTH);
    }
    private void FileDisplayer(File file) throws IOException {

        BufferedReader input = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(
                                file)));
        textofFile.read(input, "READING FILE :)");
    }
    private void MakeSidePanel(ArrayList<String> indexlist){
        MakeORFlist();
        DefaultTableModel tableModel = null;
        JPanel sidepanel = new JPanel();
        if ( selected_table == null) {
            String[] columnNames = {"ID", "Sequence"};
            tableModel = new DefaultTableModel(columnNames, 0);
        }
        else{
            sidepanel.remove(selected_table);

            tableModel = (DefaultTableModel) selected_table.getModel();
            tableModel.setRowCount(0);
        }
        for(String index : indexlist){
           ORF orf = ORFlist.get(Integer.parseInt(index));
           String sequence = orfFinder.getOrf(orf);
           String[] value = new String[3];
           value[0] = index;
           value[1] = sequence;
           tableModel.addRow(value);

        }
        selected_table = new JTable(tableModel);
        selected_table.setModel(tableModel);
        tableModel.fireTableDataChanged();
        selected_table.repaint();
        System.out.println(indexlist);

        sidepanel.setBackground(DarkBlue);
        sidepanel.setLayout(new BorderLayout(5,5));
        sidepanel.setPreferredSize(new Dimension(300,1000));


        selected_table.setPreferredSize(new Dimension(1000,300));

        JScrollPane selected_table_scrollpane = new JScrollPane();
        selected_table_scrollpane.setViewportView(selected_table);
        sidepanel.add(selected_table_scrollpane,BorderLayout.NORTH);

        sidepanel.validate();
        sidepanel.repaint();
        mainFrame.add(sidepanel, BorderLayout.EAST);
        mainFrame.validate();
        mainFrame.repaint();
    }
    class SharedListSelectionHandler implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            ArrayList<String> indexlist = new ArrayList<>();
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
                            String index = (String) table.getValueAt(i, 4);
                            indexlist.add(index);
                            MakeSidePanel(indexlist);

                        }
                    }

                }

            }
        }
    }
    class MenuItemListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // TODO: 30-3-2020 make it like so that blast can be called with Blast(blastn or whaterver you choose)

            String actionEventName = e.getActionCommand();
            switch (actionEventName) {

                case "New":
                    Reader reader = new Reader();
                    File file = reader.FileChooser();
                    try {
                        orfFinder = new ORFFinder(file);
                        orfFinder.findOrfs();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    pathToFile.setText(String.valueOf(file));
                    try {                     // todo disabled due to memory issues
                        FileDisplayer(file);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    list = reader.getSeq_list();
                    ORFtable();
                    //reclist = makeRec();
                    //ORFvisualisatie();
                    controlPanel.repaint();
                    controlPanel.remove(jLabelEmptyHolderImage);
                    controlPanel.validate();
                    break;

                case "Export":
                case "BLASTn":
                case "BLASTx":
                case "tBLASTx":
                case "Exit":
                case "Upload":
                case "download":
                default:
                    break;
            }
            System.out.println("you pressed " + actionEventName);
            statusLabel.setText(e.getActionCommand() + " JMenuItem clicked.");

        }
    }
}