/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import model.Berita;

/**
 *
 * @author safriansah
 */
public class MulticlassSVM {
    SVM[] svm;
    String kelas[];
    ArrayList<Berita>[] training;
    double cost=1, d=2, lamda=1, gamma=0.01, complex=1, epsilon=0.001;
    int iterasi=10;
    
    public MulticlassSVM(){
        
    }
    
    public MulticlassSVM(double cost, double degree, double lamda, double gamma, double complexity, double epsilon, int iterasi){
        this.cost=cost;
        this.d=degree;
        this.lamda=lamda;
        this.gamma=gamma;
        this.complex=complexity;
        this.epsilon=epsilon;
        this.iterasi=iterasi;
    }
    
    public void train(ArrayList<Berita> data){
        setKelas(data);
        setTraining(data);
        setSVM();
    }
    
    public void setKelas(ArrayList<Berita> data){
        ArrayList<String> temp=new ArrayList();
        for(Berita a: data){
            temp.add(a.getKategori());
        }
        temp=removeDuplicate(temp);
        kelas=temp.toArray(new String[temp.size()]);
    }
    
    public ArrayList<String> removeDuplicate(ArrayList<String> data) {
        LinkedHashSet<String> set = new LinkedHashSet<>(); 
        set.addAll(data);
        data.clear();
        data.addAll(set);
        Collections.sort(data);
        return data;
    }
    
    public void setTraining(ArrayList<Berita> data){
        int i;
        training=new ArrayList[kelas.length];
        for(i=0; i<kelas.length; i++){
            training[i]=new ArrayList();
        }
        for(Berita berita:data){
            for(i=0; i<kelas.length; i++){
                if(berita.getKategori().equals(kelas[i])){
                    training[i].add(berita);
                }
            }
        }
    }
    
    public void setSVM(){
        int i, j, k;
        k=kelas.length;
        svm=new SVM[k*(k-1)/2];
        k=0;
        for(i=0; i<kelas.length; i++){
            for(j=i+1; j<kelas.length; j++){
                svm[k]=new SVM(cost, d, lamda, gamma, complex, epsilon, iterasi);
                svm[k].train(training[i], training[j]);
                k++;
            }
        }
    }
    
    public String test(double[] fitur){
        int i, j, k;
        int[] hasil=new int[kelas.length];
        k=0;
        for(i=0; i<kelas.length; i++){
            for(j=i+1; j<kelas.length; j++){
                if(kelas[i].equals(svm[k].test(fitur))) hasil[i]++;
                else hasil[j]++;
                k++;
            }
        }
        System.out.println(Arrays.toString(hasil));
        //System.out.println("hasil kategori : "+kelas[getIndexOfLargest(hasil)]);
        return kelas[getIndexOfLargest(hasil)];
    }
    
    public static int getIndexOfLargest(int[] array){
        if(array==null || array.length==0) return -1; // null or empty
        int largest=0;
        for(int i=1; i<array.length; i++){
            if(array[i]>array[largest]) largest=i;
        }
        return largest; // position of the first largest found
    }

    public String[] getKelas() {
        System.out.println(Arrays.toString(kelas));
        return kelas;
    }
}
