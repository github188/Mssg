package com.fable.mssg.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;

import javax.net.ssl.*;
import java.net.Socket;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author by wolf on 2017/4/5.
 */
@Slf4j
public class HttpsURLConnectionUtilX {

	/**
	 * 获得KeyStore.
	 *
	 * @param password 密码
	 * @param resource 密钥库路径
	 * @return 密钥库
	 * @throws Exception
	 */
	public static KeyStore getKeyStore(String password, String resource)
			throws Exception {
		// 实例化密钥库
		KeyStore store = KeyStore.getInstance("JKS");
		// 获得密钥库文件流
		URL url = ResourceUtils.getURL(resource);
		// 加载密钥库
		store.load(url.openStream(), password == null ? null : password.toCharArray());
		return store;
	}

	/**
	 * 获得SSLSocketFactory.
	 *
	 * @param password       密码
	 * @param keyStorePath   密钥库路径
	 * @param trustStorePath 信任库路径
	 * @return SSLSocketFactory
	 * @throws Exception
	 */
	public static SSLContext getSSLContext(String password,
			String keyStorePath, String trustStorePath) throws Exception {
		// 实例化密钥库
		KeyManagerFactory keyManagerFactory = KeyManagerFactory
				.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		// 获得密钥库
		KeyStore keyStore = getKeyStore(password, keyStorePath);
		// 初始化密钥工厂
		keyManagerFactory.init(keyStore, password.toCharArray());

		// 实例化信任库
		TrustManagerFactory trustManagerFactory = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		// 获得信任库
		KeyStore trustStore = getKeyStore(password, trustStorePath);
		// 初始化信任库
		trustManagerFactory.init(trustStore);
		// 实例化SSL上下文
		SSLContext ctx = SSLContext.getInstance("TLS");
		// 初始化SSL上下文
		ctx.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

		// 获得SSLSocketFactory
		return ctx;
	}

	public static SSLContext getSSLContext() throws Exception {
		// 实例化SSL上下文
		SSLContext ctx = SSLContext.getInstance("TLS");
		ctx.init(getDefaultKeyManagers(), getDefaultTrustManagers(), null);
		return ctx;
	}

	private static KeyManager[] getDefaultKeyManagers() {
		return new KeyManager[] { new X509KeyManager() {
			@Override
			public String[] getClientAliases(String s, Principal[] principals) {
				return new String[0];
			}

			@Override
			public String chooseClientAlias(String[] strings, Principal[] principals, Socket socket) {
				return null;
			}

			@Override
			public String[] getServerAliases(String s, Principal[] principals) {
				return new String[0];
			}

			@Override
			public String chooseServerAlias(String s, Principal[] principals, Socket socket) {
				return null;
			}

			@Override
			public X509Certificate[] getCertificateChain(String s) {
				return new X509Certificate[0];
			}

			@Override
			public PrivateKey getPrivateKey(String s) {
				return null;
			}
		} };
	}

	private static TrustManager[] getDefaultTrustManagers() {
		return new TrustManager[] { new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
				log.debug("checkClientTrusted:" + s);
			}

			@Override
			public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
				log.debug("checkServerTrusted:" + s);
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		} };
	}

	/**
	 * 初始化HttpsURLConnection.
	 *
	 * @throws Exception
	 */
	public static void initHttpsURLConnection() throws Exception {
		// 声明SSL上下文
		SSLContext sslContext = null;
		// 实例化主机名验证接口
		HostnameVerifier hnv = new HostnameVerifier() {
			@Override
			public boolean verify(String s, SSLSession sslSession) {
				return true;
			}
		};
		try {
			sslContext = getSSLContext();
		}
		catch (GeneralSecurityException e) {
			log.error("", e);
		}
		if (sslContext != null) {
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		}
		else {
			log.warn("---初始化HttpsURLConnection, sslContext is null!");
		}
		HttpsURLConnection.setDefaultHostnameVerifier(hnv);
		log.debug("---初始化HttpsURLConnection完成---");
	}

}
