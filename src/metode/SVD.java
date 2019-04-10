/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metode;

import jama.Matrix;
import jama.SingularValueDecomposition;

/**
 *
 * @author safriansah
 */
public class SVD {
    double[][] u, s, v;
    Matrix a;
    SingularValueDecomposition svd;

    public SVD(double[][] array) {
        a=new Matrix(array);
        svd=new SingularValueDecomposition(a);
        u=svd.getU().getArrayCopy();
        s=svd.getS().getArrayCopy();
        v=svd.getV().getArrayCopy();
    }

    public double[][] getU() {
        return u;
    }

    public double[][] getS() {
        return s;
    }

    public double[][] getV() {
        return v;
    }
    
    
}
