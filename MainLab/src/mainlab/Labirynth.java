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
    public Position RobotPosition;
    public boolean isWin = false;
    public boolean isLoose = false;

    Labirynth() {
    }

    void showPole() {
        char buf = '-';
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
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
                    case LIFT:
                        buf = 'L';
                        break;
                    case GROUND:
                        buf = '.';
                        break;
                    case LAMBDA:
                        buf = '/';
                        break;
                }
                System.out.print(buf);
            }
            System.out.println();
        }
        System.out.println("scores = " + count);
    }

    void UpdateMap() {
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if (laba[i][j] == Pole.STONE) {
                    switch (laba[i + 1][j]) {
                        case EMPTY: {
                            laba[i + 1][j] = Pole.STONE;
                            laba[i][j] = Pole.EMPTY;
                            break;
                        }
                        case STONE: {
                            if (laba[i][j + 1] == Pole.EMPTY && laba[i + 1][j + 1] == Pole.EMPTY) {
                                laba[i + 1][j + 1] = Pole.STONE;
                                laba[i][j] = Pole.EMPTY;
                            } else if (laba[i][j - 1] == Pole.EMPTY && laba[i + 1][j - 1] == Pole.EMPTY) {
                                laba[i + 1][j - 1] = Pole.STONE;
                                laba[i][j] = Pole.EMPTY;
                            }
                            break;
                        }
                        case LAMBDA: {
                            if (laba[i][j + 1] == Pole.EMPTY && laba[i + 1][j + 1] == Pole.EMPTY) {
                                laba[i + 1][j + 1] = Pole.STONE;
                                laba[i][j] = Pole.EMPTY;
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    void StepToPos(int x, int y) {
        switch (laba[x][y]) {
            case LAMBDA:
                count += 25;
                break;
            case STONE:
                if (RobotPosition.x == x && RobotPosition.y < y) {
                    if (laba[x][y + 1] != Pole.EMPTY) {
                        return;
                    }
                    laba[x][y + 1] = Pole.STONE;
                } else if (RobotPosition.x == x && RobotPosition.y > y) {
                    if (laba[x][y - 1] != Pole.EMPTY) {
                        return;
                    }
                    laba[x][y - 1] = Pole.STONE;
                } else if (RobotPosition.y == y && RobotPosition.x < x) {
                    if (laba[x + 1][y] != Pole.EMPTY) {
                        return;
                    }
                    laba[x + 1][y] = Pole.STONE;
                } else if (RobotPosition.y == y && RobotPosition.x > x) {
                    if (laba[x - 1][y] != Pole.EMPTY) {
                        return;
                    }
                    laba[x - 1][y] = Pole.STONE;
                }
                count--;
                break;
            case LIFT:
                isWin = true;
                count--;
                break;
            default:
                if (laba[x][y] == Pole.WALL) {
                    return;
                }
                count--;
                break;
        }
        laba[x][y] = Pole.ROBOT;
        laba[RobotPosition.x][RobotPosition.y] = Pole.EMPTY;
        RobotPosition.x = x;
        RobotPosition.y = y;
    }

    void MakeStep() throws IOException {
        System.out.println();
        char step = (char) System.in.read();
        switch (step) {
            case 'w':
                StepToPos(RobotPosition.x - 1, RobotPosition.y);
                break;
            case 'a':
                StepToPos(RobotPosition.x, RobotPosition.y - 1);
                break;
            case 's':
                if (laba[RobotPosition.x - 1][RobotPosition.y] == Pole.STONE) {
                    isLoose = true;
                }
                StepToPos(RobotPosition.x + 1, RobotPosition.y);
                break;
            case 'd':
                StepToPos(RobotPosition.x, RobotPosition.y + 1);
                break;
        }
        System.out.println();
        UpdateMap();
        showPole();
    }

    void readMap() throws IOException {
        ArrayList<String> StringList = new ArrayList<String>();
        BufferedReader map = new BufferedReader(new FileReader("MAP.txt"));
        while (map.ready()) {
            StringList.add(map.readLine());
        }
        for (int i = 0; i < StringList.size(); i++) {
            System.out.println(StringList.get(i));
        }
        int MaxHeight = StringList.size();
        int MaxWidth = 0;
        for (int i = 0; i < StringList.size(); i++) {
            if (StringList.get(i).length() > MaxWidth) {
                MaxWidth = StringList.get(i).length();
            }
        }
        width = MaxWidth;
        height = MaxHeight;
        System.out.println(MaxHeight);
        System.out.println(MaxWidth);
        laba = new Pole[height][width];
        for (int i = 0; i < StringList.size(); i++) {
            for (int j = 0; j < StringList.get(i).length(); j++) {
                switch (StringList.get(i).charAt(j)) {
                    case 'R':
                        laba[i][j] = Pole.ROBOT;
                        RobotPosition = new Position(i, j);
                        break;
                    case '*':
                        laba[i][j] = Pole.STONE;
                        break;
                    case 'L':
                        laba[i][j] = Pole.LIFT;
                        break;
                    case '.':
                        laba[i][j] = Pole.GROUND;
                        break;
                    case '#':
                        laba[i][j] = Pole.WALL;
                        break;
                    case '/':
                        laba[i][j] = Pole.LAMBDA;
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
    }
}
