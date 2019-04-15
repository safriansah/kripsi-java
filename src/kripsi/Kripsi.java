/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kripsi;

import config.Koneksi;
import metode.Prepro;
import model.Berita;
import IndonesianStemmer.IndonesianStemmer;
import config.Dictionary;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import metode.SVD;
import metode.SVM;
import metode.TFIDF;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import stemmerindo.Stemmer;

/**
 *
 * @author safriansah
 */
public class Kripsi {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        Koneksi koneksi=new Koneksi();
        Prepro pre=new Prepro();
        ArrayList<Berita> beritaList=new ArrayList<Berita>();
        IndonesianStemmer idnStemming = new IndonesianStemmer();
        TFIDF tfidf=new TFIDF();
        
        beritaList=koneksi.getBeritaList();
        ArrayList<String> tokens=new ArrayList<String>();
        ArrayList<String>[] dok = new ArrayList[beritaList.size()];
        for(int i=0; i<beritaList.size(); i++){
            beritaList.get(i).setTokens(pre.getPrepro(beritaList.get(i).getIsi()));
            tokens.addAll(beritaList.get(i).getTokens());
            dok[i]=beritaList.get(i).getTokens();
        }
        tfidf.setTerm(tokens);
        tokens=tfidf.getTerm();
        int i=1;
        
        tfidf.setTfidf(dok);
        double[][] tif=tfidf.getTfidf();
        
