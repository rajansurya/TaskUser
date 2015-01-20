package com.taskmanager.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

public class RestClient {

	private ArrayList<NameValuePair> params;
	private ArrayList<NameValuePair> headers;
	private String postData;
	private int connectionTimeOut=7000;
	private int soTimeOut=10000;
	
	private String url;

	private int responseCode;
	private String message;

	private String response;

	public int getConnectionTimeOut() {
		return connectionTimeOut;
	}

	public void setConnectionTimeOut(int connectionTimeOut) {
		this.connectionTimeOut = connectionTimeOut;
	}

	public int getSoTimeOut() {
		return soTimeOut;
	}

	public void setSoTimeOut(int soTimeOut) {
		this.soTimeOut = soTimeOut;
	}

	public String getResponse() {
		return response;
	}

	public String getErrorMessage() {
		return message;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public RestClient(String url) {
		this.url = url;
		params = new ArrayList<NameValuePair>();
		headers = new ArrayList<NameValuePair>();
	}

	public void AddParam(String name, String value) {
		params.add(new BasicNameValuePair(name, value));
	}

	public void AddHeader(String name, String value) {
		headers.add(new BasicNameValuePair(name, value));
	}

	public void Execute(RequestMethod method) throws Exception {
		switch (method) {
		case GET: {
			// add parameters
			String combinedParams = "";
			if (!params.isEmpty()) {
				combinedParams += "?";
				for (NameValuePair p : params) {
					String paramString = p.getName() + "="
							+ URLEncoder.encode(p.getValue(), "UTF-8");
					if (combinedParams.length() > 1) {
						combinedParams += "&" + paramString;
					} else {
						combinedParams += paramString;
					}
				}
			}

			HttpGet request = new HttpGet(url + combinedParams);

			// add headers
			for (NameValuePair h : headers) {
				request.addHeader(h.getName(), h.getValue());
			}

			executeRequest(request, url);
			break;
		}
		case POST: {
			HttpPost request = new HttpPost(url);

			// add headers
			for (NameValuePair h : headers) {
				request.addHeader(h.getName(), h.getValue());
			}

			if (!params.isEmpty()) {
				request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			}
			if (postData != null) {
				StringEntity str = new StringEntity(postData);
				request.setEntity(str);
				
			}
			executeRequest(request, url);
			break;
		}
		case PUT: {
			HttpPut request = new HttpPut(url);

			// add headers
			for (NameValuePair h : headers) {
				request.addHeader(h.getName(), h.getValue());
			}

			if (!params.isEmpty()) {
				request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			}
			if(postData!=null){
				StringEntity str = new StringEntity(postData);
				request.setEntity(str);	
				}
			executeRequest(request, url);
			break;
		}
		case DELETE: {
			HttpDelete request = new HttpDelete(url);

			// add headers
			for (NameValuePair h : headers) {
				request.addHeader(h.getName(), h.getValue());
			}

			executeRequest(request, url);
			break;
		}
		}
	}

	private void executeRequest(HttpUriRequest request, String url) {

		// HTTPHelp help = new HTTPHelp();
		HttpContext localContext = new BasicHttpContext();
		HttpClient client = new DefaultHttpClient();
		
		HttpResponse httpResponse;

		try {
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, connectionTimeOut);
			HttpConnectionParams.setSoTimeout(params,soTimeOut);
			httpResponse = client.execute(request, localContext);
			responseCode = httpResponse.getStatusLine().getStatusCode();
			message = httpResponse.getStatusLine().getReasonPhrase();
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {

				InputStream instream = entity.getContent();
				response = convertStreamToString(instream);

				// Closing the input stream will trigger connection release
				instream.close();
			}

		} catch (ClientProtocolException e) {
			client.getConnectionManager().shutdown();
			throw new RuntimeException();
		}catch (SocketException e) {
			throw new RuntimeException();
		
		} catch (IOException e) {
			client.getConnectionManager().shutdown();
			throw new RuntimeException();
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
		return sb.toString();
	}

	public String getPostData() {
		return postData;
	}

	public void setPostData(String postData) {
		this.postData = postData;
	}
	
	 /*public static String getElementValueByTagName(String elementName,String xmlString) throws Exception
	    {
	    	     	
	    		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    	DocumentBuilder db = factory.newDocumentBuilder();
		    	InputSource inStream = new InputSource();
		    	inStream.setCharacterStream(new StringReader(xmlString));
		    	Document doc = db.parse(inStream);	
		    	NodeList list = doc.getElementsByTagName("*");
	         for (int i=0; i<list.getLength(); i++) {
	            Element element = (Element)list.item(i);
	          String name= element.getNodeName();
	          if(name.equals(elementName))
	          return  element.getTextContent();	
	                 }
	    	
	    	return null;				    	
	    }*/
}