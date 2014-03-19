package com.czs.yat.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import android.util.Log;
import com.czs.yat.BuildConfig;
import com.czs.yat.data.DeviceConstant;

/**
 * 发送http请求的类
 * 
 * @author asus
 * 
 */
public class Http
{
	private static int sequence = 0;
	public static HashMap<Object, Object> mHosts = new HashMap<Object, Object>();
	private static int CONNECT_TIMEOUT = 5000;
	private static int TIMEOUT = 20000;

	// /**
	// * 初始化hosts 配置文件，需要在发第一个请求前调用
	// *
	// * @param context
	// */
//	public static void init(Context context)
//	{
//		try
//		{
//			InputStream is = context.getResources()
//					.openRawResource(R.raw.hosts);
//			Properties prop = new Properties();
//			prop.load(is);
//			mHosts = new HashMap<Object, Object>(prop);
//		}
//		catch (IOException e)
//		{
//			Log.e("Application", "", e);
//		}
//	}

	public static class HttpResult<T>
	{
		public T data;
		public int code;
		public Map<String, List<String>> header;

		public HttpResult(T data, int code, Map<String, List<String>> header)
		{
			this.data = data;
			this.code = code;
			this.header = header;
		}

		/**
		 * 获取服务器返回的ETag，如服务器没返回，获得null
		 * 
		 * @return
		 */
		public String getETag()
		{
			List<String> list = this.header.get("ETag");
			if (list == null || list.size() == 0)
			{
				list = this.header.get("etag");
				if (list == null || list.size() == 0)
				{
					return null;
				}
			}
			return list.get(0);
		}

	}

	/**
	 * http请求构建工具类
	 * 
	 * @author asus
	 * 
	 */
	public static class HttpBuilder
	{
		/**
		 * 当请求抛出异常（超时、服务器返回4XX~5XX错误码）时，尝试重新发送请求的次数
		 */
		public int RETRY_COUNT = 2;
		private String url;
		private HashMap<String, String> headers;
		private byte[] body;
		private MultipartEntity[] multipartBody;

