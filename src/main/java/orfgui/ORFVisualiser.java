package orfgui;
import Database.DatabaseManager;
import blast.ORFBlaster;
import helpers.Reader;
import orffinder.FastaSequence;
import orffinder.ORF;
import orffinder.ORFFinder;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.SQLException;
import java.util.*;

/**
 * ORFVisualiser makes the GUI and functions as connection point between classes.
 */
public class ORFVisualiser extends JFrame {
    private JFrame mainFrame;
    private JLabel statusLabel;
    private JPanel controlPanel;
    private JScrollPane displayfile;
    private ORFFinder orfFinder;
    private HashMap<Integer,ORF> ORFlist;
    private JTable selected_table;
    private JPanel sidepanel;
    private JTextField pathToFile;
    private JTextArea textofFile;
    private JLabel jLabelEmptyHolderImage;
    private ArrayList<FastaSequence> list;
    private ArrayList<Rectangle> reclist;
    private JTable table;
    private HashMap<ORF,String> Selected_ORF_map;
    private JComboBox<String> Blast_option_box;
    private String Blasttype = "";
    private DatabaseManager database;
    private ORFBlaster blaster;

    private Font titel = new Font(Font.MONOSPACED,Font.BOLD,16);
    private Font combotitel = new Font(Font.MONOSPACED,Font.BOLD,13);
    private Font text = new Font(Font.MONOSPACED,Font.PLAIN,12);
    private Color black= new Color(43, 43, 43);
    private Color lighter_black= new Color(60, 63, 65);
    private Color DarkBlue= new Color(47, 79, 79);
    private Color Blue= new Color( 30,200,255);
    private Image img = Toolkit.getDefaultToolkit().getImage("src/main/resources/DNA-512.png");
    private Border blackline = BorderFactory.createLineBorder(Blue);

    /**
     * Constructor of ORFVisualiser makes basic gui
     * @throws SQLException Exception when there is a problem with connecting or uploading to MYSQL database
     */
    public ORFVisualiser() throws SQLException {

        //make loading screen
        new SplashScreenDemo();
        blaster = new ORFBlaster();
        //make GUI and add the components of the initial layout
        prepareGUI();
        HolderImage();
        showFile();
        showMenu();
    }

    public static void main(String[] args) throws SQLException {
        ORFVisualiser swingMenuDemo = new ORFVisualiser();
    }

    /**
     * SetLookAndFeel makes the UI look like the one of your operating system as well as change the Optionpane UI
     */
    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("OptionPane.messageForeground",new ColorUIResource(Color.white));
            UIManager.put("OptionPane.background",new ColorUIResource(lighter_black));
            UIManager.put("OptionPane.messageFont", titel);
            UIManager.put("OptionPane.buttonFont", combotitel);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    /**
     * prepares and makes visible, basic Frame and Controlpanel for later objects to be added
     * @throws SQLException Exception when there is a problem with connecting or uploading to MYSQL database
     */
    private void prepareGUI() throws SQLException {
        //set the look of the application to match the operating system
        setLookAndFeel();
        //make the mainFrame
        mainFrame = new JFrame("orfgui");
        mainFrame.setSize(1000, 1000);
        mainFrame.setLayout(new BorderLayout(1,1));
        mainFrame.getContentPane().setBackground(black);
        mainFrame.setIconImage(img);

        //windowListener added to mainframe
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        //Make ControlPanel
        controlPanel = new JPanel();
        controlPanel.setBounds(1, 1, 950, 930);
        controlPanel.setLayout(new BorderLayout(10,10));
        controlPanel.setBackground(black);

        //Add controlPanel to mainFrame
        mainFrame.add(controlPanel,BorderLayout.CENTER);

        //set the Gui visible
        controlPanel.setVisible(true);
        mainFrame.setVisible(true);
    }

