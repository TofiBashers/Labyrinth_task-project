/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mainlab;

import java.util.ArrayList;


/**
 *
 * @author TofixXx
 */
// Point и Egde в дальнейшем перенести на верхний уровень, или вообще в отдельный файл

class Point implements Cloneable
{
    public int x;
    public int y;
    public Point clone() throws 
            CloneNotSupportedException
            {
                 return (Point)super.clone();
            }
    public Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
}

class Edge
{
    ArrayList <Point> Path;
    Point Begin;
    Point End;
    int weight;
    int ferromonNumber; // на будущее:)
    Pole FinalField[][];
}
class PointWithParent implements Cloneable
     {
         public Point point;
         public PointWithParent pointParent;
         int G;
         int F;
         int H;
         public PointWithParent clone() throws 
            CloneNotSupportedException
            {
                 return (PointWithParent)super.clone();
            }
         public PointWithParent(){};
         public PointWithParent(Point point, PointWithParent pointParent, int G, int H) throws CloneNotSupportedException
         {
             this.G = G;
             this.H = H;
             this.F = G + H;
             this.point.x = point.x;
             this.point.y = point.y;
             this.pointParent = pointParent.clone();
         }
         public PointWithParent GetParent()
         {
             return(pointParent);
         }
         public Point toPoint() throws CloneNotSupportedException
         {
             return(point.clone()); 
         }
     }
public class LocalSearch {
 Pole Field[][];
 public class LocalSearchIteartion
 {  
     private ArrayList<PointWithParent> OpenedList;
     private ArrayList<PointWithParent> ClosedList;
     final private int xSteps[] = {1, 0, -1, 0};
     final private int ySteps[] = {0, 1, 0, -1};
     private int SetManhattanDistance(int xBegin, int yBegin, Point EndLam)
     {
         return(Math.abs(xBegin-EndLam.x) + Math.abs(yBegin + EndLam.y));
     }
     private boolean ContainedCell(ArrayList<PointWithParent> List, int xCell, int yCell )
     {
         for(int i=0; i<List.size(); i++)
         {
             if(List.get(i).point.x == xCell && List.get(i).point.y == yCell)
             {
                 return true;
             }
         }
         return false;
     }
     private PointWithParent GetContainedCell(ArrayList<PointWithParent> List, int xCell, int yCell )
     {
         for(int i=0; i<List.size(); i++)
         {
             if(List.get(i).point.x == xCell && List.get(i).point.y == yCell)
             {
                 return List.get(i);
             }
         }
         return null;
     }
     public Edge MakeEdge(Point BeginLam, Point EndLam, Pole NowField[][]) throws CloneNotSupportedException
     {
         OpenedList.add(new PointWithParent(BeginLam, null, 0, 0));
         do
         {
         int FMin = OpenedList.get(0).F;
         int FMinNum = 0;
         for(int i=1; i < OpenedList.size(); i++)
         {
             if(OpenedList.get(i).F < FMin)
             {
                 FMin = OpenedList.get(i).F;
                 FMinNum = i;
             }
         }
         ClosedList.add(OpenedList.remove(FMinNum));
         for(int i=0; i<4; i++)
         {
             int x = ClosedList.get(ClosedList.size()-1).point.x + xSteps[i];
             int y = ClosedList.get(ClosedList.size()-1).point.y + ySteps[i];
             PointWithParent NextPoint;
             NextPoint = new PointWithParent(new Point(x, y), ClosedList.get(ClosedList.size()-1) ,  ClosedList.get(ClosedList.size()-1).G + 1, SetManhattanDistance(x, y, EndLam));
             if(NowField[x][y] == Pole.WALL || ContainedCell(ClosedList, x, y) || (i == 0 && NowField[x-2][y] == Pole.STONE) || (NowField[x][y] == Pole.STONE && NowField[x + xSteps[i]][y + ySteps[i]] == Pole.STONE))
             {
                 continue;
             }
             
             
             /*if(ContainedCell(OpenedList, x, y))
             {
                 if(NextPoint.G < ClosedList.get(ClosedList.size()-1).G)
                 {
                     ClosedList.get(ClosedList.size()-1).pointParent = NextPoint;
                     ClosedList.get(ClosedList.size()-1).G = NextPoint.G + 1;
                 }
             }*/
         }
         }while(!ContainedCell(OpenedList, EndLam.x, EndLam.y) && !OpenedList.isEmpty());
         Edge edge;
         edge = new Edge();
         edge.weight = 0;//доделать, возможно параметр "вес" не нужен, его заменит размер списка
         edge.Begin = BeginLam;
         edge.End = EndLam;
         PointWithParent ParentPoint = GetContainedCell(OpenedList, EndLam.x, EndLam.y);
         do
         {
             edge.Path.add(ParentPoint.toPoint());
             edge.weight++;
             ParentPoint = ParentPoint.GetParent();
         }while(ParentPoint != null);
         return edge;
     } 
 }
 
 public LocalSearch(Pole pole[][], int poleHeight, int poleWidth)//по возможности, не передавать
 {
     this.Field = new Pole[poleHeight][poleWidth];
     for(int i=0; i<poleHeight; i++)
     {
         System.arraycopy(pole[i], 0, Field[i], 0, poleWidth); 
     }
 }
 public ArrayList<Edge> MakeEdgeList()
 {
     ArrayList<Edge> EdgeList;
     
 }
 
}
