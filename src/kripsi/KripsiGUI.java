/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kripsi;

import config.Koneksi;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import metode.ConfMatrix;
import metode.MulticlassSVM;
import metode.Prepro;
import metode.SVD;
import metode.TFIDF;
import model.Berita;

/**
 *
 * @author safriansah
 */
public class KripsiGUI extends javax.swing.JFrame {

    Koneksi koneksi=new Koneksi();
    ArrayList<Berita> beritaList=new ArrayList<>();
    ArrayList<Berita> beritaTest=new ArrayList<>();
    DefaultTableModel datasetTabel, dataTestTabel;
    NumberFormat formatter = new DecimalFormat("#0.00");
    FormValidasi form = new FormValidasi();
    Prepro pre;
    TFIDF tfidf;
    SVD svd;
    MulticlassSVM multi;
    double cost=1, d=2, lamda=1, gamma=0.01, complex=1, epsilon=0.001;
    int iterasi=10, rank=100;
        
    /**
     * Creates new form KripsiView
     * @throws java.lang.Exception
     */
    public KripsiGUI() throws Exception{
        initComponents();
        Dimension screenSize =Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((int)(screenSize.getWidth()-this.getWidth())/2,(int)(screenSize.getHeight()-this.getHeight())/2);
        this.setResizable(false);
        datasetTabel = (DefaultTableModel) jTable1.getModel();
        dataTestTabel = (DefaultTableModel) jTable2.getModel();
        pre=new Prepro();
        multi=new MulticlassSVM();
        jTextArea1.setLineWrap(true);
        jTextArea1.setText(koneksi.getStatus()+"\n\n");
        form.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        form.setGui(this);
    }
    
    public void generateDataset(){
        beritaTest.clear();
        beritaList=koneksi.getBeritaList();
    }
    
    public void setTabel(ArrayList<Berita> dataset, DefaultTableModel tabel){
        int i=1;
        String judul, kategori;
        tabel.setRowCount(0);
        for(Berita data: dataset){
            judul=data.getJudul();
            kategori=data.getKategori();
            Object[] dataTabel = {i, judul, kategori};
            tabel.addRow(dataTabel);
            i++;
        }
    }
    
    public void generateDataTest(){
        Random randomGenerator;
        randomGenerator = new Random();
        int i, index;
        for(i=0; i<28; i++){
            index=randomGenerator.nextInt(beritaList.size());
            beritaTest.add(beritaList.get(index));
            beritaList.remove(index);
        }
    }
    
    public boolean setParam(){
        boolean hasil=false;
        try{
            cost=Double.parseDouble(txtCost.getText());
            d=Double.parseDouble(txtDegree.getText());
            lamda=Double.parseDouble(txtLambda.getText());
            gamma=Double.parseDouble(txtGamma.getText());
            complex=Double.parseDouble(txtComplexity.getText());
            epsilon=Double.parseDouble(txtEpsilon.getText());
            iterasi=Integer.parseInt(txtIterasi.getText());
            rank=Integer.parseInt(txtRank.getText());
            hasil=true;
            if(rank<1 || rank>100) hasil=false;
            
        }
        catch(Exception ex){
            
        }
        return hasil;
    }
    
