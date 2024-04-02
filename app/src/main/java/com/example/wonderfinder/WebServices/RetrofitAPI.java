package com.example.wonderfinder.WebServices;

import com.example.wonderfinder.Model.DirectionLandmarkModel.DirectionResponseModel;
import com.example.wonderfinder.Model.GoogleLandmarkModel.GoogleResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RetrofitAPI {

    //Call get request of given url and get google person
    @GET
    Call<GoogleResponseModel> getNearByPlaces(@Url String url);

    @GET
    Call<DirectionResponseModel> getDirection(@Url String url);

}
