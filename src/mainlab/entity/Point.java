
package mainlab.entity;

/**
 *
 * @author TofixXx
 */
public final class Point {

    public int x;
    public int y;
    public Point(int x, int y) 
    {
        this.x = x;
        this.y = y;
    }
    
    @Override
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
            return (this.x == p.x && this.y == p.y);
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + this.x;
        hash = 97 * hash + this.y;
        return hash;
    }
}
