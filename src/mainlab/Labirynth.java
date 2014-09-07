/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mainlab;

/**
 *
 * @author Елена
 */
import java.io.*;
import java.util.ArrayList;

public class Labirynth {

    public int count = 0;
    public int width, height;
    public Pole[][] laba;
    public Position robotPosition;
    public boolean isWin = false;
    public boolean isLoose = false;
    public int lambdaNum;
    public int lambdaMeter = 0;

    Labirynth() {
    }

    void showPole() {
        char buf = '-';
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                switch (laba[i][j]) {
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

    void updateMap() {
        ArrayList<Point> movedStones = new ArrayList();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (laba[i][j] == Pole.STONE ) {
                    if(movedStones.contains(new Point(i, j)))
                            {
                                //System.out.println("Попытка повторного шага симуляции");
                                continue;        
                            }
                    switch (laba[i + 1][j]) {
                        case EMPTY: {
                            laba[i + 1][j] = Pole.STONE;
                            laba[i][j] = Pole.EMPTY;
                            if(laba[i + 2][j] == Pole.ROBOT)
                            {
                                //System.out.println("Робот уничтожен");
                                isLoose = true;
                            }
                            movedStones.add(new Point(i + 1, j));
                            //System.out.println("Под камнем пустота, камень сдвинут в клетку: " + (i + 1) + " " + j);
                            break;
                        }
                        case STONE: {
                            if (laba[i][j + 1] == Pole.EMPTY && laba[i + 1][j + 1] == Pole.EMPTY) {
                                laba[i + 1][j + 1] = Pole.STONE;
                                laba[i][j] = Pole.EMPTY;
                                if(laba[i + 2][j + 1] == Pole.ROBOT)
                                {
                                    //System.out.println("Робот уничтожен");
                                   isLoose = true;
                                }
                                movedStones.add(new Point(i + 1, j + 1));
                            } else if (laba[i][j - 1] == Pole.EMPTY && laba[i + 1][j - 1] == Pole.EMPTY) {
                                laba[i + 1][j - 1] = Pole.STONE;
                                laba[i][j] = Pole.EMPTY;
                                if(laba[i + 2][j - 1] == Pole.ROBOT)
                                {
                                    //System.out.println("Робот уничтожен");
                                   isLoose = true;
                                }
                                movedStones.add(new Point(i + 1, j - 1));
                            }
                            break;
                        }
                        case LAMBDA: {
                            if (laba[i][j + 1] == Pole.EMPTY && laba[i + 1][j + 1] == Pole.EMPTY) {
                                laba[i + 1][j + 1] = Pole.STONE;
                                laba[i][j] = Pole.EMPTY;
                                if(laba[i + 2][j + 1] == Pole.ROBOT)
                                {
                                    //System.out.println("Робот уничтожен");
                                   isLoose = true;
                                }
                                movedStones.add(new Point(i + 1, j + 1));
                            }
                            break;
                        }
                    }
                }
                if(laba[i][j] == Pole.CLOSEDLIFT && lambdaMeter == 0)
                {
                    laba[i][j] = Pole.OPENEDLIFT;
                }
            }
        }
        /*System.out.println("Список сдвинутых камней:");
        for(int i = 0; i < movedStones.size() - 1; i++)
        {
            System.out.println(movedStones.get(i).x + " " + movedStones.get(i).y);
        }*/
                
    }

    void stepToPos(int x, int y) {
        /*System.out.println("Клетка:");
        System.out.print(x);
        System.out.print(" ");
        System.out.print(y);        
        System.out.println();*/
        switch (laba[x][y]) {
            case LAMBDA:
                count += 25;
                lambdaMeter --;
                break;
            case STONE:
                if (robotPosition.x == x && robotPosition.y < y) {
                    if (laba[x][y + 1] != Pole.EMPTY) {
                        return;
                    }
                    laba[x][y + 1] = Pole.STONE;
                    count--;
                } else if (robotPosition.x == x && robotPosition.y > y) {
                    if (laba[x][y - 1] != Pole.EMPTY) {
                        return;
                    }
                    laba[x][y - 1] = Pole.STONE;
                } else
                {
                    return;
                }
                /*else if (robotPosition.y == y && robotPosition.x < x) {
                    if (laba[x + 1][y] != Pole.EMPTY) {
                        return;
                    }
                    laba[x + 1][y] = Pole.STONE;
                } else if (robotPosition.y == y && robotPosition.x > x) {
                    if (laba[x - 1][y] != Pole.EMPTY) {
                        return;
                    }
                    laba[x - 1][y] = Pole.STONE;
                }*/
                count--;
                break;
            case OPENEDLIFT:
                isWin = true;
                count += 50*(lambdaNum - lambdaMeter);
                break;
            default:
                if (laba[x][y] == Pole.WALL || laba[x][y] == Pole.CLOSEDLIFT) {
                    return;
                }
                count--;
                break;
        }
        laba[x][y] = Pole.ROBOT;
        laba[robotPosition.x][robotPosition.y] = Pole.EMPTY;
        robotPosition.x = x;
        robotPosition.y = y;
    }