		public HttpBuilder()
		{
			headers = new HashMap<String, String>();
			headers.put("Accept", "*/*");
			headers.put("Accept-Encoding", "gzip");
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + RETRY_COUNT;
			result = prime * result + Arrays.hashCode(body);
			result = prime * result
					+ ((headers == null) ? 0 : headers.hashCode());
			result = prime * result + Arrays.hashCode(multipartBody);
			result = prime * result + ((url == null) ? 0 : url.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			HttpBuilder other = (HttpBuilder) obj;
			if (RETRY_COUNT != other.RETRY_COUNT)
				return false;
			if (!Arrays.equals(body, other.body))
				return false;
			if (headers == null)
			{
				if (other.headers != null)
					return false;
			}
			else if (!headers.equals(other.headers))
				return false;
			if (!Arrays.equals(multipartBody, other.multipartBody))
				return false;
			if (url == null)
			{
				if (other.url != null)
					return false;
			}
			else if (!url.equals(other.url))
				return false;
			return true;
		}

		public String getRequestHash()
		{
			return MD5Util.getMD5Str(url + hashCode());
		}

		/**
		 * 设定请求的URL
		 * 
		 * @param surl
		 * @return
		 */
		public HttpBuilder url(String surl)
		{
			this.url = surl;
			return this;
		}

		/**
		 * 添加http头字段
		 * 
		 * @param key
		 * @param value
		 * @return
		 */
		public HttpBuilder header(String key, String value)
		{
			this.headers.put(key, value);
			return this;
		}

		public HttpBuilder formBodyWithDefault(String... keyAndValues)
				throws UnsupportedEncodingException
		{
			String[] defaultData = new String[]
			{ "device_type", DeviceConstant.DEVICE_TYPE, "time_zone",
					DeviceConstant.TIMEZONE, "mac", DeviceConstant.MAC_ADDRESS,
					"app_type", DeviceConstant.APP_TYPE, "device_id",
					DeviceConstant.DEVICE_ID, "open_udid",
					DeviceConstant.DEVICE_UUID, "version",
					DeviceConstant.VERSION, "language",
					DeviceConstant.DEVICE_LANGUAGE };
			int length = defaultData.length + keyAndValues.length;
			String[] data = new String[length];
			System.arraycopy(defaultData, 0, data, 0, defaultData.length);
			System.arraycopy(keyAndValues, 0, data, defaultData.length,
					keyAndValues.length);
			formBody(data);
			return this;
		}

		/**
		 * 添加表单格式application/x-www-form-urlencoded的body数据，以utf-8编码
		 * 
		 * @param keyAndValues
		 *            表单数据的key和value，成对添加
		 * @return
		 * @throws UnsupportedEncodingException
		 */
		public HttpBuilder formBody(String... keyAndValues)
				throws UnsupportedEncodingException
		{
			StringBuilder sb = new StringBuilder(1024);
			for (int i = 0; i < keyAndValues.length; i += 2)
			{
				if (i != 0)
				{
					sb.append("&");
				}
				sb.append(URLEncoder.encode(keyAndValues[i]));
				sb.append("=");
				sb.append(URLEncoder.encode(keyAndValues[i + 1] == null ? ""
						: keyAndValues[i + 1]));
			}
			this.body = sb.toString().getBytes("utf-8");

			return this;
		}

		public HttpBuilder multipartBodyWithDefault(
				MultipartEntity... multipartBody)
		{
			MultipartStringEntity[] defaultData = new MultipartStringEntity[]
			{
					new MultipartStringEntity("device_type",
							DeviceConstant.DEVICE_TYPE),
					new MultipartStringEntity("time_zone",
							DeviceConstant.TIMEZONE),
					new MultipartStringEntity("app_type",
							DeviceConstant.APP_TYPE),
					new MultipartStringEntity("device_id",
							DeviceConstant.DEVICE_ID),
					new MultipartStringEntity("open_udid",
							DeviceConstant.DEVICE_UUID),
					new MultipartStringEntity("version", DeviceConstant.VERSION),
					new MultipartStringEntity("language",
							DeviceConstant.DEVICE_LANGUAGE),
					new MultipartStringEntity("mac", DeviceConstant.MAC_ADDRESS) };
			int length = defaultData.length + multipartBody.length;
			MultipartEntity[] data = new MultipartEntity[length];
			System.arraycopy(defaultData, 0, data, 0, defaultData.length);
			System.arraycopy(multipartBody, 0, data, defaultData.length,
					multipartBody.length);
			return multipartBody(data);
		}

		public HttpBuilder multipartBody(MultipartEntity... multipartBody)
		{
			this.multipartBody = multipartBody;
			return this;
		}

		/**
		 * 直接给定body数据
		 * 
		 * @param body
		 * @return
		 */
		public HttpBuilder body(byte[] body)
		{
			this.body = body;
			return this;
		}

		/**
		 * 用post阻塞发送已经构建的请求
		 * 
		 * @return http server返回的数据
		 * @throws IOException
		 */
		public String post() throws IOException
		{
			int count = RETRY_COUNT;
			IOException ex = null;
			while (count > 0)
			{
				try
				{
					return HttpSender("POST", url, headers, body);
				}
				catch (IOException e)
				{
					Log.e("PeriodHttp", "", e);
					ex = e;
					count--;
				}
			}
			throw ex;
		}

		/**
		 * 用get阻塞发送已经构建的请求
		 * 
		 * @return http server返回的数据
		 * @throws IOException
		 */
		public String get() throws IOException
		{
			int count = RETRY_COUNT;
			IOException ex = null;
			while (count > 0)
			{
				try
				{
					return HttpSender("GET", url, headers, body);
				}
				catch (IOException e)
				{
					Log.e("PeriodHttp", "", e);
					ex = e;
					count--;
				}
			}
			throw ex;
		}

		public HttpResult<InputStream> getStreamExt() throws IOException
		{
			int count = RETRY_COUNT;
			IOException ex = null;
			while (count > 0)
			{
				try
				{
					HttpResult<InputStream> result = HttpInputStreamSender(
							"GET", url, headers, body);
					return result;
				}
				catch (IOException e)
				{
					Log.e("PeriodHttp", "", e);
					ex = e;
					count--;
				}
			}
			throw ex;
		}

		public InputStream getStream() throws IOException
		{
			return getStreamExt().data;
		}

		public HttpResult<InputStream> postStreamExt() throws IOException
		{
			int count = RETRY_COUNT;
			IOException ex = null;
			while (count > 0)
			{
				try
				{
					HttpResult<InputStream> result = HttpInputStreamSender(
							"POST", url, headers, body);
					return result;
				}
				catch (IOException e)
				{
					Log.e("PeriodHttp", "", e);
					ex = e;
					count--;
				}
			}
			throw ex;
		}

		public InputStream postStream() throws IOException
		{
			return postStreamExt().data;
		}

		public String postMultipart() throws IOException
		{
			int count = RETRY_COUNT;
			IOException ex = null;
			String bound = getRandomBound();
			headers.put("Content-Type",
					String.format("multipart/form-data; boundary=%s", bound));
			while (count > 0)
			{
				try
				{
					return HttpMultipartSender("POST", url, headers, bound,
							multipartBody);
				}
				catch (IOException e)
				{
					Log.e("PeriodHttp", "", e);
					ex = e;
					count--;
				}
			}
			throw ex;
		}

		private static String getRandomBound()
		{
			int length = 30;
			String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
			Random random = new Random(System.currentTimeMillis());
			StringBuffer sb = new StringBuffer(length + 4);
			sb.append("----");
			for (int i = 0; i < length; i++)
			{
				int number = random.nextInt(base.length());
				sb.append(base.charAt(number));
			}
			return sb.toString();
		}
	}

