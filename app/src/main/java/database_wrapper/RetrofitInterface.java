package database_wrapper;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Body;

public interface RetrofitInterface {

    @POST("/Registrarse")
    Call<Void> executeSignUp(@Body HashMap<String,String> map);

}
