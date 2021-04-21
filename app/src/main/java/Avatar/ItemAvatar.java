package Avatar;

public class ItemAvatar {

    private final String nombre;
    private final String tipo;
    private final String imagen;

    public ItemAvatar(String nombre, String tipo, String imagen) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.imagen = imagen;
    }


    public String getNombre() {
        return nombre;
    }


    public String getTipo() {
        return tipo;
    }

    public String getImagen() {
        return imagen;
    }
}
