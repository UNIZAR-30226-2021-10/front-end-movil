package database_wrapper;

public class APIUtils {

        //URL para API
        private static String BASE_URL = "https://trivial-db.herokuapp.com/";            //URL para acceder a la API en la nube
        private static String URL_LOCAL = "http://10.0.2.2:3050";                        //URL para acceder en local
        //URL para servidor de imagenes
        private static String URL_LOCAL_IMAGES = "http://10.0.2.2:3060";                //local
        private static String URL_IMAGENES = "https://trivial-images.herokuapp.com/";   //cloud

        private void ApiUtils() {}

        public static RetrofitInterface getAPIService() {
            return ClienteRetrofit.getClient(URL_LOCAL).create(RetrofitInterface.class);
        }

        public static RetrofitInterface getAPIServiceImages() {
                return ClienteRetrofit.getClientImages(URL_LOCAL_IMAGES).create(RetrofitInterface.class);
        }


}
