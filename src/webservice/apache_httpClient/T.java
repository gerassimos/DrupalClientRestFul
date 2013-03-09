package webservice.apache_httpClient;


 
import org.apache.http.protocol.HTTP;
import org.apache.http.entity.StringEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
 
import org.json.JSONObject;
import org.json.JSONException;
 
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
 
/*
 * This base class handles session-based authentication for the "JSON Server" Drupal module.
 * Extend this class, change to your BASE_URL and add requests for a particular REST data model implementation.
 */
public class T {
 
    protected static String TAG = "JSONServerNetworkUtil";
 
    public static final String PARAM_SESSION_ID = "sessid";
    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_PASSWORD = "password";
 
    public static final int REGISTRATION_TIMEOUT = 30 * 1000; // ms
     
    public static final String BASE_URL = "http://localhost/d7r/pa";
 
    public static final String CONNECT_URI = BASE_URL + "/system/connect.json";
    public static final String AUTH_URI = BASE_URL + "/user/login.json";
 
 
    protected static HttpClient mHttpClient;
    protected static String mSessId;
 
 
    /**
     * Configures the httpClient to connect to the URL provided.
     */
    public static void maybeCreateHttpClient() {
        if (mHttpClient == null) {
            mHttpClient = new DefaultHttpClient();
            final HttpParams params = mHttpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params,
                REGISTRATION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, REGISTRATION_TIMEOUT);
            ConnManagerParams.setTimeout(params, REGISTRATION_TIMEOUT);
        }
    }
 
    /**
     * Executes the network requests on a separate thread.
     * 
     * @param runnable The runnable instance containing network mOperations to
     *        be executed.
     */
    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {
 
                }
            }
        };
        t.start();
        return t;
    }
     
     
 
    /**
     * Connects to the JSON server, authenticates the provided username and
     * password.
     * 
     * @param username The user's username
     * @param password The user's password
     * @param handler The hander instance from the calling UI thread.
     * @param context The context of the calling Activity.
     * 
     * @return boolean The boolean result indicating whether the user was
     *         successfully authenticated.
     */
    public static boolean authenticate(String username, String password) {
         
        mSessId = connectForSessId();
        System.out.println( "sessid = "+mSessId);
         
        if (mSessId == null) {
            return false;
        }
         
        Map params = new HashMap();
        params.put(PARAM_SESSION_ID, mSessId);
        params.put(PARAM_USERNAME, username);
        params.put(PARAM_PASSWORD, password);
 
        JSONObject json = prepareAndSendHttpPost(AUTH_URI, params);
         
        if (json == null) {
            System.out.println( "auth failed");
            return false;
        }
        else {
        	System.out.println( "auth successful "+json.toString());
            return true;
        }
 
    }
     
    /*
     * This handles the initial POST to JSON Server to retrieve the sessid.
     */
    protected static String connectForSessId() {
        System.out.println( "connectForSessId()");
         
        JSONObject json = prepareAndSendHttpPost(CONNECT_URI, null);
        if (json != null) {
            try {
                return json.getString("sessid");
            }
            catch (JSONException e) {
            	System.out.println( "could not find sessid from system.connect");
                return null;
            }
        }
        else {
            return null;
        }
    }
     
     
 
    /*
     * Does all the work for JSON POST requests.
     * 
     * @param URI The URI for the post
     * @param params The variables to be converted to JSON and POSTed with this request.
     */
    protected static JSONObject prepareAndSendHttpPost(String URI, Map params) {
        JSONObject json = null;
        try {
            json = new JSONObject();
            if (params != null) {
                Iterator it = params.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    json.put(pair.getKey().toString(), pair.getValue());
                }
            }
        }
        catch (JSONException e) {
            System.out.println( "unable to encode JSON for http request");
        }
     
        StringEntity se = null;
        try {
            se = new StringEntity(json.toString());    
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));  
        }
        catch (Exception e) {
        	System.out.println( "unable to create string entity");
        }
 
        final HttpPost post = new HttpPost(URI);
        post.setEntity(se);  
     
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-type", "application/json");
         
        maybeCreateHttpClient();
     
        HttpResponse resp = null;
        try {
             
            resp = mHttpClient.execute(post);
             
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                System.out.println( "Successful POST");
                return decodeJSONResponse(resp);
            } else {
                
            	System.out.println( "Error POSTing: " + resp.getStatusLine());
                
                return null;
            }
        } catch (final IOException e) {
            
        	System.out.println("IOException when POSTING");
        	e.printStackTrace();
            
            return null;
        } finally {
            
        	System.out.println( "POST completing");
            
        }
    }
 
     
    /*
     * Process the raw HttpResponse into a JSON object.
     */
    protected static JSONObject decodeJSONResponse(HttpResponse resp) {
 
        InputStream is = null;
        try {
            is = resp.getEntity().getContent();
        }
        catch (IOException e) {
        	System.out.println( "unable to get content from response entity");
            e.printStackTrace();
            return null;
        }
 
        String in = convertStreamToString(is);
 
        JSONObject json = null;
        try {
            json = new JSONObject(in);
        }
        catch (JSONException e) {
        	System.out.println( "could not decode JSON response from: "+in);
        }
         
        return json;
    }
     
     
    /*
     * Server responses are in stream format. This delivers the data as a String for easy parsing.
     */
    protected static String convertStreamToString(InputStream is) {
 
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    
    public static void main (String [] args){
    	authenticate("admin", "admin");
    }
}