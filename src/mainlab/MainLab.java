/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mainlab;

import java.io.IOException;

/**
 *
 * @author TofixXx
 */
public class MainLab {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, CloneNotSupportedException {
        // TODO code application logic here
        mainlab.Labirynth labirynth;
        labirynth = new Labirynth();
        labirynth.readMap();
        
        
        /*labirynth.showPole();
        mainlab.LocalSearch TestLocalSearchIt = new LocalSearch( labirynth );
        TestLocalSearchIt.testLocalSearchIteration(new Point(1, 4), new Point(3, 1));*/
        AntSystem TestSys = new AntSystem(labirynth);
        TestSys.SearchOptimalPath();
        /*{
            System.out.println("MakeStep!");
            labirynth.MakeStep();
        }while(!labirynth.isWin && !labirynth.isLoose);*/
    }
}
