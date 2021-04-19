package database_wrapper;

public class APIUtils {

        //URL para acceder a la API en la nube
        private static String BASE_URL = "https://trivial-db.herokuapp.com/";
        //URL para acceder en local
        private static String URL_LOCAL = "http://10.0.2.2:3050";
        private static String URL_IMAGENES = "http://10.0.2.2:3060";

        private void ApiUtils() {}

        public static RetrofitInterface getAPIService() {
            return ClienteRetrofit.getClient(URL_LOCAL).create(RetrofitInterface.class);
        }

}