	public static interface MultipartEntity
	{
		public void printEntiry(OutputStream os)
				throws UnsupportedEncodingException, IOException;
	}

	public static class MultipartStringEntity implements MultipartEntity
	{
		static final String frefix = "Content-Disposition: form-data; name=\"%s\"\r\n\r\n";
		private String key;
		private String value;

		@Override
		public void printEntiry(OutputStream os)
				throws UnsupportedEncodingException, IOException
		{
			StringBuilder sb = new StringBuilder(256);
			sb.append(String.format(frefix, key));
			sb.append(value);
			sb.append("\r\n");
			os.write(sb.toString().getBytes("utf-8"));
		}

		public MultipartStringEntity(String key, String value)
		{
			this.key = key;
			this.value = value;
		}

		@Override
		public String toString()
		{
			return "key=" + key + " value=" + value;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MultipartStringEntity other = (MultipartStringEntity) obj;
			if (key == null)
			{
				if (other.key != null)
					return false;
			}
			else if (!key.equals(other.key))
				return false;
			if (value == null)
			{
				if (other.value != null)
					return false;
			}
			else if (!value.equals(other.value))
				return false;
			return true;
		}

	}

	public static class MultipartStreamEntity implements MultipartEntity
	{
		static final String desc = "Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"\r\nContent-Type: application/octet-stream\r\n\r\n";
		private String key;
		private String fileName;
		private File file;

		@Override
		public void printEntiry(OutputStream os)
				throws UnsupportedEncodingException, IOException
		{
			InputStream is = new FileInputStream(file);
			try
			{
				String temp = String.format(desc, key, fileName);
				os.write(temp.getBytes("utf-8"));
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1)
				{
					os.write(buffer, 0, len);
				}
				os.write("\r\n".getBytes("utf-8"));
			}
			finally
			{
				is.close();
			}
		}

		public MultipartStreamEntity(String key, File file)
				throws FileNotFoundException
		{
			this(key, file.getName(), file);
		}

		public MultipartStreamEntity(String key, String fileName, File file)
				throws FileNotFoundException
		{
			this.key = key;
			this.fileName = fileName;
			this.file = file;
		}

