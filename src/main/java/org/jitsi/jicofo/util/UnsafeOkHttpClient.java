package org.jitsi.jicofo.util;

import okhttp3.OkHttpClient;
import org.jitsi.jicofo.JitsiMeetConferenceImpl;
import org.jitsi.utils.logging.Logger;

import javax.net.ssl.*;
import java.security.SecureRandom;
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