    public void makeStep() throws IOException {
        System.out.println();
        char step = (char) System.in.read();
        switch (step) {
            case 'w':
                stepToPos(robotPosition.x - 1, robotPosition.y);
                break;
            case 'a':
                stepToPos(robotPosition.x, robotPosition.y - 1);
                break;
            case 's':
                if (laba[robotPosition.x - 1][robotPosition.y] == Pole.STONE) {
                    isLoose = true;
                }
                stepToPos(robotPosition.x + 1, robotPosition.y);
                break;
            case 'd':
                stepToPos(robotPosition.x, robotPosition.y + 1);
                break;
        }
        System.out.println();
        updateMap();
        showPole();
    }
    public void makeStep(char step) throws IOException {
        switch (step) {
            case 'w':
                stepToPos(robotPosition.x - 1, robotPosition.y);
                break;
            case 'a':
                stepToPos(robotPosition.x, robotPosition.y - 1);
                break;
            case 's':
                if (laba[robotPosition.x - 1][robotPosition.y] == Pole.STONE) {
                    isLoose = true;
                }
                stepToPos(robotPosition.x + 1, robotPosition.y);
                break;
            case 'd':
                stepToPos(robotPosition.x, robotPosition.y + 1);
                break;
        }
        System.out.println();
        updateMap();
        showPole();
    }

    void readMap() throws IOException {
        ArrayList<String> StringList = new ArrayList<String>();
        InputStreamReader reader = new InputStreamReader(System.in);
        BufferedReader map = new BufferedReader(new FileReader("map4.txt"));
        while (map.ready()) {
            System.out.println("Map ready!");
            StringList.add(map.readLine());
        }
        for (int i = 0; i < StringList.size(); i++) {
            System.out.println(StringList.get(i));
        }
        int maxHeight = StringList.size();
        int maxWidth = 0;
        for (int i = 0; i < StringList.size(); i++) {
            if (StringList.get(i).length() > maxWidth) {
                maxWidth = StringList.get(i).length();
            }
        }
        width = maxWidth;
        height = maxHeight;
        System.out.println(maxHeight);
        System.out.println(maxWidth);
        laba = new Pole[height][width];
        for (int i = 0; i < StringList.size(); i++) {
            for (int j = 0; j < StringList.get(i).length(); j++) {
                switch (StringList.get(i).charAt(j)) {
                    case 'R':
                        laba[i][j] = Pole.ROBOT;
                        robotPosition = new Position(i, j);
                        System.out.println(i);
                        System.out.println(j);
                        break;
                    case '*':
                        laba[i][j] = Pole.STONE;
                        break;
                    case 'L':
                        laba[i][j] = Pole.CLOSEDLIFT;
                        break;
                    case 'O':
                        laba[i][j] = Pole.OPENEDLIFT;
                        break;
                    case '.':
                        laba[i][j] = Pole.GROUND;
                        break;
                    case '#':
                        laba[i][j] = Pole.WALL;
                        break;
                    case '\\':
                        laba[i][j] = Pole.LAMBDA;
                        lambdaMeter++;
                        System.out.println("lambda++");
                        break;
                    case ' ':
                        laba[i][j] = Pole.EMPTY;
                        break;
                }
            }
            for (int j = StringList.get(i).length(); j < width; j++) {
                laba[i][j] = Pole.EMPTY;
            }
        }
        lambdaNum = lambdaMeter;
    }
    void MakeAborted()
    {
        count += 25*(lambdaNum - lambdaMeter);
        System.out.println();
        System.out.println("Aborted!");
        System.out.println("count = " + count);
    }
    Labirynth (Labirynth lab)
    {
        //System.out.println("Конструтор");
        this.width = lab.width;
        this.height = lab.height;
        laba = new Pole[height][width];
        for(int i=0 ; i<laba.length; i++)
        {
            System.arraycopy(lab.laba[i], 0, this.laba[i], 0, lab.laba[i].length);
        }
        this.robotPosition = new Position(lab.robotPosition.x, lab.robotPosition.y);
        this.count = lab.count;
        this.lambdaMeter = lab.lambdaMeter;
        this.lambdaNum = lab.lambdaNum;
    }
}
