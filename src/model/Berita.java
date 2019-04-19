/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author safriansah
 */
public class Berita {
    String judul, isi, kategori, prediksi;
    ArrayList<String> tokens = new ArrayList<String>();
    double[] tfidf, svd;

    public Berita(String judul, String isi, String kategori) {
        this.judul = judul;
        this.isi = isi;
        this.kategori = kategori;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getPrediksi() {
        return prediksi;
    }

    public void setPrediksi(String prediksi) {
        this.prediksi = prediksi;
    }

    public void setIsi(String isi) {
        this.isi = isi;
    }

    public String getJudul() {
        return judul;
    }

    public String getIsi() {
        Document doc = Jsoup.parse(isi);
        return doc.text();
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public ArrayList<String> getTokens() {
        return tokens;
    }

    public void setTokens(ArrayList<String> tokens) {
        this.tokens = tokens;
    }

    public double[] getTfidf() {
        return tfidf;
    }

    public void setTfidf(double[] tfidf) {
        this.tfidf = tfidf;
    }

    public double[] getSvd() {
        return svd;
    }

    public void setSvd(double[] svd) {
        this.svd = svd;
    }
}
