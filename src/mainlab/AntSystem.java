package mainlab;

import mainlab.entity.MapCell;
import mainlab.entity.Point;
import mainlab.entity.Edge;
import gui.MainFrame;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.math.*;
import java.lang.*;

/**
 *
 * @author TofixXx
 */

/** Реалиазция задачи поиска глобального пути с помощью алгоритма 
 * муравьиной колонии. Алгоритм оптимизирован под задачу поиска
 * пути на графе, где ребро - это путь от одной лямбды (т.е., вершины)
 * до другой, с учётом состояния поля.
 * В ходе обхода муравьём, пополняется общий список
 * рёбер (Edge), а также модифицируются их эвристические параметры.
 * При поиске, с каждой новой итерацией, эвристические параметры рёбер,
 * постепенно, сходятся к одному пути. Точность пути, а также их разброс при 
 * N запусках поиска, зависит от количества итераций
 * 
 */
public class AntSystem {

    private static final int numAnts = 100; // количество муравьев(итераций)
    private static final double ALPHA = 0.5;
    private static final double BETA = 0.5;
    private static List<Point> vertexes; // список лямбд 
    private Point lift;
    private ArrayList<Edge> edges; // список ребер, участвующих в пути
    private Labyrinth labyrinth;

    public AntSystem(Labyrinth labyrinth) {
        this.labyrinth = new Labyrinth(labyrinth);
        vertexes = new ArrayList();
        edges = new ArrayList();
        this.checkLambda();
    }

    public final void checkLambda() {
        for (int i = 0; i < labyrinth.height; ++i) {
            for (int j = 0; j < labyrinth.width; ++j) {
                if (labyrinth.map[i][j] == MapCell.LAMBDA
                        || labyrinth.map[i][j] == MapCell.CLOSEDLIFT) {
                    Point t = new Point(i, j);
                    vertexes.add(t);
                }
                if (labyrinth.map[i][j] == MapCell.CLOSEDLIFT) {
                    lift = new Point(i, j);
                }
            }
        }
    }

    private class Ant {

        List<Point> lambdasAnt;
        Labyrinth labAnt;
        public ArrayList<Edge> path; // каждый муравей хранит свой путь(лямбды)

        Ant() {
            lambdasAnt = new ArrayList();
            lambdasAnt.addAll(vertexes);
            path = new ArrayList();
            labAnt = new Labyrinth(labyrinth);
        }

        public void updateFerromones() {
            //идёт перебор рёбер из общего списка, на каждом ребре колиество ферромонов уменьшается
            for (Edge edge : edges) {
                if (edge.ferromonNumber > 1) {
                    edge.ferromonNumber--;
                }
            }
            //далее нужно просто прибавить ко всему пути ферромоны, а затем добавить все рёбра пути в общий список
            for (Edge pathEdge : path) {
                pathEdge.ferromonNumber++;
                edges.add(pathEdge);
            }
            //повторный перебор пути, рёбра. которых ещё нет в общеем списке, добавляются в него
            for (Edge pathEdge : path) {
                pathEdge.ferromonNumber++;
                edges.add(pathEdge);
            }
        }

        public ArrayList<Edge> makePath() {
            lambdasAnt.remove(new Point(labAnt.robotPosition.x, labAnt.robotPosition.y));//сразу удаляем точку старта робота
            //1) обход всех лямбд
            do {
                System.out.println("Размер списка вершин: " + lambdasAnt.size());
                Edge TryEdge = checkProbability();
                if (TryEdge == null) {
                    return path;
                }
                Edge ChosenEdge = new Edge(TryEdge);
                for (int i = ChosenEdge.path.size() - 2; i >= 0; i--) {
                    if (labAnt.map[ChosenEdge.path.get(i).x][ChosenEdge.path.get(i).y] == MapCell.LAMBDA) {
                        lambdasAnt.remove(new Point(ChosenEdge.path.get(i).x, ChosenEdge.path.get(i).y));
                    }
                    labAnt.stepToPos(ChosenEdge.path.get(i).x, ChosenEdge.path.get(i).y);
                    /* TODO: В будущем - обязательно
                     * убрать список лямбд в лабиринт, уже там регулировать всё это дело
                     */
                    labAnt.updateMap();
                }
                lambdasAnt.remove(ChosenEdge.end);//удаляем из списка ту лямбду, в которую пришли
                path.add(ChosenEdge);
            } while (!lambdasAnt.isEmpty());
            updateFerromones();
            return path;
        }

