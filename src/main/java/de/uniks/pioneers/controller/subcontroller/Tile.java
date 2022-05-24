package de.uniks.pioneers.controller.subcontroller;

public class Tile {

    int q;
    int r;
    int s;

    double x;
    double y;

    String type = "";
    int number;

    public Tile(int q, int r, int s){

        this.q = q;
        this.r = r;
        this.s = s;

        double[] kartCoords = cubeToCart(q, r);
        x = kartCoords[0];
        y = kartCoords[1];

    }

    private double[] cubeToCart(double q, double r){

        double[] coords = new double[2];

        coords[0] = Math.sqrt(3) * (r/2 + q);
        coords[1] = -(3.0/2.0) * r;

        return coords;
    }

    @Override
    public String toString(){

        return "X: " + this.x + "Y: " +this.y;

    }
}
