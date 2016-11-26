package net.evlikat.siberian.model;

public abstract class LivingUnit {

    private Position position;

    public LivingUnit(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    protected void setPosition(Position position) {
        this.position = position;
    }
}
