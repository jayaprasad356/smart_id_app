package com.app.fortuneapp.chat.fcm;



import com.app.fortuneapp.chat.fcmmodels.MyResponse;
import com.app.fortuneapp.chat.fcmmodels.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization: key=AAAAdmIkaQE:APA91bHwF4LxBdtNtX4EXEnE6lUs6BCzO_Q3T4nAzuB1qIC7bhYr4SUUlOp1uYv0cI5DVcFZbuvXITF8GsRDngu30SnGTAVnv1lgBk2OZoXoOqrZJ7OrOHdPt3x5JewZiaobgaW0XOaI"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
