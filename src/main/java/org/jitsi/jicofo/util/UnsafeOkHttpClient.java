package org.jitsi.jicofo.util;

import okhttp3.OkHttpClient;
import org.jitsi.jicofo.JitsiMeetConferenceImpl;
import org.jitsi.utils.logging.Logger;

import javax.net.ssl.*;
import java.security.cert.CertificateException;

public class UnsafeOkHttpClient {
    private final static Logger logger
            = Logger.getLogger(UnsafeOkHttpClient.class);
    public static OkHttpClient.Builder getUnsafeOkHttpClientBuilder(OkHttpClient.Builder builder)
    {
        try
        {
            if(!AppConstants.VCLASROOM_SERVICE_URL.contains("https://"))
                return builder;
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
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            builder.sslSocketFactory(sslContext.getSocketFactory());

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            //MemorizingTrustManager mtm = new MemorizingTrustManager(context);
            //final SSLSocketFactoryCompat sslSocketFactory = new SSLSocketFactoryCompat();

            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    logger.debug("Hostname "+hostname+" verified");
                    return true;
                }
            });

            return builder;

        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    public static OkHttpClient getUnsafeOkHttpClient()
    {
        try
        {

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder = getUnsafeOkHttpClientBuilder(builder);
            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
