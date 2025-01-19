package model.world;

import processing.core.PVector;

public class Cell {
    public final PVector position;

    public Cell(PVector position) {
        this.position = position;
    }

    public PVector getPosition(){
        return position.copy();
    }


}