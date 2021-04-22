package Avatar;


import android.graphics.Canvas;

import java.util.Vector;

public interface DrawAVMethods {

    Vector<DrawAVMethods> listaItemsEquipados = new Vector<>();
    Canvas onUpdateItem(Canvas canvas);
    Canvas onRemoveItem(Canvas canvas);
}
