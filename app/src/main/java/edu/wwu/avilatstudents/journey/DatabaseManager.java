package edu.wwu.avilatstudents.journey;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by avila_000 on 3/2/2017.
 */

public class DatabaseManager {
    Context context;
    private StringBuilder dbResponse;


    public DatabaseManager(Context context){
        this.dbResponse = new StringBuilder();
        this.context = context;
    }


    public String login(String url, String email, String password){
        JSONObject jsonObjectUser = null;
        JSONObject jsonObjectInfo = null;

        try{
            jsonObjectInfo = new JSONObject();
            jsonObjectInfo.put("email", email);
            jsonObjectInfo.put("password", password);

            jsonObjectUser = new JSONObject();
            jsonObjectUser.put("user", jsonObjectInfo);
        }catch (JSONException e){
            Log.e("database", "Error creating JSONObject: " + e);
        }

        new DownloadData().execute(url, jsonObjectUser.toString(), "login");
        String dbResponseToReturn = dbResponse.toString();
        dbResponse.delete(0, dbResponse.length());
        return dbResponseToReturn;
    }

    public String signUp(String url, String username, String email, String password, String passwordConfirmation){
        JSONObject jsonObjectUser = null;
        JSONObject jsonObjectInfo = null;

        try{
            jsonObjectInfo = new JSONObject();
            jsonObjectInfo.put("username", username);
            jsonObjectInfo.put("email", email);
            jsonObjectInfo.put("password", password);
            jsonObjectInfo.put("password_confirmation", passwordConfirmation);

            jsonObjectUser = new JSONObject();
            jsonObjectUser.put("user", jsonObjectInfo);
        }catch (JSONException e){
            Log.e("database", "Error creating JSONObject: " + e);
        }

        new DownloadData().execute(url, jsonObjectUser.toString(), "signUp");
        String dbResponseToReturn = dbResponse.toString();
        dbResponse.delete(0, dbResponse.length());
        return dbResponseToReturn;
    }

    private void updateSession(String outputData, String inputData){
        SessionManager sessionManager = new SessionManager(context);
        JSONObject input = null;
        JSONObject output = null;
        try {
            output = new JSONObject(outputData).getJSONObject("user");
            input = new JSONObject(inputData).getJSONObject("data");
            Log.d("database", "input: " + input.toString());
            sessionManager.saveUsername((output.has("username") ? output.getString("username") : input.getString("username")));
            Log.d("database", "Username: " + sessionManager.getUsername());
            sessionManager.saveAuthentication(input.getString("authentication_token"));
            Log.d("database", "Authentication: " + sessionManager.getAuthentication());
            sessionManager.saveEmail(output.getString("email"));
            Log.d("database", "Email: " + sessionManager.getEmail());
        }catch(JSONException e){
            Log.e("database", "Error converting server output to JSON: " + e);
        }
    }

    public void updateSteps(String url, String email, String steps, String authentication){
        JSONObject jsonObjectInfo = null;

        try{
            jsonObjectInfo = new JSONObject();
            jsonObjectInfo.put("steps", steps);
        }catch (JSONException e){
            Log.e("database", "Error creating JSONObject: " + e);
        }

        Log.d("database", "JSON: " + jsonObjectInfo.toString() + "\tEmail: " + email + "\tSteps: " + steps + "\tAuth: " + authentication);
        new DownloadData().execute(url, jsonObjectInfo.toString(), "updateSteps", email, authentication);
    }

    //PARAMETERS: (url, output, method, email, authentication)
    private class DownloadData extends AsyncTask<String, Void, String> {
        HttpURLConnection connection = null;
        String inputData = "";

        @Override
        protected String doInBackground(String... strings) {

            try{
                URL url = new URL(strings[0]);
                String outputData = strings[1];
                String method = strings[2];
                connection = (HttpURLConnection) url.openConnection();
                connection.setInstanceFollowRedirects(true);
                connection.setDoOutput(true);

                connection.setRequestProperty("Content-type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("User-Agent", "curl/7.47.0");
                if(!(method.equals("signUp")) && !(method.equals("login"))){
                    String email = strings[3];
                    String authentication = strings[4];
                    connection.setRequestProperty("X-User-Email", email);
                    connection.setRequestProperty("X-User-Token", authentication);
                }
                connection.connect();

                sendOutput(outputData);
                receiveInput();

                if((method.equals("signUp")) || (method.equals("login"))) updateSession(outputData, inputData);



            }catch(Exception e){
                Log.e("database", "Connection fail: " + e);
            }finally{
                if(connection != null) connection.disconnect();
                Log.d("database", "Connection disconnected");
            }
            return inputData;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("database", "Input: " + s);
            SessionManager sessionManager = new SessionManager(context);
            Toast.makeText(context, "Hello " + sessionManager.getUsername() + "!", Toast.LENGTH_LONG).show();
            Toast.makeText(context, "Your email is " + sessionManager.getEmail() + "!", Toast.LENGTH_LONG).show();
            Toast.makeText(context, "Authentication is " + sessionManager.getAuthentication() + "!", Toast.LENGTH_LONG).show();
        }


        private void sendOutput(String message) {
            try {
                Writer output = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                output.write(message);
                Log.d("database", "Output: " + message);
                output.close();
            }catch (IOException e){
                Log.e("database", "Output fail: " + e);
            }
            Log.d("database", "Output success");
        }


        private void receiveInput(){
            InputStream input = null;

            try {
                input = connection.getInputStream();
            }catch(Exception e){
                Log.e("database", "Input fail " + e);
            }

            InputStreamReader inputStreamReader = new InputStreamReader(input);
            int inputStreamData;

            try {
                while ((inputStreamData = inputStreamReader.read()) != -1) {
                    inputData += (char) inputStreamData;
/*                    Log.d("database", "Still reading from input stream");
                    Log.d("database", "inputStreamData: " + inputStreamData);*/
                }
            }catch(IOException e){
                Log.e("database", "Input fail: " + e);
            }
            Log.d("database", "Input success");
        }
    }
}