		@Override
		public String toString()
		{
			return "key=" + key + " fileName=" + fileName + " file="
					+ file.getAbsoluteFile();
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((file == null) ? 0 : file.hashCode());
			result = prime * result
					+ ((fileName == null) ? 0 : fileName.hashCode());
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MultipartStreamEntity other = (MultipartStreamEntity) obj;
			if (file == null)
			{
				if (other.file != null)
					return false;
			}
			else if (!file.equals(other.file))
				return false;
			if (fileName == null)
			{
				if (other.fileName != null)
					return false;
			}
			else if (!fileName.equals(other.fileName))
				return false;
			if (key == null)
			{
				if (other.key != null)
					return false;
			}
			else if (!key.equals(other.key))
				return false;
			return true;
		}

	}

	static
	{
		try
		{
			TrustManager[] trustAllCerts = new TrustManager[]
			{ new X509TrustManager()
			{
				@Override
				public void checkClientTrusted(final X509Certificate[] chain,
						final String authType)
				{
				}

				@Override
				public void checkServerTrusted(final X509Certificate[] chain,
						final String authType)
				{
				}

				@Override
				public X509Certificate[] getAcceptedIssuers()
				{
					return null;
				}
			} };
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, trustAllCerts, null);

			SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

			HttpsURLConnection
					.setDefaultHostnameVerifier(new HostnameVerifier()
					{
						@Override
						public boolean verify(String hostname,
								SSLSession session)
						{
							return true;
						}
					});
			HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		catch (KeyManagementException e)
		{
			e.printStackTrace();
		}
	}

