package webservice.apache_httpClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class UpdateNodeWithImageField {


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



			JSONObject nodeJSONObject = new JSONObject();
//			nodeJSONObject.put("type", "blog");

			nodeJSONObject.put("nid", "16");
			nodeJSONObject.put("title", "update blog node via HttpClient 2:");
			
			
			
			JSONObject fileJSONObject= new JSONObject();
			fileJSONObject.put("fid", "28");
//			fileJSONObject.put("filename", "public://w1.jpg");
//			fileJSONObject.put("uri", "public://blog_images/w1.jpg");
//			fileJSONObject.put("filemime", "image/jpeg");
//			fileJSONObject.put("filesize", "1351882412");
//			fileJSONObject.put("status", "1");
			
			JSONArray jsonArray = new JSONArray();
			jsonArray.put(fileJSONObject);
			
			JSONObject undJSONObject= new JSONObject();
			undJSONObject.put("und", jsonArray);
			
			
			nodeJSONObject.put("field_blog_image",undJSONObject);
			
			
			
			
			StringEntity se = new StringEntity(nodeJSONObject.toString());
			se.setContentType("application/json");

			HttpPut httpPut = new HttpPut("http://localhost/d7r/pa/node/16");
			
			httpPut.setHeader("Content-type", "application/json");
			//httppost.setHeader("Cookie", cookie);
			httpPut.setEntity(se);
			HttpResponse response2 = httpClient.execute(httpPut);


			if (response2.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response2.getStatusLine().getStatusCode());
			}
			String response2Str = EntityUtils.toString(response2.getEntity());
			// save the sessid and session_name
			JSONObject objNodeCreated = new JSONObject(response2Str);

			System.out.println("Output from Server .... \n");
			System.out.println(objNodeCreated.toString());




			httpClient.getConnectionManager().shutdown();


		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}


