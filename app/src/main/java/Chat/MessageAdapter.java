package Chat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import eina.unizar.front_end_movil.R;

public class MessageAdapter extends BaseAdapter {

    List<Mensaje> messages = new ArrayList<Mensaje>();
    Context context;

    public MessageAdapter(Context context) {
        this.context = context;
    }

    public void add(Mensaje message) {
        this.messages.add(message);
        notifyDataSetChanged(); // to render the list we need to notify
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    // This is the backbone of the class, it handles the creation of single ListView row (chat bubble)
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Mensaje message = messages.get(i);

        if (message.isBelongsToCurrentUser()) { // this message was sent by us so let's create a basic chat bubble on the right
            convertView = messageInflater.inflate(R.layout.my_message, null);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);
            holder.messageBody.setText(message.getMensaje());
        } else if(!message.isBelongsToCurrentUser() && !message.isAdmin()){ // this message was sent by someone else so let's create an advanced chat bubble on the left
            convertView = messageInflater.inflate(R.layout.their_message, null);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);
            holder.name.setText(message.getUsuario());
            holder.messageBody.setText(message.getMensaje());
            cargarImagenUusario(message.getImagen(),holder.avatar);
        } else{
            convertView = messageInflater.inflate(R.layout.admin_message, null);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);
            holder.messageBody.setText(message.getMensaje());
        }

        return convertView;
    }

    private void cargarImagenUusario(String url, ImageView perfilButton) {
        Picasso.get().load(url).fit()
                .error(R.drawable.ic_baseline_error_24)
                .placeholder(R.drawable.animacion_carga)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(perfilButton);
    }

}

class MessageViewHolder {
    public ImageView avatar;
    public TextView name;
    public TextView messageBody;
}