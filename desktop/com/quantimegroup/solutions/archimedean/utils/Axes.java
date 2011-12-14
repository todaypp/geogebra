/*
Archimedean 1.1, a 3D applet/application for visualizing, building, 
transforming and analyzing Archimedean solids and their derivatives.
Copyright 1998, 2011 Raffi J. Kasparian, www.raffikasparian.com.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.quantimegroup.solutions.archimedean.utils;public class Axes {	private OrderedTriple X, Y, Z;	public OrderedTriple origin;	public double sizeFactor = 1;	public Axes() {		X = new OrderedTriple(1, 0, 0);		Y = new OrderedTriple(0, 1, 0);		Z = new OrderedTriple(0, 0, 1);		origin = OrderedTriple.origin();	}	public Axes(Axes a) {		X = new OrderedTriple(a.X);		Y = new OrderedTriple(a.Y);		Z = new OrderedTriple(a.Z);		origin = new OrderedTriple(a.origin);		sizeFactor = a.sizeFactor;	}	public Axes(Quick3X3Matrix m) {		this(m, 1);	}	public Axes(Quick3X3Matrix m, double size) {		X = new OrderedTriple(m.mat[0][0], m.mat[1][0], m.mat[2][0]);		Y = new OrderedTriple(m.mat[0][1], m.mat[1][1], m.mat[2][1]);		Z = new OrderedTriple(m.mat[0][2], m.mat[1][2], m.mat[2][2]);		origin = OrderedTriple.origin();		sizeFactor = size;	}	public void become(Axes a) {		X = new OrderedTriple(a.X);		Y = new OrderedTriple(a.Y);		Z = new OrderedTriple(a.Z);		origin = new OrderedTriple(a.origin);		sizeFactor = a.sizeFactor;	}	public OrderedTriple getX() {		return new OrderedTriple(X);	}	public OrderedTriple getY() {		return new OrderedTriple(Y);	}	public OrderedTriple getZ() {		return new OrderedTriple(Z);	}	public void setX(OrderedTriple x) {		X.become(x);	}	public void setY(OrderedTriple y) {		Y.become(y);	}	public void setZ(OrderedTriple z) {		Z.become(z);	}	public void unrotate() {		X = new OrderedTriple(1, 0, 0);		Y = new OrderedTriple(0, 1, 0);		Z = new OrderedTriple(0, 0, 1);	}	public void correct() {		Z = X.cross(Y).unit();		Y = Z.cross(X).unit();		X = X.unit();	}	public Quick3X3Matrix createMatrix() {		Quick3X3Matrix matrix = new Quick3X3Matrix(X, Y, Z);		return matrix;	}	public Axes invert() {		return new Axes((Quick3X3Matrix) createMatrix().inverse(), 1 / sizeFactor);	}	public void transformVector(OrderedTriple v) {		// rotates a vector the same way a default axes would have to rotate to		// become this		double rx = X.x * v.x + Y.x * v.y + Z.x * v.z;		double ry = X.y * v.x + Y.y * v.y + Z.y * v.z;		double rz = X.z * v.x + Y.z * v.y + Z.z * v.z;		v.become(rx, ry, rz);		v.timesEquals(sizeFactor);	}	public void transformPoint(OrderedTriple p) {		// rotates and translates a point the same way a default axes would have to		// rotate and translate to become this		transformVector(p);		p.plusEquals(origin);	}	void print() {	}	public void timesEquals(Quick3X3Matrix B) {		double xx, xy, xz, yx, yy, yz, zx, zy, zz;		xx = B.mat[0][0] * X.x + B.mat[0][1] * X.y + B.mat[0][2] * X.z;		xy = B.mat[1][0] * X.x + B.mat[1][1] * X.y + B.mat[1][2] * X.z;		xz = B.mat[2][0] * X.x + B.mat[2][1] * X.y + B.mat[2][2] * X.z;		yx = B.mat[0][0] * Y.x + B.mat[0][1] * Y.y + B.mat[0][2] * Y.z;		yy = B.mat[1][0] * Y.x + B.mat[1][1] * Y.y + B.mat[1][2] * Y.z;		yz = B.mat[2][0] * Y.x + B.mat[2][1] * Y.y + B.mat[2][2] * Y.z;		zx = B.mat[0][0] * Z.x + B.mat[0][1] * Z.y + B.mat[0][2] * Z.z;		zy = B.mat[1][0] * Z.x + B.mat[1][1] * Z.y + B.mat[1][2] * Z.z;		zz = B.mat[2][0] * Z.x + B.mat[2][1] * Z.y + B.mat[2][2] * Z.z;		X.become(xx, xy, xz);		Y.become(yx, yy, yz);		Z.become(zx, zy, zz);	}	public String toString() {		return X.toString() + "\n" + Y.toString() + "\n" + Z.toString() + "\n";	}}