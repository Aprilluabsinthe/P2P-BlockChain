package LabBlockChain.BlockChain.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class HttpRequest {
	/**
	 * Get method for a URL
	 * @param url the url to get
	 * @param param the param, should be like field=value&field=value......
	 * @return Response result
	 */
	public static String sendGet(String url, String param) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url + "?" + param;
			URL realUrl = new URL(urlNameString);
			//A new instance of URLConnection is created
			URLConnection connection = realUrl.openConnection();

			//Sets the general request property
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

			// Opens a communications link to the resource referenced by this URL,
			// if such a connection has not already been established
			connection.connect();

			// Returns an unmodifiable Map of the header fields.
			Map<String, List<String>> map = connection.getHeaderFields();

			// print for debug
			for (String key : map.keySet()) {
				System.out.println(key + "--->" + map.get(key));
			}

			// to read URL response
			// https://stackoverflow.com/questions/29534157/why-utf-8-contents-not-displayed-in-a-servlet-deployed-in-jetty
			// https://stackoverflow.com/questions/8560395/how-to-use-readline-method-in-java
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("Getter Error" + e);
			e.printStackTrace();
		}
		// close finally
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * POST method for a url
	 * @param url the url to post
	 * @param param the param for post, should be like field=value&field=value;
	 *                 caution! transaction should be put in body not in url
	 * @return
	 */
	public static String sendPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			//A new instance of URLConnection is created
			URLConnection conn = realUrl.openConnection();

			//Sets the general request property
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

			// https://docs.oracle.com/javase/7/docs/api/java/net/URLConnection.html
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// defign a output stream
			out = new PrintWriter(conn.getOutputStream());
			// send output params
			out.print(param);

			out.flush();
			// for InputStreamReader
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("POST Error" + e);
			e.printStackTrace();
		}

		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
}