/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mainlab;

/**
 *
 * @author TofixXx
 */
import mainlab.entity.MapCell;
import mainlab.entity.Point;
import java.io.*;
import java.util.ArrayList;

/** Класс представляет собой удобный способ хранения лабиринта с состоянием*/
public class Labyrinth {
    
    public int count = 0;
    public int width, height;
    public MapCell[][] map;
    public Point robotPosition;
    public boolean isWin = false;
    public boolean isLoose = false;
    public int lambdaNum;
    public int lambdaMeter = 0;
    
    Labyrinth() {
    }
    
    void printMap() {
        char buf = '-';
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                switch (map[i][j]) {
                    case EMPTY:
                        buf = ' ';
                        break;
                    case WALL:
                        buf = '#';
                        break;
                    case ROBOT:
                        buf = 'R';
                        break;
                    case STONE:
                        buf = 'S';
                        break;
                    case OPENEDLIFT:
                        buf = 'O';
                        break;
                    case CLOSEDLIFT:
                        buf = 'L';
                        break;
                    case GROUND:
                        buf = '.';
                        break;
                    case LAMBDA:
                        buf = '\\';
                        break;
                }
                System.out.print(buf);
            }
            System.out.println();
        }
        System.out.println("scores = " + count);
        System.out.println("lambds = " + lambdaMeter);
    }
    
    public void updateMap() {
        ArrayList<Point> movedStones = new ArrayList();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (map[i][j] == MapCell.STONE) {
                    if (movedStones.contains(new Point(i, j))) {
                        continue;
                    }
                    switch (map[i + 1][j]) {
                        case EMPTY: {
                            map[i + 1][j] = MapCell.STONE;
                            map[i][j] = MapCell.EMPTY;
                            if (map[i + 2][j] == MapCell.ROBOT) {
                                //Робот уничтожен
                                isLoose = true;
                            }
                            movedStones.add(new Point(i + 1, j));
                            //Под камнем пустота, камень сдвинут в клетку
                            break;
                        }
                        case STONE: {
                            if (map[i][j + 1] == MapCell.EMPTY
                                    && map[i + 1][j + 1] == MapCell.EMPTY) {
                                map[i + 1][j + 1] = MapCell.STONE;
                                map[i][j] = MapCell.EMPTY;
                                if (map[i + 2][j + 1] == MapCell.ROBOT) {
                                    //Робот уничтожен
                                    isLoose = true;
                                }
                                movedStones.add(new Point(i + 1, j + 1));
                            } else if (map[i][j - 1] == MapCell.EMPTY
                                    && map[i + 1][j - 1] == MapCell.EMPTY) {
                                map[i + 1][j - 1] = MapCell.STONE;
                                map[i][j] = MapCell.EMPTY;
                                if (map[i + 2][j - 1] == MapCell.ROBOT) {
                                    //Робот уничтожен
                                    isLoose = true;
                                }
                                movedStones.add(new Point(i + 1, j - 1));
                            }
                            break;
                        }
                        case LAMBDA: {
                            if (map[i][j + 1] == MapCell.EMPTY
                                    && map[i + 1][j + 1] == MapCell.EMPTY) {
                                map[i + 1][j + 1] = MapCell.STONE;
                                map[i][j] = MapCell.EMPTY;
                                if (map[i + 2][j + 1] == MapCell.ROBOT) {
                                    //System.out.println("Робот уничтожен");
                                    isLoose = true;
                                }
                                movedStones.add(new Point(i + 1, j + 1));
                            }
                            break;
                        }
                    }
                }
                if (map[i][j] == MapCell.CLOSEDLIFT && lambdaMeter == 0) {
                    map[i][j] = MapCell.OPENEDLIFT;
                }
            }
        }
        
    }
    
    public void stepToPos(int x, int y) {
        switch (map[x][y]) {
            case LAMBDA:
                count += 25;
                lambdaMeter--;
                break;
            case STONE:
                if (robotPosition.x == x && robotPosition.y < y) {
                    if (map[x][y + 1] != MapCell.EMPTY) {
                        return;
                    }
                    map[x][y + 1] = MapCell.STONE;
                    count--;
                } else if (robotPosition.x == x && robotPosition.y > y) {
                    if (map[x][y - 1] != MapCell.EMPTY) {
                        return;
                    }
                    map[x][y - 1] = MapCell.STONE;
                } else {
                    return;
                }
                count--;
                break;
            case OPENEDLIFT:
                isWin = true;
                count += 50 * (lambdaNum - lambdaMeter);
                break;
            default:
                if (map[x][y] == MapCell.WALL
                        || map[x][y] == MapCell.CLOSEDLIFT) {
                    return;
                }
                count--;
                break;
        }
        map[x][y] = MapCell.ROBOT;
        map[robotPosition.x][robotPosition.y] = MapCell.EMPTY;
        robotPosition.x = x;
        robotPosition.y = y;
    }
    
    void readMap() {
        ArrayList<String> stringMap = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("map4.txt"));) {
            while (reader.ready()) {
                stringMap.add(reader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        
        for (String stringMapElem : stringMap) {
            System.out.println(stringMapElem);
        }
        int maxHeight = stringMap.size();
        int maxWidth = 0;
        for (String stringMapElem : stringMap) {
            if (stringMapElem.length() > maxWidth) {
                maxWidth = stringMapElem.length();
            }
        }
        width = maxWidth;
        height = maxHeight;
        System.out.println(maxHeight);
        System.out.println(maxWidth);
        map = new MapCell[height][width];
        for (int i = 0; i < stringMap.size(); i++) {
            for (int j = 0; j < stringMap.get(i).length(); j++) {
                switch (stringMap.get(i).charAt(j)) {
                    case 'R':
                        map[i][j] = MapCell.ROBOT;
                        robotPosition = new Point(i, j);
                        break;
                    case '*':
                        map[i][j] = MapCell.STONE;
                        break;
                    case 'L':
                        map[i][j] = MapCell.CLOSEDLIFT;
                        break;
                    case 'O':
                        map[i][j] = MapCell.OPENEDLIFT;
                        break;
                    case '.':
                        map[i][j] = MapCell.GROUND;
                        break;
                    case '#':
                        map[i][j] = MapCell.WALL;
                        break;
                    case '\\':
                        map[i][j] = MapCell.LAMBDA;
                        lambdaMeter++;
                        break;
                    case ' ':
                        map[i][j] = MapCell.EMPTY;
                        break;
                    default:
                        System.err.println("Некорректное значение клетки поля");
                        System.exit(0);
                }
            }
            for (int j = stringMap.get(i).length(); j < width; j++) {
                map[i][j] = MapCell.EMPTY;
            }
        }
        lambdaNum = lambdaMeter;
    }
    
    Labyrinth(Labyrinth lab) {
        this.width = lab.width;
        this.height = lab.height;
        map = new MapCell[height][width];
        for (int i = 0; i < map.length; i++) {
            System.arraycopy(lab.map[i], 0, this.map[i], 0, lab.map[i].length);
        }
        this.robotPosition = new Point(lab.robotPosition.x, lab.robotPosition.y);
        this.count = lab.count;
        this.lambdaMeter = lab.lambdaMeter;
        this.lambdaNum = lab.lambdaNum;
    }
}
