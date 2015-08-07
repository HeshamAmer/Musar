package com.musar.system;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONException;
import org.json.JSONObject;


import com.musar.gui.R;

import android.annotation.SuppressLint;
import android.content.Context;



public class SimpleHttpClient {
 /** The time it takes for our client to timeout */
    public static final int HTTP_TIMEOUT = 30 * 1000; // milliseconds

    /** Single instance of our HttpClient */
    private static HttpClient mHttpClient;
    /**
     * Get our single instance of our HttpClient object.
     *
     * @return an HttpClient object with connection parameters set
     * @throws IOException 
     * @throws NoSuchAlgorithmException 
     * @throws CertificateException 
     * @throws NoSuchProviderException 
     * @throws KeyStoreException 
     * @throws UnrecoverableKeyException 
     * @throws KeyManagementException 
     */
    private static HttpClient getHttpClient(Context top_activity) throws KeyStoreException, NoSuchProviderException, CertificateException, NoSuchAlgorithmException, IOException, KeyManagementException, UnrecoverableKeyException {
        if (mHttpClient == null) {
         //sets up parameters
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, "utf-8");
            params.setBooleanParameter("http.protocol.expect-continue", false);
            //registers schemes for both http and https
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
           
            registry.register(new Scheme("https", newSslSocketFactory(top_activity), 443));
            SingleClientConnManager mgr = new SingleClientConnManager(params, registry);
            mHttpClient = new DefaultHttpClient(mgr, params);
            
        }
        return mHttpClient;
    }
    
    private static SocketFactory newSslSocketFactory(Context top_activity) throws KeyStoreException, NoSuchProviderException, CertificateException, NoSuchAlgorithmException, IOException, KeyManagementException, UnrecoverableKeyException {
    	// read .crt file from memory
    
    	//File file=new File ("/mnt/sdcard/server.pem");
    	InputStream inStream =  top_activity.getResources().openRawResource(R.raw.server);
    	//File file_2 =new File("/mnt/sdcard/client.pem");
    	InputStream instream_2=top_activity.getResources().openRawResource(R.raw.client);
    	//InputStream instream_2=new BufferedInputStream(new FileInputStream(file_2));
    	SSLSocketFactory sf=null;
    	if(inStream != null)
    	{
    	    KeyStore server_cert = ConvertCerToBKS(inStream, "MyAlias", "31101991".toCharArray());
    	    // initialize trust manager factory with the read truststore
    	      TrustManagerFactory trustManagerFactory = null;
    	      trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    	      trustManagerFactory.init(server_cert);
    	     
    	      KeyStore client_cert=convert_client_toBKS(instream_2,"client","31101991".toCharArray());
    	      // initialize key manager factory with the read client certificate
    	      KeyManagerFactory keyManagerFactory = null;
    	      keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    	      keyManagerFactory.init(client_cert, "31101991".toCharArray());
    	   
    	      
    	      // initialize SSLSocketFactory to use the certificates
    	      
    	      sf = new SSLSocketFactory(SSLSocketFactory.TLS, client_cert, "31101991",
    	          server_cert, null, null);

    	    //  sf = new SSLSocketFactory(server_cert);
            // Hostname verification from certificate
            // http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html#d4e506
            sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
           
    	    inStream.close();
    	}
    	System.out.println(sf);
    return sf;
      }
    public static KeyStore convert_client_toBKS(InputStream cerStream, String alias, char [] password) throws KeyStoreException, NoSuchProviderException, CertificateException, NoSuchAlgorithmException, IOException
    {
        KeyStore keyStore = null;
        {
		    keyStore = KeyStore.getInstance("BKS","BC");
		   
            CertificateFactory factory = CertificateFactory.getInstance("X.509", "BC");
            java.security.cert.Certificate certificate =  factory.generateCertificate(cerStream);
            keyStore.load(null, password);
            keyStore.setCertificateEntry(alias,  certificate);
            System.out.println("loading client certificate");
        }
        
        return keyStore;                                    
    }
    public static KeyStore ConvertCerToBKS(InputStream cerStream, String alias, char [] password) throws KeyStoreException, NoSuchProviderException, CertificateException, NoSuchAlgorithmException, IOException
    {
        KeyStore keyStore = null;
        {
		    keyStore = KeyStore.getInstance("BKS", "BC");
            CertificateFactory factory = CertificateFactory.getInstance("X.509", "BC");
            java.security.cert.Certificate certificate =  factory.generateCertificate(cerStream);
            keyStore.load(null, password);
            keyStore.setCertificateEntry(alias,  certificate);
            System.out.println("loading server certificate");
        }
        
        return keyStore;                                    
    }

    /**
     * Performs an HTTP Post request to the specified url with the
     * specified parameters.
     *
     * @param url The web address to post the request to
     * @param postParameters The parameters to send via the request
     * @return The result of the request
     * @throws JSONException 
     * @throws Exception
     */
    @SuppressLint("NewApi")
	public static String jsonString(Object user) throws JSONException {
    	
    	JSONObject obj2 = new JSONObject(); 
    	//if(object_type=="user_model")
    	 // obj2.put("user_model", user);
    	//else if(object_type=="user")
    	  obj2.put("user", user);
    	return obj2.toString();
    }
    

    public static String executeHttpPost(String url,StringEntity params,Context top_activity) throws Exception {
        BufferedReader in = null;
        try {
        	System.out.println("hhtp post");
            HttpClient client = new DefaultHttpClient();//getHttpClient(top_activity.getApplicationContext());
            System.out.println(url);
            HttpPost request = new HttpPost(url);
            params.setContentType("application/json; charset=UTF-8");
            request.setEntity(params);
            HttpResponse response = client.execute(request);
           
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
                
            }
            in.close();

            String result = sb.toString();
            System.out.println("post:"+result);
            return result;
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Performs an HTTP GET request to the specified url.
     *
     * @param url The web address to post the request to
     * @return The result of the request
     * @throws Exception
     */
    public static String executeHttpGet(String url,Context top_activity) throws Exception {
        BufferedReader in = null;
        try {
            HttpClient client = getHttpClient(top_activity);
            HttpGet request = new HttpGet();
            request.setURI(new URI(url));
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            System.out.println("da5aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaal");
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
                System.out.println("looooooooooooop");
            }
            in.close();

            String result = sb.toString();
            return result;
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
