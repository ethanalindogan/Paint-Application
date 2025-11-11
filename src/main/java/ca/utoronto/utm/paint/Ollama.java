package ca.utoronto.utm.paint;

import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

// https://www.geeksforgeeks.org/how-to-use-httpurlconnection-for-sending-http-post-requests-in-java/
// https://www.baeldung.com/java-org-json
// http://www.JSON.org/
// https://developer.android.com/reference/org/json/package-summary
// https://www.llama.com/docs/model-cards-and-prompt-formats/meta-llama-3/

public abstract class Ollama {
    private String host = "dh2010pcXX.utm.utoronto.ca";
    private String url = "http://dh2010pc03.utm.utoronto.ca:11434/api/generate";
    public Ollama(String host){
        this.host=host;
        this.url = "http://"+this.host+":11434/api/generate";
    }

    public String call(String system, String prompt) {
        /**
         curl http://localhost:11434/api/generate -d '{
         "model": "llama3",
         "prompt": "Answer the question: Give 3 examples of things that are red? Provide only the list of 3 items. Your answer should begin with ANSWER_START and end with ANSWER_END",
         "stream": false
         }'
         **/

        JSONObject data = new JSONObject();
        data.put("model","llama3");
        data.put("stream",false);

        JSONObject options = new JSONObject();
        options.put("num_ctx",8192);

        data.put("options",options);
        data.put("system", system);
        data.put("prompt", prompt);

        String data_to_send = data.toString();
        System.out.println(data_to_send);

        String sResponse="";

        HttpURLConnection connection=null;
        try {
            URL obj = new URI(url).toURL(); // Making an object to point to the API URL

            // attempts to establish a connection to the URL represented by the obj.
            connection = (HttpURLConnection)obj.openConnection();

            // Set request method and enable writing to the connection
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Set content type header,
            // input (Content-Type) is in JSON (application/json) format.
            connection.setRequestProperty("Content-Type", "application/json");

            // Calling the API and send request data
            // connection.getOutputStream() purpose is to obtain an output stream for sending data to the server.
            try (DataOutputStream os = new DataOutputStream(connection.getOutputStream())) {
                os.writeBytes(data_to_send);
                os.flush();
            }

            // Get response code and handle response
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // HTTP_OK or 200 response code generally means that the server ran successfully without any errors
                StringBuilder response = new StringBuilder();

                // Read response content
                // connection.getInputStream() purpose is to obtain an input stream for reading the server's response.
                try (
                        BufferedReader reader = new BufferedReader( new InputStreamReader( connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line); // Adds every line to response till the end of file.
                    }
                }
                JSONObject jo = new JSONObject(response.toString());
                sResponse=(String)jo.get("response");
            } else {
                System.out.println("Error: HTTP Response code - " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(connection!=null)connection.disconnect();
        }
        return sResponse;
    }

    /**
     * Ask llama3 to generate a new File based on a modification of inFileName and the prompt
     * @param prompt the user supplied prompt
     * @param inFileName the File Format file to be read and modified to outFileName
     * @param outFileName the file name where the response will live
     */
    public abstract void modifyFile(String prompt, String inFileName, String outFileName);

    /**
     * Ask llama3 to generate a new File based on the given prompt
     * @param prompt
     * @param outFileName
     */
    public abstract void newFile(String prompt, String outFileName);

    /**
     * The following 3 use Ollama to create the contents of a very interesting outFileName.
     * File outFileName will be created IN THE USERS HOME DIRECTORY
     * You don't have to use newFile(prompt, outFileName).
     * @param outFileName
     */

    public abstract void newFile1(String outFileName);
    public abstract void newFile2(String outFileName);
    public abstract void newFile3(String outFileName);

    /**
     * The following 3 use Ollama to create the contents of outFileName as a very interesting modification of inFileName.
     * Files inFileName and outFileName are IN THE USERS HOME DIRECTORY!!
     * You do not have to use modifyFile(prompt, inFileName, outFileName)
     */
    public abstract void modifyFile1(String inFileName, String outFileName);
    public abstract void modifyFile2(String inFileName, String outFileName);
    public abstract void modifyFile3(String inFileName, String outFileName);
}

