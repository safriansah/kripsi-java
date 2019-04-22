/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metode;

import jama.Matrix;
import jama.SingularValueDecomposition;
import java.util.Arrays;

/**
 *
 * @author safriansah
 */
public class SVD {
    double[][] u, s, v, si;
    Matrix a;
    SingularValueDecomposition svd;

    public SVD(double[][] array) {
        a=new Matrix(array);
        svd=new SingularValueDecomposition(a);
        u=svd.getU().getArrayCopy();
        s=svd.getS().getArrayCopy();
        v=svd.getV().getArrayCopy();
        si=svd.getS().inverse().getArrayCopy();
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
    
    public double[] getVektorQ(double[] tfidf){
        double[] hasil=new double[s.length];
        double[] temp=new double[s.length];
        int i=0, j=0;
        for(i=0; i<u[0].length; i++){
            for(j=0; j<tfidf.length; j++){
                temp[i]+=tfidf[j]*u[j][i];
            }
        }
        for(i=0; i<si.length; i++){
            for(j=0; j<temp.length; j++){
                hasil[i]+=temp[j]*si[j][i];
            }
        }
        return hasil;
    }
}
