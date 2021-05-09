package me.brian.NulledAuth;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.lang.System.exit;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Auth {
    private String user;
    private String uid;
    
    public Auth(String key) {
        try {
            String[] user = this.checkAuth(key);
            if (user == null) {
                return; // Auth invalid
            }
            this.uid = user[0];
            this.user = user[1];
        } catch (Exception e) {
            // This should never happen
            e.printStackTrace();
            exit(1);
        }
    }
    
    public String getUser() {
        return user;
    }
    
    public String getUid() {
        return uid;
    }

    private static String[] checkAuth(String key) throws Exception {
        String[] user = new String[2];

        // Auth key to MD5
        key = getMd5(key);
        String authurl = "https://www.nulled.to/misc.php?action=validateKey&authKey=" + key;

        URL url = new URL(authurl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestProperty("accept", "application/json");
        connection.setRequestMethod("GET");
        
        if (connection.getResponseCode() != 200) {
            System.out.println("Error establishing connection with the nulled servers!\nPlease try again or check the status at https://www.nulled.to");
            System.in.read();
            exit(0);
        }
        
        InputStream responseStream = connection.getInputStream();

        String response = inputStreamToString(responseStream);
        Gson gson = new Gson();
        Json values = gson.fromJson(response, Json.class);

        if (values.getUsername() == null || Boolean.parseBoolean(values.getAuth()) == false) {
            return null;
        }

        user[0] = values.getUid();
        user[1] = values.getUsername();
        return user;
    }

    private static String inputStreamToString(InputStream is) throws IOException { // this is not my code but it works just fine :)
        String s = "";
        String line = "";

        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        while ((line = rd.readLine()) != null) {
            s += line;
        }

        return s;
    }
    
    // Method moved here from Md5 class
    public static String getMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
}
