package com.dable.cubes;

import android.util.Log;

import java.io.Serializable;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.complex.*;

public class Quant4d {
    double w,x,y,z;

    public Quant4d(double w,double x, double y, double z){
        this.w =w;
        this.z= z;
        this.x = x;
        this.y = y;
    }

    public Quant4d(final double scalar,
                      final double[] v)
            throws DimensionMismatchException {
        if (v.length != 3) {
            throw new DimensionMismatchException(v.length, 3);
        }
        this.w = scalar;
        this.x = v[0];
        this.y = v[1];
        this.z = v[2];
    }

    public Quant4d(final double[] v) {
        this(0, v);
    }

    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
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

    public Quant4d normalize() {
        final double norm = getNorm();

        if (norm < Precision.SAFE_MIN) {

            //throw new ZeroException(LocalizedFormats.Norm, norm);
            return null;
        }

        return new Quant4d(getW() / norm,
                 getX()/ norm,
                getY() / norm,
                getZ() / norm);
    }

    public double getNorm() {
        return FastMath.sqrt(getW() * getW() +
                getX() * getX() +
                getY() * getY() +
                getZ()* getZ());
    }

    public axisAngle transQToAx() {
        axisAngle result = new axisAngle();

        if (getW() > 1){
            Quant4d temp = this.normalize();
            if (temp  == null){
                return null;
            }
        }
        result.angle = 2 * Math.acos(getW());

        double s = Math.sqrt(1-getW()*getW());
        //Log.w(TAG, "The W VALUE IS: " + getW());
        if (s < 0.000001) { // test to avoid divide by zero, s is always positive due to sqrt
            // if s close to zero then direction of axis not important
            result.x = this.x; // if it is important that axis is normalised then replace with x=1; y=z=0;
            result.y = this.y;
            result.z = this.z;
        } else {
            result.x = this.x / s; // normalise axis
            result.y = this.y / s;
            result.z = this.z / s;
        }
        return result;
    }



}
