/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;
import javax.swing.border.LineBorder;
import mainlab.entity.Edge;
import mainlab.entity.MapCell;
import mainlab.Labyrinth;
/**
 *
 * @author Елена
 */
public class MainPanel extends JPanel{
    Labyrinth lab;
    ArrayList<Edge> OptimalPath;
    java.util.Timer timer =  new java.util.Timer();
    TimerTask task = new TimerTask()
    {
        public void run()
        {
            if (EdgeNum == 0 && PointNum == OptimalPath.get(EdgeNum).path.size() - 1) {
                PointNum--;
                repaint();
                return;
            }
            if (PointNum == 0) {
                EdgeNum++;
                if (EdgeNum == OptimalPath.size()) {
                    int x = OptimalPath.get(OptimalPath.size() - 1).path.get(0).x;
                    int y = OptimalPath.get(OptimalPath.size() - 1).path.get(0).y;
                    lab.stepToPos(x, y);
                    lab.updateMap();
                    timer.cancel();
                    repaint();
                    return;
                } else {
                    PointNum = OptimalPath.get(EdgeNum).path.size() - 1;
                }
            }
            int x = OptimalPath.get(EdgeNum).path.get(PointNum).x;
            int y = OptimalPath.get(EdgeNum).path.get(PointNum).y;
            lab.stepToPos(x, y);
            lab.updateMap();
            PointNum--;
            repaint();
        }
    };
    int EdgeNum;
    int PointNum;
    MainPanel(Labyrinth lab, ArrayList<Edge> OptimalPath){
        super();
        this.lab=lab;
        EdgeNum = 0;
        PointNum = OptimalPath.get(EdgeNum).path.size()-1;
        setBackground(Color.GRAY);
        setBorder(new LineBorder(Color.DARK_GRAY, 1));
        this.OptimalPath = new ArrayList();
        this.OptimalPath.addAll(OptimalPath);   
        timer.schedule(task,0, 1000);
        };
 
    @Override
    public void paint(Graphics g){
        Graphics2D g2d = (Graphics2D)g;
        super.paint(g2d);
        for (int i=0; i<=lab.height*48; i+=48){
            g.drawLine(0, i, lab.height*48, i);
        }
        for (int j=0; j<=lab.width*48; j+=48){
            g.drawLine(j, 0, j, lab.width*48);
        }
        for (int i=0; i<lab.height; i++){
            for (int j=0; j<lab.width; j++){
                if (lab.map[i][j]==MapCell.ROBOT) {
                    ImageIcon icon = new ImageIcon("miner.jpg");
                    Image miner = icon.getImage();
                    g2d.drawImage(miner, j*48, i*48, this);
                }
                if(lab.map[i][j]==MapCell.STONE) {
                    ImageIcon icon = new ImageIcon("stone.png");
                    Image stone = icon.getImage();
                    g2d.drawImage(stone, j*48, i*48, this);
                }
                 if(lab.map[i][j]==MapCell.WALL) {
                    ImageIcon icon = new ImageIcon("wall.png");
                    Image wall = icon.getImage();
                    g2d.drawImage(wall, j*48, i*48, this);
                }
                  if(lab.map[i][j]==MapCell.OPENEDLIFT || lab.map[i][j]==MapCell.CLOSEDLIFT || lab.map[i][j]==MapCell.LIFT) {
                    ImageIcon icon = new ImageIcon("lift.jpg");
                    Image lift = icon.getImage();
                    g2d.drawImage(lift, j*48, i*48, this);
                }
                   if(lab.map[i][j]==MapCell.LAMBDA) {
                    ImageIcon icon = new ImageIcon("lambda.jpg");
                    Image lambda = icon.getImage();
                    g2d.drawImage(lambda, j*48, i*48, this);
                }
                  if(lab.map[i][j]==MapCell.GROUND) {
                    ImageIcon icon = new ImageIcon("ground.png");
                    Image ground = icon.getImage();
                    g2d.drawImage(ground, j*48, i*48, this);
                } 
                if(lab.map[i][j]==MapCell.EMPTY) {
                    ImageIcon icon = new ImageIcon("empty.png");
                    Image empty = icon.getImage();
                    g2d.drawImage(empty, j*48, i*48, this);
                }  
                if(lab.map[i][j]==MapCell.OPENEDLIFT) {
                    ImageIcon icon = new ImageIcon("opened_lift.jpg");
                    Image openedlift = icon.getImage();
                    g2d.drawImage(openedlift, j*48, i*48, this);
                }  
            }
        }
    }
}
