/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metode;

import stemmerindo.dictionary.Dictionary;
import java.util.ArrayList;
import java.util.List;
import stemmerindo.Stemmer;

/**
 *
 * @author safriansah
 */
public class Prepro {
    String[] stopWords={"ada","adalah","adanya","adapun","agak","agaknya","agar","akan","akankah","akhir","akhiri","akhirnya","aku","akulah","amat","amatlah","anda","andalah","antar","antara","antaranya","apa","apaan","apabila","apakah","apalagi","apatah","artinya","asal","asalkan","atas","atau","ataukah","ataupun","awal","awalnya","bagai","bagaikan","bagaimana","bagaimanakah","bagaimanapun","bagi","bagian","bahkan","bahwa","bahwasanya","baik","bakal","bakalan","balik","banyak","bapak","baru","bawah","beberapa","begini","beginian","beginikah","beginilah","begitu","begitukah","begitulah","begitupun","bekerja","belakang","belakangan","belum","belumlah","benar","benarkah","benarlah","berada","berakhir","berakhirlah","berakhirnya","berapa","berapakah","berapalah","berapapun","berarti","berawal","berbagai","berdatangan","beri","berikan","berikut","berikutnya","berjumlah","berkali-kali","berkata","berkehendak","berkeinginan","berkenaan","berlainan","berlalu","berlangsung","berlebihan","bermacam","bermacam-macam","bermaksud","bermula","bersama","bersama-sama","bersiap","bersiap-siap","bertanya","bertanya-tanya","berturut","berturut-turut","bertutur","berujar","berupa","besar","betul","betulkah","biasa","biasanya","bila","bilakah","bisa","bisakah","boleh","bolehkah","bolehlah","buat","bukan","bukankah","bukanlah","bukannya","bulan","bung","cara","caranya","cukup","cukupkah","cukuplah","cuma","dahulu","dalam","dan","dapat","dari","daripada","datang","dekat","demi","demikian","demikianlah","dengan","depan","di","dia","diakhiri","diakhirinya","dialah","diantara","diantaranya","diberi","diberikan","diberikannya","dibuat","dibuatnya","didapat","didatangkan","digunakan","diibaratkan","diibaratkannya","diingat","diingatkan","diinginkan","dijawab","dijelaskan","dijelaskannya","dikarenakan","dikatakan","dikatakannya","dikerjakan","diketahui","diketahuinya","dikira","dilakukan","dilalui","dilihat","dimaksud","dimaksudkan","dimaksudkannya","dimaksudnya","diminta","dimintai","dimisalkan","dimulai","dimulailah","dimulainya","dimungkinkan","dini","dipastikan","diperbuat","diperbuatnya","dipergunakan","diperkirakan","diperlihatkan","diperlukan","diperlukannya","dipersoalkan","dipertanyakan","dipunyai","diri","dirinya","disampaikan","disebut","disebutkan","disebutkannya","disini","disinilah","ditambahkan","ditandaskan","ditanya","ditanyai","ditanyakan","ditegaskan","ditujukan","ditunjuk","ditunjuki","ditunjukkan","ditunjukkannya","ditunjuknya","dituturkan","dituturkannya","diucapkan","diucapkannya","diungkapkan","dong","dua","dulu","empat","enggak","enggaknya","entah","entahlah","guna","gunakan","hal","hampir","hanya","hanyalah","hari","harus","haruslah","harusnya","hendak","hendaklah","hendaknya","hingga","ia","ialah","ibarat","ibaratkan","ibaratnya","ibu","ikut","ingat","ingat-ingat","ingin","inginkah","inginkan","ini","inikah","inilah","itu","itukah","itulah","jadi","jadilah","jadinya","jangan","jangankan","janganlah","jauh","jawab","jawaban","jawabnya","jelas","jelaskan","jelaslah","jelasnya","jika","jikalau","juga","jumlah","jumlahnya","justru","kala","kalau","kalaulah","kalaupun","kalian","kami","kamilah","kamu","kamulah","kan","kapan","kapankah","kapanpun","karena","karenanya","kasus","kata","katakan","katakanlah","katanya","ke","keadaan","kebetulan","kecil","kedua","keduanya","keinginan","kelamaan","kelihatan","kelihatannya","kelima","keluar","kembali","kemudian","kemungkinan","kemungkinannya","kenapa","kepada","kepadanya","kesampaian","keseluruhan","keseluruhannya","keterlaluan","ketika","khususnya","kini","kinilah","kira","kira-kira","kiranya","kita","kitalah","kok","kurang","lagi","lagian","lah","lain","lainnya","lalu","lama","lamanya","lanjut","lanjutnya","lebih","lewat","lima","luar","macam","maka","makanya","makin","malah","malahan","mampu","mampukah","mana","manakala","manalagi","masa","masalah","masalahnya","masih","masihkah","masing","masing-masing","mau","maupun","melainkan","melakukan","melalui","melihat","melihatnya","memang","memastikan","memberi","memberikan","membuat","memerlukan","memihak","meminta","memintakan","memisalkan","memperbuat","mempergunakan","memperkirakan","memperlihatkan","mempersiapkan","mempersoalkan","mempertanyakan","mempunyai","memulai","memungkinkan","menaiki","menambahkan","menandaskan","menanti","menanti-nanti","menantikan","menanya","menanyai","menanyakan","mendapat","mendapatkan","mendatang","mendatangi","mendatangkan","menegaskan","mengakhiri","mengapa","mengatakan","mengatakannya","mengenai","mengerjakan","mengetahui","menggunakan","menghendaki","mengibaratkan","mengibaratkannya","mengingat","mengingatkan","menginginkan","mengira","mengucapkan","mengucapkannya","mengungkapkan","menjadi","menjawab","menjelaskan","menuju","menunjuk","menunjuki","menunjukkan","menunjuknya","menurut","menuturkan","menyampaikan","menyangkut","menyatakan","menyebutkan","menyeluruh","menyiapkan","merasa","mereka","merekalah","merupakan","meski","meskipun","meyakini","meyakinkan","minta","mirip","misal","misalkan","misalnya","mula","mulai","mulailah","mulanya","mungkin","mungkinkah","nah","naik","namun","nanti","nantinya","nyaris","nyatanya","oleh","olehnya","pada","padahal","padanya","pak","paling","panjang","pantas","para","pasti","pastilah","penting","pentingnya","per","percuma","perlu","perlukah","perlunya","pernah","persoalan","pertama","pertama-tama","pertanyaan","pertanyakan","pihak","pihaknya","pukul","pula","pun","punya","rasa","rasanya","rata","rupanya","saat","saatnya","saja","sajalah","saling","sama","sama-sama","sambil","sampai","sampai-sampai","sampaikan","sana","sangat","sangatlah","satu","saya","sayalah","se","sebab","sebabnya","sebagai","sebagaimana","sebagainya","sebagian","sebaik","sebaik-baiknya","sebaiknya","sebaliknya","sebanyak","sebegini","sebegitu","sebelum","sebelumnya","sebenarnya","seberapa","sebesar","sebetulnya","sebisanya","sebuah","sebut","sebutlah","sebutnya","secara","secukupnya","sedang","sedangkan","sedemikian","sedikit","sedikitnya","seenaknya","segala","segalanya","segera","seharusnya","sehingga","seingat","sejak","sejauh","sejenak","sejumlah","sekadar","sekadarnya","sekali","sekali-kali","sekalian","sekaligus","sekalipun","sekarang","sekecil","seketika","sekiranya","sekitar","sekitarnya","sekurang-kurangnya","sekurangnya","sela","selagi","selain","selaku","selalu","selama","selama-lamanya","selamanya","selanjutnya","seluruh","seluruhnya","semacam","semakin","semampu","semampunya","semasa","semasih","semata","semata-mata","semaunya","sementara","semisal","semisalnya","sempat","semua","semuanya","semula","sendiri","sendirian","sendirinya","seolah","seolah-olah","seorang","sepanjang","sepantasnya","sepantasnyalah","seperlunya","seperti","sepertinya","sepihak","sering","seringnya","serta","serupa","sesaat","sesama","sesampai","sesegera","sesekali","seseorang","sesuatu","sesuatunya","sesudah","sesudahnya","setelah","setempat","setengah","seterusnya","setiap","setiba","setibanya","setidak-tidaknya","setidaknya","setinggi","seusai","sewaktu","siap","siapa","siapakah","siapapun","sini","sinilah","soal","soalnya","suatu","sudah","sudahkah","sudahlah","supaya","tadi","tadinya","tahu","tahun","tak","tambah","tambahnya","tampak","tampaknya","tandas","tandasnya","tanpa","tanya","tanyakan","tanyanya","tapi","tegas","tegasnya","telah","tempat","tengah","tentang","tentu","tentulah","tentunya","tepat","terakhir","terasa","terbanyak","terdahulu","terdapat","terdiri","terhadap","terhadapnya","teringat","teringat-ingat","terjadi","terjadilah","terjadinya","terkira","terlalu","terlebih","terlihat","termasuk","ternyata","tersampaikan","tersebut","tersebutlah","tertentu","tertuju","terus","terutama","tetap","tetapi","tiap","tiba","tiba-tiba","tidak","tidakkah","tidaklah","tiga","tinggi","toh","tunjuk","turut","tutur","tuturnya","ucap","ucapnya","ujar","ujarnya","umum","umumnya","ungkap","ungkapnya","untuk","usah","usai","waduh","wah","wahai","waktu","waktunya","walau","walaupun","wong","yaitu","yakin","yakni","yang"};
    List dictionary;
    Stemmer stemmer;

