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
    String judul, isi, kategori;
    ArrayList<String> tokens = new ArrayList<String>(); 

    public Berita(String judul, String isi, String kategori) {
        this.judul = judul;
        this.isi = isi;
        this.kategori = kategori;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public void setIsi(String isi) {
        this.isi = isi;
    }

    public void setToken(ArrayList<String> tokens) {
        this.tokens = tokens;
    }

    public String getJudul() {
        return judul;
    }

    public String getIsi() {
        Document doc = Jsoup.parse(isi);
        return doc.text();
    }

    public ArrayList<String> getToken() {
        return tokens;
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
    
}
