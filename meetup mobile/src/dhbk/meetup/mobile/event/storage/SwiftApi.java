package dhbk.meetup.mobile.event.storage;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParamBean;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dhbk.meetup.mobile.utils.ReadWriteFile;

public class SwiftApi {

	public static final String AUTH_ACCOUNT = "AUTH_bcd32eeab61a4b9287721ba11e8110";
	public static final String URL_SWIFT = "http://192.168.50.159:8080/v1/AUTH_bcd32eeab61a4b299287721ba11e8110/";
	public static final String URL_AUTHX = "http://192.168.50.159:5000/v2.0/tokens";
	public static final String CONTAINER_TEMPLATES = "templates";
	public static final String CONTAINER_DOCUMENTS = "documents";
	
	public HttpClient httpClient;
	public String auth_token = "";
	
	public SwiftApi () {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
		httpClient = new DefaultHttpClient(httpParams);
	}
	
	// lay token
	public boolean authentication () {
		String authx = "{\"auth\": {\"tenantName\": \"admin\", \"passwordCredentials\": {\"username\": \"admin\", \"password\": \"OpenStack123\"}}}";
		HttpResponse response  = null;
		try {
			HttpPost requestPost = new HttpPost(URL_AUTHX);
			requestPost.addHeader("Content-Type", "application/json");
			requestPost.setEntity(new StringEntity(authx, HTTP.UTF_8));
			response = httpClient.execute(requestPost);
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				JSONObject jso_parent = new JSONObject(EntityUtils.toString(response.getEntity()));
				JSONObject jso_access = jso_parent.getJSONObject("access");
				JSONObject jso_token = jso_access.getJSONObject("token");
				auth_token = jso_token.getString("id");
				return true;
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(response != null)
				try {
					response.getEntity().consumeContent();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return false;
	}
	
	public ArrayList<String[]> getListFile (String container) {
		ArrayList<String[]> listFile = new ArrayList<String[]>();
		
		if(auth_token.equals("")) {
			if(!authentication())
				return listFile;
		} 
		
		HttpResponse response  = null;
		try {
			String url = URL_SWIFT + container;
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("format", "json"));
			String paramsUrl = URLEncodedUtils.format(params, HTTP.UTF_8);
			url += "?" + paramsUrl;
			HttpGet requestGet = new HttpGet(url);
			requestGet.addHeader("X-Auth-Token", auth_token);
			response = httpClient.execute(requestGet);
			JSONArray jsa = new JSONArray(EntityUtils.toString(response.getEntity()));
			for(int i = 0; i < jsa.length(); i++) {
				JSONObject jso = jsa.getJSONObject(i);
				String names[] = jso.getString("name").split("/");
				if(names.length == 1)
					 listFile.add(new String[] {"", names[0]});
				else listFile.add(new String[] {names[0], names[1]});
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(response != null) {
				try {
					response.getEntity().consumeContent();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return listFile;
	}
	
	/* fullpath = path + name
	 * 
	 * path == null : setEntity(String)
	 * path != null : setEntity(File)
	 */
	public boolean uploadObject (String container, String id, String name, String path, String content) {
		if(auth_token.equals("")) {
			if(!authentication())
				return false;
		} 
		System.out.println("UPLOAD 1");
		HttpResponse response = null;
		try {
			String url = URL_SWIFT + container + "/" + id + "/" + name;
			HttpPut requestPut = new HttpPut(url);
			requestPut.addHeader("X-Auth-Token", auth_token);
//			requestPut.setEntity(new Strin)
			if(path == null)
				requestPut.setEntity(new StringEntity(content, HTTP.UTF_8));
			else
				requestPut.setEntity(new FileEntity(new File(path + name), HTTP.UTF_8));
			response = httpClient.execute(requestPut);
			System.out.println("UPLOAD 2 : " + response.getStatusLine().getStatusCode());
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
//				response.getEntity().consumeContent();
				System.out.println("UPLOAD 3 : ");
				return true;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(response != null) {
				try {
					response.getEntity().consumeContent();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	public boolean downloadObject (String container, String id, String name) {
		if(auth_token.equals("")) {
			if(!authentication())
				return false;
		} 
		
		HttpResponse response = null;
		try {
			String url = URL_SWIFT + container + "/" + id + "/" + name;
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("format", "json"));
			String paramsUrl = URLEncodedUtils.format(params, HTTP.UTF_8);
			url += "?" + paramsUrl;
			HttpGet requestGet = new HttpGet(url);
			requestGet.addHeader("X-Auth-Token", auth_token);
			response = httpClient.execute(requestGet);
			String content = EntityUtils.toString(response.getEntity());
			return ReadWriteFile.writeFile(name, content);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { 
			if(response != null) {
				try {
					response.getEntity().consumeContent();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return false;
	}
	
	public String readObject (String container, String id, String name) {
		if(auth_token.equals("")) {
			if(!authentication())
				return null;
		} 
		
		HttpResponse response = null;
		try {
			String url = URL_SWIFT + container + "/" + id + "/" + name;
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("format", "json"));
			String paramsUrl = URLEncodedUtils.format(params, HTTP.UTF_8);
			url += "?" + paramsUrl;
			HttpGet requestGet = new HttpGet(url);
			requestGet.addHeader("X-Auth-Token", auth_token);
			response = httpClient.execute(requestGet);
			String content = EntityUtils.toString(response.getEntity());
			return content;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { 
			if(response != null) {
				try {
					response.getEntity().consumeContent();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
	
}
