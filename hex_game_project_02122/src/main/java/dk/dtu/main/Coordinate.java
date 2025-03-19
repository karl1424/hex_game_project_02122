package dk.dtu.main;
import java.util.*;

class Coordinate {
    int x, y;
    int state;

    Coordinate(int x, int y, int state){
        this.x = x;
        this.y = y;
        this.state = state;
    }
    
    //#region Get/Set
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
    //#endregion

    //#region IDK?!
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Coordinate that = (Coordinate) obj;
        return x == that.x && y == that.y;
    }
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    @Override
    public String toString() {
        return "(" + x + ", " + y + ") -> " + state;
    }
    //#endregion
}