/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import model.Berita;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author safriansah
 */
public class Koneksi {
    public static Connection con;
    public static Statement stm;
   
    public Koneksi() {
        try {
            String url ="jdbc:mysql://localhost/db_kripsi";
            String user="root";
            String pass="";
            Class.forName("com.mysql.jdbc.Driver");
            con =DriverManager.getConnection(url,user,pass);
            stm = con.createStatement();
            System.out.println("koneksi berhasil;");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("koneksi gagal: " +e.getMessage());
        }
    }
    
    public ArrayList<Berita> getBeritaList(){
        ArrayList<Berita> beritaList = new ArrayList<>();
        try{
            ResultSet srs = stm.executeQuery("SELECT * FROM tb_berita_coba limit 100");
            while(srs.next()){
                Berita berita=new Berita(srs.getString("judul"), srs.getString("isi"), srs.getString("kategori"));
                beritaList.add(berita);
            }
        } 
        catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }
        return beritaList;
    }
    
    public boolean isKataDasar(String kata){
        int a=0;
        boolean hasil=false;
        try{
            ResultSet srs = stm.executeQuery("SELECT * FROM tb_katadasar WHERE katadasar='"+kata+"' ");
            while(srs.next()){
                a++;
            }
        } 
        catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }
        if(a>0) hasil=true;
        return hasil;
    }
}
