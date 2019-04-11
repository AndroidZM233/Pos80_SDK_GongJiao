/**
 *
 */
package com.spd.yinlianpay.net;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.DatatypeConverter;

/**
 * @author Administrator
 *
 */
public class test {
	public static void main(String[] args) throws IOException {
		String hostname = "120.204.69.139";
		int port = 30000;

		Socket socket = null;
		PrintWriter writer = null;
		BufferedReader reader = null;

		try {
			BaseHttpSSLSocketFactory fc = new BaseHttpSSLSocketFactory();

			socket = fc.createSocket(hostname, port);
			socket.setSoTimeout(10000);
			OutputStream os = socket.getOutputStream();
			writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			StringBuilder sb = new StringBuilder();

			sb.append("POST / HTTP/1.1\r\n");
			sb.append(("Host: " + hostname + "\r\n"));
			sb.append("Accept: */*\r\n");
			sb.append("User-Agent: Java\r\n"); // Be honest.
			sb.append("Content-Type: x-ISO-TPDU/x-auth\r\n");
			sb.append("Content-Length: 62\r\n");
			sb.append("\r\n");
			byte[] a = sb.toString().getBytes();
			String s = "003C600501000060210000000008000020000000c00012001221373130303032303038393831323030343131313030303700110000011200300003303120";
			byte[] b = DatatypeConverter.parseHexBinary(s);

			byte[] data3 = new byte[a.length + b.length];
			System.arraycopy(a, 0, data3, 0, a.length);
			System.arraycopy(b, 0, data3, a.length, b.length);
			data3 = DataConversionUtils.HexString2Bytes("504f5354202f20485454502f312e310d0a486f73743a203134302e3230372e3136382e36320d0a4163636570743a202a2f2a0d0a557365722d4167656e743a204a6176610d0a436f6e74656e742d547970653a20782d49534f2d545044552f782d617574680d0a436f6e74656e742d4c656e6774683a2036320d0a0d0a003c600601000060320032401708000020000000c00012000111313030313037313033303836343030343131313030303700110000000100300003303031");
			os.write(data3);
			os.flush();

			int retlen = 0;
			InputStream is = socket.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));
			for (String line; (line = reader.readLine()) != null;) {
				if (line.isEmpty())
					break; // Stop when headers are completed.
				System.out.println(line);
				Matcher m=Pattern.compile("Content-Length:\\s?(\\d+)").matcher(line);
				if (m.find()) {
					retlen = Integer.parseInt(m.group(1));
				}
			}
			char[] cbuf=new char[retlen];
			int realreadlen = reader.read(cbuf);
			System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(new String(cbuf).getBytes("ISO-8859-1")));


		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException logOrIgnore) {
				}
			if (writer != null) {
				writer.close();
			}
			if (socket != null)
				try {
					socket.close();
				} catch (IOException logOrIgnore) {
				}
		}
	}

	public static void main1(String[] args) {
		try {
			System.setProperty("http.keepAlive", String.valueOf(false));
			// System.setProperty("https.proxyHost", "localhost");
			// System.setProperty("https.proxyPort", "8888");
			// String s =
			// "003c600501000060210000000008000020000000c00012001221373130303032303038393831323030343131313030303700110000011200300003303120";
			String s = "000A600501000060210000000008000020000000c00012001221373130303032303038393831323030343131313030303700110000011200300003303120";
			byte[] b = DatatypeConverter.parseHexBinary(s);

			URL url = new URL("https://120.204.69.139:30000/unp/webtrans/WPOS");
			// URL url = new URL("http://123.206.54.253/httpspos");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(10000);// 连接超时时间
			connection.setReadTimeout(10000);// 读取结果超时时间
			connection.setDoInput(true); // 可读
			connection.setDoOutput(true); // 可写
			// connection.setUseCaches(false);// 取消缓存
			connection.setRequestProperty("Content-Type", "x-ISO-TPDU/x-auth");
			connection.setRequestProperty("Content-Length", Integer.toString(b.length));
			// connection.setFixedLengthStreamingMode(10);
			connection.setRequestProperty("User-Agent", "curl/7.46.0");
			connection.setRequestProperty("Accept", "*/*");
			// connection.setRequestProperty("Connection", "close");
			connection.setRequestMethod("POST");

			if ("https".equalsIgnoreCase(url.getProtocol())) {
				HttpsURLConnection husn = (HttpsURLConnection) connection;
				// 是否验证https证书，测试环境请设置false，生产环境建议优先尝试true，不行再false
				if (true) {
					husn.setSSLSocketFactory(new BaseHttpSSLSocketFactory());
					husn.setHostnameVerifier(new BaseHttpSSLSocketFactory.TrustAnyHostnameVerifier());// 解决由于服务器证书问题导致HTTPS无法访问的情况
				}
			}
			OutputStream os = connection.getOutputStream();
			os.write(b);
			os.flush();
			os.close();
			connection.connect();

			InputStream in = null;
			StringBuilder sb = new StringBuilder(1024);
			BufferedReader br = null;
			try {
				System.out.println("HTTP Return Status-Code:[" + connection.getResponseCode() + "]");
				if (200 == connection.getResponseCode()) {
					in = connection.getInputStream();
					sb.append(javax.xml.bind.DatatypeConverter.printHexBinary(read(in, 100)));
				} else {
					in = connection.getErrorStream();
					sb.append(javax.xml.bind.DatatypeConverter.printHexBinary(read(in, 100)));
				}
			} catch (Exception e) {
				throw e;
			} finally {
				if (null != br) {
					br.close();
				}
				if (null != in) {
					in.close();
				}
				if (null != connection) {
					connection.disconnect();
				}
			}

			System.out.println(sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static byte[] read(InputStream in, int readlength) throws IOException {

		byte[] buf = new byte[readlength];
		int length = 0;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		while ((length = in.read(buf, 0, buf.length)) > 0) {

			bout.write(buf, 0, length);
		}
		bout.flush();
		return bout.toByteArray();
	}
}