    public Prepro() throws Exception{
        this.dictionary=new Dictionary().read().getDictionaryData();
        this.stemmer = new Stemmer(dictionary);
    }

    public ArrayList<String> getPrepro(String isi) {
        ArrayList<String> token=this.getFilter(this.getTokens(this.getCaseFolding(isi)));
        for(int i=0; i<token.size(); i++){
            token.set(i, this.getStemming(token.get(i)));
        }
        return token;
    }
    
    public String getCaseFolding(String isi){
        isi=isi.toLowerCase();
        isi=isi.replaceAll("[^a-zA-Z -]", "");
        isi=isi.replaceAll("-", " ");
        isi=isi.trim().replaceAll(" +", " ");
        return isi;
    }
    
    public String[] getTokens(String isi){
        String[] tokens=isi.split(" ");
        return tokens;
    }
    
    public ArrayList<String> getFilter(String[] tokens){
        ArrayList<String> hasil=new ArrayList<String>();
        for(String token:tokens){
            int a=0;
            for(String stop:this.stopWords){
                if(stop.equals(token)){
                    a++;
                    break;
                }
            }
            if(a<1) hasil.add(token);
        }
        return hasil;
    }
    
    public String getStemming(String token){
        String hasil=token;
        try {
            if(!this.stemmer.getRootWord(token).equals("null")) hasil=this.stemmer.getRootWord(token);
        }
        catch(Exception e) {
            //System.out.println(e.toString());
        }
        return hasil;
    }
    
