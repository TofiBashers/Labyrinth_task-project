/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mainlab;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author TofixXx
 */
// Point и Egde в дальнейшем перенести на верхний уровень, или вообще в отдельный файл
final class Point {

    public final int x;
    public final int y;
    public Point(int x, int y) 
    {
        this.x = x;
        this.y = y;
    }
    public boolean equals (Object obj)
    {
        if(obj == this)
        {
            return true;
        }
        if(obj == null)
        {
            return false;
        }
        if(obj.getClass() != this.getClass())
        {
            return false;
        }
        else
        {
            Point p = (Point)obj;
            if(this.x == p.x && this.y == p.y)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }
}

class Edge {

    ArrayList<Point> Path;
    Point begin;
    Point end;
    int weight;
    int ferromonNumber; // на будущее:)
    Pole BeginField[][];

    public Edge(ArrayList<Point> Path, Point Begin, Point End, int weight, int ferromonNumber, Pole BeginField[][]) 
    {
        this.Path = new ArrayList();
        this.Path.addAll(Path);
        this.weight = weight;//доделать, возможно параметр "вес" не нужен, его заменит размер списка
        this.begin = Begin;
        this.end = End;
        this.ferromonNumber = ferromonNumber;
        this.BeginField = new Pole[BeginField.length][];
        for(int i=0; i<BeginField.length; i++)
        {
            this.BeginField[i] = new Pole[BeginField[i].length];
        }
        for(int i=0; i<BeginField.length; i++)
        {
            System.arraycopy(BeginField[i], 0, this.BeginField[i], 0, BeginField[i].length);
        }
    }

    public Edge(Edge edge) {
        Path = new ArrayList();
        Path.addAll(edge.Path);
        begin = edge.begin;
        end = edge.end;
        weight = edge.weight;
        ferromonNumber = edge.ferromonNumber;
        this.BeginField = new Pole[edge.BeginField.length][edge.BeginField[0].length];
        for(int i = 0; i < edge.BeginField.length; i++)
        {
            System.arraycopy(edge.BeginField[i], 0, this.BeginField[i], 0, edge.BeginField[i].length);
        }
        }
    public boolean PoleEquals(Pole NowField[][])//пока так, м.б. можно и проще
    {
        for(int i=0; i < this.BeginField.length; i++)
        {
            for(int j=0; j < this.BeginField[i].length; j++)
            {
                if(this.BeginField[i][j] != NowField[i][j])
                {
                    return false;
                }
            }
        }
        return true;
    }
    public boolean equals(Object obj)
    {
        if(obj == this)
        {
            return true;
        }
        if(obj == null)
        {
            return false;
        }
        if(obj.getClass() != this.getClass())
        {
            return false;
        }
        else;
        {
            Edge ObjEdge = (Edge)obj;
            //сравниваем по 3 критериям: начало, конец, начальное состояние поля (по логике если это всё равно, пути должны быть одинаковы)
            if(PoleEquals(ObjEdge.BeginField) && this.begin == ObjEdge.begin && this.end == ObjEdge.end)
            {
                return true;
            }
        }
        return false;
    }
}
/*
 * Для реализации А* необходимо, чтобы у каждой клетки было 
 * было несколько параметров + ссылка на обьект-"родитель" 
 * С этой целью создан класс PointWhithParent
 */
class PointWithParent implements Cloneable {

    public Point point;
    public PointWithParent pointParent;
    int G;
    int F;
    int H;
    //возможно, будет нужен в дальнейшем
    public PointWithParent clone() throws
            CloneNotSupportedException {
        return (PointWithParent) super.clone();
    }


    public PointWithParent(Point point, PointWithParent pointParent, int G, int H) throws CloneNotSupportedException {
        this.G = G;
        this.H = H;
        this.F = G + H;
        this.point = point;
        if (pointParent != null) {
           this.pointParent = pointParent;//нам нужна ссылка на родителя
        } else {
            this.pointParent = null;
        }
    }
}


public class LocalSearch {

    Labirynth lab;
    /*
     * В данном случае, одна итерация алгоритма локального поиска - 
     * это нахождение оптимального пути от одной вершины графа к другой
     */
    public static class LocalSearchIteration {

        private ArrayList<PointWithParent> OpenedList;
        private ArrayList<PointWithParent> ClosedList;
        final private int xSteps[] = {1, 0, -1, 0};
        final private int ySteps[] = {0, 1, 0, -1};

        public LocalSearchIteration() 
        {
            OpenedList = new ArrayList<PointWithParent>();
            ClosedList = new ArrayList<PointWithParent>();
        }

        private int setManhattanDistance(int xBegin, int yBegin, Point EndLam) {
            return (Math.abs(xBegin - EndLam.x) + Math.abs(yBegin + EndLam.y));
        }