	private static String HttpMultipartSender(String method, String surl,
			HashMap<String, String> headers, String bound,
			MultipartEntity[] body) throws IOException
	{
		int seq = sequence++;
		URL url = new URL(surl);
		String hostname = url.getHost();
		if (mHosts != null && mHosts.containsKey(hostname))
		{
			String localIp = (String) mHosts.get(hostname);
			headers.put("host", hostname);
			surl = surl.replaceFirst(hostname, localIp);
			url = new URL(surl);
			Log.d("PeriodHttp", "seq=" + seq + " use host: " + hostname + " "
					+ localIp);
		}
		if (BuildConfig.DEBUG)
		{
			Log.d("PeriodHttp", "seq=" + seq + " " + method + " surl=" + surl);
			Log.d("PeriodHttp", "seq=" + seq + " headers=" + headers);
			Log.d("PeriodHttp", "seq=" + seq + " bound=" + bound);
			for (MultipartEntity multipartEntity : body)
			{
				Log.d("PeriodHttp",
						"seq=" + seq + " body=" + multipartEntity.toString());
			}
		}

		HttpURLConnection c = (HttpURLConnection) url.openConnection();
		c.setConnectTimeout(CONNECT_TIMEOUT);
		c.setReadTimeout(TIMEOUT);
		c.setRequestMethod(method);

		for (Entry<String, String> header : headers.entrySet())
		{
			c.addRequestProperty(header.getKey(), header.getValue());
		}

		try
		{
			if (body != null)
			{
				c.setDoOutput(true);
				OutputStream os = c.getOutputStream();
				byte[] prefix = "--".getBytes("utf-8");
				byte[] byteBound = bound.getBytes("utf-8");
				byte[] divider = "\r\n".getBytes("utf-8");
				for (MultipartEntity multipartEntity : body)
				{
					os.write(prefix);
					os.write(byteBound);
					os.write(divider);
					multipartEntity.printEntiry(os);
				}
				os.write(prefix);
				os.write(byteBound);
				os.write("--\r\n".getBytes("utf-8"));
				os.flush();
				os.close();
			}
			if (BuildConfig.DEBUG)
			{
				Log.d("PeriodHttp",
						"seq=" + seq + " Http Code=" + c.getResponseCode());
			}
			InputStream is = null;
			is = c.getErrorStream();
			if (is == null)
			{

				is = c.getInputStream();
			}
			else
			{
				if ("gzip".equals(c.getHeaderField("Content-Encoding")))
				{
					is = new GZIPInputStream(is);
				}
				char[] recvData = new char[1024];
				StringBuffer buffer = new StringBuffer(1024);
				int l = 0;
				BufferedReader br = new BufferedReader(new InputStreamReader(
						is, Charset.forName("utf8")));
				while ((l = br.read(recvData)) != -1)
				{
					buffer.append(recvData, 0, l);
				}
				Log.e("PeriodHttp", "seq=" + seq + " " + buffer.toString());
				is.close();
				br.close();
				is = c.getInputStream();
			}

			if ("gzip".equals(c.getHeaderField("Content-Encoding")))
			{
				is = new GZIPInputStream(is);
			}
			char[] recvData = new char[1024];
			StringBuffer buffer = new StringBuffer(1024);
			int l = 0;
			BufferedReader br = new BufferedReader(new InputStreamReader(is,
					Charset.forName("utf8")));
			while ((l = br.read(recvData)) != -1)
			{
				buffer.append(recvData, 0, l);
			}
			is.close();
			br.close();
			String result = buffer.toString();
			if (BuildConfig.DEBUG)
			{
				Log.d("PeriodHttp", "seq=" + seq + " Http response=" + result);
			}
			return result;
		}
		catch (IOException e)
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Log.e("PeriodHttp", "seq=" + seq + " " + sw.toString());
			throw e;
		}

	}

	/**
	 * 发送http请求的方法
	 * 
	 * @param method
	 *            GET或者POST
	 * @param surl
	 *            请求的url
	 * @param headers
	 *            http头信息
	 * @param body
	 *            http body，无则是null
	 * @return http server返回的数据
	 * @throws IOException
	 */
	private static String HttpSender(String method, String surl,
			HashMap<String, String> headers, byte[] body) throws IOException
	{
		int seq = sequence++;
		URL url = new URL(surl);
		String hostname = url.getHost();
		if (mHosts != null && mHosts.containsKey(hostname))
		{
			String localIp = (String) mHosts.get(hostname);
			headers.put("host", hostname);
			surl = surl.replaceFirst(hostname, localIp);
			url = new URL(surl);
			Log.d("PeriodHttp", "seq=" + seq + " use host: " + hostname + " "
					+ localIp);
		}
		if (BuildConfig.DEBUG)
		{
			Log.d("PeriodHttp", "seq=" + seq + " " + method + " surl=" + surl);
			Log.d("PeriodHttp", "seq=" + seq + " headers=" + headers);
			if (body != null)
			{
				Log.d("PeriodHttp", "seq=" + seq + " body="
						+ new String(body, "utf-8"));
			}
		}

		HttpURLConnection c = (HttpURLConnection) url.openConnection();
		c.setConnectTimeout(CONNECT_TIMEOUT);
		c.setReadTimeout(TIMEOUT);
		c.setRequestMethod(method);

		for (Entry<String, String> header : headers.entrySet())
		{
			c.addRequestProperty(header.getKey(), header.getValue());
		}

		try
		{
			if (body != null)
			{
				c.setDoOutput(true);
				OutputStream os = c.getOutputStream();
				os.write(body, 0, body.length);
				os.flush();
				os.close();
			}
			if (BuildConfig.DEBUG)
			{
				Log.d("PeriodHttp",
						"seq=" + seq + " Http Code=" + c.getResponseCode());
			}
			InputStream is = null;
			is = c.getErrorStream();
			if (is == null)
			{

				is = c.getInputStream();
			}
			else
			{
				if ("gzip".equals(c.getHeaderField("Content-Encoding")))
				{
					is = new GZIPInputStream(is);
				}
				char[] recvData = new char[1024];
				StringBuffer buffer = new StringBuffer(1024);
				int l = 0;
				BufferedReader br = new BufferedReader(new InputStreamReader(
						is, Charset.forName("utf8")));
				while ((l = br.read(recvData)) != -1)
				{
					buffer.append(recvData, 0, l);
				}
				Log.e("PeriodHttp", "seq=" + seq + " " + buffer.toString());
				is.close();
				br.close();
				is = c.getInputStream();
			}

			if ("gzip".equals(c.getHeaderField("Content-Encoding")))
			{
				is = new GZIPInputStream(is);
			}
			char[] recvData = new char[1024];
			StringBuffer buffer = new StringBuffer(1024);
			int l = 0;
			BufferedReader br = new BufferedReader(new InputStreamReader(is,
					Charset.forName("utf8")));
			while ((l = br.read(recvData)) != -1)
			{
				buffer.append(recvData, 0, l);
			}
			is.close();
			br.close();
			String result = buffer.toString();
			if (BuildConfig.DEBUG)
			{
				Log.d("PeriodHttp", "seq=" + seq + " Http response=" + result);
			}
			return result;
		}
		catch (IOException e)
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Log.e("PeriodHttp", "seq=" + seq + " " + sw.toString());
			throw e;
		}

	}

	private static HttpResult<InputStream> HttpInputStreamSender(String method,
			String surl, HashMap<String, String> headers, byte[] body)
			throws IOException
	{
		int seq = sequence++;
		URL url = new URL(surl);
		String hostname = url.getHost();
		if (mHosts != null && mHosts.containsKey(hostname))
		{
			String localIp = (String) mHosts.get(hostname);
			headers.put("host", hostname);
			surl = surl.replaceFirst(hostname, localIp);
			url = new URL(surl);
			Log.d("PeriodHttp", "seq=" + seq + " use host: " + hostname + " "
					+ localIp);
		}
		if (BuildConfig.DEBUG)
		{
			Log.d("PeriodHttp", "seq=" + seq + " " + method + " surl=" + surl);
			Log.d("PeriodHttp", "seq=" + seq + " headers=" + headers);
			if (body != null)
			{
				Log.d("PeriodHttp", "seq=" + seq + " body="
						+ new String(body, "utf-8"));
			}
		}

		HttpURLConnection c = (HttpURLConnection) url.openConnection();
		c.setConnectTimeout(CONNECT_TIMEOUT);
		c.setReadTimeout(TIMEOUT);
		c.setRequestMethod(method);

		for (Entry<String, String> header : headers.entrySet())
		{
			c.addRequestProperty(header.getKey(), header.getValue());
		}
		try
		{
			if (body != null)
			{
				c.setDoOutput(true);
				OutputStream os = c.getOutputStream();
				os.write(body, 0, body.length);
				os.flush();
				os.close();
			}
			if (BuildConfig.DEBUG)
			{
				Log.d("PeriodHttp",
						"seq=" + seq + " Http Code=" + c.getResponseCode());
			}
			InputStream is = null;
			int code = c.getResponseCode();
			is = c.getErrorStream();
			if (is == null)
			{

				is = c.getInputStream();
			}
			else
			{
				if ("gzip".equals(c.getHeaderField("Content-Encoding")))
				{
					is = new GZIPInputStream(is);
				}
				char[] recvData = new char[1024];
				StringBuffer buffer = new StringBuffer(1024);
				int l = 0;
				BufferedReader br = new BufferedReader(new InputStreamReader(
						is, Charset.forName("utf8")));
				while ((l = br.read(recvData)) != -1)
				{
					buffer.append(recvData, 0, l);
				}
				Log.e("PeriodHttp", "seq=" + seq + " " + buffer.toString());
				is.close();
				br.close();
				is = c.getInputStream();
			}

			if ("gzip".equals(c.getHeaderField("Content-Encoding")))
			{
				is = new GZIPInputStream(is);
			}
			Map<String, List<String>> header = c.getHeaderFields();

			return new HttpResult<InputStream>(is, code, header);
		}
		catch (IOException e)
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Log.e("PeriodHttp", "seq=" + seq + " " + sw.toString());
			throw e;
		}

	}

}
