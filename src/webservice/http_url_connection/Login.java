package webservice.http_url_connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class Login {


	public static void main(String[] args) {
		try {

			URL url = new URL("http://localhost/d7r/pa/user/login");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("accept", "application/json");
			

			JSONObject jsonObjUser = new JSONObject();  
			jsonObjUser.put("password", "admin");
			jsonObjUser.put("username", "admin");

			String input = jsonObjUser.toString();
			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			//			input.setContentType("application/json");
			//			postRequest.setEntity(input);

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			String output = convertStreamToString(conn.getInputStream());
			JSONObject jsonObject = new JSONObject(output); 
			System.out.println("Output from Server .... \n");
			System.out.println(jsonObject.toString());



		} catch (Exception e) {

			e.printStackTrace();
		}

	}


	private static String convertStreamToString(InputStream is) {

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
		return sb.toString();}

}