    /**
     * Makes a textarea for the content of the file and places it in a scrollpane as well as a path to file textfield
     * which is added to the controlpanel.
     */
    private void showFile() {

        //make TextArea for text of the file
        textofFile = new JTextArea(200, 100);
        textofFile.setBackground(lighter_black);
        textofFile.setForeground(Color.white);
        textofFile.setFont(text);
        textofFile.setPreferredSize(new Dimension(940,100));
        textofFile.setText("Display of file content");

        //make textField to add the path to the file to
        pathToFile = new JTextField("Path/of/File");
        pathToFile.setBackground(Blue);
        pathToFile.setForeground(Color.white);
        pathToFile.setEditable(false);
        pathToFile.setFont(titel);
        pathToFile.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Blue));
        pathToFile.setPreferredSize(new Dimension(970,25));

        //make a Scrollpane to add the textArea to so that all of the file can be read
        displayfile = new JScrollPane(textofFile);
        displayfile.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Blue));
        displayfile.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        displayfile.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        displayfile.setPreferredSize(new Dimension(940,100));

        //add components to the controlPanel
        controlPanel.add(pathToFile, BorderLayout.NORTH);
        controlPanel.add(displayfile,BorderLayout.CENTER);
    }

    /**
     * makes Rectangle objects out of the generated ORF objects with size and start position and a random color
     * for visualisation. These rectangles are put into an Arraylist.
     * @return Arraylist of rectangles
     */
    private ArrayList<Rectangle> makeRec() {

        //list for rectangles of ORF objects
        reclist = new ArrayList<Rectangle>();
        //distance from border of the panel at the top
        int firstline = 10;

        //loop through fastaSequence objects add 30 each time there is a new Sequence (ORFs are displayed per sequence)
        for (FastaSequence fastaSequence : list) {
             firstline = firstline + 30;
             // loop through ORFs of the sequence
             for (ORF orf : fastaSequence) {
                 Random rand = new Random();
                 //start position of orf
                 int start = (int) orf.getStartPosInSequence();
                 //size of the orf
                 int size = (int) orf.getSize();
                 //random color of the orf rectangle
                 Color randomColor = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
                 //make rectangle with this information and add it to Arraylist of Rectangles
                 Rectangle rec = new Rectangle(10 + start, firstline + 5, size, 10, randomColor);
                 reclist.add(rec);
            }
         }
         System.out.println(reclist);
        return reclist;
    }

    /**
     * ORFvisualisatie makes a Jpanel by calling to VisualisatiePane. and makes it to the correct size according to
     * data input. (work in progress..)
     */
    private void ORFvisualisatie(){
        //make the visScreen
        JPanel visScreen = new VisualisatiePane(list,reclist);
        visScreen.setBackground(lighter_black);

        // get the size of the largest sequence
        int largest = 0;
        for (FastaSequence fastaSequence : list){
            int Length = (int) fastaSequence.getRealSize();
            if (Length > largest) {
                largest = Length;
                System.out.println(largest);
                }
            }
        // set the visScreen size according to the largest sequence size
        visScreen.setPreferredSize(new Dimension(largest,685));

        // add the visScreen to a Scrollpane
        JScrollPane displayORF = new JScrollPane(visScreen);
        displayORF.setBorder(blackline);
        displayORF.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        displayORF.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // add the display to the ControlPanel
        controlPanel.add(displayORF,BorderLayout.SOUTH);

        // TODO: 31-3-2020 make visualisation work
    }

    /**
     * ORFtable makes a table with all orfs generated from teh file containing Sequence ID, Start postion, End position
     * Length and the ID of the orf. ORFtable is added to a Jscrollpanel for full visualisation.
     */
    private void ORFtable() {

        //initialise the tableModel
        DefaultTableModel tableModel;
        //Arraylist with data to put in the table
        ArrayList<String[]> table_list;

        //if there is no table then make a table model with the columnNames
        if (table == null) {
            String[] columnNames = {"FastaSequence ID", "Start", "End", "Length", "ID"};
            tableModel = new DefaultTableModel(columnNames, 0);
        }
        // if there is already a table, get the table model and delete all rows
        else{

                tableModel = (DefaultTableModel) table.getModel();
                tableModel.setRowCount(0);
            }
            // for each sequence make a table list
            for (FastaSequence fastaSequence : list) {
                table_list = fastaSequence.makeTable_list();
                // for all values in the table list add them to the tableModel
                for (String[] string : table_list) {
                    tableModel.addRow(string);
                }
                //make a table with the tableModel created earlier
                table = new JTable(tableModel);
                table.setBackground(lighter_black);
                table.setForeground(Color.white);
                JTableHeader header = table.getTableHeader();
                header.setOpaque(false);
                header.setFont(titel);
                header.setBackground(Blue);
                header.setForeground(Color.white);
                table.setBorder(blackline);
                table.setFont(text);
                table.setCellSelectionEnabled(true);
                table.setModel(tableModel);
                //set a selection model to make selection possible
                table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                //make sure the table updates if there have been changes
                tableModel.fireTableDataChanged();
                //repaint to make updated data appear in gui
                table.repaint();

                //adding it to ScrollPane
                JScrollPane sp = new JScrollPane(table);;
                sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                sp.setBorder(blackline);
                sp.setViewportView(table);

                // get the listSelectionModel and add a selection listner to it
                ListSelectionModel listSelectionModel = table.getSelectionModel();
                listSelectionModel.addListSelectionListener(
                        new SharedListSelectionHandler()
                );
                // add the scrollPane containing the table
                controlPanel.add(sp,BorderLayout.SOUTH);
            }
    }

    /**
     * ShowMenu makes the Menu bar of the application
     */
    private void showMenu() {
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

        //add actionlistners to all menu items
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

                //remove the display of the file contents
                if (showWindowMenu.getState()) {
                    controlPanel.remove(displayfile);
                    mainFrame.repaint();
                } else {
                    //add the contents of the file
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

    /**
     * MakeORFlist makes a hashmap with the orf-ID as key and the correlated ORF object as value
     */
    private void MakeORFlist(){
        ORFlist =  new HashMap<>();
        for(FastaSequence fastaSequence : list){
            for(ORF orf : fastaSequence){
               int ID =  orf.getID();
               ORFlist.put(ID,orf);
            }
        }
    }

    /**
     * UploadSuccesful makes a label containing an gif and a label to let the user know the upload was successful.
     * this is added to the side panel
     */
    private void UploadSuccessful(){
        //get the image
        ImageIcon image = new ImageIcon("src/main/resources/dance.gif");
        //make the label containing the image
        JLabel succesupload = new JLabel("", image, JLabel.CENTER);
        //make a label to show text
        JLabel textupload = new JLabel("Uploaded data Succesfully!!");
        succesupload.setBorder(blackline);
        succesupload.setPreferredSize(new Dimension(250,300));
        textupload.setForeground(Color.white);
        textupload.setFont(titel);

        //add components to sidePanel and validate/repaint it to update it in the GUI
        sidepanel.add(textupload);
        sidepanel.add(succesupload);
        sidepanel.validate();
        sidepanel.repaint();
    }

    /**
     * HolderImage creates an image it has no use.. but its cute! and added in the place of the future ORF table
     */

    private void HolderImage(){
        ImageIcon image = new ImageIcon("src/main/resources/hatebed.gif");
        jLabelEmptyHolderImage =new JLabel("",image,JLabel.CENTER);
        controlPanel.add(jLabelEmptyHolderImage,BorderLayout.SOUTH);
    }

    /**
     * FileDisplayer takes in a File name and reads it in. File content is displayed in the Text Area(textofFile)
     * @param file the path of the file chosen in Reader
     * @throws IOException Exception during file reading
     */
    private void FileDisplayer(File file) throws IOException {

        BufferedReader input = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(
                                file)));
        textofFile.read(input, "READING FILE :)");
    }

    /**
     * MakeSeleted_Table makes a table with all the orfs selected from the big ORF table. In this table the orf-ID
     * and its sequence is shown. It takes in a list of indexes of the orfs selected this index is the ID of the ORF.
     * These ID's are then used to get the ORF object from the ORFlist. Selected ORF map is created with as key ORF and
     * as value the sequence of the ORF. This map is used to display the data in the table
     * @param indexlist Arraylist containing ID's of selected ORFs
     */
    private void MakeSelected_Table(ArrayList<String> indexlist){

        // Hashmap with the selected ORFs as key and the sequence as value
        Selected_ORF_map = new HashMap<>();
        // List with ORFs
        ArrayList<ORF> Selected_ORF_list = new ArrayList<>();
        //initialise tableModel
        DefaultTableModel tableModel;

        // if the selected table doesnt exist make it
        if ( selected_table == null) {
            String[] columnNames = {"ID", "FastaSequence"};
            tableModel = new DefaultTableModel(columnNames, 0);
        }
        // if it does exist then remove the table get the model and delete all rows
        else{
            sidepanel.remove(selected_table);
            tableModel = (DefaultTableModel) selected_table.getModel();
            tableModel.setRowCount(0);
        }
        //for each index get the ORF object and add the needed information to the list or the map and add it to table
        for(String index : indexlist){
            ORF orf = ORFlist.get(Integer.parseInt(index));
            String sequence = orfFinder.getOrf(orf);
            Selected_ORF_map.put(orf,sequence);
            Selected_ORF_list.add(orf);
            String[] value = new String[3];
            value[0] = index;
            value[1] = sequence;
            tableModel.addRow(value);
        }
        //make the table with the tableModel created earlier
        selected_table = new JTable(tableModel);
        //set the model (can sometimes create problems if not done)
        selected_table.setModel(tableModel);
        //make table update and repaint
        tableModel.fireTableDataChanged();
        selected_table.repaint();
        selected_table.setPreferredSize(new Dimension(1000,1000));

        // make scrollPane to put the table in
        JScrollPane selected_table_scrollpane = new JScrollPane();
        selected_table_scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        selected_table_scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        selected_table_scrollpane.setViewportView(selected_table);
        selected_table_scrollpane.setBorder(blackline);
        selected_table_scrollpane.setPreferredSize(new Dimension(280,480));

        // customize table
        selected_table.setBackground(lighter_black);
        selected_table.setForeground(Color.white);
        selected_table.setBorder(blackline);
        selected_table.setFont(text);
        JTableHeader header = selected_table.getTableHeader();
        header.setOpaque(false);
        header.setFont(titel);
        header.setBackground(Blue);
        header.setForeground(Color.white);

        // add the scrollPane to the sidePanel
        sidepanel.add(selected_table_scrollpane);
        // call method to add upload button
        MakeUpload_button();
        // call method to add blast button and combobox
        MakeBlast_button(Selected_ORF_list);
    }

    /**
     * MakeBlast_button makes the button and combobox related to the blast process. it takes the selected orfs via an
     * Arraylist to the blaster.
     * @param selected_ORF_list Arraylist containing ORFs
     */
    private void MakeBlast_button(ArrayList<ORF> selected_ORF_list){
        //make Blast combobox with the two types of blast that can be used
        String[] BLAST_types = { "blastn","blastx" };
        Blast_option_box = new JComboBox<>(BLAST_types);
        Blast_option_box.setPreferredSize(new Dimension(280,30));
        Blast_option_box.setBackground(DarkBlue);
        Blast_option_box.setForeground(Color.black);
        Blast_option_box.setFont(combotitel);
        Blast_option_box.setOpaque(false);

        // make blast button
        JButton blast_button = new JButton("BLAST");
        blast_button.setPreferredSize(new Dimension(140,20));
        // add actionlistner to blast button
        blast_button.addActionListener(e -> {
            //get the string selected in the combobox and give it to ORFBlaster
            Blasttype = (String) Blast_option_box.getSelectedItem();
            assert Blasttype != null;
            switch (Blasttype) {
                case "blastn": blaster.blastORFselection(selected_ORF_list, Blasttype, "nt");
                    break;
                case "blastx": blaster.blastORFselection(selected_ORF_list,Blasttype,"nr");
                    break;
                case ""  : blaster.blastORFselection(selected_ORF_list,"blastn", "nt");
                    break;
            }
        });
        // add components to sidePanel
        sidepanel.add(blast_button);
        sidepanel.add(Blast_option_box);
    }

    /**
     * MakeUpload_Button makes a button to upload to the database and calls on the DatabaseManager to insert
     * the selected orfs in the selected_ORF_map.
     */
    private void MakeUpload_button(){
        //make upload button
        JButton upload_button = new JButton("UPLOAD");
        upload_button.setPreferredSize(new Dimension(140,20));
        // add action listener to upload button
        upload_button.addActionListener(e -> {
            try {
                // call on the DatabaseManager to insert the data
                System.out.println("amount "+Selected_ORF_map.size());
                database.insert(Selected_ORF_map);
                // call on method to make clear to the user it was successful
                UploadSuccessful();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        // add component to sidePanel
        sidepanel.add(upload_button);
    }

    /**
     * MakeSidePanel makes the sidepanel that appears after a selection has been made from the ORF table.
     * @param indexlist list of IDs of the selected orfs
     */
    private void MakeSidePanel(ArrayList<String> indexlist){
        // make sidePanel
        sidepanel = new JPanel();

        sidepanel.setBackground(black);
        sidepanel.setLayout(new FlowLayout());
        sidepanel.setPreferredSize(new Dimension(300,1000));
        sidepanel.setBorder(blackline);
        MakeSelected_Table(indexlist);

        //update the sidePanel
        sidepanel.validate();
        sidepanel.repaint();
        //add the sidPanel to the mainFrame
        mainFrame.add(sidepanel, BorderLayout.EAST);
        //update the mainFrame
        mainFrame.validate();
        mainFrame.repaint();
    }

    /**
     * SharedListSelectionHandler handles the selection in the ORF table
     */
    class SharedListSelectionHandler implements ListSelectionListener {
        /**
         * valueChanged makes an indexlist of all de IDs of the selected rows when the user stops changing the selection
         * @param e action
         */
        public void valueChanged(ListSelectionEvent e) {
            ArrayList<String> indexlist = new ArrayList<>();
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();

            // boolean to check if the user is done with changing the selection
            boolean isAdjusting = e.getValueIsAdjusting();
            if (lsm.isSelectionEmpty()) {
            } else {
                // if no more changes are made to the selection get the index of the row selected
                if (!isAdjusting) {
                    int minIndex = lsm.getMinSelectionIndex();
                    int maxIndex = lsm.getMaxSelectionIndex();
                    // for all selected rows get the ID out of column 4 and put it in the index list
                    for (int i = minIndex; i <= maxIndex; i++) {
                        if (lsm.isSelectedIndex(i)) {
                            String index = (String) table.getValueAt(i, 4);
                            System.out.println(index);
                            indexlist.add(index);

                        }
                    }
                    // make the sidePanel
                    MakeSidePanel(indexlist);
                }

            }
        }
    }

    /**
     * MenuItemlistner handles the clicks on the menu
     */
    class MenuItemListener implements ActionListener {

        /**
         * takes the click and gets the actionEventName to check which menu item was clicked and then gives
         * the correlated response
         * @param e action
         */
        public void actionPerformed(ActionEvent e) {
            // TODO: 30-3-2020 make it like so that blast can be called with Blast(blastn or whaterver you choose)

            String actionEventName = e.getActionCommand();
            switch (actionEventName) {

                // if new ORF search was clicked
                case "New":
                    // make a new reader
                    Reader reader = new Reader();
                    // open file chooser
                    File file = reader.FileChooser();
                    try {
                        // make ORFFinder
                        orfFinder = new ORFFinder();
                        orfFinder.setFile(file);
                        orfFinder.findOrfs();
                        // background for error popups
                        UIManager.put("Panel.background",new ColorUIResource(black));
                        // start connection to database
                        database = new DatabaseManager();
                    } catch (IOException | SQLException ex) {
                        ex.printStackTrace();
                    }
                    pathToFile.setText(String.valueOf(file));
                    try {
                        // make file content display
                        FileDisplayer(file);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    //list with all FastaSequence objects
                    list = orfFinder.getFastaSequences();
                    //make the list of ORFs with their id as key
                    MakeORFlist();
                    //make the table with all orfs
                    ORFtable();
                    // update the controlPanel and remove the holder image
                    controlPanel.repaint();
                    controlPanel.remove(jLabelEmptyHolderImage);
                    controlPanel.validate();
                    break;

                    // not yet implemented
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
        }
    }
}
