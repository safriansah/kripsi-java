/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stemmerindo;

import stemmerindo.dictionary.Dictionary;
import java.util.List;

/**
 *
 * @author adrianaden
 */
public class Stemmer {
    
    private final List dictionaries;
    private String rootWord;
    @Deprecated
    public Stemmer() throws Exception {
        this.dictionaries = new Dictionary().getDictionaryData();
    }
    
    public Stemmer(List dictionaries) {
        this.dictionaries = dictionaries;
    }
    
    /**    
     * @param keyword kata yang ingin dicari akar kata-nya    
     * @return akar kata
     */
    public String getRootWord(String keyword) {
        return this.getRootWord(keyword, null);
    }
    
    /**
     * @param keyword kata yang ingin dicari akar kata-nya
     * @param defaultWord balikan kata jika tidak ditemukan 
     * @return akar kata atau default word yang sudah ditentukan
     */
    public String getRootWord(String keyword, String defaultWord) {        
        if (this.hasRootWord(keyword)) {
            return this.rootWord;
        } else {
            return defaultWord;
        }        
    }
    
    public boolean hasRootWord(String keyword){
        return dictionaries.contains(fetch(keyword));
    }
    
    private String fetch(String keyword){
        this.rootWord = keyword;        
        this.rootWord = new Suffix(dictionaries).remove(rootWord);
        this.rootWord = new Prefix(dictionaries).remove(rootWord);
        return rootWord;
    }
}
