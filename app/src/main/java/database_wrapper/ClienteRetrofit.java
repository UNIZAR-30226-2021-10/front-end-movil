package database_wrapper;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClienteRetrofit {

    private static  Retrofit retrofit = null;

    /**
     * Función que nos devuelve el objeto retrofit en caso de que no haya sido inicializado
     * @param baseUrl URL de la API con la que nos conectaremos
     * @return
     */
    public static Retrofit getClient(String baseUrl) {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
