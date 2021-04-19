package models.model;

public class MainModel {
    String foto;
    String nombre;
    Integer precio;

    public MainModel(String foto, String nombre, Integer precio){
        this.foto = foto;
        this.nombre = nombre;
        this.precio = precio;
    }

    public String getFoto() {
        return foto;
    }

    public String getNombre() {
        return nombre;
    }

    public Integer getPrecio() {
        return precio;
    }
}
