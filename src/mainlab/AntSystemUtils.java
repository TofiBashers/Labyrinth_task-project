/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainlab;

import mainlab.entity.Point;
import mainlab.entity.Edge;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author TofixXx
 */
public class AntSystemUtils {

    public static void writePathToFile(ArrayList<Edge> path, String fileName) {
        try (FileWriter writer = new FileWriter(new File(fileName));) {
            Point pastPoint = path.get(0).begin;
            for (int i = 0; i < path.size(); i++) {
                for (int j = path.get(i).path.size() - 1; j >= 0; j--) {
                    if (i == 0 && j == path.get(i).path.size() - 1) {
                        continue;
                    }
                    if (pastPoint.x == path.get(i).path.get(j).x
                            && pastPoint.y < path.get(i).path.get(j).y) {
                        writer.append('R');
                        writer.flush();
                    } else if (pastPoint.x == path.get(i).path.get(j).x
                            && pastPoint.y > path.get(i).path.get(j).y) {
                        writer.append('L');
                        writer.flush();
                    } else if (pastPoint.x > path.get(i).path.get(j).x
                            && pastPoint.y == path.get(i).path.get(j).y) {
                        writer.append('U');
                        writer.flush();
                    } else if (pastPoint.x < path.get(i).path.get(j).x
                            && pastPoint.y == path.get(i).path.get(j).y) {
                        writer.append('D');
                        writer.flush();
                    } else {
                        continue;
                    }
                    pastPoint = path.get(i).path.get(j);
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при попытке записи в файл результата");
            e.printStackTrace();
        }
    }
}
