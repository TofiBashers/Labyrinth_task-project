/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ACO;
import mainlab.*;
import java.util.*;
import java.math.*;
import java.lang.*;
/**
 *
 * @author Zhestkov
 */


class Ant {
    int x;
    int y;
    public List<Edge> path; // каждый муравей хранит свой путь(лямбды)
    public double pathLength; // длина пути
    public Edge next; // следующее предполагаемое по вероятности ребро
    Ant() {
        this.pathLength = 0;
    }
}

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

class MyComparator implements Comparator<Edge> {
    public int compare(Edge o1, Edge o2) {
        return o1.compareTo(o2);
    }
}

 public class AntSystem {
    static final int numAnts = 10; // количество муравьев(итераций)
    static final double ALPHA = 1;
    static final double BETA = 1;
    public static LinkedList<Point> lambdas; // список лямбд 
    public List<Edge> edges; // оптимальный список ребер
    public static List<Ant> ants; // список муравьев
    public int num_ants = lambdas.size() * lambdas.size(); // кол-во муравьев
    public void checkLambda(Pole[][] field, int width, int height) {
        for (int i = 0; i < width; ++i)
            for (int j = 0; j < height; ++j)
                if (field[i][j] == Pole.LAMBDA){
                    Point t = new Point(i,j);
                    lambdas.add(t);
                }
    }
    
  //  public boolean isDone(int left_lambdas) {
  //      return (left_lambdas == 0);
  //  }
    
    public LinkedList<Edge> checkDistance(Point t) { 
        /**
         * выдает список ребер(путей) от выбранной лямбды до других
         */
        LinkedList<Edge> list = new LinkedList<Edge>();
        for (Point p : lambdas) {
            if (p != t) {
                Edge r = new Edge(t, p, distance(t, p));
                list.add(r);
            }
        }
        return list;
    }
    
    public double getProbability(Ant ant, Edge e) {
        if (ant.x == e.a.x && ant.y == e.a.y){
        double selected_edge_prob = Math.pow(e.pheromone, ALPHA) * Math.pow(e.length, BETA);
        double edges_sum = 0;
        
        for (Edge t : checkDistance(new Point(ant.x, ant.y)))
            if (!ant.path.contains(t))
                edges_sum += Math.pow(e.pheromone, ALPHA) * Math.pow(e.length, BETA);
        return selected_edge_prob/edges_sum;
        }
        return 0; //если муравей ant не находится в точке e.a и не может за 1 шаг пройти ребро e
        // или кидать какое-то исключение
    }
    
    public Edge checkProbability(Ant ant) {
        LinkedList<Edge> lst = checkDistance(new Point(ant.x, ant.y));
        int n = lst.size();
        ArrayList<Double> chances = new ArrayList<Double>();
        double sum = 0.0;
        double prob = Math.random();
        int j; // index of random probability edge
        for (int i = 0; i < n; i++)
            chances.add(getProbability(ant, lst.get(i)));
        Collections.sort(chances); // принцип сравнения при сортировке!
        for (j = 0; j < chances.size(); j++) {
            sum += chances.get(j);
            if (sum > prob)
                break;
        }
        return lst.get(j);
        
       
        
    }
       
    
    public static int distance(Point a, Point b) {
        int t1 = (a.x - b.x) * (a.x - b.x);
        int t2 = (a.y - b.y) * (a.y - b.y);
        return t1 + t2;
    }
    
   public Point checkRobot(Pole[][] field, int width, int height) {
       Point r = null;
       for (int i = 0; i < width; ++i)
            for (int j = 0; j < height; ++j)
                if (field[i][j] == Pole.ROBOT)
                    r = new Point(i, j);
       return r;
   }
    
    public void stepmove(Ant ant, Pole[][] field, int width, int height) {
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
       Point p1 = new Point(2,3);
       Point p2 = new Point(5,7);
       lambdas.add(p1);
       lambdas.add(p2);
       Edge t = new Edge(lambdas.get(0),lambdas.get(1), distance(lambdas.get(0),lambdas.get(1)));
       edges.add(t);
       
        
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