    public String delPrefix(String token){
        String aw=token.substring(0, 6);
        if(aw.equals("memper")) token=token.substring(6);
        else{
            aw=token.substring(0, 3);
            if(aw.equals("ber")) token=token.substring(3);
            else{
                aw=token.substring(0, 4);
                if(aw.equals("bela")) token=token.substring(4);
                else{
                    aw=token.substring(0, 2);
                    if(aw.equals("di")) token=token.substring(2);
                    else{
                        aw=token.substring(0, 2);
                        if(aw.equals("ke")) token=token.substring(2);
                        else{
                            aw=token.substring(0, 2);
                            if(aw.equals("ku")) token=token.substring(2);
                            else{
                                aw=token.substring(0, 3);
                                if(aw.equals("kau")) token=token.substring(3);
                                else{
                                    aw=token.substring(0, 2);
                                    if(aw.equals("me")){
                                        aw=token.substring(0, 4);
                                        if(aw.equals("memb")) token=token.substring(3);
                                        else{
                                            aw=token.substring(0, 4);
                                            if(aw.equals("mend") || aw.equals("menf") || aw.equals("menj")) token=token.substring(3);
                                            else{
                                                aw=token.substring(0, 4);
                                                if(aw.equals("meny")) token="s"+token.substring(4);
                                                else{
                                                    aw=token.substring(0, 4);
                                                    if(aw.equals("meng")){
                                                        if(token.substring(4, 5).equals("a") || token.substring(4, 5).equals("i") || token.substring(4, 5).equals("u") || token.substring(4, 5).equals("e") || token.substring(4, 5).equals("o") || token.substring(4, 5).equals("g") || token.substring(4, 5).equals("h")) token=token.substring(4);
                                                    }
                                                    else{
                                                        aw=token.substring(0, 3);
                                                        if(aw.equals("men")) token="t"+token.substring(3);
                                                        else{
                                                            if(token.substring(2, 3).equals("l") || token.substring(2, 3).equals("m") || token.substring(2, 3).equals("r")) token=token.substring(2);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else{
                                        aw=token.substring(0, 2);
                                        if(aw.equals("pe")) token=token.substring(2);
                                        else{
                                            aw=token.substring(0, 2);
                                            if(aw.equals("se")) token=token.substring(2);
                                            else{
                                                aw=token.substring(0, 3);
                                                if(aw.equals("ter")) token=token.substring(3);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return token;
    }
    
    public String delSuffix(String token){
        String ak=token.substring(token.length()-3);
        if(ak.equals("nda")) token=token.substring(0, token.length()-3);
        else{
            ak=token.substring(token.length()-3);
            if(ak.equals("nya")) token=token.substring(0, token.length()-3);
            else{
                ak=token.substring(token.length()-3);
                if(ak.equals("kan") && token.length()>5) token=token.substring(0, token.length()-3);
                else{
                    ak=token.substring(token.length()-2);
                    if(ak.equals("an") && token.length()>5) token=token.substring(0, token.length()-2);
                    else{
                        ak=token.substring(token.length()-2);
                        if(ak.equals("ku") && token.length()>6) token=token.substring(0, token.length()-2);
                        else{
                            ak=token.substring(token.length()-2);
                            if(ak.equals("mu") && token.length()>6) token=token.substring(0, token.length()-2);
                            else{
                                ak=token.substring(token.length()-1);
                                if(ak.equals("i") && token.length()>6) token=token.substring(0, token.length()-1);
                            }
                        }
                    }
                }
            }
        }
        return token;
    }
}
