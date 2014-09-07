/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mainlab;
//import mainlab.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.math.*;
import java.lang.*;

/**
 *
 * @author Zhestkov
 */
/*
 class Edge implements Comparable<Edge> {
 Point a;
 Point b;
 int length;
 double pheromone = 5; // начальное кол-во феромонов на всех ребрах
    
 Edge(Point a, Point b, int length) {
 this.a = a;
 this.b = b;
 this.length = length;
 }
    
    
 public int compareTo(Edge o) {
 int comp_len;
 if (length > o.length)
 comp_len = 1;
 else if (length < o.length)
 comp_len = -1; 
 else
 comp_len = 0; // равны
 return comp_len;
 }
    
    
 }
 */
/*class MyComparator implements Comparator<Edge> {
 public int compare(Edge o1, Edge o2) {
 return o1.compareTo(o2);
 }
 }
 */
public class AntSystem {

    static final int numAnts = 10; // количество муравьев(итераций)
    static final double ALPHA = 0.5;
    static final double BETA = 0.5;
    public static List<Point> vertexes; // список лямбд // позиция меняется //возможно, в списке должны быть начальное положение робота и лифт
    public Point Lift;
    public ArrayList<Edge> edges; // список ребер, участвующих в пути
    //public static List<Ant> ants; // список муравьев
    //public int num_ants = lambdas.size() * lambdas.size(); // кол-во муравьев
    Labirynth labir;

    public AntSystem(Labirynth labir) {
        this.labir = new Labirynth(labir);
        vertexes = new ArrayList();
        edges = new ArrayList();
        this.checkLambda();
    }

    public void checkLambda() {
        for (int i = 0; i < labir.height; ++i) {
            for (int j = 0; j < labir.width; ++j) {
                if (labir.laba[i][j] == Pole.LAMBDA || labir.laba[i][j] == Pole.CLOSEDLIFT) {
                    Point t = new Point(i, j);
                    vertexes.add(t);
                }
                if(labir.laba[i][j] == Pole.CLOSEDLIFT)
                {
                    Lift = new Point(i, j);
                }
            }
        }
    }

    private class Ant {
        //Position AntPos;
      /*int x;
         int y;*/
        List<Point> lambdasAnt;
        Labirynth labAnt;
        public ArrayList<Edge> path; // каждый муравей хранит свой путь(лямбды)
        //...public double pathLength; // длина пути
        //public Edge next; // следующее предполагаемое по вероятности ребро

        Ant() {
            //AntPos = new Position(RobotPosition.x, RobotPosition.y);
            lambdasAnt = new ArrayList();
            lambdasAnt.addAll(vertexes);
            path = new ArrayList();
            labAnt = new Labirynth(labir);
        }

        public void UpdateFerromones() {
                //идёт перебор рёбер из общего списка, на каждом ребре колиество ферромонов уменьшается
                for (int i = 0; i < edges.size(); i++) {
                    if (edges.get(i).ferromonNumber > 1) 
                    {
                        edges.get(i).ferromonNumber--;
                    }
                }
                //далее нужно просто прибавить ко всему пути ферромоны, а затем добавить все рёбра пути в общий список
                for (int i = 0; i < path.size(); i++) {
                    path.get(i).ferromonNumber++;
                    edges.add(path.get(i));
                }
            //повторный перебор пути, рёбра. которых ещё нет в общеем списке, добавляются в него
            for (int i = 0; i < path.size(); i++) {
                path.get(i).ferromonNumber++;
                edges.add(path.get(i));
            }
        }
        /*
         * Не умеет обрабатывать случаи с недоступными лямбдами
         */

        public ArrayList<Edge> MakePath() throws CloneNotSupportedException {
            lambdasAnt.remove(new Point(labAnt.robotPosition.x, labAnt.robotPosition.y));//сразу удаляем точку старта робота
            //1) обход всех лямбд
            do {
                System.out.println("Размер списка вершин: " + lambdasAnt.size());
                Edge TryEdge = checkProbability();
                if(TryEdge == null)
                {
                    return path;
                }
                Edge ChosenEdge = new Edge(TryEdge);
                for (int i = ChosenEdge.Path.size() - 2; i >= 0; i--) {
                    if(labAnt.laba[ChosenEdge.Path.get(i).x][ChosenEdge.Path.get(i).y] == Pole.LAMBDA)
                    {
                        lambdasAnt.remove(new Point(ChosenEdge.Path.get(i).x, ChosenEdge.Path.get(i).y));
                    }
                    labAnt.stepToPos(ChosenEdge.Path.get(i).x, ChosenEdge.Path.get(i).y);
                    /* пока этим способом можно почти решить проблему
                     * случайного прохода лямбд. В будущем - обязательно
                     * убрать список лямбд в лабиринт, уже там регулировать всё это дело
                     */
                    labAnt.updateMap();
                }
                lambdasAnt.remove(ChosenEdge.end);//удаляем из списка ту лямбду, в которую пришли
                path.add(ChosenEdge);
            } while (!lambdasAnt.isEmpty());
            UpdateFerromones();
            return path;
        }
        //возможно, точку, передаваемую в этот метод, нужно заменить на текущую позицию робота

