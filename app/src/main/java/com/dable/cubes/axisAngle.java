package com.dable.cubes;
/**
 * Created by user on 16/6/8.
 */
public class axisAngle {

    double angle,x,y,z;

    public axisAngle(){}

    public axisAngle(double angle, double x, double y, double z){
        this.angle =angle;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double changeRadianToDegree(double radian){
        double degree = Math.toDegrees(radian);
        return degree;
    }
}
