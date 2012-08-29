package org.lua.commons.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.lua.commons.nativeapi.LuaRuntimeException;
import org.lua.commons.webapi.http.BasicLuaWebResponse;
import org.lua.commons.webapi.http.LuaWebResponse;
import org.lua.commons.webapi.http.LuaWebSender;

public class HttpClientLuaWebSender implements LuaWebSender {

	protected final String url;

	protected final DefaultHttpClient client;

	public HttpClientLuaWebSender(String username, String password, String url,
			int timeout) throws URISyntaxException {
		this.client = new DefaultHttpClient();
		client.getParams().setIntParameter("http.socket.timeout", timeout);
		client.getParams().setIntParameter("http.connection.timeout", timeout);
		URI uri = new URI(url);
		if (username != null || password != null) {
			client.getCredentialsProvider().setCredentials(
					new AuthScope(uri.getHost(), uri.getPort()),
					new UsernamePasswordCredentials(username, password));
		}
		this.url = url;
	}

	public LuaWebResponse send(byte[] requestBody) {
		HttpPost post = new HttpPost(url);
		post.setEntity(new ByteArrayEntity(requestBody));
		try {
			HttpResponse response = client.execute(post);
			try {
				int code = response.getStatusLine().getStatusCode();
				InputStream body = response.getEntity().getContent();
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				int length = 0;
				byte[] buf = new byte[10 * 1024];
				while (length >= 0) {
					bout.write(buf, 0, length);
					length = body.read(buf);
				}
				String bodyStr = new String(bout.toByteArray());
				return new BasicLuaWebResponse(code, bodyStr);
			} finally {
				response.getEntity().getContent().close();
			}
		} catch (ClientProtocolException e) {
			throw new LuaRuntimeException(
					"Error while sending request to server", e);
		} catch (IOException e) {
			throw new LuaRuntimeException(
					"Error while sending request to server", e);
		}
	}
}
