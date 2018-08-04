package com.example.pen.android_study_01;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkHelper {

    private static NetworkHelper instance = null;
    public static NetworkHelper getInstance(){
        if(instance == null)
            instance = new NetworkHelper();
        //이렇게 하면 instance 객체를 새로 만들거나 (없는경우)
        //null이 아니면 이미 만들어져있는 인스턴스를 반환하게 된다.
        return instance;
    }

    public Retrofit retrofit;
    public MemoService memoService;

    //기본생성자도 private으로 만들어서 오직 getInstance()를 통해서만 객체 생성하게 함.
    private NetworkHelper(){
        retrofit = new Retrofit.Builder()
                .baseUrl(MemoService.API_URL)  //baseURL
                .addConverterFactory(GsonConverterFactory.create()) //JSON 컨버터
                .build();

        memoService = retrofit.create(MemoService.class);
    }


}
