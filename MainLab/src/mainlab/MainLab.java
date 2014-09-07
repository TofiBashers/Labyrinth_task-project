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
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        mainlab.Labirynth labirynth;
        labirynth = new Labirynth();
        labirynth.readMap();
        labirynth.showPole();
        do
        {
            System.out.println("MakeStep!");
            labirynth.MakeStep();
        }while(!labirynth.isWin && !labirynth.isLoose);
    }
}
