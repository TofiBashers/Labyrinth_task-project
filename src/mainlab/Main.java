
package mainlab;

import mainlab.entity.Edge;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import gui.MainFrame;

/**
 *
 * @author TofixXx
 */

/** Релизация задачи ICFPC-2012, при помощи алгоритмов
 * A*, и Ant Colony.
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Labyrinth labyrinth;
        labyrinth = new Labyrinth();
        labyrinth.readMap();
        AntSystem antSystem = new AntSystem(labyrinth);
        ArrayList<Edge> optimalPath = antSystem.searchOptimalPath();
        AntSystemUtils.writePathToFile(optimalPath, "result.txt");
        MainFrame mainFrame = new MainFrame("robot", labyrinth, optimalPath);
    }
}
