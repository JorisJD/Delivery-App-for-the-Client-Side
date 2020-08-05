package com.example.sirojiddinjumaev.niholeatit.Remote;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class GoogleRetrofitClient {
    private static Retrofit retrofit2=null;
    public static Retrofit getGoogleClient(String baseURL)
    {
        if (retrofit2 == null)
        {
            retrofit2 = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit2;
    }
}