        /**
         * Выдает список ребер(путей) от выбранной лямбды до других. Cначала
         * перебираются все лямбды кроме заданной, затем проверяется, есть ли
         * рёбро между этими двумя вершинами в списке, если есть, то добавляется
         * в соотв список, если нету - генерировать его локальным поиском.
         */
        public ArrayList<Edge> checkDistance(MapCell NowField[][]) {

            ArrayList<Edge> list = new ArrayList<Edge>();
            System.out.println("Список вершин: ");
            for (int i = 0; i < lambdasAnt.size(); i++) {
                System.out.println(lambdasAnt.get(i).x + " " + lambdasAnt.get(i).y);
            }
            for (Point p : lambdasAnt) {
                if (p.equals(new Point(labAnt.robotPosition.x, labAnt.robotPosition.y))) {
                    System.out.println("Попытка хода в текущюю точку блокирована");
                    continue;
                }
                if (p.equals(lift) && lambdasAnt.size() != 1) {
                    System.out.println("Попытка пройти к заврытому лифту блокирована");
                    continue;
                }
                for (Edge edge : edges) {
                    if (edge.begin.equals(new Point(labAnt.robotPosition.x, labAnt.robotPosition.y)) && edge.end.equals(p) && edge.fieldEquals(NowField)) {
                        System.out.println("Добавление ребра из списка существующих рёбер");
                        list.add(edge);
                    }
                }

                Labyrinth labAntForIt = new Labyrinth(labAnt);
                LocalSearchIteration localSearchIteration = new LocalSearchIteration();
                for (int i = 0; i < 8; i++) {
                    System.out.println();
                }
                System.out.println("Создание нового ребра из точки : "
                        + labAnt.robotPosition.x
                        + " " + labAnt.robotPosition.y
                        + " в точку: " + p.x + " " + p.y);
                Edge makedEdge = localSearchIteration.makeEdge(new Point(labAnt.robotPosition.x, labAnt.robotPosition.y), p, labAntForIt);
                if (makedEdge == null) {
                    continue;
                }
                list.add(makedEdge);
                System.out.println("Размер списка вершин: " + lambdasAnt.size());
                System.out.println("Размер списка возможных путей: " + list.size());
            }
            if (list.isEmpty()) {
                System.out.println("Список пуст!");
            }
            return list;
        }

        public double getProbability(Edge e, ArrayList<Edge> incidentEdges) {
            //if (ant.x == e.a.x && ant.y == e.a.y){
            double selected_edge_prob = Math.pow(e.ferromonNumber, ALPHA) * Math.pow(1 / (e.path.size()) + 1, BETA);
            double edges_sum = 0;

            for (Edge t : incidentEdges) {
                if (!path.contains(t)) {
                    edges_sum += Math.pow(e.ferromonNumber, ALPHA) * Math.pow(1 / (e.path.size()) + 1, BETA);
                }
            }
            return selected_edge_prob / edges_sum;
        }

        public Edge checkProbability() {
            ArrayList<Edge> lst = new ArrayList();
            lst.addAll(checkDistance(labAnt.map));
            if (lst.isEmpty()) {
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

    public ArrayList<Edge> searchOptimalPath() {
        for (int i = 0; i < numAnts - 1; i++) {
            new Ant().makePath();
        }
        return new Ant().makePath();
    }

}

