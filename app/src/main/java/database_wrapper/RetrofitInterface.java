package database_wrapper;

import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Body;

public interface RetrofitInterface {

    @POST("/Registrarse")
    Call<JsonObject> executeSignUp(@Body HashMap<String,String> map);

    @POST("/MenuInicio")
    Call<JsonObject> executeLogin(@Body HashMap<String,String> map);
}
