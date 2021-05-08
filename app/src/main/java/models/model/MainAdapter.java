package models.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import eina.unizar.front_end_movil.R;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> implements View.OnClickListener {

    ArrayList<MainModel> mainModels;
    Context context;
    private View.OnClickListener listener;

    public MainAdapter(Context context, ArrayList<MainModel> mainModels) {
        this.mainModels = mainModels;
        this.context = context;
    }

    @NonNull
    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rows_objetos_tienda,
                parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, int position) {
        //holder.foto.setImageBitmap(mainModels.get(position).getFoto());
        Picasso.get().load(mainModels.get(position).getFoto()).into(holder.foto);
        holder.nombre.setText(mainModels.get(position).getNombre());
        if(mainModels.get(position).getPrecio() != 0){
            holder.precio.setText(mainModels.get(position).getPrecio().toString());
        }
    }

    @Override
    public int getItemCount() {
        return mainModels.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if(listener!=null){
            listener.onClick(v);
        }
    }

    public class ViewHolder  extends RecyclerView.ViewHolder{

        ImageView foto;
        TextView nombre;
        TextView precio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.foto_objeto);
            nombre = itemView.findViewById(R.id.nombre_objeto);
            precio = itemView.findViewById(R.id.precio_objeto);
        }
    }
}