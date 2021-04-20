package database_wrapper;

public class APIUtils {

        //URL para acceder a la API en la nube
        private static String BASE_URL = "https://trivial-db.herokuapp.com/";
        //URL para acceder en local
        private static String URL_LOCAL = "http://10.0.2.2:3050";
        private static String URL_IMAGENES = "https://trivial-images.herokuapp.com/";

        private void ApiUtils() {}

        public static RetrofitInterface getAPIService() {
            return ClienteRetrofit.getClient(BASE_URL).create(RetrofitInterface.class);
        }

        public static RetrofitInterface getAPIServiceImages() {
                return ClienteRetrofit.getClient(URL_IMAGENES).create(RetrofitInterface.class);
        }


}
