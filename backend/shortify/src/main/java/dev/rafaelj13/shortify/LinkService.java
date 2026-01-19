package dev.rafaelj13.shortify;

import org.springframework.stereotype.Service;

@Service
public class LinkService {
    
    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = BASE62.length();
    
    public static String encodeBase62(int num) {
        if (num == 0) return "0";
        
        StringBuilder encoded = new StringBuilder();
        while (num > 0) {
            int remainder = (int) (num % BASE);
            encoded.insert(0, BASE62.charAt(remainder));
            num = num / BASE;
        }
        return encoded.toString();
    }
    
    public static int decodeBase62(String str) {
        int decoded = 0;
        for (int i = 0; i < str.length(); i++) {
            int digit = BASE62.indexOf(str.charAt(i));
            decoded = decoded * BASE + digit;
        }
        return decoded;
    }
}
