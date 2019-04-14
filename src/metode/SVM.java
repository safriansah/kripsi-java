/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metode;

import java.util.ArrayList;
import java.util.Arrays;
import model.Berita;

/**
 *
 * @author safriansah
 */
public class SVM {
    double lamda=1, maxD=0, c=1, d=2, gamma=0.01, epsilon=0.001;
    double[] alpha, deltaAlpha, error;
    double[][] data, kernel, hessian;
    String[] kelas=new String[2];
    int[] y;
    ArrayList<Berita> dataBerita=new ArrayList();

    public SVM() {
        
    }
    
    public void train(ArrayList<Berita> train1, ArrayList<Berita> train2){
        dataBerita.addAll(train1);
        dataBerita.addAll(train2);
        kelas[0]=train1.get(0).getKategori();
        kelas[1]=train2.get(0).getKategori();
        data=new double[dataBerita.size()][dataBerita.get(0).getSvd().length];
        y=new int[dataBerita.size()];
        alpha=new double[dataBerita.size()];
        Arrays.fill(alpha, 0);
        deltaAlpha=new double[dataBerita.size()];
        Arrays.fill(deltaAlpha, 0);
        int i;
        for(i=0; i<dataBerita.size(); i++){
            data[i]=dataBerita.get(i).getSvd();
            if(dataBerita.get(i).getKategori().equals(kelas[0])) y[i]=-1;
            else y[i]=1;
        }
        setKernel();
        setHessian();
        setGamma();
    }
    
    public void setKernel(){
        kernel=new double[data.length][data.length];
        int i,j;
        for(i=0; i<data.length; i++){
            for(j=0; j<data.length; j++){
                kernel[i][j]=hitungKernel(data[i], data[j]);
            }
        }
    }
    
    public double hitungKernel(double[] x1, double[] x2){
        int row=x1.length;
        int a=0;
        double hasil=0; 
        while(a<row){
            hasil+=x1[a]*x2[a];
            a++;
        }
        hasil+=c;
        hasil=Math.pow(hasil, d);
        return hasil;
    }
    
    public void setHessian(){
        hessian=new double[data.length][data.length];
        int i,j;
        for(i=0; i<data.length; i++){
            for(j=0; j<data.length; j++){
                hessian[i][j]=hitungHessian(y[i], data[i], y[j], data[j]);
            }
        }
        
    }
    
    public double hitungHessian(int y1, double[] x1, int y2, double[] x2){
        double hasil=0;
        hasil+=y1*y2*(hitungKernel(x1, x2)+Math.pow(lamda, 2));
        if(hasil>maxD) maxD=hasil;
        return hasil;
    }
    
    public void setGamma(){
        gamma=gamma/maxD;
        System.out.print(gamma);
    }
    
    public void setError(){
        //for(int i: data)
    }
    
    public void getError(double[] y, int i, double[][] x){
        double hasil=0;
        int j=0;
        for(double[] xj: x){
            //hasil+=alpha[i]*hitungHessian(y[i], x[i], y[j], xj);
            //System.out.println(+alpha[i]+" * "+hitungHessian(y[i], x[i], y[j], xj)+" = "+alpha[i]*hitungHessian(y[i], x[i], y[j], xj));
            j++;
        }
        System.out.println(hasil);
        error[i]=hasil;
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
