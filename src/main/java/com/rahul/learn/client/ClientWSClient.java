package com.rahul.learn.client;

import java.io.BufferedInputStream;
import java.util.Properties;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.codehaus.jackson.map.ObjectMapper;

import com.rahul.learn.domain.Client;

public class ClientWSClient {
	String url = null;
	String eTag = null;
	String jsonResponse = null;

	public void getAll() throws Exception {
		Properties properties = new Properties();
		properties.load(this.getClass().getClassLoader()
				.getResourceAsStream("sample.properties"));
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(properties.getProperty("endPointUrl")
				+ "clients");
		HttpResponse response = httpClient.execute(httpGet);
		Header[] headers = response.getAllHeaders();
		for (Header header : headers) {
			System.out.println(header.getName() + ":" + header.getValue());
		}
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			BufferedInputStream bis = new BufferedInputStream(
					entity.getContent());
			byte[] tmp = new byte[2048];
			while ((bis.read(tmp)) != -1) {
			}
			String jsonResponse = new String(tmp);
			System.out.println(jsonResponse);
		}
	}

	public void create() throws Exception {
		System.out.println("************* CREATE **************");
		Properties properties = new Properties();
		properties.load(this.getClass().getClassLoader()
				.getResourceAsStream("sample.properties"));

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(properties.getProperty("endPointUrl")
				+ "clients");
		Header requestHeader = new BasicHeader("Content-Type",
				"application/json");
		httpPost.addHeader(requestHeader);

		HttpEntity requestEntity = new StringEntity(
				properties.getProperty("createClient"));
		httpPost.setEntity(requestEntity);

		HttpResponse response = httpClient.execute(httpPost);

		Header[] headers = response.getAllHeaders();
		for (Header header : headers) {
			if (header.getName().equals("Location")) {
				url = header.getValue();
				break;
			}
		}
		System.out.println("Status: " + response.getStatusLine());
	}

	public void fetch() throws Exception {
		System.out.println("************* FETCH **************");
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		HttpResponse response = httpClient.execute(httpGet);
		Header[] headers = response.getAllHeaders();
		for (Header header : headers) {
			if (header.getName().equals("Etag")) {
				eTag = header.getValue();
				break;
			}
		}
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			BufferedInputStream bis = new BufferedInputStream(
					entity.getContent());
			byte[] tmp = new byte[2048];
			while ((bis.read(tmp)) != -1) {
			}
			jsonResponse = new String(tmp);
			System.out.println(jsonResponse);
		}
		System.out.println("Status: " + response.getStatusLine());
	}

	public void update() throws Exception {
		System.out.println("************* UPDATE **************");
		HttpClient httpClient = new DefaultHttpClient();
		HttpPut httpPut = new HttpPut(url);
		updateJsonResponse();
		HttpEntity requestEntity = new StringEntity(jsonResponse);
		httpPut.setEntity(requestEntity);

		Header requestHeader = new BasicHeader("Content-Type",
				"application/json");
		httpPut.addHeader(requestHeader);
		requestHeader = new BasicHeader("If-Match", eTag);
		httpPut.addHeader(requestHeader);

		HttpResponse response = httpClient.execute(httpPut);
		Header[] headers = response.getAllHeaders();
		for (Header header : headers) {
			if (header.getName().equals("Etag")) {
				eTag = header.getValue();
				break;
			}
		}
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			BufferedInputStream bis = new BufferedInputStream(
					entity.getContent());
			byte[] tmp = new byte[2048];
			while ((bis.read(tmp)) != -1) {
			}
			jsonResponse = new String(tmp);
			System.out.println(jsonResponse);
		}
		System.out.println("Status: " + response.getStatusLine());
	}

	public void delete() throws Exception {
		System.out.println("************* DELETE **************");
		HttpClient httpClient = new DefaultHttpClient();
		HttpDelete httpDelete = new HttpDelete(url);
		Header requestHeader = new BasicHeader("Content-Type",
				"application/json");
		httpDelete.addHeader(requestHeader);
		requestHeader = new BasicHeader("If-Match", eTag);
		httpDelete.addHeader(requestHeader);

		HttpResponse response = httpClient.execute(httpDelete);
		System.out.println(response.getStatusLine());
	}

	public static void main(String[] args) throws Exception {
		ClientWSClient client = new ClientWSClient();
		client.create();
		client.create();
		client.create();
		client.fetch();
		client.update();
		client.fetch();
//		client.delete();
		client.fetch();
		client.getAll();
	}

	private void updateJsonResponse() {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			Client client = objectMapper.readValue(jsonResponse, Client.class);
			client.setDescription("Updating the asset description");
			jsonResponse = objectMapper.writeValueAsString(client);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
