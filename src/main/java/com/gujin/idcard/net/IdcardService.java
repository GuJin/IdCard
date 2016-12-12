package com.gujin.idcard.net;

import com.gujin.idcard.model.LeakBean;
import com.gujin.idcard.model.LossBean;
import com.gujin.idcard.model.SearchBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IdcardService {
    @GET("index")
    Call<SearchBean> search(@Query("key") String key, @Query("cardno") String cardNo, @Query("dtype") String dtype);

    @GET("leak")
    Call<LeakBean> leak(@Query("key") String key, @Query("cardno") String cardNo, @Query("dtype") String dtype);

    @GET("loss")
    Call<LossBean> loss(@Query("key") String key, @Query("cardno") String cardNo, @Query("dtype") String dtype);
}