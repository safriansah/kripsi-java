/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kripsi;

/**
 *
 * @author safriansah
 */
public class SvmKu {
    double lamda=1, maxD=0, c=1, d=2, gamma=0.01, epsilon=0.001;
    double[] alpha={ 0, 0, 0, 0 }, deltaAlpha={ 0, 0, 0, 0 }, error={ 0, 0, 0, 0 };
    
    public double getKernel(double[] x1, double[] x2){
        int row=x1.length;
        int a=0;
        double hasil=0; 
        while(a<row){
            hasil+=x1[a]*x2[a];
            a++;
        }
        hasil+=c;
        hasil=Math.pow(hasil, d);
        System.out.print("kernel : ");
        System.out.println(hasil);
        return hasil;
    }
    
    public double getMatrikHes(double y1, double y2, double[] x1, double[] x2){
        double hasil=0;
        hasil+=y1*y2*(getKernel(x1, x2)+Math.pow(lamda, 2));
        if(hasil>maxD) maxD=hasil;
        //System.out.print("hessian : ");
        //System.out.println(hasil);
        return hasil;
    }
    
    public void getError(double[] y, int i, double[][] x){
        double hasil=0;
        int j=0;
        for(double[] xj: x){
            hasil+=alpha[i]*getMatrikHes(y[i], y[j], x[i], xj);
            System.out.println(+alpha[i]+" * "+getMatrikHes(y[i], y[j], x[i], xj)+" = "+alpha[i]*getMatrikHes(y[i], y[j], x[i], xj));
            j++;
        }
        System.out.println(hasil);
        error[i]=hasil;
    }
    
    public void setGamma(){
        gamma=gamma/maxD;
        System.out.println("maxd :"+maxD);
        System.out.println("gamma :"+gamma);
    }
    
    public void getDeltaAlpha(){
        for(int i=0; i<deltaAlpha.length; i++){
            deltaAlpha[i]=Math.min(Math.max(gamma*(1-error[i]), alpha[i]), c-alpha[i]);
            //System.out.println(alpha[i]+" + "+deltaAlpha[i]);
            alpha[i]+=deltaAlpha[i];
        }
        for(int i=0; i<deltaAlpha.length; i++){
            System.out.print("da"+i+" ");
            System.out.println(deltaAlpha[i]);
            System.out.print("alpha"+i+" ");
            System.out.println(alpha[i]);
        }
    }
    
    public double getMaxDeltaAlpha(){
        double hasil=0;
        for(double da:deltaAlpha){
            hasil=Math.max(da, hasil);
        }
        return hasil;
    }
    
    public double getEpsilon(){
        return epsilon;
    }
}
