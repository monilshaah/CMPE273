import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.google.gson.Gson;


public class RestCallJsonParsing {
	
	 public String getJSON(String url) {
		 System.out.println(url);
		 TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
			    public X509Certificate[] getAcceptedIssuers(){return null;}
			    public void checkClientTrusted(X509Certificate[] certs, String authType){}
			    public void checkServerTrusted(X509Certificate[] certs, String authType){}
			}};   
		 try {
		        URL u = new URL(url);
		        SSLContext sc = SSLContext.getInstance("TLS");
		        sc.init(null, trustAllCerts, new SecureRandom());
		        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		        HttpsURLConnection.setDefaultHostnameVerifier( new HostnameVerifier(){
		            public boolean verify(String string,SSLSession ssls) {
		                return true;
		            }
		        });
		        HttpsURLConnection c = (HttpsURLConnection) u.openConnection();
		        
		        c.setRequestMethod("GET");
		        
		        c.connect();
		        int status = c.getResponseCode();
		        System.out.println(status);
		        switch (status) {
		        
		            case 200:
		            {
		            	BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
		                StringBuilder sb = new StringBuilder();
		                String line;
		                while ((line = br.readLine()) != null) {
		                    sb.append(line+"\n");
		                }
		                br.close();
		                System.out.println(sb);
		                return sb.toString();
		                
		            }
		            case 201: {
		                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
		                StringBuilder sb = new StringBuilder();
		                String line;
		                while ((line = br.readLine()) != null) {
		                    sb.append(line+"\n");
		                }
		                br.close();
		                return sb.toString();
		               
		            }
		            case 404 : {
			        	throw new Exception();
			        }
		        }
		        
		    } catch (MalformedURLException ex) {
		        
		    } catch (IOException ex) {
		        ex.printStackTrace();
		    }catch(Exception e) {
		    	e.printStackTrace();
		    }
		    return null;
		}
}
