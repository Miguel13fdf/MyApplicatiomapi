package com.example.myapplicatiomapi.Interfaz;

import com.example.myapplicatiomapi.modelo.Posts;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface Jsonpaceahollder {
    @GET("posts")
    Call<List<Posts>> getPost();
    @POST("posts")
    Call<Posts> createPost(@Body Posts post);
    @PUT("posts/{id}")
    Call<Posts> updatePost(@Path("id") int postId, @Body Posts post);
    @DELETE("posts/{id}")
    Call<Void> deletePost(@Path("id") int postId);
    @GET("posts/{id}")
    Call<Posts> getPostById(@Path("id") int postId);
}