package Avatar;

public class ItemAvatar {

    private final String nombre;
    private final String tipo;
    private final String imagen;
    private final String idItem;
    private boolean IsEquipped;

    public ItemAvatar(String nombre, String tipo, String imagen, boolean isEquipped, String id) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.imagen = imagen;
        this.IsEquipped = isEquipped;
        this.idItem = id;
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
    public void negateEquipped () { this.IsEquipped = !this.IsEquipped;}
    public void setEquippedTrue () { this.IsEquipped = true;}
    public boolean isEquipped() { return IsEquipped; }
    public String getIdItem() { return  idItem;}
}
