/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kripsi;

import java.util.ArrayList;

/**
 *
 * @author safriansah
 */
public class Berita {
    String judul, isi;
    ArrayList<String> tokens = new ArrayList<String>(); 

    public Berita(String judul, String isi) {
        this.judul = judul;
        this.isi = isi;
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
        return isi;
    }

    public ArrayList<String> getToken() {
        return tokens;
    }
    
}
