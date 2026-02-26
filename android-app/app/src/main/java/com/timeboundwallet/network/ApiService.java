package com.timeboundwallet.network;

import com.timeboundwallet.models.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("users")
    Call<UserResponse> createUser(@Body UserCreateRequest request);

    @GET("users")
    Call<List<UserResponse>> getAllUsers();

    @GET("users/{id}")
    Call<UserResponse> getUserById(@Path("id") Long userId);

    @GET("users/search")
    Call<UserResponse> getUserByEmailAndName(@Query("email") String email, @Query("name") String name);

    @retrofit2.http.PUT("users/{id}")
    Call<UserResponse> updateUser(@Header("X-USER-ID") Long loggedInUserId,
                                  @Path("id") Long userId,
                                  @Body UserUpdateRequest request);

    @POST("wallet")
    Call<WalletResponse> createWallet(@Body WalletCreateRequest request);

    @GET("wallet/{userId}/balance")
    Call<WalletResponse> getWalletBalance(@Path("userId") Long userId);

    @retrofit2.http.PUT("wallet/{userId}/max-limit")
    Call<WalletResponse> updateWalletLimit(@Path("userId") Long userId, @Body WalletLimitUpdateRequest request);

    @POST("wallet/{userId}/add")
    Call<WalletResponse> addMoney(@Path("userId") Long userId, @Body AddMoneyRequest request);

    @POST("wallet/transfer")
    Call<ApiMessageResponse> transfer(@Body TransferRequest request);

    @GET("wallet/{walletId}/transactions")
    Call<List<WalletTransactionResponse>> getTransactions(@Path("walletId") Long walletId);
}