    public void startKlasifikasi(){
        if(!setParam()){
            JOptionPane.showMessageDialog(this,"Parameter tidak valid");
            return;
        }
        int i, j;
        //proses perhitungan TFIDF dan SVD untuk data training
        tfidf=new TFIDF();
        ArrayList<String> tokens=new ArrayList<>();
        ArrayList<String>[] dok = new ArrayList[beritaList.size()];
        for(i=0; i<beritaList.size(); i++){
            beritaList.get(i).setTokens(pre.getPrepro(beritaList.get(i).getIsi()));
            tokens.addAll(beritaList.get(i).getTokens());
            dok[i]=beritaList.get(i).getTokens();
            //System.out.println(beritaList.get(i).getKategori());
        }
        tfidf.setTerm(tokens);
        tokens=tfidf.getTerm();
        jTextArea1.append("Jumlah term TF-IDF:\t"+tokens.size());
        //for(String a:tokens) System.out.println(a);
        
        tfidf.setTfidf(dok);
        double[][] tif=tfidf.getTfidf();
        /*for(i=0; i<beritaList.size(); i++){
            beritaList.get(i).setTfidf(tfidf.getQueryTfidf(beritaList.get(i).getTokens()));
        }*/
        
        svd=new SVD(tif);
        int k=svd.getV()[0].length*rank/100;
        svd.setRank(k);
        tif=svd.getV();
        for(i=0; i<beritaList.size(); i++){
            beritaList.get(i).setSvd(tif[i]);
            //System.out.println(Arrays.toString(tif[i]));
        }
        jTextArea1.append("\nHasil reduksi fitur SVD:\t"+tif[0].length);
        jTextArea1.append("\nPenggunaan K-rank SVD:\t"+rank+"%\n\n");
        
        //proses training multiclass svm
        multi=new MulticlassSVM(cost, d, lamda, gamma, complex, epsilon, iterasi);
        multi.train(beritaList);
                
        //proses testing multiclass svm
        String hasil;
        for(i=0; i<beritaTest.size(); i++){
            hasil="x";
            beritaTest.get(i).setTokens(pre.getPrepro(beritaTest.get(i).getIsi()));
            beritaTest.get(i).setTfidf(tfidf.getQueryTfidf(beritaTest.get(i).getTokens()));
            beritaTest.get(i).setSvd(svd.getVektorQ(beritaTest.get(i).getTfidf()));
            beritaTest.get(i).setPrediksi(multi.test(beritaTest.get(i).getSvd()));
            if(beritaTest.get(i).getKategori().equals(beritaTest.get(i).getPrediksi())) hasil=" ";
            jTextArea1.append((i+1)+". "+beritaTest.get(i).getPrediksi()+" ("+beritaTest.get(i).getKategori()+")"+" "+hasil+"\n");
        }
        
        String[] kelas=multi.getKelas();
        ConfMatrix cm=new ConfMatrix(beritaTest, kelas);
        jTextArea1.append("\nConfusion Matrix");
        jTextArea1.append("\nKategori\t");
        for(String a:kelas){
            jTextArea1.append(a+"\t");
        }
        jTextArea1.append("\n");
        
        int[][] value=cm.getValue();
        for(i=0; i<kelas.length; i++){
            jTextArea1.append(kelas[i]+"\t");
            for(j=0; j<kelas.length; j++){
                jTextArea1.append(value[i][j]+"\t");
            }
            jTextArea1.append("\n");
        }
        
        int[] tp=cm.getTp();
        jTextArea1.append("\nTP: \t");
        for(i=0; i<kelas.length; i++){
            jTextArea1.append(tp[i]+"\t");
        }
        
        int[] fp=cm.getFp();
        jTextArea1.append("\nFP: \t");
        for(i=0; i<kelas.length; i++){
            jTextArea1.append(fp[i]+"\t");
        }
        
        int[] fn=cm.getFn();
        jTextArea1.append("\nFN: \t");
        for(i=0; i<kelas.length; i++){
            jTextArea1.append(fn[i]+"\t");
        }
        
        int[] tn=cm.getTn();
        jTextArea1.append("\nTN: \t");
        for(i=0; i<kelas.length; i++){
            jTextArea1.append(tn[i]+"\t");
        }
        jTextArea1.append("\n");
        
        double[] akurasi=cm.getAkurasi();
        jTextArea1.append("\nAkurasi: \t");
        for(i=0; i<kelas.length; i++){
            jTextArea1.append(formatter.format(akurasi[i])+"\t");
        }
        
        double[] presisi=cm.getPresisi();
        jTextArea1.append("\nPresisi: \t");
        for(i=0; i<kelas.length; i++){
            jTextArea1.append(formatter.format(presisi[i])+"\t");
        }
        
        double[] recall=cm.getRecall();
        jTextArea1.append("\nRecall: \t");
        for(i=0; i<kelas.length; i++){
            jTextArea1.append(formatter.format(recall[i])+"\t");
        }
        jTextArea1.append("\n");
        
        jTextArea1.append("\nRata-rata");
        jTextArea1.append("\nAkurasi: \t"+formatter.format(cm.getRata(akurasi)));
        jTextArea1.append("\nPresisi: \t"+formatter.format(cm.getRata(presisi)));
        jTextArea1.append("\nRecall: \t"+formatter.format(cm.getRata(recall)));
        jTextArea1.append("\n");
    }
    
