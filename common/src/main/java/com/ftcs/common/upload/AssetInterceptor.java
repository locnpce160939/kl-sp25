package com.ftcs.common.upload;


import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
public class AssetInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        HttpUrl originalUrl = originalRequest.url();

        // Log original request details
        log.debug("Original URL: {}", originalUrl);

        // Insert /asset/ as the first segment after the host
        HttpUrl modifiedUrl = originalUrl.newBuilder()
                .encodedPath(originalUrl.encodedPath())
                .build();

        // Create a new request with the modified URL
        Request modifiedRequest = originalRequest.newBuilder()
                .url(modifiedUrl)
                .build();

        // log.info("Modified Request URL: {}", modifiedRequest.url());

        // Execute the request
        Response response = chain.proceed(modifiedRequest);

        // log.debug("Response Status: {}", response.code());

        if (!response.isSuccessful()) {
            log.error("Request failed with status: {}", response.code());
        }

        return response;
    }

}
