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
    int m, n;
    
    public SVD(){
        
    }

    public SVD(double[][] array) {
        m=array.length;
        n=array[0].length;
        a=new Matrix(array);
        svd=new SingularValueDecomposition(a);
        u=svd.getU().getArrayCopy();
        s=svd.getS().getArrayCopy();
        v=svd.getV().getArrayCopy();
        si=svd.getS().inverse().getArrayCopy();
    }
    
    public void setRank(int k){
        //System.out.println(k);
        if(k<1 || k>=Math.min(m, n)) return;
        int i, j;
        double[][] hasil;
        
        hasil=new double[m][k];
        for(i=0; i<m; i++){
            for(j=0; j<k; j++){
                hasil[i][j]=u[i][j];
            }
        }
        u=hasil;
        //System.out.println(u[0].length+" "+hasil[0].length);
        
        hasil=new double[k][k];
        for(i=0; i<k; i++){
            for(j=0; j<k; j++){
                hasil[i][j]=s[i][j];
            }
        }
        s=hasil;
        Matrix msi=new Matrix(s);
        si=msi.inverse().getArrayCopy();
        //System.out.println(s[0].length+" "+hasil[0].length);
        
        hasil=new double[n][k];
        for(i=0; i<n; i++){
            for(j=0; j<k; j++){
                hasil[i][j]=v[i][j];
            }
        }
        v=hasil;
        //System.out.println(v[0].length+" "+hasil[0].length);
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
        int i, j;
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
