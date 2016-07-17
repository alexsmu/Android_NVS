/**
 * GraphView
 * Copyright (C) 2014  Jonas Gehring
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License,
 * with the "Linking Exception", which can be found at the license.txt
 * file in this program.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * with the "Linking Exception" along with this program; if not,
 * write to the author Jonas Gehring <g.jjoe64@gmail.com>.
 */
package com.jjoe64.graphview.series;

import android.provider.ContactsContract;

import java.io.Serializable;
import java.util.Date;

/**
 * default data point implementation.
 * This stores the x and y values.
 *
 * @author jjoe64
 */
public class DataPoint implements DataPointInterface, Serializable {
    private static final long serialVersionUID=1428263322645L;

    private double x;
    private double y;
    private String t;
    private boolean o;

    public DataPoint(double x, double y) {
        this.x=x;
        this.y=y;
        this.t = null;
        this.o = false;
    }

    public DataPoint(Date x, double y) {
        this.x = x.getTime();
        this.y = y;
        this.t = null;
        this.o = false;
    }

    public DataPoint(double x, double y, String tag) {
        this.x=x;
        this.y=y;
        this.t = tag;
        this.o = false;
    }

    public DataPoint(double x, double y, String tag, boolean occurrence) {
        this.x=x;
        this.y=y;
        this.t = tag;
        this.o = occurrence;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "["+x+"/"+y+"]";
    }

    @Override
    public String getTag() {
        return t;
    }
    @Override
    public boolean isOcc() {
        return o;
    }
}
