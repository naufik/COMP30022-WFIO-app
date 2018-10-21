package com.navigation.wfio_dlyw.utility;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.koushikdutta.async.parser.JSONObjectParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/***
 * A class that specialize in reading and writing from the file system
 */
public class FileIO {

    /***
     * Store credentials (a json object) in form of string stored into internal storage
     * @param tokenValue value of the session token
     * @param email Email of the user
     * @param context activity context
     */
    public static void storeCredentials(String tokenValue, String email, Context context){
        JSONObject credentials = new JSONObject();

        try (FileWriter file = new FileWriter(context.getFilesDir().toString() +"/login.json")){
            credentials.put("token", tokenValue);
            credentials.put("email", email);
            file.write(credentials.toString());
            Log.d("FileIO","Successfully Copied JSON Object to " + context.getFilesDir().toString() +"/login.json");
            Log.d("FileIO","JSON Object: " + credentials);
        } catch (Exception e) {}
    }

    /***
     * Get a credentials from internal storage
     * @param context activity context
     * @return a JSONObject that contains 2 string: "token" a token value, and "email" an email
     */
    public static JSONObject getCredentials(Context context){
        JSONObjectParser parser = new JSONObjectParser();
        String directory = context.getFilesDir().toString() + "/login.json";
        if (!(new File(directory).exists())){
            Log.d("FileIO", "login credentials does not exist");
            return null;
        }
        try {
            JSONObject credentials = loadJSONFromAsset(context);
            return credentials;
        } catch (Exception e) {}
        return null;
    }

    /***
     * Read a login.json file from internal storage
     * @param context activity context
     * @return
     */
    public static JSONObject loadJSONFromAsset(Context context) {
        File myFile = new File(context.getFilesDir().toString() +"/login.json");
        String json = null;
        JSONObject obj;
        try {
            InputStream is = new FileInputStream(myFile);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            obj = new JSONObject(json);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        return obj;
    }

    /***
     * delete currently existing login.json file
     * @param context activity context
     * @return shows whether the file is deleted or not
     */
    public static boolean deleteCredentials(Context context){
        File myFile = new File(context.getFilesDir().toString() +"/login.json");
        return myFile.delete();
    }
}
