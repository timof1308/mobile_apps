package de.vms.vmsapp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;

import de.vms.vmsapp.Models.User;

public class JwtController {
    /**
     * Parse JWT Token and return array of split token
     *
     * @param token String
     * @return String[]
     */
    public static String[] parseJwt(String token) {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        // split out the "parts" (header, payload and signature)
        String[] parts = token.split("\\.");

        // get parts and put to array
        String[] decoded = new String[3];
        decoded[0] = new String(decoder.decode(parts[0]));
        decoded[1] = new String(decoder.decode(parts[1]));
        decoded[2] = new String(decoder.decode(parts[2]));

        return decoded;
    }

    /**
     * Get header from JWT String
     *
     * @param token String
     * @return JSONObject
     */
    public static JSONObject getHeaderJwt(String token) throws JSONException {
        String[] parts = parseJwt(token);
        // return 2nd position in array => payload
        return new JSONObject(parts[0]);
    }

    /**
     * Get payload from JWT String
     *
     * @param token String
     * @return JSONObject
     */
    public static JSONObject getPayloadJwt(String token) throws JSONException {
        String[] parts = parseJwt(token);
        // return 2nd position in array => payload
        return new JSONObject(parts[1]);
    }

    /**
     * Get signature from JWT String
     *
     * @param token String
     * @return JSONObject
     */
    public static JSONObject getSignatureJwt(String token) {
        String[] parts = parseJwt(token);
        // return 2nd position in array => payload
        try {
            return new JSONObject(parts[2]);
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Decode JWT String and parse it to User model
     *
     * @param token String to decode
     * @return User
     */
    public static User decodeJwt(String token) throws JSONException {
        // get payload from jwt token as json
        JSONObject json = JwtController.getPayloadJwt(token);

        // create new user
        User user = new User();
        user.setId(json.getInt("id"));
        user.setName(json.getString("name"));
        user.setEmail(json.getString("email"));
        user.setPassword(json.getString("password"));
        if (json.has("token")) {
            user.setToken(json.getString("token"));
        }
        user.setRole(json.getInt("role"));

        return user;
    }
}
