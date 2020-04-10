package orfgui;

import javax.swing.*;
import java.awt.*;

public class SplashScreenDemo {
    private JFrame frame;
    private JLabel image=new JLabel(new ImageIcon("src/main/resources/genes.gif"));
    private JLabel text=new JLabel("orffinder");
    private JProgressBar progressBar=new JProgressBar();
    private JLabel message=new JLabel();

    /**
     * Constructor of splashScreen
     */
    SplashScreenDemo()
    {
        createGUI();
        addImage();
        addText();
        addProgressBar();
        addMessage();
        runningPBar();
    }

    /**
     * Creates the GUI
     */
    private void createGUI(){
        frame=new JFrame();
        frame.getContentPane().setLayout(null);
        frame.setUndecorated(true);
        frame.setSize(600,400);
        frame.setLocationRelativeTo(null);
        Color x= new Color( 30,200,255);
        frame.getContentPane().setBackground(x);
        frame.setVisible(true);

    }

    private void addImage(){
        image.setSize(600,200);
        frame.add(image);
    }

    private void addText()
    {
        text.setFont(new Font("arial",Font.BOLD,30));
        text.setBounds(220,220,600,40);
        text.setForeground(Color.white);
        frame.add(text);
    }
    private void addMessage()
    {
        message.setBounds(250,320,200,40);
        message.setForeground(Color.white);
        message.setFont(new Font("arial",Font.BOLD,15));
        frame.add(message);
    }
    private void addProgressBar(){
        progressBar.setBounds(100,280,400,30);
        progressBar.setBorderPainted(true);
        progressBar.setStringPainted(true);
        Color x= new Color(47, 79, 79);
        progressBar.setBackground(Color.WHITE);
        progressBar.setForeground(x);
        progressBar.setValue(0);
        frame.add(progressBar);
    }

    /**
     * Make the progressbar run till it hits 100 then make it disappear
     */
    private void runningPBar(){
        int i=0;

        while( i<=100)
        {
            try{
                Thread.sleep(25);            // todo  echte dingen laden
                progressBar.setValue(i);
                message.setText("LOADING "+Integer.toString(i)+"%");
                i++;
                if(i==100)
                    frame.dispose();
            }catch(Exception e){
                e.printStackTrace();
            }



        }
    }
}
