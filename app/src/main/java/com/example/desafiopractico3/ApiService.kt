package com.example.desafiopractico3
import retrofit2.*
import retrofit2.http.*


interface ApiService {

    @GET("to-do")
    suspend fun getToDos(): Response<List<Todo>>

    @GET("to-do")
    suspend fun getToDosByUser(@Query("createdBy") userId: String): Response<List<Todo>>

    @GET("to-do/{id}")
    suspend fun getTodoById(@Path("id") id: String): Response<Todo>

    @POST("to-do")
    suspend fun createTodo(@Body todo: Todo): Response<Todo>

    @PUT("to-do/{id}")
    suspend fun updateTodo(@Path("id") id: String, @Body todo: Todo): Response<Todo>

    @DELETE("to-do/{id}")
    suspend fun deleteTodo(@Path("id") id: String): Response<Unit>
}