    public void startKlasifikasi2(){
        if(!setParam()){
            JOptionPane.showMessageDialog(this,"Parameter tidak valid");
            return;
        }
        int i, j;
        //proses perhitungan TFIDF dan SVD untuk data training
        tfidf=new TFIDF();
        ArrayList<String> tokens=new ArrayList<>();
        ArrayList<String>[] dok = new ArrayList[beritaList.size()];
        for(i=0; i<beritaList.size(); i++){
            beritaList.get(i).setTokens(pre.getPrepro(beritaList.get(i).getIsi()));
            tokens.addAll(beritaList.get(i).getTokens());
            dok[i]=beritaList.get(i).getTokens();
            //System.out.println(beritaList.get(i).getKategori());
        }
        tfidf.setTerm(tokens);
        tokens=tfidf.getTerm();
        jTextArea1.append("Jumlah term TF-IDF:\t"+tokens.size()+"\n\n");
        //for(String a:tokens) System.out.println(a);
        
        tfidf.setTfidf(dok);
        svd=new SVD();
        //double[][] tif=tfidf.getTfidf();
        for(i=0; i<beritaList.size(); i++){
            beritaList.get(i).setTfidf(tfidf.getQueryTfidf(beritaList.get(i).getTokens()));
            beritaList.get(i).setSvd(beritaList.get(i).getTfidf());
        }
        
        //proses training multiclass svm
        multi=new MulticlassSVM(cost, d, lamda, gamma, complex, epsilon, iterasi);
        multi.train(beritaList);
                
        //proses testing multiclass svm
        String hasil;
        for(i=0; i<beritaTest.size(); i++){
            hasil="x";
            beritaTest.get(i).setTokens(pre.getPrepro(beritaTest.get(i).getIsi()));
            beritaTest.get(i).setTfidf(tfidf.getQueryTfidf(beritaTest.get(i).getTokens()));
            beritaTest.get(i).setSvd(beritaTest.get(i).getTfidf());
            beritaTest.get(i).setPrediksi(multi.test(beritaTest.get(i).getSvd()));
            if(beritaTest.get(i).getKategori().equals(beritaTest.get(i).getPrediksi())) hasil=" ";
            jTextArea1.append((i+1)+". "+beritaTest.get(i).getPrediksi()+" ("+beritaTest.get(i).getKategori()+")"+" "+hasil+"\n");
        }
        
        String[] kelas=multi.getKelas();
        ConfMatrix cm=new ConfMatrix(beritaTest, kelas);
        jTextArea1.append("\nConfusion Matrix");
        jTextArea1.append("\nKategori\t");
        for(String a:kelas){
            jTextArea1.append(a+"\t");
        }
        jTextArea1.append("\n");
        
        int[][] value=cm.getValue();
        for(i=0; i<kelas.length; i++){
            jTextArea1.append(kelas[i]+"\t");
            for(j=0; j<kelas.length; j++){
                jTextArea1.append(value[i][j]+"\t");
            }
            jTextArea1.append("\n");
        }
        
        int[] tp=cm.getTp();
        jTextArea1.append("\nTP: \t");
        for(i=0; i<kelas.length; i++){
            jTextArea1.append(tp[i]+"\t");
        }
        
        int[] fp=cm.getFp();
        jTextArea1.append("\nFP: \t");
        for(i=0; i<kelas.length; i++){
            jTextArea1.append(fp[i]+"\t");
        }
        
        int[] fn=cm.getFn();
        jTextArea1.append("\nFN: \t");
        for(i=0; i<kelas.length; i++){
            jTextArea1.append(fn[i]+"\t");
        }
        
        int[] tn=cm.getTn();
        jTextArea1.append("\nTN: \t");
        for(i=0; i<kelas.length; i++){
            jTextArea1.append(tn[i]+"\t");
        }
        jTextArea1.append("\n");
        
        double[] akurasi=cm.getAkurasi();
        jTextArea1.append("\nAkurasi: \t");
        for(i=0; i<kelas.length; i++){
            jTextArea1.append(formatter.format(akurasi[i])+"\t");
        }
        
        double[] presisi=cm.getPresisi();
        jTextArea1.append("\nPresisi: \t");
        for(i=0; i<kelas.length; i++){
            jTextArea1.append(formatter.format(presisi[i])+"\t");
        }
        
        double[] recall=cm.getRecall();
        jTextArea1.append("\nRecall: \t");
        for(i=0; i<kelas.length; i++){
            jTextArea1.append(formatter.format(recall[i])+"\t");
        }
        jTextArea1.append("\n");
        
        jTextArea1.append("\nRata-rata");
        jTextArea1.append("\nAkurasi: \t"+formatter.format(cm.getRata(akurasi)));
        jTextArea1.append("\nPresisi: \t"+formatter.format(cm.getRata(presisi)));
        jTextArea1.append("\nRecall: \t"+formatter.format(cm.getRata(recall)));
        jTextArea1.append("\n");
    }
    
