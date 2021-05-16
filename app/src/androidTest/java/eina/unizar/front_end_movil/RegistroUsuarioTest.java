package eina.unizar.front_end_movil;

import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.gson.JsonObject;

import java.util.HashMap;

import database_wrapper.APIUtils;
import database_wrapper.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroUsuarioTest {

    private final String nombre = "Fsáñ8";
    private final static String email = "email@gmail.com";
    private final String password_1 = "Fsáñ!0";
    private final String password_2 = "Fsáñ!0";
    private final String error_message = "El campo no puede estar vacío";
    private static boolean notIgnored = false;
    private static final RetrofitInterface retrofitInterface =  APIUtils.getAPIService();


    @Rule
    public ActivityScenarioRule<RegistroUsuario> activityRule = new ActivityScenarioRule<>(RegistroUsuario.class);

    @Test //Test para comprobrar que al hacer click en confirmar si el nombre del usuario es vacio da un mensaje de error.
    public void test_nombre_vacio(){
        onView(withId(R.id.nombre_usuario)).perform(typeText(""),closeSoftKeyboard());
        onView(withId(R.id.confirmar)).perform(click());
        onView(withId(R.id.nombre_usuario)).check(matches(hasErrorText(error_message)));
    }

    @Test //Test para comprobrar que al hacer click en confirmar, si el nombre del email es vacio, da un mensaje de error.
    public void test_email_vacio(){

        onView(withId(R.id.nombre_usuario)).perform(replaceText(nombre),closeSoftKeyboard());
        onView(withId(R.id.email)).perform(typeText(""),closeSoftKeyboard());
        onView(withId(R.id.confirmar)).perform(click());
        onView(withId(R.id.email)).check(matches(hasErrorText(error_message)));
    }

    @Test //Test para comprobrar que al hacer click en confirmar, si el nombre de password_1 es vacio, da un mensaje de error.
    public void test_password_1_vacio(){

        onView(withId(R.id.nombre_usuario)).perform(replaceText(nombre),closeSoftKeyboard());
        onView(withId(R.id.email)).perform(replaceText(email),closeSoftKeyboard());
        onView(withId(R.id.password_new)).perform(typeText(""),closeSoftKeyboard());
        onView(withId(R.id.confirmar)).perform(click());
        onView(withId(R.id.password_new)).check(matches(hasErrorText(error_message)));
    }

    @Test //Test para comprobrar que al hacer click en confirmar si el nombre de password_2 es vacio da un mensaje de error.
    public void test_password_2_vacio(){

        onView(withId(R.id.nombre_usuario)).perform(replaceText(nombre),closeSoftKeyboard());
        onView(withId(R.id.email)).perform(replaceText(email),closeSoftKeyboard());
        onView(withId(R.id.password_new)).perform(replaceText(password_1),closeSoftKeyboard());
        onView(withId(R.id.password_new2)).perform(typeText(""),closeSoftKeyboard());
        onView(withId(R.id.confirmar)).perform(click());
        onView(withId(R.id.password_new2)).check(matches(hasErrorText(error_message)));
    }

    @Test //Test para comprobrar que al hacer click en confirmar, si el nombre del email es vacio, da un mensaje de error.
    public void test_contrasenyas_no_coinciden(){

        onView(withId(R.id.nombre_usuario)).perform(replaceText(nombre),closeSoftKeyboard());
        onView(withId(R.id.email)).perform(replaceText(email),closeSoftKeyboard());
        onView(withId(R.id.password_new)).perform(replaceText(password_1),closeSoftKeyboard());
        onView(withId(R.id.password_new2)).perform(replaceText(password_2 + "hola"),closeSoftKeyboard());
        onView(withId(R.id.confirmar)).perform(click());
        onView(withId(R.id.password_new2)).check(matches(hasErrorText("Las contraseñas no coinciden")));
    }

    @Test //Test para comprobrar que al hacer click en confirmar, si el nombre del email es vacio, da un mensaje de error.
    public void test_email_no_valido(){

        final String expectedError = "El email es invalido, introduzca un email valido por ejemplo: pedro@gmail.com";

        onView(withId(R.id.nombre_usuario)).perform(replaceText(nombre),closeSoftKeyboard());
        onView(withId(R.id.email)).perform(typeText("pedro.com"),closeSoftKeyboard());
        onView(withId(R.id.password_new)).perform(replaceText(password_1),closeSoftKeyboard());
        onView(withId(R.id.password_new2)).perform(replaceText(password_2),closeSoftKeyboard());
        onView(withId(R.id.confirmar)).perform(click());
        onView(withId(R.id.email)).check(matches(hasErrorText(expectedError)));
    }

    @Ignore("Asegurarse de lanzar la API sino el test fallara, si está lanzada comenta esta línea.")
    @Test //Test para comprobrar que al hacer click en confirmar el usuario se registra exitosamente.
    public void test_registro_exitoso(){

        notIgnored = true;
        onView(withId(R.id.nombre_usuario)).perform(replaceText(nombre),closeSoftKeyboard());
        onView(withId(R.id.email)).perform(replaceText(email),closeSoftKeyboard());
        onView(withId(R.id.password_new)).perform(replaceText(password_1),closeSoftKeyboard());
        onView(withId(R.id.password_new2)).perform(replaceText(password_2),closeSoftKeyboard());
        onView(withId(R.id.confirmar)).perform(click());
        onView(withText("Vale"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
    }


    @AfterClass
    public static void tearDown(){
        if(notIgnored) {
            eliminarUsuario();
        }
    }

    private static void eliminarUsuario(){
        HashMap<String, String> dropUser = new HashMap<>();
        dropUser.put("email", email);
        Call<JsonObject> call = retrofitInterface.executeDropUser(dropUser);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }
}