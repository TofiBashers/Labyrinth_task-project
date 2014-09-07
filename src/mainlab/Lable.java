/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mainlab;

/**
 *
 * @author Елена
 */
public class Lable {

    Lable(Position oldPosition, Position newPosition, boolean isDestroy) {
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
        this.isDestroy = isDestroy;
    }
    Position oldPosition;
    Position newPosition;
    boolean isDestroy;
}
