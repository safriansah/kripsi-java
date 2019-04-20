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
import stemmerindo.dictionary.Dictionary;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import metode.ConfMatrix;
import metode.MulticlassSVM;
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
        int i=1;
        Koneksi koneksi=new Koneksi();
        Prepro pre=new Prepro();
        ArrayList<Berita> beritaList=new ArrayList<Berita>();
        ArrayList<Berita> beritaTest=new ArrayList<Berita>();
        IndonesianStemmer idnStemming = new IndonesianStemmer();
        TFIDF tfidf=new TFIDF();
        
        beritaList=koneksi.getBeritaList();
        
        //bagi data menjadi data training(beritaList) dan testing(beritaTesting)
        Random randomGenerator;
        randomGenerator = new Random();
        int index=0;
        for(i=0; i<20; i++){
            index=randomGenerator.nextInt(beritaList.size());
            //System.out.print("\n"+index+" : ");
            //System.out.print(beritaList.get(index).getKategori());
            //System.out.print(" "+beritaList.size());
            beritaTest.add(beritaList.get(index));
            beritaList.remove(index);
        }
        
        //proses perhitungan TFIDF dan SVD untuk data training
        ArrayList<String> tokens=new ArrayList<String>();
        ArrayList<String>[] dok = new ArrayList[beritaList.size()];
        for(i=0; i<beritaList.size(); i++){
            beritaList.get(i).setTokens(pre.getPrepro(beritaList.get(i).getIsi()));
            tokens.addAll(beritaList.get(i).getTokens());
            dok[i]=beritaList.get(i).getTokens();
            //System.out.println(beritaList.get(i).getKategori());
        }
        tfidf.setTerm(tokens);
        tokens=tfidf.getTerm();
        for(String a:tokens) System.out.println(a);
        
        tfidf.setTfidf(dok);
        double[][] tif=tfidf.getTfidf();
        
        SVD svd=new SVD(tif);
        tif=svd.getV();
        for(i=0; i<beritaList.size(); i++){
            beritaList.get(i).setSvd(tif[i]);
            //System.out.println(Arrays.toString(tif[i]));
        }
        
        //proses training multiclass svm
        MulticlassSVM multi=new MulticlassSVM();
        multi.train(beritaList);
                
        //proses testing multiclass svm
        for(i=0; i<beritaTest.size(); i++){
            beritaTest.get(i).setTokens(pre.getPrepro(beritaTest.get(i).getIsi()));
            beritaTest.get(i).setTfidf(tfidf.getQueryTfidf(beritaTest.get(i).getTokens()));
            beritaTest.get(i).setSvd(svd.getVektorQ(beritaTest.get(i).getTfidf()));
            beritaTest.get(i).setPrediksi(multi.test(beritaTest.get(i).getSvd()));
            System.out.println(beritaTest.get(i).getKategori()+" "+beritaTest.get(i).getPrediksi());
        }
        
        ConfMatrix cm=new ConfMatrix(beritaTest, multi.getKelas());
        
        /*String[] kelas={"ekonomi", "entertainment", "olahraga", "teknologi"};
        for(i=0; i<beritaTest.size(); i++){
            index=randomGenerator.nextInt(kelas.length);
            beritaTest.get(i).setPrediksi(kelas[index]);
            System.out.println(beritaTest.get(i).getKategori()+" "+beritaTest.get(i).getPrediksi());
        }
        String[] t={    "Galaxy M20 adalah andalan baru Samsung untuk pasaran ponsel papan tengah di Indonesia yang baru saja diresmikan kehadirannya awal pekan ini.", 
                        "Indonesia Bakal Manfaatkan Minyak Sawit Jadi Bensin dan LPG",
                        "Arema Vs Persebaya, Milomir Yakin Singo Edan Juara Piala Presiden 2019",
                        "marvel memamerkan film barunya yang akan tayang pada tanggal 9 april 2019 berjudul",
                        "Menyambut pemilu, beberapa brand lokal dan internasional yang tergabung dalam Klingking Fun-Pesta Diskon Anti-golput berbondong-bondong menjajakan diskon untuk para warga pemilih. Hanya dengan menunjukkan jari bertinta biru pada 17 April, Anda sudah bisa menikmati diskon di berbagai tempat. Lantas, brand-brand apa saja yang menawarkan diskon dengan hanya menunjukkan jari warna biru? Berikut ini daftar brand yang menjajakan diskon pada 17 April: 1. Buccheri, diskon hingga 50 persen untuk setiap transaksi. 2. Mokka Coffee Cabana, setiap beli satu minuman apa pun, gratis satu minuman. 3. J.Co Indonesia, beli J.Coffee ukuran due seharga Rp 25.000 dengan minimum pembelian 2 J.Coffee. 4. Bakmi Naga Resto, dapatkan diskon 12 persen dalam setiap pembelian produk apa pun. 5. Wacoal, diskon 50 persen setiap pembelian apa pun. 6. Shihlin, gratis kentang goreng untuk 100 pembeli pertama. 7. Sushi Tei, dapatkan makanan gratis setiap minimum transaksi Rp 300.000. 8. Hong Tang, beli satu minuman gratis satu minuman. 9. Singgalang HS Jewelry, dapatkan voucher hingga Rp 250.000 setiap pembelian berlian. 10. Bread Talk, dapatkan roti floss seharga Rp 7.500. 11. Zoya, dapatkan diskon 50 persen dalam setiap pembelian Zoya Lip Paint. 12. Fladeo, dapatkan diskon 50 persen + 30 persen setiap pembelian sepatu. 13. Dan+Dan, potongan harga Rp 25.000 setiap pembelian apa pun. 14. Alfamart, dapatkan potongan harga di beberapa produk. 15. Mama Malaka, gratis Mama Special ABC dengan minimal pembelian Rp 150.000. 16. Roppan, dapatkan harga spesial Rp 150.000 setiap pembelian 2 dozen pastries atau 2 box T-pan. 17. LGS, diskon pakaian hingga 50 persen di setiap produk. 18. Johnwin, diskon pakaian 50 persen di setiap produk. 19. CFC, beli 5 ayam gratis 3 botol Coca-cola. 20. The Duck King Group, cashback 100 persen hingga Rp 500.000 bagi para pemilih yang dine-in. Selain 20 brand di atas, masih banyak lagi brand lain, seperti Sogo, Seibu, Cinema XXI, Ace Hardwere, Electronic City, dan berbagai brand ternama lainnya. Toko offline, toko online seperti Tokopedia dan Bukalapak, pun memberikan diskon pada hari pemilu. Caranya, Anda cukup mengunggah foto jari bertinta biru pada aplikasi khusus, kemudian Anda bisa mendapatkan diskon hingga 50 persen di Tokopedia dan Bukalapak. Jadi, bagaimana? Apakah tertarik untuk memanfaatkan promo di hari pemilu? Video Pilihan PenulisFika Nurul Ulya EditorBambang Priyo Jatmiko Tag: diskon pemilu Berita Terkait PLN Berikan 3 Paket Diskon untuk Pelanggan yang Tambah Daya Listrik Berencana Nikmati Musim Panas di Bangkok? Intip Diskon Tiket Lion Air Garuda Indonesia Beri Diskon, Jakarta-Labuan Bajo Cuma Rp 1,9 Juta Diskon hingga 65 Persen, Tiket Promo Garuda Indonesia Masih Tersedia 170.000 Kursi Ini Daftar Merchant yang Beri Diskon dengan Hanya Tunjukkan Jari Ungu"
                        };
        String tes=t[1];
        ArrayList<String> prep=pre.getPrepro(tes);
        double[] tfi=tfidf.getQueryTfidf(prep);
        double[] fitur=svd.getVektorQ(tfi);
        System.out.println(tes);
        
        MulticlassSVM multi=new MulticlassSVM();
        multi.train(beritaList);
        multi.test(fitur);
        //bagiDataTraining(beritaList, fitur);
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
        int[] hasil=new int[4];
        ArrayList<Berita>[] training = new ArrayList[kategori.length];
        int i=0, j=0, k=0;
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
        SVM[] svmku=new SVM[3];
        k=0;
        for(i=0; i<svmku.length; i++){
            for(j=i+1; j<svmku.length; j++){
                svmku[k]=new SVM();
                svmku[k].train(training[i], training[j]);
                //System.out.println(k+"-"+i+" "+j+"");
                k++;
            }
        }
        
        k=0;
        for(i=0; i<svmku.length; i++){
            for(j=i+1; j<svmku.length; j++){
                System.out.println(svmku[k].test(fitur));
                if(kategori[i].equals(svmku[k].test(fitur))) hasil[i]++;
                else hasil[j]++;
                k++;
            }
        }
        
        System.out.println("hasil kategori : "+kategori[getIndexOfLargest(hasil)]);
        //svmku.train(training[2], training[1]);
        //svmku.test(fitur);
    }
    
    

    public static int getIndexOfLargest( int[] array ){
        if ( array == null || array.length == 0 ) return -1; // null or empty

        int largest = 0;
        for ( int i = 1; i < array.length; i++ ){
            if ( array[i] > array[largest] ) largest = i;
        }
        return largest; // position of the first largest found
    }
}
