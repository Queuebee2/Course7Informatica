package orfgui;

import javax.swing.*;
import java.awt.*;

public class SplashScreenDemo {
    JFrame frame;
    JLabel image=new JLabel(new ImageIcon("src/main/resources/genes.gif"));
    JLabel text=new JLabel("orffinder");
    JProgressBar progressBar=new JProgressBar();
    JLabel message=new JLabel();
    SplashScreenDemo()
    {
        createGUI();
        addImage();
        addText();
        addProgressBar();
        addMessage();
        runningPBar();
    }
    public void createGUI(){
        frame=new JFrame();
        frame.getContentPane().setLayout(null);
        frame.setUndecorated(true);
        frame.setSize(600,400);
        frame.setLocationRelativeTo(null);
        Color x= new Color( 30,200,255);
        frame.getContentPane().setBackground(x);
        frame.setVisible(true);

    }
    public void addImage(){
        image.setSize(600,200);
        frame.add(image);
    }
    public void addText()
    {
        text.setFont(new Font("arial",Font.BOLD,30));
        text.setBounds(220,220,600,40);
        text.setForeground(Color.white);
        frame.add(text);
    }
    public void addMessage()
    {
        message.setBounds(250,320,200,40);
        message.setForeground(Color.white);
        message.setFont(new Font("arial",Font.BOLD,15));
        frame.add(message);
    }
    public void addProgressBar(){
        progressBar.setBounds(100,280,400,30);
        progressBar.setBorderPainted(true);
        progressBar.setStringPainted(true);
        Color x= new Color(47, 79, 79);
        progressBar.setBackground(Color.WHITE);
        progressBar.setForeground(x);
        progressBar.setValue(0);
        frame.add(progressBar);
    }
    public void runningPBar(){
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
