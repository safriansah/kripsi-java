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
public class kerpol {
    public void getKernel(double[] x1, double[] x2){
        int row=x1.length;
        int a=0;
        double hasil=0, c=1, d=2; 
        while(a<row){
            hasil+=x1[a]*x2[a];
            a++;
        }
        hasil+=c;
        hasil=Math.pow(hasil, d);
        System.out.println(hasil);
    }
    
}
