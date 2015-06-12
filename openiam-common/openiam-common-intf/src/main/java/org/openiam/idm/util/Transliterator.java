package org.openiam.idm.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Transliterate unicode characters to ASCII.
 * Supported symbols from ranges Latin, Latin extended A & B (up to 0x021B), Cyrillic
 */
public class Transliterator {

    private final static String UNICODE = "\u00c6\u00d0\u00d7\u00d8\u00de\u00df\u00e6\u00f0\u00f7\u00f8\u00fe\u0110\u0111\u0126\u0127\u0131\u0132\u0133\u0138\u013f\u0140\u0141\u0142\u0149\u014a\u014b\u0152\u0153\u0166\u0167\u017f\u0180\u0181\u0182\u0183\u0184\u0185\u0186\u0187\u0188\u0189\u018a\u018b\u018c\u018d\u018e\u018f\u0190\u0191\u0192\u0193\u0194\u0195\u0196\u0197\u0198\u0199\u019a\u019b\u019c\u019d\u019e\u019f\u01a2\u01a3\u01a4\u01a5\u01a6\u01a7\u01a8\u01a9\u01aa\u01ab\u01ac\u01ad\u01ae\u01b1\u01b2\u01b3\u01b4\u01b5\u01b6\u01b7\u01b8\u01b9\u01ba\u01bb\u01bc\u01bd\u01be\u01bf\u01c0\u01c1\u01c2\u01c3\u01c4\u01c5\u01c6\u01c7\u01c8\u01c9\u01ca\u01cb\u01cc\u01dd\u00c6\u00e6\u01e4\u01e5\u01b7\u0292\u01f1\u01f2\u01f3\u01f6\u01f7\u00c6\u00e6\u00d8\u00f8\u0410\u0411\u0412\u0413\u0414\u0415\u0401\u0416\u0417\u0418\u0419\u041a\u041b\u041c\u041d\u041e\u041f\u0420\u0421\u0422\u0423\u0424\u0425\u0426\u0427\u0428\u0429\u042c\u042a\u042d\u042e\u042f\u0430\u0431\u0432\u0433\u0434\u0435\u0451\u0436\u0437\u0438\u0439\u043a\u043b\u043c\u043d\u043e\u043f\u0440\u0441\u0442\u0443\u0444\u0445\u0446\u0447\u0448\u0449\u044a\u044c\u044a\u044d\u044e\u044f";
    private final static String PLAIN_ASCII = "AeD   O Thssaed   o thD d H h i Ijijk L l L l n N n OeoeT t s b B B b     O C c D D D d z E A E F f G G hvI I K k l L M N n O OioiP p Yr    S s t T t T U V Y y Z z Z Z z z         w         DzDzdzLjLjljNjNjnja AeaeG g Z z DzDzdzHvW AeaeO o A B V G D E E ZhZ I J K L M N O P R S T U F H TzChShSc    E JuJaa b v g d e e zhz i j k l m n o p r s t u f h tzchshscy     e juja";

    public static String transliterate(String unicodeString) {

        if (unicodeString == null) return null;

        String nfdNormalizedString = Normalizer.normalize(unicodeString, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String preprocessedString = pattern.matcher(nfdNormalizedString).replaceAll("");

        StringBuilder sb = new StringBuilder(preprocessedString.length());
        for (char c : preprocessedString.toCharArray()) {
            int pos = UNICODE.indexOf(c);
            if (pos > -1) {
                char ch = PLAIN_ASCII.charAt(pos*2);
                if (ch != 0x0020) {
                    sb.append(ch);
                    ch = PLAIN_ASCII.charAt(pos*2+1);
                    if (ch != 0x0020) {
                        sb.append(ch);
                    }
                }
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public static String transliterate(String unicodeString, boolean removeNonAscii) {

        String transliteratedString = transliterate(unicodeString);
        return !removeNonAscii ? transliteratedString : transliteratedString.replaceAll("[^\\x00-\\x7F]", "");

    }
}
