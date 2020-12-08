package nsu.timofeev.core;

import me.ippolitov.fit.snakes.SnakesProto;

import java.util.Objects;

public class Vector {
    private int x;
    private int y;

    public Vector (int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Vector dotsCalc(Vector b) {
        return new Vector(this.getX() - b.getX(), this.getY() - b.getY());
    }

    public SnakesProto.GameState.Coord coord() {
        return SnakesProto.GameState.Coord.newBuilder().setX(getX()).setY(getY()).build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector vector = (Vector) o;
        return x == vector.x &&
                y == vector.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
