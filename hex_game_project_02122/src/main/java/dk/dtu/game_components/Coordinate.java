package dk.dtu.game_components;
import java.util.*;

public class Coordinate {
    int x, y;
    int state;

    public Coordinate(int x, int y, int state){
        this.x = x;
        this.y = y;
        this.state = state;
    }

    public int getX() { 
        return x;
    }
    public int getY() {
        return y;
    }
    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    @Override
    public String toString() {
        return "(" + x + ", " + y + ") -> " + state;
    }
}
