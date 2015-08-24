package mainlab;

import mainlab.entity.MapCell;
import mainlab.entity.Point;
import mainlab.entity.Edge;
import java.util.ArrayList;

/**
 *
 * @author TofixXx
 */
/*
 * В данном случае, одна итерация алгоритма локального поиска - 
 * это нахождение оптимального пути от одной лямбды к другой, с 
 * учётом наличия препятствий, и изменения состояния поля.
 * Используется алгоритм A*. 
 */
public class LocalSearchIteration {

    private ArrayList<AStarPoint> openedList;
    private ArrayList<AStarPoint> closedList;
    private static final int xSteps[] = {1, 0, -1, 0};
    private static final int ySteps[] = {0, 1, 0, -1};

    public LocalSearchIteration() {
        openedList = new ArrayList<AStarPoint>();
        closedList = new ArrayList<AStarPoint>();
    }

    private int setManhattanDistance(int xBegin, int yBegin, Point EndLam) {
        return (Math.abs(xBegin - EndLam.x) + Math.abs(yBegin + EndLam.y));
    }

    private boolean containedCell(ArrayList<AStarPoint> List, int xCell, int yCell) {
        for (AStarPoint aStarPoint : List) {
            if (aStarPoint.point.x == xCell && aStarPoint.point.y == yCell) {
                return true;
            }
        }
        return false;
    }

    private AStarPoint getContainedCell(ArrayList<AStarPoint> List, int xCell, int yCell) {
        for (AStarPoint pointWithParent : List) {
            if (pointWithParent.point.x == xCell && pointWithParent.point.y == yCell) {
                return pointWithParent;
            }
        }
        return null;
    }

    public Edge makeEdge(Point beginLam, Point endLam, Labyrinth currentStateLabirynth) {
        openedList.add(new AStarPoint(beginLam, null, 0, 0));
        do {
            int FMin = openedList.get(0).F;
            int FMinNum = 0;
            for (int i = 1; i < openedList.size(); i++) {
                if (openedList.get(i).F < FMin) {
                    FMin = openedList.get(i).F;
                    FMinNum = i;
                }
            }
            closedList.add(openedList.remove(FMinNum));
            Labyrinth thisStepLab = new Labyrinth(currentStateLabirynth);
            /* Далее, по идее, мы должны создать имитацию пути до этой клетки.
             * Для этого мы должны пройти по её родителям до самого начала (до 
             * тех пор, пока родитель не станет null, записать весь этот "мини-путь" 
             * в некоторый список, а затем "пройти" его роботом начиная с конца
             * по этому лабиринту. В итоге получим лабиринт, в котором путь пройден
             * до нашей клетки, готовый к следующему шагу.
             */
            ArrayList<AStarPoint> StepsToThisList = new ArrayList();
            AStarPoint thisPoint = closedList.get(closedList.size() - 1);//ссылка на клетку, только что добавленную в закрытый список
            while (thisPoint.pointParent != null)//начальную клетку не добавляем, т.к. предполагается, что робот стоит в ней, т.е. ходить в неё не нужно
            {
                StepsToThisList.add(thisPoint);
                thisPoint = thisPoint.pointParent;
            }
            for (int i = StepsToThisList.size() - 1; i >= 0; i--) {
                thisStepLab.stepToPos(StepsToThisList.get(i).point.x, StepsToThisList.get(i).point.y);
                thisStepLab.updateMap();
            }
            thisPoint = closedList.get(closedList.size() - 1);
            for (int i = 0; i < 4; i++) {
                Labyrinth nextStepLab = new Labyrinth(thisStepLab);
                int x = thisPoint.point.x + xSteps[i];
                int y = thisPoint.point.y + ySteps[i];
                nextStepLab.stepToPos(x, y);
                nextStepLab.updateMap();

                if (nextStepLab.map[x][y] == MapCell.WALL
                        || nextStepLab.map[x][y] == MapCell.CLOSEDLIFT
                        || containedCell(closedList, x, y)
                        || (i == 0 && nextStepLab.map[x - 2][y] == MapCell.STONE)
                        || (nextStepLab.map[x][y] == MapCell.STONE && ((i == 0 || i == 2)
                        || nextStepLab.map[x + xSteps[i]][y + ySteps[i]] != MapCell.EMPTY)
                        || nextStepLab.isLoose)) {
                    continue;
                }
                //ситуация, когда камнем загородили выход:
                if (x + 2 * xSteps[i] < nextStepLab.height
                        && x + 2 * xSteps[i] >= 0
                        && y + 2 * ySteps[i] < nextStepLab.width
                        && y + 2 * ySteps[i] >= 0) {
                    if (nextStepLab.map[x + xSteps[i]][y + ySteps[i]] == MapCell.STONE
                            && nextStepLab.map[x + 2 * xSteps[i]][y + 2 * ySteps[i]] == MapCell.OPENEDLIFT) {
                        continue;
                    }
                }
                if (getContainedCell(openedList, x, y) != null) {
                    if (thisPoint.G + 1 < getContainedCell(openedList, x, y).G) {
                        getContainedCell(openedList, x, y).G = thisPoint.G + 1;
                        getContainedCell(openedList, x, y).F = getContainedCell(openedList, x, y).G
                                + getContainedCell(openedList, x, y).H;
                        getContainedCell(openedList, x, y).pointParent = thisPoint;
                    }
                } else {
                    AStarPoint nextPoint
                            = new AStarPoint(new Point(x, y), closedList.get(closedList.size() - 1), closedList.get(closedList.size() - 1).G + 1, setManhattanDistance(x, y, endLam));
                    openedList.add(nextPoint);
                }
            }
        } while (!containedCell(openedList, endLam.x, endLam.y) && !openedList.isEmpty());

        if (openedList.isEmpty()) {
            System.out.println("Путь не найден");
            return null;
        }
        System.out.println("Путь найден");
        ArrayList<Point> path = new ArrayList();
        AStarPoint parentPoint = getContainedCell(openedList, endLam.x, endLam.y);
        System.out.println(parentPoint.point.x + " " + parentPoint.point.y);
        do {
            path.add(parentPoint.point);
            parentPoint = parentPoint.pointParent;
        } while (parentPoint != null);
        //для логгирования поиска по лабиринту//
        currentStateLabirynth.printMap();
        for (int i = path.size() - 2; i >= 0; i--) {
            currentStateLabirynth.stepToPos(path.get(i).x, path.get(i).y);
            currentStateLabirynth.updateMap();
            currentStateLabirynth.printMap();
        }
        return new Edge(path, beginLam, endLam, path.size() - 1, 1, currentStateLabirynth.map);
    }

    /*
     * Для реализации А* необходимо, чтобы у каждой клетки было 
     * было несколько параметров + ссылка на обьект-"родитель" 
     * С этой целью создан класс PointWhithParent
     */
    private class AStarPoint {

        Point point;
        AStarPoint pointParent;
        int G;
        int F;
        int H;

        public AStarPoint(Point point, AStarPoint pointParent, int G, int H) {
            this.G = G;
            this.H = H;
            this.F = G + H;
            this.point = point;
            this.pointParent = pointParent;//нам нужна ссылка на родителя
        }
    }

}
