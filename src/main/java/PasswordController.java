package main.java;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * main.java.PasswordController Class
 * Handling password hashing and
 * comparing if password in cleartext matches hashed one
 */
public class PasswordController {

    /**
     * Create SHA256 hash value of cleartext
     * adding alt at the beginning of the string
     * adding salt at the end of the string to increase complexity and security
     *
     * @param in String to create hash value for
     * @return hashed string (of password)
     */
    public static String generateHash(String in) {
        String in_modified = "alt" + in + "salt";

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(in_modified.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Use javax.xml.bind.DatatypeConverter class in JDK to convert byte array
     * to a hexadecimal string. Note that this generates hexadecimal in upper case.
     *
     * @param hash bytes to convert into string
     * @return printable String
     */
    private static String bytesToHex(byte[] hash) {
        return DatatypeConverter.printHexBinary(hash);
    }

    /**
     * Check if clear text password matches hash value
     *
     * @param in   cleartext password
     * @param hash of password (stored in DB)
     * @return true on match; false if not
     */
    public static boolean compare(String in, String hash) {
        return generateHash(in).equals(hash);
    }
}
