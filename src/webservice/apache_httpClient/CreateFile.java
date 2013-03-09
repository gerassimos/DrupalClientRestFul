package webservice.apache_httpClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;



public class CreateFile {


	public static void main(String[] args) {
		try {

			HttpParams mHttpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(mHttpParams, 10000);
			HttpConnectionParams.setSoTimeout(mHttpParams, 10000);
			DefaultHttpClient httpClient = new DefaultHttpClient();
			httpClient.setParams(mHttpParams);

			HttpPost postLoginRequest = new HttpPost("http://localhost/d7r/pa/user/login");
			JSONObject jsonObjUser = new JSONObject();  
			jsonObjUser.put("password", "admin");
			jsonObjUser.put("username", "admin");   
			StringEntity input = new StringEntity(jsonObjUser.toString());
			input.setContentType("application/json");
			postLoginRequest.setEntity(input);
			postLoginRequest.setHeader("Accept", "application/json");
			postLoginRequest.setHeader("Content-type", "application/json");
			HttpResponse response = httpClient.execute(postLoginRequest);
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}
			String mResponse = EntityUtils.toString(response.getEntity());
			// save the sessid and session_name
			JSONObject obj = new JSONObject(mResponse);
			String cookie =obj.getString("session_name") + "="+ obj.getString("sessid");
			System.out.println(cookie);

			String filePath = "/home/gerassimos/Pictures/examples/w2.jpg";
			File imageFile =new File(filePath);

			byte[] binaryData = new byte[(int) imageFile.length()];
			try {
				FileInputStream fileInputStream = new FileInputStream(imageFile);
				fileInputStream.read(binaryData);}
			catch (FileNotFoundException e) {
				System.out.println("File Not Found.");
				e.printStackTrace();
			}
			catch (IOException e1)
			{
				System.out.println("Error Reading The File.");
				e1.printStackTrace();
			}
			String base64String = Base64.encodeToString(binaryData, false);

			JSONObject fileJSONObject = new JSONObject();
			fileJSONObject.put("file", base64String);
			fileJSONObject.put("filename", imageFile.getName());
			fileJSONObject.put("filesize", ""+binaryData.length);
			//public://blog_images/computer.jpeg
			fileJSONObject.put("filepath", "public://blog_images/"+imageFile.getName());
			fileJSONObject.put("uid", "1");
			long timestamp = System.currentTimeMillis();
			System.out.println("file timestamp:" +timestamp);
			fileJSONObject.put("timestamp", ""+timestamp);


			//			fileJSONObject.put("uri_full", "http://localhost/d7r/sites/default/files/blog_images/"+imageFile.getName());



			StringEntity seFile = new StringEntity(fileJSONObject.toString());
			//se.setContentType("application/json");

			HttpPost httppost = new HttpPost("http://localhost/d7r/pa/file");
			httppost.setHeader("Content-type", "application/json");
			//httppost.setHeader("Cookie", cookie);
			
			
			
//			httppost.setEntity(seFile);
//			HttpResponse response2 = httpClient.execute(httppost);
//
//
//			if (response2.getStatusLine().getStatusCode() != 200) {
//				throw new RuntimeException("Failed : HTTP error code : "
//						+ response2.getStatusLine().getStatusCode());
//			}
//			String response2Str = EntityUtils.toString(response2.getEntity());
//			// save the sessid and session_name
//			JSONObject objNodeCreated = new JSONObject(response2Str);
//			System.out.println("Output from Server .... \n");
//			System.out.println(objNodeCreated.toString());

			HttpGet requestGenImageStyle = new HttpGet("http://localhost/d7r/sites/default/files/styles/full_blog_image/public/blog_images/"+imageFile.getName());

			HttpResponse response3 = httpClient.execute(requestGenImageStyle);
			if (response3.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}


			httpClient.getConnectionManager().shutdown();


		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}