    public void setOutput(String output){
        jTextArea1.append(output);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton5 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtDegree = new javax.swing.JTextField();
        txtLambda = new javax.swing.JTextField();
        txtGamma = new javax.swing.JTextField();
        txtComplexity = new javax.swing.JTextField();
        txtEpsilon = new javax.swing.JTextField();
        txtIterasi = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtCost = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtRank = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(java.awt.Color.white);
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("logo.png")).getImage());
        setResizable(false);

        jPanel1.setBackground(java.awt.Color.white);
        jPanel1.setMaximumSize(new java.awt.Dimension(1024, 768));
        jPanel1.setMinimumSize(new java.awt.Dimension(1024, 768));
        jPanel1.setPreferredSize(new java.awt.Dimension(1024, 768));

        jPanel2.setBackground(new java.awt.Color(240, 240, 240));

        jLabel1.setFont(new java.awt.Font("Open Sans", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Klasifikasi Kategori Berita");

        jLabel2.setFont(new java.awt.Font("Open Sans", 0, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Menggunakan Metode Support Vector Machine Dan Reduksi Fitur Dengan Singular Value Decomposition");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(java.awt.Color.white);
        jPanel3.setMaximumSize(new java.awt.Dimension(1024, 32767));
        jPanel3.setPreferredSize(new java.awt.Dimension(506, 676));

        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Dataset");

        jScrollPane1.setBackground(java.awt.Color.white);
        jScrollPane1.setMaximumSize(new java.awt.Dimension(1024, 489));

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nomor", "Judul", "Kategori"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTable1.setRowHeight(24);
        jTable1.setSelectionBackground(new java.awt.Color(51, 153, 255));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jButton1.setBackground(new java.awt.Color(51, 153, 255));
        jButton1.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jButton1.setForeground(java.awt.Color.white);
        jButton1.setText("Generate");
        jButton1.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(51, 153, 255));
        jButton4.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jButton4.setForeground(java.awt.Color.white);
        jButton4.setText("ConnetDB");
        jButton4.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 488, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton1)
                    .addComponent(jButton4))
                .addContainerGap())
        );

        jButton1.getAccessibleContext().setAccessibleName("");
        jButton4.getAccessibleContext().setAccessibleName("");

        jPanel4.setBackground(java.awt.Color.white);
        jPanel4.setMaximumSize(new java.awt.Dimension(1024, 32767));
        jPanel4.setPreferredSize(new java.awt.Dimension(500, 334));

        jLabel5.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel5.setText("Data Testing");

        jScrollPane3.setBackground(java.awt.Color.white);
        jScrollPane3.setMaximumSize(new java.awt.Dimension(1024, 32767));
        jScrollPane3.setMinimumSize(new java.awt.Dimension(480, 14));

        jTable2.setAutoCreateRowSorter(true);
        jTable2.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nomor", "Judul", "Kategori"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTable2.setRowHeight(24);
        jTable2.setSelectionBackground(new java.awt.Color(51, 153, 255));
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable2MousePressed(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTable2);

        jButton2.setBackground(new java.awt.Color(51, 153, 255));
        jButton2.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jButton2.setForeground(java.awt.Color.white);
        jButton2.setText("Generate");
        jButton2.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(51, 153, 255));
        jButton3.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jButton3.setForeground(java.awt.Color.white);
        jButton3.setText("Classify");
        jButton3.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton6.setText("ClassifyWitoutSVD");
        jButton6.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 482, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton3)
                        .addComponent(jButton6))
                    .addComponent(jButton2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton2.getAccessibleContext().setAccessibleName("");
        jButton3.getAccessibleContext().setAccessibleName("");

        jPanel5.setBackground(java.awt.Color.white);
        jPanel5.setMaximumSize(new java.awt.Dimension(1024, 32767));
        jPanel5.setMinimumSize(new java.awt.Dimension(1024, 100));

        jLabel4.setFont(new java.awt.Font("Open Sans", 1, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Output:");

        jTextArea1.setEditable(false);
        jTextArea1.setBackground(java.awt.Color.white);
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setMargin(new java.awt.Insets(4, 8, 2, 2));
        jScrollPane2.setViewportView(jTextArea1);

        jButton5.setFont(new java.awt.Font("Open Sans", 1, 12)); // NOI18N
        jButton5.setText("Validation");
        jButton5.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                        .addGap(35, 35, 35))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jButton5)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jPanel6.setBackground(new java.awt.Color(51, 153, 255));
        jPanel6.setToolTipText("");

        jLabel6.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel6.setForeground(java.awt.Color.white);
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Parameter SVM");

        jLabel7.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel7.setForeground(java.awt.Color.white);
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Degree");

        txtDegree.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtDegree.setText("2");
        txtDegree.setToolTipText("");
        txtDegree.setMargin(new java.awt.Insets(4, 4, 4, 4));
        txtDegree.setName(""); // NOI18N
        txtDegree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDegreeActionPerformed(evt);
            }
        });

        txtLambda.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtLambda.setText("1");
        txtLambda.setToolTipText("");
        txtLambda.setMargin(new java.awt.Insets(4, 4, 4, 4));

        txtGamma.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtGamma.setText("0.01");
        txtGamma.setToolTipText("");
        txtGamma.setMargin(new java.awt.Insets(4, 4, 4, 4));

        txtComplexity.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtComplexity.setText("1");
        txtComplexity.setToolTipText("");
        txtComplexity.setMargin(new java.awt.Insets(4, 4, 4, 4));

        txtEpsilon.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtEpsilon.setText("0.001");
        txtEpsilon.setToolTipText("");
        txtEpsilon.setMargin(new java.awt.Insets(4, 4, 4, 4));

        txtIterasi.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtIterasi.setText("10");
        txtIterasi.setToolTipText("");
        txtIterasi.setMargin(new java.awt.Insets(4, 4, 4, 4));

        jLabel8.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel8.setForeground(java.awt.Color.white);
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Gamma");

        jLabel9.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel9.setForeground(java.awt.Color.white);
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Lambda");

        jLabel10.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel10.setForeground(java.awt.Color.white);
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Complexity");

        jLabel11.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel11.setForeground(java.awt.Color.white);
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Epsilon");

        jLabel12.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel12.setForeground(java.awt.Color.white);
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Iterasi");

        jLabel13.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel13.setForeground(java.awt.Color.white);
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Cost");

        txtCost.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtCost.setText("1");
        txtCost.setMargin(new java.awt.Insets(4, 4, 4, 4));
        txtCost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCostActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Open Sans", 1, 12)); // NOI18N
        jLabel15.setForeground(java.awt.Color.white);
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("SVD K-rank %");

        txtRank.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtRank.setText("100");
        txtRank.setToolTipText("");
        txtRank.setMargin(new java.awt.Insets(4, 4, 4, 4));
        txtRank.setName(""); // NOI18N
        txtRank.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRankActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtCost, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(32, 32, 32)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtDegree)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE))
                .addGap(32, 32, 32)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtLambda)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE))
                .addGap(32, 32, 32)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtGamma)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE))
                .addGap(32, 32, 32)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtComplexity)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE))
                .addGap(32, 32, 32)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtEpsilon, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(32, 32, 32)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtIterasi)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE))
                .addGap(32, 32, 32)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtRank))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDegree, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLambda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtGamma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtComplexity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEpsilon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIterasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCost, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRank, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 512, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE))
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        generateDataset();
        if(beritaList.size()<1){
            JOptionPane.showMessageDialog(this,"Kesalahan dalam mengambil data dari database");
            return;
        }
        setTabel(beritaTest, dataTestTabel);
        setTabel(beritaList, datasetTabel);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        if(beritaList.size()<=56){
            JOptionPane.showMessageDialog(this,"Dataset tidak mencukupi");
            return;
        }
        generateDataTest();
        setTabel(beritaList, datasetTabel);
        setTabel(beritaTest, dataTestTabel);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        if(beritaTest.size()<1){
            JOptionPane.showMessageDialog(this,"Siapkan dahulu data yang akan diuji");
            return;
        }
        double startTime=System.currentTimeMillis();
        startKlasifikasi();
        double stopTime=System.currentTimeMillis();
        double seconds=((stopTime-startTime)/1000)%60;
        jTextArea1.append("\nWaktu Proses: \t"+formatter.format(seconds)+" detik");
        jTextArea1.append("\n--------------------------------------------\n\n");
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        int index=jTable1.getSelectedRow();
        String isi="judul: "+beritaList.get(index).getJudul()+"\nIsi: "+beritaList.get(index).getIsi()+"\nkategori: "+beritaList.get(index).getKategori();
        jTextArea1.append(isi);
        jTextArea1.append("\n--------------------------------------------\n\n");
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTable2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MousePressed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jTable2MousePressed

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        // TODO add your handling code here:
        int index=jTable2.getSelectedRow();
        String isi="judul: "+beritaTest.get(index).getJudul()+"\nIsi: "+beritaTest.get(index).getIsi()+"\nkategori: "+beritaTest.get(index).getKategori();
        jTextArea1.append(isi);
        jTextArea1.append("\n--------------------------------------------\n\n");
    }//GEN-LAST:event_jTable2MouseClicked

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        koneksi=new Koneksi();
        jTextArea1.append(koneksi.getStatus()+"\n\n");
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        if(multi.getKelas()==null){
            JOptionPane.showMessageDialog(this,"Mesin Klasifikasi belum siap");
            return;
        }
        form.setModel(tfidf, svd, multi);
        form.setVisible(true);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        if(beritaTest.size()<1){
            JOptionPane.showMessageDialog(this,"Siapkan dahulu data yang akan diuji");
            return;
        }
        double startTime=System.currentTimeMillis();
        startKlasifikasi2();
        double stopTime=System.currentTimeMillis();
        double seconds=((stopTime-startTime)/1000)%60;
        jTextArea1.append("\nWaktu Proses: \t"+formatter.format(seconds)+" detik");
        jTextArea1.append("\n--------------------------------------------\n\n");
    }//GEN-LAST:event_jButton6ActionPerformed

    private void txtDegreeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDegreeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDegreeActionPerformed

    private void txtCostActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCostActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCostActionPerformed

    private void txtRankActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRankActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRankActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(KripsiGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(KripsiGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(KripsiGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(KripsiGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new KripsiGUI().setVisible(true);
                } catch (Exception ex) {
                    Logger.getLogger(KripsiGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField txtComplexity;
    private javax.swing.JTextField txtCost;
    private javax.swing.JTextField txtDegree;
    private javax.swing.JTextField txtEpsilon;
    private javax.swing.JTextField txtGamma;
    private javax.swing.JTextField txtIterasi;
    private javax.swing.JTextField txtLambda;
    private javax.swing.JTextField txtRank;
    // End of variables declaration//GEN-END:variables
}
