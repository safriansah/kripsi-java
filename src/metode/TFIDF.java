/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metode;

import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 *
 * @author safriansah
 */
public class TFIDF {
    LinkedHashSet<String> set = new LinkedHashSet<>();
    ArrayList<String> term = new ArrayList<String>();
    double[][] tf, tfidf;
    double[] idf;
    int[] df;

    public TFIDF() {
    
    }

    public ArrayList<String> getTerm() {
        return term;
    }

    public void setTerm(ArrayList<String> tokens) {
        LinkedHashSet<String> set = new LinkedHashSet<>(); 
        set.addAll(tokens);
        tokens.clear();
        tokens.addAll(set);
        this.term = tokens;
    }

    public void setTfidf(ArrayList<String>[] dok) {
        int n=dok.length,
                m=term.size();
        this.tfidf=new double[m][n];
        this.tf=new double[m][n];
        this.idf=new double[m];
        this.df=new int[m];
        
        int i=0, j=0;
        for(String a:term){
            df[i]=0;
            j=0;
            for(ArrayList<String> d:dok){
                tf[i][j]=0;
                for(String b: d){
                    if(a.equals(b)){
                        tf[i][j]+=1;
                    }
                }
                if(tf[i][j]>0){
                    df[i]+=1;
                }
                j++;
            }
            i++;
        }

        i=0;
        for(int a: df){
            idf[i]=Math.log10(n/a);
            i++;
        }
        
        i=0;
        while(i<m){
            j=0;
            while(j<n){
                tfidf[i][j]=tf[i][j]*idf[i];
                j++;
            }
            i++;
        }
    }

    public double[][] getTf() {
        return tf;
    }

    public double[][] getTfidf() {
        return tfidf;
    }
    
    public double[] getIdf() {
        return idf;
    }

    public int[] getDf() {
        return df;
    }
    
    /*
    public double[][] getTFIDF(ArrayList<String>[] dok){
        int n=dok.length,
                m=term.size();
        this.tfidf=new double[m][n];
        this.tf=new double[m][n];
        this.idf=new double[m];
        this.df=new int[m];
        
        int i=0, j=0;
        for(String a:term){
            df[i]=0;
            j=0;
            for(ArrayList<String> d:dok){
                tf[i][j]=0;
                for(String b: d){
                    if(a.equals(b)){
                        tf[i][j]+=1;
                    }
                }
                if(tf[i][j]>0){
                    df[i]+=1;
                }
                j++;
            }
            i++;
        }
        
        i=0;
        System.out.println("\n TF :");
        for(String a:term){
            System.out.println("\t "+tf[i][0]+" "+tf[i][1]+" "+tf[i][2]+" "+tf[i][3]);
            i++;
        }
        
        //System.out.println("\n IDF :");
        i=0;
        for(int a: df){
            idf[i]=Math.log10(n/a);
            //System.out.println("log("+n+"/"+a+")="+idf[i]);
            i++;
        }
        
        i=0;
        //System.out.println("\n TFIDF :");
        while(i<m){
            j=0;
            while(j<n){
                tfidf[i][j]=tf[i][j]*idf[i];
                j++;
            }
            //System.out.println("\t "+tfidf[i][0]+" "+tfidf[i][1]+" "+tfidf[i][2]+" "+tfidf[i][3]);
            i++;
        }
        
        return tfidf;
    }*/
    
}
