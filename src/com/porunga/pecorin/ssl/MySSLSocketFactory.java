package com.porunga.pecorin.ssl;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;

/**
 * MySSLSocketFactory
 * @author cube
 * @see http://www.glamenv-septzen.net/view/981
 */
public class MySSLSocketFactory extends SSLSocketFactory {
	SSLContext sslContext = SSLContext.getInstance("TLS");

	public MySSLSocketFactory(KeyStore truststore)
			throws NoSuchAlgorithmException, KeyManagementException,
			KeyStoreException, UnrecoverableKeyException {
		super(truststore);
		// 自己署名証明書を受け付けるカスタムSSLContextの準備
        TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain,
                    String authType) throws CertificateException {
            }
 
            public void checkServerTrusted(X509Certificate[] chain,
                    String authType) throws CertificateException {
            }
 
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        sslContext.init(null, new TrustManager[] { tm }, null);
	}
	
	@Override
    public Socket createSocket(Socket socket, String host, int port,
            boolean autoClose) throws IOException, UnknownHostException {
        // カスタムSSLContext経由で生成したSSLソケットを返す。
        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }
 
    @Override
    public Socket createSocket() throws IOException {
        // カスタムSSLContext経由で生成したSSLソケットを返す。
        return sslContext.getSocketFactory().createSocket();
    }
}
