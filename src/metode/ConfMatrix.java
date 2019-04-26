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
public class ConfMatrix {
    int[][] value;
    String[] kelas;
    ArrayList<Berita> data;
    int[] tp, fp, fn, tn;
    double[] akurasi, presisi, recall;

    public ConfMatrix(ArrayList<Berita> beritaTest, String[] label) {
        data=beritaTest;
        kelas=label;
        setValue();
        setTp();
        setFp();
        setFn();
        setTn();
        setAkurasi();
        setPresisi();
        setRecall();
    }
    
    public void setKelas(String[] label){
        kelas=label;
    }
    
    public void setValue(){
        value=new int[kelas.length][kelas.length];
        int i, j;
        for(i=0; i<kelas.length; i++){
            for(j=0; j<kelas.length; j++){
                value[i][j]=0;
                for(Berita a: data){
                    if(a.getKategori().equals(kelas[i]) && a.getPrediksi().equals(kelas[j])){
                        value[i][j]++;
                    }
                }
            }
        }
        for(i=0; i<kelas.length; i++){
            for(j=0; j<kelas.length; j++){
                System.out.print(value[i][j]+" ");
            }
            System.out.println(" ");
        }
    }

    public void setTp() {
        tp=new int[kelas.length];
        int i;
        for(i=0; i<tp.length; i++){
            tp[i]=value[i][i];
        }
        System.out.println("tp "+Arrays.toString(tp));
    }

    public void setFp() {
        fp=new int[kelas.length];
        int i, j;
        for(i=0; i<fp.length; i++){
            fp[i]=0;
            for(j=0; j<fp.length; j++){
                if(j!=i){
                    fp[i]+=value[j][i];
                }
            }
        }
        System.out.println("fp "+Arrays.toString(fp));
    }

    public void setFn() {
        fn=new int[kelas.length];
        int i, j;
        for(i=0; i<fn.length; i++){
            fn[i]=0;
            for(j=0; j<fn.length; j++){
                if(j!=i){
                    fn[i]+=value[i][j];
                }
            }
        }
        System.out.println("fn "+Arrays.toString(fn));
    }

    public void setTn() {
        tn=new int[kelas.length];
        int i, j, k;
        for(i=0; i<tn.length; i++){
            tn[i]=0;
            for(j=0; j<tn.length; j++){
                if(j!=i){
                    for(k=0; k<tn.length; k++){
                        if(k!=i){
                            tn[i]+=value[j][k];
                        }
                    }
                }
            }
        }
        System.out.println("tn "+Arrays.toString(tn));
    }
    
    public void setAkurasi(){
        akurasi=new double[kelas.length];
        int i;
        double a, b;
        for(i=0; i<akurasi.length; i++){
            a=tp[i]+tn[i];
            b=tp[i]+fp[i]+fn[i]+tn[i];
            akurasi[i]=a/b;
        }
        System.out.println("akur "+Arrays.toString(akurasi));
    }
    
    public void setPresisi(){
        presisi=new double[kelas.length];
        int i;
        double a, b;
        for(i=0; i<presisi.length; i++){
            a=tp[i];
            b=tp[i]+fp[i];
            presisi[i]=a/b;
        }
        System.out.println("pres "+Arrays.toString(presisi));
    }
    
    public void setRecall(){
        recall=new double[kelas.length];
        int i;
        double a, b;
        for(i=0; i<recall.length; i++){
            a=tp[i];
            b=tp[i]+fn[i];
            recall[i]=a/b;
        }
        System.out.println("reca "+Arrays.toString(recall));
    }
    
    public double getRata(double[] data){
        double hasil=0;
        int i;
        for(i=0; i<data.length; i++){
            hasil+=data[i];
        }
        hasil=hasil/data.length;
        return hasil;
    }
    
    public int[][] getValue() {
        return value;
    }

    public int[] getTp() {
        return tp;
    }

    public int[] getFp() {
        return fp;
    }

    public int[] getFn() {
        return fn;
    }

    public int[] getTn() {
        return tn;
    }

    public double[] getAkurasi() {
        return akurasi;
    }

    public double[] getPresisi() {
        return presisi;
    }

    public double[] getRecall() {
        return recall;
    }
          
}
