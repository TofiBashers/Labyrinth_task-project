/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mainlab;

/**
 *
 * @author Елена
 */
public enum Turn {
    
    UP{
        @Override
        void turn(Position pos){
            pos.y--;
        }
    },
    DOWN{
        @Override
        void turn(Position pos){
            pos.y++;
        }
    },
    LEFT{
        @Override
        void turn(Position pos){
            pos.x--;
        }
    },
    RIGHT{
        @Override
        void turn(Position pos){
            pos.x++;
        }
    };
     abstract void turn(Position pos);
    
}
