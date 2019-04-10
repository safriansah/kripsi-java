/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stemmerindo;

import java.util.List;

/**
 *
 * @author adrianaden
 */
public class Prefix implements Affix{

    private final List dictionaries;

    public Prefix(List dictionaries) {
        this.dictionaries = dictionaries;
    }

    @Override
    public String remove(String keyword) {
        //penghapusan kata depan
        return this.removeDerivation(keyword);
    }

    public String removeDerivation(String keyword) {
        if (!this.dictionaries.contains(keyword)) {
            if (keyword.startsWith("di")) {
                keyword = keyword.substring(2);
            } else if (keyword.startsWith("ke")) {
                keyword = keyword.substring(2);
            } else if (keyword.startsWith("se")) {
                keyword = keyword.substring(2);
            }
        }
        if (!this.dictionaries.contains(keyword)) {
            /*
             * Pengahapuskan kata awalan me-, te-, be-
             */
            if (keyword.startsWith("me")) {
                if (keyword.startsWith("meng")) {
                    if (dictionaries.contains("k" + keyword.substring(4))) {
                        keyword = "k" + keyword.substring(4);
                    } else if (keyword.substring(4, 5).matches("[gh]")) {
                        keyword = keyword.substring(4);
                    }
                } else if (keyword.startsWith("meny")) {
                    if (dictionaries.contains("s" + keyword.substring(4))) {
                        keyword = "k" + keyword.substring(4);
                    }
                } else if (keyword.startsWith("mem")) {
                    if (keyword.substring(3, 4).matches("[bpf]")) {
                        keyword = keyword.substring(3);
                    }
                } else if (keyword.startsWith("men")) {
                    if (keyword.substring(3, 4).matches("[cdj]")) {
                        keyword = keyword.substring(3);
                    } else if (dictionaries.contains("t" + keyword.substring(3))) {
                        keyword = "k" + keyword.substring(3);
                    }
                } else {
                    keyword = keyword.substring(2);
                }

            } else if (keyword.startsWith("te")) {
                if (keyword.startsWith("ter")) {
                    if (dictionaries.contains("r" + keyword.substring(3))) {
                        keyword = "r" + keyword.substring(3);
                    } else {
                        keyword = keyword.substring(3);
                    }
                }
            } else if (keyword.startsWith("be")) {
                if (keyword.substring(3, 5).matches("er")) {
                    keyword = keyword.substring(2);
                } else if (dictionaries.contains(keyword.substring(2))) {
                    keyword = keyword.substring(2);
                } else {
                    keyword = keyword.substring(3);
                }
            }
        }
        /*
         * Pengahapuskan kata awalan pe-
         */
        if (!this.dictionaries.contains(keyword)) {
            if (keyword.startsWith("pe")) {
                if (dictionaries.contains(keyword.substring(2))) {
                    keyword = keyword.substring(2);
                } else if (keyword.startsWith("per")) {
                    keyword = keyword.substring(3);
                } else if (keyword.startsWith("pem")) {
                    if (keyword.substring(3, 4).matches("[bfv]")) {
                        keyword = keyword.substring(3);
                    } else if (dictionaries.contains("p" + keyword.substring(3))) {
                        keyword = "p" + keyword.substring(3);
                    }
                } else if (keyword.startsWith("peny")) {
                    if (dictionaries.contains("s" + keyword.substring(4))) {
                        keyword = "s" + keyword.substring(4);
                    }
                } else if (keyword.startsWith("pen")) {
                    if (dictionaries.contains("t" + keyword.substring(3))) {
                        keyword = "t" + keyword.substring(3);
                    } else if (keyword.substring(3, 4).matches("[jdcz]")) {
                        keyword = keyword.substring(3);
                    }
                }

            }
        }
        return keyword;
    }
}
