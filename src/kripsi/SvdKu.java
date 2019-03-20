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
public class SvdKu {
    public double dot(double[] x, double[] y) {
        if (x.length != y.length) throw new RuntimeException("Illegal vector dimensions.");
        double sum = 0.0;
        for (int i = 0; i < x.length; i++){
            //System.out.print(x[i]+" * "+y[i]+" = ");
            //System.out.println(x[i] * y[i]);
            sum += x[i] * y[i];
        }
        System.out.println(sum);
        return sum;
    }
}