        public ArrayList<Edge> checkDistance(Pole NowField[][]) throws CloneNotSupportedException {
            /**
             * выдает список ребер(путей) от выбранной лямбды до других сначала
             * нужно перебирать все лямбды кроме этой, затем проверять, есть ли
             * рёбро между этими двумя вершинами в списке, если есть, то
             * добавлять в соотв список, если нету - генерировать его локальным
             * поиском
             */
            ArrayList<Edge> list = new ArrayList<Edge>();
            System.out.println("Список вершин: ");
            for(int i = 0; i < lambdasAnt.size(); i++)
            {
                System.out.println(lambdasAnt.get(i).x + " " + lambdasAnt.get(i).y);
            }
            for (Point p : lambdasAnt) {
                if (p.equals(new Point(labAnt.robotPosition.x, labAnt.robotPosition.y)))
                {
                    System.out.println("Попытка хода в текущюю точку блокирована");
                    continue;
                }
                if (p.equals(Lift) && lambdasAnt.size() != 1) {
                    System.out.println("Попытка пройти к заврытому лифту блокирована");
                    continue;
                }
                for (int i = 0; i < edges.size(); i++) {
                    if (edges.get(i).begin.equals(new Point(labAnt.robotPosition.x, labAnt.robotPosition.y)) && edges.get(i).end.equals(p) && edges.get(i).PoleEquals(NowField)) {
                        System.out.println("Добавление ребра из списка существующих рёбер");
                        list.add(edges.get(i));
                        continue;
                    }
                }
                //итерацию вынести в самостоятельный класс, если это возможно!!!
                Labirynth labAntForIt = new Labirynth(labAnt);
                mainlab.LocalSearch.LocalSearchIteration localSearchIteration;
                localSearchIteration = new mainlab.LocalSearch.LocalSearchIteration();
                for(int i = 0; i < 8; i++)
                {
                    System.out.println();
                }
                System.out.println("Создание нового ребра из точки : " + labAnt.robotPosition.x + " " + labAnt.robotPosition.y + " в точку: " + p.x + " " + p.y);
                Edge makedEdge = localSearchIteration.makeEdge(new Point(labAnt.robotPosition.x, labAnt.robotPosition.y), p, labAntForIt);
                if(makedEdge == null)
                {
                    continue;
                }
                list.add(makedEdge);
                /*if (p.begin == t && p.PoleEquals(NowField)) {
                 list.add(p);
                 }*/
                System.out.println("Размер списка вершин: " + lambdasAnt.size());
                System.out.println("Размер списка возможных путей: " + list.size());
            }
            if(list.isEmpty())
            {
                System.out.println("Список пуст!");
            }
            return list;
        }

        public double getProbability(Edge e, ArrayList<Edge> incidentEdges) throws CloneNotSupportedException {
            //if (ant.x == e.a.x && ant.y == e.a.y){
            double selected_edge_prob = Math.pow(e.ferromonNumber, ALPHA) * Math.pow(1/(e.Path.size()) + 1, BETA);
            double edges_sum = 0;

            for (Edge t : incidentEdges) {
                if (!path.contains(t)) {
                    edges_sum += Math.pow(e.ferromonNumber, ALPHA) * Math.pow(1/(e.Path.size()) + 1, BETA);
                }
            }
            return selected_edge_prob / edges_sum;
            /*}
             return 0; //если муравей ant не находится в точке e.a и не может за 1 шаг пройти ребро e*/
            // или кидать какое-то исключение
        }

        public Edge checkProbability() throws CloneNotSupportedException {
            ArrayList<Edge> lst = new ArrayList();
            lst.addAll(checkDistance(labAnt.laba));
            if(lst.isEmpty())
            {
                return null;
            }
            int n = lst.size();
            ArrayList<Double> chances = new ArrayList<Double>();
            double sum = 0.0;
            double prob = Math.random();
            int j; // index of random probability edge
            for (int i = 0; i < n; i++) {
                chances.add(getProbability(lst.get(i), lst));
            }
            Collections.sort(chances); // принцип сравнения при сортировке!
            for (j = 0; j < chances.size(); j++) {
                sum += chances.get(j);
                if (sum > prob) {
                    break;
                }
            }
            return lst.get(j);
        }
    }
    //  public boolean isDone(int left_lambdas) {
    //      return (left_lambdas == 0);
    //  }