        private boolean containedCell(ArrayList<PointWithParent> List, int xCell, int yCell) {
            for (int i = 0; i < List.size(); i++) {
                if (List.get(i).point.x == xCell && List.get(i).point.y == yCell) {
                    return true;
                }
            }
            return false;
        }

        private PointWithParent getContainedCell(ArrayList<PointWithParent> List, int xCell, int yCell) {
            for (int i = 0; i < List.size(); i++) {
                if (List.get(i).point.x == xCell && List.get(i).point.y == yCell) {
                    return List.get(i);
                }
            }
            return null;
        }

        public Edge makeEdge(Point beginLam, Point endLam, Labirynth NowLab) throws CloneNotSupportedException {
            OpenedList.add(new PointWithParent(beginLam, null, 0, 0));
            do {
                int FMin = OpenedList.get(0).F;
                int FMinNum = 0;
                for (int i = 1; i < OpenedList.size(); i++) {
                    if (OpenedList.get(i).F < FMin) {
                        FMin = OpenedList.get(i).F;
                        FMinNum = i;
                    }
                }
                /*System.out.println("Выбранная клетка:");
                System.out.print(OpenedList.get(FMinNum).point.x);
                System.out.print(" ");
                System.out.print(OpenedList.get(FMinNum).point.y);
                System.out.println();*/
                ClosedList.add(OpenedList.remove(FMinNum));
                Labirynth thisStepLab = new Labirynth(NowLab);
                /* Далее, по идее, мы должны создать имитацию пути до этой клетки.
                 * Для этого мы должны пройти по её родителям до самого начала (до 
                 * тех пор, пока родитель не станет null, записать весь этот "мини-путь" 
                 * в некоторый список, а затем "пройти" его роботом начиная с конца
                 * по этому лабиринту. В итоге получим лабиринт, в котором путь пройден
                 * до нашей клетки, готовый к следующему шагу.
                 */
                ArrayList<PointWithParent> StepsToThisList = new ArrayList();
                PointWithParent thisPoint = ClosedList.get(ClosedList.size() - 1);//ссылка на клетку, только что добавленную в закрытый список
                /*System.out.println("Её координаты:");
                System.out.print(thisPoint.point.x);
                System.out.print(" ");
                System.out.print(thisPoint.point.y);
                System.out.println();*/
                while( thisPoint.pointParent!= null)//начальную клетку не добавляем, т.к. предполагается, что робот стоит в ней, т.е. ходить в неё не нужно
                {
                    StepsToThisList.add(thisPoint);
                    thisPoint = thisPoint.pointParent;
                }
                for(int i=StepsToThisList.size() - 1; i >= 0; i--)
                {
                    //thisStepLab.showPole();
                    thisStepLab.stepToPos(StepsToThisList.get(i).point.x, StepsToThisList.get(i).point.y);
                    thisStepLab.updateMap();
                    //thisStepLab.showPole();
                }
                thisPoint = ClosedList.get(ClosedList.size() - 1);
                for (int i = 0; i < 4; i++) {
                    Labirynth nextStepLab = new Labirynth(thisStepLab);
                    //nextStepLab.showPole();
                    int x = thisPoint.point.x + xSteps[i];
                    int y = thisPoint.point.y + ySteps[i];
                    /*if(x > 5 || y > 5 || x < 0 || y < 0)
                    {
                        System.out.println("Попытка зайти за границы поля в клетку: " + x + " " + y);
                        System.out.println("В данный момент рассматриваемая клетка: " + thisPoint.point.x + " " + thisPoint.point.y);
                        nextStepLab.showPole();
                        continue;
                    }*/
                    nextStepLab.stepToPos(x, y);
                    nextStepLab.updateMap();
                    //nextStepLab.showPole();
                    //System.out.println(nextStepLab.isLoose);
                    //PointWithParent NextPoint = new PointWithParent();
                    /* Следующее условие можно заменить,
                     * либо обязательно добавить к нему условие,
                     * когда камень падает на только что 
                     * пришедшего в клетку робота.
                     * */
                    if (nextStepLab.laba[x][y] == Pole.WALL || nextStepLab.laba[x][y] == Pole.CLOSEDLIFT || containedCell(ClosedList, x, y) || (i == 0 && nextStepLab.laba[x - 2][y] == Pole.STONE) || (nextStepLab.laba[x][y] == Pole.STONE && ((i == 0 || i == 2) || nextStepLab.laba[x + xSteps[i]][y + ySteps[i]] != Pole.EMPTY) || nextStepLab.isLoose)) {
                        if(nextStepLab.laba[x][y] == Pole.CLOSEDLIFT)
                        {
                        /*System.out.println("Данную клетку включить в поиск невозможно");
                        System.out.print(x);
                        System.out.print(" ");
                        System.out.print(y);
                        System.out.println();*/
                        }
                        continue;
                    }
                    //ситуация, когда камнем загородили выход:
                    if(x + 2*xSteps[i] < nextStepLab.height && x + 2*xSteps[i] >= 0 && y + 2*ySteps[i] < nextStepLab.width && y + 2*ySteps[i] >= 0)
                    {
                        if (nextStepLab.laba[x + xSteps[i]][y + ySteps[i]] == Pole.STONE && nextStepLab.laba[x + 2 * xSteps[i]][y + 2 * ySteps[i]] == Pole.OPENEDLIFT) {
                            continue;
                        }
                    }
                    if (getContainedCell(OpenedList, x, y) != null) {
                        /*System.out.println("Найдена клетка в открытом списке");
                        System.out.print(x);
                        System.out.print(" ");
                        System.out.print(y);
                        System.out.println();*/
                        if (thisPoint.G + 1 < getContainedCell(OpenedList, x, y).G) {
                            getContainedCell(OpenedList, x, y).G = thisPoint.G + 1;
                            getContainedCell(OpenedList, x, y).F = getContainedCell(OpenedList, x, y).G + getContainedCell(OpenedList, x, y).H;
                            getContainedCell(OpenedList, x, y).pointParent = thisPoint;
                            /*OpenedList.remove(GetContainedCell(OpenedList, x, y));
                             OpenedList.add(NextPoint);*/
                        }
                    } else {
                        PointWithParent nextPoint = new PointWithParent(new Point(x, y), ClosedList.get(ClosedList.size() - 1), ClosedList.get(ClosedList.size() - 1).G + 1, setManhattanDistance(x, y, endLam));
                        OpenedList.add(nextPoint);
                    }
                }
                /*System.out.println("Открытый список:");
                for(int i=0; i<OpenedList.size(); i++)
                {
                System.out.print(OpenedList.get(i).point.x);
                System.out.print(" ");
                System.out.print(OpenedList.get(i).point.y);
                System.out.println();
                }
                System.out.println("Закрытый список:");
                for(int i=0; i<ClosedList.size(); i++)
                {
                System.out.print(ClosedList.get(i).point.x);
                System.out.print(" ");
                System.out.print(ClosedList.get(i).point.y);
                System.out.println();
                }
                System.out.println("Следующая итерация");*/
            } while (!containedCell(OpenedList, endLam.x, endLam.y) && !OpenedList.isEmpty());
            if( OpenedList.isEmpty())
            {
                System.out.println("Путь не найден");
                return null;
            }
            else
            {
            System.out.println("Путь найден");
            }
            ArrayList <Point>Path = new ArrayList();
            /*for(int i=0; i<ClosedList.size(); i++)
            {
                System.out.print(ClosedList.get(i).point.x);
                System.out.print(" ");
                System.out.print(ClosedList.get(i).point.y);
                System.out.println();
            }
            System.out.println(OpenedList.size());
            System.out.println(ClosedList.size());
            if(OpenedList.isEmpty())
            {
                System.out.println("Закрытый список");
                for(int i = 0; i < ClosedList.size(); i++)
                {
                    System.out.println(ClosedList.get(i).point.x + " " + ClosedList.get(i).point.y);
                }
            }*/
            PointWithParent parentPoint = getContainedCell(OpenedList, endLam.x, endLam.y);
            System.out.println(parentPoint.point.x + " " + parentPoint.point.y);
            do {
                Path.add(parentPoint.point);
                parentPoint = parentPoint.pointParent;
            } while (parentPoint != null);
            //пока для тестов//
            NowLab.showPole();
            for( int i=Path.size()-2; i >= 0; i--)
            {
                NowLab.stepToPos(Path.get(i).x, Path.get(i).y);
                NowLab.updateMap();
                NowLab.showPole();
            }
            return new Edge(Path, beginLam, endLam, Path.size()-1 , 1, NowLab.laba);
        }
    }

    public LocalSearch(Labirynth labir) {
        System.out.println("Конструктор локального поиска");
        this.lab = new Labirynth(labir);
    }

    public void testLocalSearchIteration(Point begin, Point end) throws CloneNotSupportedException, NullPointerException {
        LocalSearchIteration TestIt;
        TestIt = new LocalSearchIteration();
        Edge edge = new Edge(TestIt.makeEdge(begin, end, lab));
        System.out.println("путь:");
        for(int i=0; i<edge.Path.size(); i++)
            {
                System.out.print(edge.Path.get(i).x);
                System.out.print(" ");
                System.out.print(edge.Path.get(i).y);
                System.out.println();
            }
        /*for (int i = 0; i < edge.Path.size(); i++) {
            if(lab.laba[edge.Path.get(i).x][edge.Path.get(i).y] != Pole.STONE)
            {
            lab.laba[edge.Path.get(i).x][edge.Path.get(i).y] = Pole.ROBOT;
            lab.UpdateMap();
            }
            */
        lab.showPole();
    }
    public ArrayList<Edge> MakeEdgeList()
     {
     ArrayList<Edge> EdgeList = new ArrayList();
     return EdgeList;
     }
     
}
