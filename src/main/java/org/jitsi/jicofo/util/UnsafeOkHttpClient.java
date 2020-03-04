package org.jitsi.jicofo.util;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.jitsi.jicofo.JitsiMeetConferenceImpl;
import org.jitsi.utils.logging.Logger;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

public class UnsafeOkHttpClient {
    private final static Logger logger
            = Logger.getLogger(UnsafeOkHttpClient.class);
    public static OkHttpClient.Builder getUnsafeOkHttpClientBuilder()
    {
        try
        {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            if(!AppConstants.VCLASROOM_SERVICE_URL.contains("https://"))
            {
                logger.debug("Url does not contain https part");
                return builder;
            }


            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException
                        {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            //return new java.security.cert.X509Certificate[]{};
                            return null;
                        }
                    }
            };

//            X509TrustManager x509TrustManager = new X509TrustManager() {
//                @Override
//                public X509Certificate[] getAcceptedIssuers() {
//                    X509Certificate[] x509Certificates = new X509Certificate[0];
//                    return x509Certificates;
//                }
//
//                @Override
//                public void checkServerTrusted(final X509Certificate[] chain,
//                                               final String authType) throws CertificateException {
//                    logger.debug("UnsafeOkHttpClient: authType: " + String.valueOf(authType));
//                }
//
//                @Override
//                public void checkClientTrusted(final X509Certificate[] chain,
//                                               final String authType) throws CertificateException {
//                    logger.debug("UnsafeOkHttpClient: authType: " + String.valueOf(authType));
//                }
//            };
            X509TrustManager[] x509TrustManagers = TrustSelfSignedX509TrustManager.wrap(systemTrustManager());
            // Install the all-trusting trust manager
            //final SSLContext sslContext = SSLContext.getInstance("TLS");
            //sslContext.init(null, trustAllCerts, null);

            // Create an ssl socket factory with our all-trusting manager
            //final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            //MemorizingTrustManager mtm = new MemorizingTrustManager(context);
            //final SSLSocketFactoryCompat sslSocketFactory = new SSLSocketFactoryCompat();
            HostnameVerifier hostnameVerifier = new HostnameVerifier()
            {
                @Override
                public boolean verify(String s, SSLSession sslSession)
                {
                    logger.debug("Hostname "+s+" verified");
                    return true;
                }
            };

            SSLContext sslContext = getSSLContext(null, new TrustManager[]{x509TrustManagers[0]});
            SSLSocketFactory socketFactory = sslContext.getSocketFactory();
            HttpsURLConnection.setDefaultSSLSocketFactory(socketFactory);
            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
            //builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.sslSocketFactory(socketFactory, x509TrustManagers[0]);
            builder.hostnameVerifier(hostnameVerifier);
            builder.connectionSpecs(Arrays.asList(ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.MODERN_TLS));
            return builder;

        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private static SSLContext getSSLContext(KeyManager[] keyManagers, TrustManager[] trustManagers)
    {
        try
        {
            logger.debug("GetSSL Context");
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers,
                    trustManagers,
                    null);
            return sslContext;
        }
        catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new IllegalStateException("Couldn't init TLS context", e);
        }
    }
    public static X509TrustManager systemTrustManager() {
        logger.debug("getSystemTrustManager");
        TrustManager[] trustManagers = systemTrustManagerFactory().getTrustManagers();
        if (trustManagers.length != 1)
        {
            throw new IllegalStateException("Unexpected default trust managers:"
                    + Arrays.toString(trustManagers));
        }
        TrustManager trustManager = trustManagers[0];
        if (trustManager instanceof X509TrustManager) {
            return (X509TrustManager) trustManager;
        }
        throw new IllegalStateException("'" + trustManager + "' is not a X509TrustManager");
    }


    private static TrustManagerFactory systemTrustManagerFactory() {
        try
        {
            logger.debug("get system trust manager factory");
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((KeyStore) null);
            return tmf;
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            throw new IllegalStateException("Can't load default trust manager", e);
        }
    }

    public static OkHttpClient getUnsafeOkHttpClient()
    {
        try
        {

            OkHttpClient.Builder builder = getUnsafeOkHttpClientBuilder();
            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static class TrustSelfSignedX509TrustManager implements X509TrustManager
    {
        private X509TrustManager delegate;
        public TrustSelfSignedX509TrustManager(X509TrustManager delegate)
        {
            this.delegate = delegate;
        }
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
        {
            delegate.checkClientTrusted(chain, authType);
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
        {
            if(isSelfSigned(chain))
                return;
            delegate.checkServerTrusted(chain, authType);
        }


        private boolean isSelfSigned(X509Certificate[] chain) {
            return chain.length == 1;
        }

        @Override
        public X509Certificate[] getAcceptedIssuers()
        {
            return delegate.getAcceptedIssuers();
        }

        public static X509TrustManager[] wrap(X509TrustManager delegate) {
            return new X509TrustManager[]{new TrustSelfSignedX509TrustManager(delegate)};
        }
    }
}
