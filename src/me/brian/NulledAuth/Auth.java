package me.brian.NulledAuth;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.lang.System.exit;
import java.net.HttpURLConnection;
import java.net.URL;

public class Auth {

    public static String[] checkAuth(String key) throws Exception {
        String[] user = new String[2];

        // Auth key to MD5
        key = Md5.getMd5(key);
        String authurl = "https://www.nulled.to/misc.php?action=validateKey&authKey=" + key;

        URL url = new URL(authurl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestProperty("accept", "application/json");
        connection.setRequestMethod("GET");
        // request
        if (connection.getResponseCode() == 200) {
            // Everything good, do whatever you want here
        } else {
            System.out.println("Error establishing connection with the nulled servers!\nPlease try again or check the status at https://www.nulled.to");
            System.in.read();
            exit(0);
        }
        
        InputStream responseStream = connection.getInputStream();
        //

        String response = inputStreamToString(responseStream);
        Gson gson = new Gson();
        Json values = gson.fromJson(response, Json.class);

        if (values.getUsername() == null) {
            return null;
        }

        user[0] = values.getUid();
        user[1] = values.getUsername();
        return user;
    }

    private static String inputStreamToString(InputStream is) throws IOException { // this is not my code but it works just fine :)
        String s = "";
        String line = "";

        // Wrap a BufferedReader around the InputStream
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        // Read response until the end
        while ((line = rd.readLine()) != null) {
            s += line;
        }

        // Return full string
        return s;
    }
}
