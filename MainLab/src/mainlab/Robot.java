/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mainlab;

import java.io.IOException;

/**
 *
 * @author Елена
 */
public class Robot {

    public Position currentPosition = new Position(0, 0);
    public Position newPosition = new Position(0, 0);
    int count;
    int turn;
    Lable R = new Lable(currentPosition, currentPosition, false);
    Lable L = new Lable(currentPosition, currentPosition, false);
    Lable S = new Lable(currentPosition, currentPosition, false);

    public void makeTurn(Turn turn) throws IOException {
        do {
            turn = System.in.read();
           

        }while(turn!=10||laba[currentPosition.x][currentPosition.y]!=LIFT);
    }
}
