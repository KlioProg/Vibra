package com.mycompany.vibra.Content;

import com.mycompany.vibra.Content.Main_Page.MainCardPanel;

import javax.swing.*;
import java.awt.*;

public class MainAppFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainAppFrame(){
        initializeFrame();
        setupUi();
        setVisible(true);
    }

    private void initializeFrame(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int height =(int) (screensize.height * 0.75);
        int width = (int) (screensize.width * 0.75);

        setSize(width,height);
        setBackground(Color.PINK);
        setLocationRelativeTo(null);
    }

    private void setupUi(){
        this.setContentPane(new MainCardPanel());
    }


    private void changeCard(String text){
        cardLayout.show(mainPanel,text);
    }


}




