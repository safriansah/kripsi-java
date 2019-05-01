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
    double lamda=1, maxD=0, c=1, d=2, gamma=0.01, epsilon=0.001, maxDeltaAlpha=0, complex=1, b;
    double[] alpha, deltaAlpha, error, w;
    double[][] data, kernel, hessian;
    String[] kelas=new String[2];
    int[] y;
    int maxIterasi=10;
    ArrayList<Berita> dataBerita=new ArrayList();

    public SVM() {
        
    }
    
    public SVM(double cost, double degree, double lamda, double gamma, double complexity, double epsilon, int iterasi){
        this.c=cost;
        this.d=degree;
        this.lamda=lamda;
        this.gamma=gamma;
        this.complex=complexity;
        this.epsilon=epsilon;
        this.maxIterasi=iterasi;
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
        error=new double[dataBerita.size()];
        w=new double[dataBerita.size()];
        int i;
        for(i=0; i<dataBerita.size(); i++){
            data[i]=dataBerita.get(i).getSvd();
            if(dataBerita.get(i).getKategori().equals(kelas[0])) y[i]=-1;
            else y[i]=1;
        }
        setKernel();
        setHessian();
        setGamma();
        i=0;
        while(i<maxIterasi){
            setError();
            setDeltaAlpha();
            if(maxDeltaAlpha<epsilon) break;
            i++;
        }
        setW();
        setB();
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
    }
    
    public void setError(){
        for(int i=0; i<error.length; i++){
            error[i]=hitungError(alpha[i], hessian[i]);
        }
    }
    
    public double hitungError(double al, double[] hes){
        double hasil=0;
        for(double d: hes){
            hasil+=al*d;
        }
        return hasil;
    }
    
    public void setDeltaAlpha(){
        for(int i=0; i<deltaAlpha.length; i++){
            deltaAlpha[i]=Math.min(Math.max(gamma*(1-error[i]), alpha[i]), complex-alpha[i]);
            alpha[i]+=deltaAlpha[i];
            maxDeltaAlpha=Math.max(maxDeltaAlpha, deltaAlpha[i]);
        }
    }
    
    public void setW(){
        for(int i=0; i<w.length; i++){
            w[i]=0;
            for(double k:kernel[i]){
                w[i]+=alpha[i]*y[i]*k;
            }
        }
    }
    
    public void setB(){
        b=(-0.5)*(perkalianMatrix(w, alphaTertinggi(1)) + perkalianMatrix(w, alphaTertinggi(-1)));
        //System.out.println(b); 
    }
    
    public double[] alphaTertinggi(int kelas){
        double[] hasil=new double[kernel.length];
        double maxAlpha=0;
        int i=0;
        for(double[] k:kernel){
            if(y[i]==kelas){
                if(maxAlpha<alpha[i]){
                    maxAlpha=alpha[i];
                    hasil=k;
                }
            }
            i++;
        }
        return hasil;
    }
    
    public double perkalianMatrix(double[] a, double[] b){
        double hasil=0;
        for(int i=0; i<a.length; i++){
            hasil+=a[i]*b[i];
        }
        //System.out.println(hasil);
        return hasil;
    }
    
    public String test(double[] test){
        double[] testKernel=new double[data.length];
        int i=0;
        String hasil;
        for(i=0; i<data.length; i++){
            testKernel[i]=hitungKernel(test, data[i]);
        }
        //System.out.println(Arrays.toString(testKernel));
        //System.out.println(Arrays.toString(w));
        //System.out.println(data.length);
        double sign=perkalianMatrix(testKernel, w)+b;
        if(sign>=0) hasil=kelas[1];
        else hasil=kelas[0];
        //System.out.println(kelas[0]+" : "+kelas[1]+" = "+hasil);
        return hasil;
    }
}