        SVD svd=new SVD(tif);
        tif=svd.getV();
        for(i=0; i<beritaList.size(); i++){
            beritaList.get(i).setSvd(tif[i]);
            //System.out.println(Arrays.toString(tif[i]));
        }
        String[] t={    "Galaxy M20 adalah andalan baru Samsung untuk pasaran ponsel papan tengah di Indonesia yang baru saja diresmikan kehadirannya awal pekan ini.", 
                        "Indonesia Bakal Manfaatkan Minyak Sawit Jadi Bensin dan LPG",
                        "Huawei Bantah Ponsel Lipat Mate X Dijual Rp 24 Juta di Indonesia"
                        };
        String tes=t[2];
        ArrayList<String> prep=pre.getPrepro(tes);
        double[] tfi=tfidf.getQueryTfidf(prep);
        double[] fitur=svd.getVektorQ(tfi);
        System.out.println(tes);
        bagiDataTraining(beritaList, fitur);
        /*for(i=0; i<data.length; i++){
            for(j=0; j<data.length; j++){
                System.out.print(" "+ hessian[i][j]);
            }
            System.out.println(" ");
        }
        System.out.println("\nmatrik V:");
        for (Berita arr : beritaList) {
            System.out.println(Arrays.toString(arr.getSvd()));
        }
        Random randomGenerator;
        randomGenerator = new Random();
        int index=0;
        for(int i=0; i<20; i++){
            index=randomGenerator.nextInt(beritaList.size());
            System.out.print("\n"+index+" : ");
            System.out.print(beritaList.get(index).getKategori());
        }
        /*for(int i=0; i<beritaList.size(); i++){
            beritaList.get(i).setTokens(pre.getPrepro(beritaList.get(i).getIsi()));
            tokens.addAll(beritaList.get(i).getTokens());
            dok[i]=beritaList.get(i).getTokens();
        }
        tfidf.setTerm(tokens);
        tokens=tfidf.getTerm();
        int i=1;
        
        tfidf.setTfidf(dok);
        double[][] tif=tfidf.getTfidf();
        
        SVD svd=new SVD(tif);
        tif=svd.getV();
        for(i=0; i<beritaList.size(); i++){
            beritaList.get(i).setSvd(tif[i]);
        }
        System.out.println("\nmatrik V:");
        for (double[] arr : tif) {
            System.out.println(Arrays.toString(arr));
        }
        
        /*for(String a:tokens){
            System.out.println(i+". "+a);
            i++;
        }
        for (double[] arr : tif) {
            System.out.println(Arrays.toString(arr));
        }
        if(koneksi.isKataDasar("ada"))System.out.println("katadasar");
        String kata="menghapus";
        System.out.println(kata);
        System.out.println("idstemming\t:"+idnStemming.findRootWord(kata));
        //System.out.println("buat sendiri\t:"+pre.delSuffix(pre.delSuffix(pre.delPrefix(pre.delPrefix(pre.delPrefix(kata))))));
        
        List dictionary = new Dictionary().read().getDictionaryData();
        Stemmer stemmer = new Stemmer(dictionary);
        System.out.println("stemeridn\t:" + stemmer.getRootWord(kata));
        
        
        /*ArrayList<String> tokens = new ArrayList<String>();
        ArrayList<String>[] dok = new ArrayList[4];
        String[] isi=new String[4];
        isi[0]="Pihak Samsung tak menampik bahwa harga Samsung Galaxy M20 di Indonesia memang lebih mahal dibandingkan dengan India.";
        isi[1]="Samsung menaruh harapan besar pada Galaxy M10 dan M20 untuk mengembalikan kedudukannya di India, Indonesia, dan negara-negara berkembang lainnya.";
        isi[2]="Sejumlah jenis Bahan Bakar Minyak (BBM) non subsidi atau BBM umum mengalami penurunan harga.";
        isi[3]="Penurunan Harga BBM Salah Satu Indikator Ekonomi Nasional Berhasil.";
        
        int no=0;
        for(String a:isi){
            tokens.addAll(pre.getPrepro(a));
            dok[no]=pre.getPrepro(a);
            no++;
        }
        
        no=1;
        for(String a:tokens){
            //System.out.println(no+". "+a);
            no++;
        }
        tfidf.setTerm(tokens);
        tokens=tfidf.getTerm();
        no=1;
        for(String a:tokens){
            //System.out.println(no+". "+a);
            no++;
        }
        
        double[][] tf=tfidf.getTFIDF(dok);
        for(Berita berita: beritaList){
            System.out.println(berita.getJudul());
            System.out.println(berita.getIsi());
            System.out.println(berita.getKategori());
            System.out.println("");
        }
        
        ArrayList<String> tokens = new ArrayList<String>(); 
        String isi="Galaxy M20 adalah andalan baru Samsung untuk pasaran ponsel papan tengah di Indonesia yang baru saja diresmikan kehadirannya awal pekan ini.";
        tokens=pre.getPrepro(isi);
        int no=1;
        for(String a:tokens){
            //System.out.println(no+". "+a);
            no++;
        }
        LinkedHashSet<String> set = new LinkedHashSet<>(); 
        set.addAll(tokens);
        tokens.clear();
        tokens.addAll(set);
        no=1;
        for(String a:tokens){
            //System.out.println(no+". "+a);
            no++;
        }
        
        double[][] x={  { -0.02, 0.42, 0.02, -0.91 },
                        { -0.07, 0.9, -0.01, 0.42 },
                        { 0.96, 0.07, -0.26, 0 },
                        { 0.26, 0.02, 0.97, 0.03 } };
        double[][] x2={ { 0.602, 0.602, 0.125, 0.301, 0.301, 0.301, 0.602, 0.602, 0.301, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            { 0.301, 0, 0, 0.301, 0.602, 0.301, 0, 0, 0.301, 0.602, 0.602, 0.602, 0.602, 0.602, 0.602, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0.125, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.602, 0.602, 0.602, 0.602, 0.602, 0.602, 0.602, 0.602, 0.301, 0, 0, 0, 0, 0 },
            { 0, 0, 0.125, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.301, 0, 0, 0, 0.301, 0.602, 0.602, 0.602, 0.602, 0.602 } 
        };
        double[][] tes2={ { 0.301, 0, 0, 0.301, 0.301, 0.301, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            { 0.602, 0.602, 0.602, 0.602, 0.602, 0.301, 0.602, 0.602, 0.602, 0.602, 0.602, 0.301, 0, 0, 0, 0.602, 0.602, 0.602, 0.602, 0.602, 0.602, 0.602, 0.602, 0.301, 0, 0, 0, 0, 0 }
        };
        double[] y={ 1, 1, -1, -1};
        SvmKu svm=new SvmKu();
        int i=1;
        int j=1;
        double[][] tes={    { 0.46, -0.01, 0.01, -0.33 },
                            { 0.59, 0, -0.39, -0.01 },
                            { 0.69, 0, -0.01, 0.15 },
                            { 2.120, 2.095, 0.990, 0.994}
        };
        double[] w={ 3.015, 2.957, -2.967, -3.041 };
        double[][] u={{-0.02, -0.01, 0.09,  -0.02, -0.03, -0.02, -0.01, -0.01, -0.02, -0.02, -0.02, -0.02, -0.02, -0.02, -0.02, 0.33,  0.33,  0.33,  0.33,  0.37,  0.33,  0.33,  0.33,  0.21,  0.09,  0.09,  0.09,  0.09,  0.09 },
                { 0.29, 0.14, 0.04, 0.22, 0.38, 0.22, 0.14, 0.14, 0.22, 0.3,  0.3,  0.3,  0.3,  0.3,  0.3,  0.02, 0.02, 0.02, 0.02, 0.03, 0.02, 0.02, 0.02, 0.02, 0.01, 0.01, 0.01, 0.01, 0.01
},
                {0.01, 0.01, 0.07, 0,    -0,  0,    0.01, 0.01, 0,    -0.01, -0.01, -0.01, -0.01, -0.01, -0.01,  -0.11, -0.11, -0.11, -0.11, 0.1,  -0.11, -0.11, -0.11, 0.15, 0.42, 0.42, 0.42, 0.42,  0.42 },
                {-0.34, -0.44, -0.09, -0.12, -0.02, -0.12, -0.44, -0.44, -0.12, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0, 0, 0, 0, 0.01, 0, 0, 0, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01
}
        };
        SvdKu svd=new SvdKu();
        double[] qu={ svd.dot(tes2[0], u[0]),
        svd.dot(tes2[0], u[1]),
        svd.dot(tes2[0], u[2]),
        svd.dot(tes2[0], u[3]) };
        double[][] sigma={ {	0.568, 0.0, 0.0, 0.0},
            {0.0, 0.559, 0.0, 0.0},
            {0.0, 0.0, 	0.719, 0.0},
            {0.0, 0.0, 0, 0.806}
        };
        System.out.println("hsil vektor:");
        double[] vektor={ svd.dot(qu, sigma[0]),
        svd.dot(qu, sigma[1]),
        svd.dot(qu, sigma[2]),
        svd.dot(qu, sigma[3]) };
        j=1;
        System.out.println();
        for(double[] xj:x){
                //System.out.println("matrikhes"+i+j);
                svm.getKernel(vektor, xj);
                j++;
            }
        System.out.println();
        svd.dot(w, vektor);
        for(double[] xi: x2){
            j=1;
            for(double[] xj:x2){
                //System.out.println("matrikhes"+i+j);
                svm.getMatrikHes(y[i-1], y[j-1], xi, xj);
                j++;
            }
            System.out.println(" ");
            i++;
        }
        svm.setGamma();
        for(int a=0; a<10; a++){
            i=1;
            j=1;
            System.out.println("iterasi ke "+(a+1));
            for(double[] xi: x2){
                System.out.print("erro"+i+" ");
                svm.getError(y, i-1, x2);
                i++;
            }
            svm.getDeltaAlpha();
            System.out.println();
            System.out.println(svm.getMaxDeltaAlpha()+" < "+svm.getEpsilon());
            if(svm.getMaxDeltaAlpha()<svm.getEpsilon()) return;
        }
        for(double[] xi: x){
            j=1;
            for(double[] xj:x){
                System.out.println("matrikhes"+i+j);
                svm.getMatrikHes(y[i-1], y[j-1], xi, xj);
                j++;
            }
            System.out.println(" ");
            i++;
        }
        svm.setGamma();
        for(int a=0; a<10; a++){
            i=1;
            j=1;
            System.out.println("iterasi ke "+(a+1));
            for(double[] xi: x){
                System.out.print("erro"+i+" ");
                svm.getError(y, i-1, x);
                i++;
            }
            svm.getDeltaAlpha();
            System.out.println();
            if(svm.getMaxDeltaAlpha()<svm.getEpsilon()) return;
        }
        /*for(double[] xi: x){
            j=1;
            for(double[] xj:x){
                System.out.println("matrikhes"+i+j);
                svm.getMatrikHes(y[i-1], y[j-1], xi, xj);
                j++;
            }
            System.out.println(" ");
            i++;
        }
        for(double[] xi: x){
            j=1;
            for(double[] xj:x){
                System.out.println("kernel"+i+j+" : ");
                svm.getKernel(xi, xj);
                j++;
            }
            System.out.println(" ");
            i++;
        }
        svm.setGamma();
        for(int a=0; a<30; a++){
            i=1;
            j=1;
            System.out.println("iterasi ke "+(a+1));
            for(double[] xi: x){
                System.out.print("erro"+i+" ");
                svm.getError(y, i-1, x);
                i++;
            }
            svm.getDeltaAlpha();
            System.out.println();
            if(svm.getMaxDeltaAlpha()<svm.getEpsilon()) return;
        }*/

    }
    
    public static void bagiDataTraining(ArrayList<Berita> beritaList, double[] fitur){
        String[] kategori={"ekonomi", "olahraga", "teknologi", "entertainment"};
        ArrayList<Berita>[] training = new ArrayList[kategori.length];
        int i=0;
        for(i=0; i<training.length; i++){
            training[i]=new ArrayList();
        }
        for(Berita berita:beritaList){
            for(i=0; i<kategori.length; i++){
                if(berita.getKategori().equals(kategori[i])){
                    training[i].add(berita);
                }
            }
        }
        //for(Berita berita:beritaList){
        //    System.out.println(berita.getKategori());
        //}
        SVM svmku=new SVM();
        svmku.train(training[0], training[2]);
        svmku.test(fitur);
    }
    
}
