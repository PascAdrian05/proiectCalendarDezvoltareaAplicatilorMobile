package com.example.proiectcalendarumfst.ConectareBackEnd;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @POST("/api/auth/register")
    Call<DtoResponseUtilizator> register(@Body DTOutilizatori utilizator);


    @POST("/api/auth/logare")
    Call<DtoResponseUtilizator> login(@Body DTOutilizatori utilizator);


    @GET("/eveniment")
    Call<List<DtoEveniment>> evenimenteleUtilizatorului(@Header("Authorization")String token);

    @POST("/eveniment")
    Call<DtoEveniment> adaugareEveniment(@Body DtoEveniment dto,@Header("Authorization")String token);

    @DELETE("/eveniment/{id}")
    Call<Void> stergereEveniment(@Header("Authorization")String token,@Path("id") int id);

    // În ApiService.java
    @PUT("/eveniment/{id}")
    Call<DtoEveniment> updateEveniment(
            @Header("Authorization") String token,
            @Path("id") int id,
            @Body DtoEveniment dto
    );
}
