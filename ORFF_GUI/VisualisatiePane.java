package ORFF_GUI;

import java.awt.*;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class VisualisatiePane extends JPanel {

    @Override
    protected void paintComponent(Graphics g) {
        int firstline = 10;
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        List<Integer> list = Reader.getLengths();
        for (Integer l:list){
            firstline = firstline + 30;
            g.drawLine(10,10 + firstline,l-10,10 + firstline);
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