    /*public static int distance(Point a, Point b) {
     int t1 = (a.x - b.x) * (a.x - b.x);
     int t2 = (a.y - b.y) * (a.y - b.y);
     return t1 + t2;
     }*/
    /*public Point checkRobot(Pole[][] field, int width, int height) {
     Point r = null;
     for (int i = 0; i < width; ++i)
     for (int j = 0; j < height; ++j)
     if (field[i][j] == Pole.ROBOT)
     r = new Point(i, j);
     return r;
     }*/
    /*public void stepmove(Ant ant, Pole[][] field, int width, int height) {
        //ant.x = checkRobot(field, width, height).x;
        //ant.y = checkRobot(field, width, height).y;
        Point t = new Point(ant.x, ant.y);
        //ant.path.add(t);
        MyComparator comp = new MyComparator();
        LinkedList<Edge> cur_point = checkDistance(t);
        Collections.sort(cur_point, comp); // сортировка списка cur_point по расстояниям
        Point next = cur_point.getFirst().b;
    }

    public void test() {
        Point p1 = new Point(2, 3);
        Point p2 = new Point(5, 7);
        vertexes.add(p1);
        vertexes.add(p2);
        Edge t = new Edge(vertexes.get(0), vertexes.get(1), distance(vertexes.get(0), vertexes.get(1)));
        edges.add(t);
    }*/

    public void AntTest() throws CloneNotSupportedException {
        Ant TestAnt = new Ant();
        ArrayList<Edge> Path = new ArrayList();
        Path.addAll(TestAnt.MakePath());
        for(int i = 0; i < Path.size(); i++)
        {
            for(int j = Path.get(i).Path.size()-1; j > 0; j--)
            {
                System.out.println(Path.get(i).Path.get(j).x + " " + Path.get(i).Path.get(j).y);
            }
        }
        labir.showPole();
        for(int i = 0; i < Path.size(); i++)
        {
            for(int j = Path.get(i).Path.size()-1; j > 0; j--)
            {
                if(i == 0 && j == Path.get(i).Path.size()-1)
                {
                    continue;
                }
                labir.stepToPos(Path.get(i).Path.get(j).x, Path.get(i).Path.get(j).y);
                labir.updateMap();
                labir.showPole();
            }
        }
          //ход в выход
        if(labir.laba[Path.get(Path.size() - 1).Path.get(0).x][Path.get(Path.size() - 1).Path.get(0).y] != Pole.OPENEDLIFT)
        {
            labir.stepToPos(Path.get(Path.size() - 1).Path.get(0).x, Path.get(Path.size() - 1).Path.get(0).y);
            labir.updateMap();
            labir.showPole();
            labir.MakeAborted();
        } 
        else
        {
            labir.stepToPos(Path.get(Path.size() - 1).Path.get(0).x, Path.get(Path.size() - 1).Path.get(0).y);
            labir.updateMap();
            labir.showPole();
        }
    }
    public void SearchOptimalPath() throws CloneNotSupportedException, IOException
    {
        int NumEqualsPaths = 0;
        ArrayList<Edge> MainPath = new ArrayList();
        for(int i =0; i<30; i++)
        {
            Ant ant = new Ant();
            ArrayList<Edge> NowPath = new ArrayList();
            NowPath.addAll(ant.MakePath());
            if(i == 29)
            {
                MainPath.addAll(NowPath);
            }
        }
        MainFrame mainFrame = new MainFrame("robot", labir, MainPath);
        WritePathToFile(MainPath);
    }
    public void WritePathToFile(ArrayList<Edge> Path) throws IOException
    {
        File file = new File("result.txt");
        FileWriter writer = new FileWriter(file);
        Point pastPoint = Path.get(0).begin;
         for(int i = 0; i < Path.size(); i++)
        {
            for(int j = Path.get(i).Path.size()-1; j >= 0; j--)
            {
                if(i == 0 && j == Path.get(i).Path.size()-1)
                {
                    continue;
                }
                if(pastPoint.x == Path.get(i).Path.get(j).x && pastPoint.y < Path.get(i).Path.get(j).y)
                {
                    writer.append('R');
                    writer.flush();
                }
                else if(pastPoint.x == Path.get(i).Path.get(j).x && pastPoint.y > Path.get(i).Path.get(j).y)
                {
                    writer.append('L');
                    writer.flush();
                }
                else if(pastPoint.x > Path.get(i).Path.get(j).x && pastPoint.y == Path.get(i).Path.get(j).y)
                {
                    writer.append('U');
                    writer.flush();
                }
                else if(pastPoint.x < Path.get(i).Path.get(j).x && pastPoint.y == Path.get(i).Path.get(j).y)
                {
                    writer.append('D');
                    writer.flush();
                }
                else
                {
                    continue;
                }
                pastPoint = Path.get(i).Path.get(j);
            }
        }
        
    }
}


/*class lambda {
 public int x, y;
 public ArrayList<range> distances;
 lambda(Point p) {
 this.x = p.x;
 this.y = p.y;
 }
 public void setDistance() {
 // int sz = AntSystem.lambdas.size();
     
 for (Point p : AntSystem.lambdas) {
 lambda to = new lambda(p);
 range r = new range(this,to, AntSystem.distance());
                    
 }
 }
 }
    
 }
 */
