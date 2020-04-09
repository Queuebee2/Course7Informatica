package orfgui;
import orffinder.FastaSequence;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class VisualisatiePane extends JPanel {
    private ArrayList<Rectangle> reclist;
    private ArrayList<FastaSequence> seq_list;

    VisualisatiePane(ArrayList<FastaSequence> list, ArrayList<orfgui.Rectangle> rectlist) {
        seq_list = list;
        reclist = rectlist;
    }
    @Override
    protected void paintComponent(Graphics g) {
        int firstline = 10;
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        for (FastaSequence s: seq_list){
            int size = (int) s.getRealSize();
            firstline = firstline + 30;
            g.drawLine(10,10 + firstline,size - 10,10 + firstline);
        }
        for (Rectangle rec : reclist) {
            g.setColor(rec.getColor());
            g.drawRect(rec.getXpos(), rec.getYpos(), rec.getWidth(), rec.getHeight());
        }
    }

    //public static void createAndShowGui() {
    //    JFrame frame = new JFrame();
    //    frame.add(new VisualisatiePane());
    //    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //    frame.setLocationByPlatform(true);
    //    frame.pack();
    //    frame.setVisible(true);

    //}

}