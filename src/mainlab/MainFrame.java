/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mainlab;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Елена
 */
public class MainFrame extends JFrame{
//    MainFrame(String s, Labirynth lab) {
//        super(s);
//    
//    setSize(lab.height*48+100, lab.width*48+100);
//    setVisible(true);
//    JPanel panel = new MainPanel(lab);
//    this.add(panel);
//    
//    addWindowListener(new WindowAdapter() {
//        public void windowClosing(WindowEvent e){
//            System.exit(0);
//        }
//    });
//}
final JPanel mainPanel;
  MainFrame(String s, final Labirynth lab, ArrayList<Edge> OptimalPath){ 
    super(s);
    setSize(lab.width*48+215, lab.height*48+66);
    setVisible(true);
    this.setLayout(new BorderLayout());
    mainPanel = new MainPanel(lab, OptimalPath);
    mainPanel.setPreferredSize(new Dimension(lab.width*48+48, lab.height*48+4));
    this.add(mainPanel, BorderLayout.CENTER);
    JPanel headPanel = new JPanel();
    headPanel.setPreferredSize(new Dimension(200+lab.width*48, 30));
    headPanel.setBorder(new LineBorder(Color.DARK_GRAY, 1));
    headPanel.setBackground(Color.YELLOW);
    this.add(headPanel, BorderLayout.NORTH);
    JPanel optionPanel = new JPanel();
    optionPanel.setPreferredSize(new Dimension(200, 300));
    optionPanel.setBorder(new LineBorder(Color.DARK_GRAY, 1));
    this.add(optionPanel, BorderLayout.EAST);
    addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e){
            System.exit(0);
        }
    });
        }
}
