package main.java;

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

        byte[] hash = in_modified.getBytes();
        StringBuffer hashString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) hashString.append('0');
            hashString.append(hex);
        }
        return hashString.toString();
    }

    /**
     * Check if clear text password matches hash value
     *
     * @param in cleartext password
     * @param hash of password (stored in DB)
     * @return true on match; false if not
     */
    public static boolean compare(String in, String hash) {
        return generateHash(in).equals(hash);
    }
}
