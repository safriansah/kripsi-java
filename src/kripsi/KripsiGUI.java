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
    Prepro pre;
    ArrayList<Berita> beritaList=new ArrayList<Berita>();
    ArrayList<Berita> beritaTest=new ArrayList<Berita>();
    DefaultTableModel datasetTabel, dataTestTabel;
        
    /**
     * Creates new form KripsiView
     */
    public KripsiGUI() throws Exception{
        initComponents();
        Dimension screenSize =Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((int)(screenSize.getWidth()-this.getWidth())/2,(int)(screenSize.getHeight()-this.getHeight())/2);
        this.setResizable(false);
        datasetTabel = (DefaultTableModel) jTable1.getModel();
        dataTestTabel = (DefaultTableModel) jTable2.getModel();
        pre=new Prepro();
        jTextArea1.setText("Output:\n");
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
            Object[] dataTabel = {i+".", judul, kategori};
            tabel.addRow(dataTabel);
            i++;
        }
    }
    
    public void generateDataTest(){
        Random randomGenerator;
        randomGenerator = new Random();
        int i, index=0;
        for(i=0; i<20; i++){
            index=randomGenerator.nextInt(beritaList.size());
            beritaTest.add(beritaList.get(index));
            beritaList.remove(index);
        }
    }
    
    public void startKlasifikasi(){
        int i, j;
        //proses perhitungan TFIDF dan SVD untuk data training
        TFIDF tfidf=new TFIDF();
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
        //for(String a:tokens) System.out.println(a);
        
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
        
        NumberFormat formatter = new DecimalFormat("#0.00");  
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
        jTextArea1.append("\n--------------------------------------------\n\n");
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
        jPanel4 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(java.awt.Color.white);
        setMaximumSize(new java.awt.Dimension(1024, 768));
        setResizable(false);

        jPanel1.setBackground(java.awt.Color.white);
        jPanel1.setMaximumSize(new java.awt.Dimension(1024, 768));
        jPanel1.setMinimumSize(new java.awt.Dimension(1024, 768));
        jPanel1.setPreferredSize(new java.awt.Dimension(1024, 768));

        jPanel2.setBackground(new java.awt.Color(247, 247, 247));

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
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1024, Short.MAX_VALUE)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addContainerGap(24, Short.MAX_VALUE))
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
        jScrollPane1.setViewportView(jTable1);

        jButton1.setBackground(new java.awt.Color(51, 153, 255));
        jButton1.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jButton1.setForeground(java.awt.Color.white);
        jButton1.setText("Generate");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

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
        jScrollPane3.setViewportView(jTable2);

        jButton2.setBackground(new java.awt.Color(51, 153, 255));
        jButton2.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jButton2.setForeground(java.awt.Color.white);
        jButton2.setText("Generate");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(51, 153, 255));
        jButton3.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jButton3.setForeground(java.awt.Color.white);
        jButton3.setText("Start Classification");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton2))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE))
                        .addGap(0, 2, Short.MAX_VALUE))
                    .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel5.setBackground(java.awt.Color.white);
        jPanel5.setMaximumSize(new java.awt.Dimension(1024, 32767));
        jPanel5.setMinimumSize(new java.awt.Dimension(1024, 100));

        jLabel4.setFont(new java.awt.Font("Open Sans", 1, 12)); // NOI18N
        jLabel4.setText("Hasil Klasifikasi:");

        jTextArea1.setEditable(false);
        jTextArea1.setBackground(java.awt.Color.white);
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setMargin(new java.awt.Insets(4, 8, 0, 0));
        jScrollPane2.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 512, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE))
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        if(beritaList.size()<21){
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
        startKlasifikasi();
    }//GEN-LAST:event_jButton3ActionPerformed

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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
