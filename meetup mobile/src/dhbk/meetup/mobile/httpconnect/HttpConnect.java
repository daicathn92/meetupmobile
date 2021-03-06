package dhbk.meetup.mobile.httpconnect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

public class HttpConnect {

	private HttpClient httpClient;
	
	public HttpConnect () {
		httpClient = new DefaultHttpClient() ;
	}
	
	public HttpResponse sendRequestGet (String url, Header[] headers, ArrayList<String[]> values) 
						throws ClientProtocolException, IOException {
		String urlfull = url;
		
		if(values != null) {
			List<NameValuePair> params = new ArrayList<NameValuePair>() ;
			for (String[] value : values) {
				params.add(new BasicNameValuePair(value[0], value[1])) ;
			}
			String paramsUrl = URLEncodedUtils.format(params, HTTP.UTF_8);
			urlfull = urlfull + "?" + paramsUrl;
			System.out.println("PARAM : " + paramsUrl);
		}
		HttpGet request = new HttpGet(urlfull) ;
		
		if(headers != null) {
			for(Header header : headers) 
				request.addHeader(header);
		}
//		request.addHeader("Accept", "application/json");
		return httpClient.execute(request) ;
	}
	
	public HttpResponse sendRequestPost (String url, Header[] headers, ArrayList<String[]> values) 
						throws ClientProtocolException, IOException {
		HttpPost request = new HttpPost(url) ;
		if(headers != null) {
			for(Header header : headers) 
				request.addHeader(header);
		}
		
		List<NameValuePair> params = new ArrayList<NameValuePair>() ;
		for (String[] value : values) {
			params.add(new BasicNameValuePair(value[0], value[1])) ;
		}
		
		request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		
		return httpClient.execute(request) ;
	}
}

