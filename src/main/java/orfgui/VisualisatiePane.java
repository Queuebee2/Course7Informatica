package orfgui;
import orffinder.FastaSequence;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * VisualisatiePane is a JPanel dat can visualise the ORFs using a list with rectangles and the Fasta sequence objects
 */
public class VisualisatiePane extends JPanel {
    private ArrayList<Rectangle> reclist;
    private ArrayList<FastaSequence> seq_list;

    /**
     * constructor of the VisualisatiePane
     * @param list list of FastaSequence objects
     * @param rectlist list of Rectangle objects
     */
    VisualisatiePane(ArrayList<FastaSequence> list, ArrayList<orfgui.Rectangle> rectlist) {
        seq_list = list;
        reclist = rectlist;
    }

    @Override
    /**
     * paintComponent paints on the panel with graphics. It draws lines the size of the Sequence and goes through
     * the rectangle list to visualise each at the given position with its color
     * (for some reason this doesnt work properly)
     * todo FIX THIS
     */
    protected void paintComponent(Graphics g) {
        int firstline = 10;
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        //draw a line for each sequence with the length of it
        for (FastaSequence s: seq_list){
            int size = (int) s.getRealSize();
            firstline = firstline + 30;
            g.drawLine(10,10 + firstline,size - 10,10 + firstline);
        }
        //draw all rectangles with their information stored in the object
        for (Rectangle rec : reclist) {
            g.setColor(rec.getColor());
            g.drawRect(rec.getXpos(), rec.getYpos(), rec.getWidth(), rec.getHeight());
        }
    }

}