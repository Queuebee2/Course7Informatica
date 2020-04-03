package ORFF_GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class VisualisatiePane extends JPanel {

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        List<Integer> list = Reader.getLengths();
        for (Integer l:list){
            g.drawLine(10,10,l-10,10);
        }
    }

    public static void createAndShowGui() {
        JFrame frame = new JFrame();
        frame.add(new VisualisatiePane());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.pack();
        frame.setVisible(true);

    }

}