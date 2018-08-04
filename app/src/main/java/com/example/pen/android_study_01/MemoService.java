package com.example.pen.android_study_01;

import com.example.pen.android_study_01.VO.BackupRequest;
import com.example.pen.android_study_01.VO.BaseResult;
import com.example.pen.android_study_01.VO.GetBackupResult;
import com.example.pen.android_study_01.VO.TokenResult;
import com.example.pen.android_study_01.VO.ValidResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MemoService {
    String API_URL = "https://memobackup.herokuapp.com";

    @POST("createToken")
    Call<TokenResult> createToken();

    @GET("checkToken")
    Call<ValidResult> checkToken(@Query("token") String token);

    @POST("backup")
    Call<BaseResult> backup(@Body BackupRequest body);

    @GET("backup")
    Call<GetBackupResult> getBackup(@Query("token") String token);
}
