/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainlab.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author TofixXx
 */

/** Представляет описание ребра. Ребро представляет из себя путь от 
 * одной лямбды, до другой, в определённом состоянии поля. Строится
 * при помощи алгоритма A* (класс LocalSearchIteration)
 * Ребро хранит текущее состояние поля (на момент создания ребра), а также информацию,
 * необходимую для алгоритма муравьиной колонии
 */
public final class Edge {

    public ArrayList<Point> path;
    public Point begin;
    public Point end;
    public int weight;
    public int ferromonNumber;
    public MapCell[][] beginMap;

    public Edge(ArrayList<Point> Path, Point Begin, Point End, int weight, int ferromonNumber, MapCell[][] beginMap) {
        this.path = new ArrayList();
        this.path.addAll(Path);
        this.weight = weight;//TODO: доделать, возможно параметр "вес" не нужен, его заменит размер списка
        this.begin = Begin;
        this.end = End;
        this.ferromonNumber = ferromonNumber;
        this.beginMap = new MapCell[beginMap.length][];
        for (int i = 0; i < beginMap.length; i++) {
            this.beginMap[i] = new MapCell[beginMap[i].length];
        }
        for (int i = 0; i < beginMap.length; i++) {
            System.arraycopy(beginMap[i], 0, this.beginMap[i], 0, beginMap[i].length);
        }
    }

    public Edge(Edge edge) {
        path = new ArrayList();
        path.addAll(edge.path);
        begin = edge.begin;
        end = edge.end;
        weight = edge.weight;
        ferromonNumber = edge.ferromonNumber;
        this.beginMap = new MapCell[edge.beginMap.length][edge.beginMap[0].length];
        for (int i = 0; i < edge.beginMap.length; i++) {
            System.arraycopy(edge.beginMap[i], 0, this.beginMap[i], 0, edge.beginMap[i].length);
        }
    }

    public boolean fieldEquals(MapCell[][] nowMap) {
        for (int i = 0; i < this.beginMap.length; i++) {
            for (int j = 0; j < this.beginMap[i].length; j++) {
                if (this.beginMap[i][j] != nowMap[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        } else;
        {
            Edge objEdge = (Edge) obj;
            //сравниваем по 3 критериям: начало, конец, начальное состояние поля 
            //(если это всё равно, пути должны быть одинаковы)
            if (fieldEquals(objEdge.beginMap)
                    && this.begin == objEdge.begin
                    && this.end == objEdge.end) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.begin);
        hash = 97 * hash + Objects.hashCode(this.end);
        hash = 97 * hash + Arrays.deepHashCode(this.beginMap);
        return hash;
    }
}
