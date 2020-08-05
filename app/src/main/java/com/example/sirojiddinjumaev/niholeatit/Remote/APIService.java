package com.example.sirojiddinjumaev.niholeatit.Remote;


import com.example.sirojiddinjumaev.niholeatit.Model.DataMessage;
import com.example.sirojiddinjumaev.niholeatit.Model.MyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService{

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAZzJ-yMM:APA91bFfvqfb1vvwUJdyG_JdIWAHalhfU7fzJoiwkgh8j2hGp790gcndxfe0SU66yilVbv6e5bN12562Y9v6TBHkcBCXLuc0cJeoPg5njK9x1WHSvilDEllI6wEmuQ_EuZEsCwscy5nh"
            }
    )

    @POST("fcm/send")

    Call<MyResponse> sendNotification(@Body DataMessage body);




}
