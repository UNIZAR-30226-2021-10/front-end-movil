package Avatar;


import java.util.Vector;

public interface DrawAVMethods {

    Vector<DrawAVMethods> listaItemsEquipados = new Vector<>();

    void onPaint();
    void onUpdateItem();
    void onRemoveItem();
    void onClearCanvas();
}
