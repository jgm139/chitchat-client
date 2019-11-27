package es.ua.eps.chatcliente;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class BubblesArrayAdapter extends ArrayAdapter {
    private Context context; //contexto de la clase que lo va a instanciar

    private static class ViewHolder { //elementos que tiene cada celda de la lista
        private TextView userID; //etiqueta del ID del usuario
        private TextView userLine; //etiqueta para el mensaje
        private ImageView userIcon;
    }

    public BubblesArrayAdapter(Context context, int resource, List objects) { //constructor para inicializar los atributos de la clase
        super(context, resource, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Bubble b = (Bubble) getItem(position); //obtenemos el objeto Bubble
        ViewHolder viewHolder = new ViewHolder(); //instanciamos el viewHolder


        if(getItemViewType(position) == 0) { //Cargamos el layout para los mensajes del propio cliente
            convertView = LayoutInflater.from(context).inflate(R.layout.item_bubble_right, null); //cargamos la vista de cada celda
            //inicializamos las vistas del viewHolder según el convertView que tengamos
            viewHolder.userID = convertView.findViewById(R.id.uID);
            viewHolder.userLine = convertView.findViewById(R.id.uLine);
            viewHolder.userIcon = convertView.findViewById(R.id.uIcon);

            convertView.setTag(viewHolder); //asignamos la vista con el viewHolder

            //les damos valor a las vistas del holder con los datos de la lista de mensajes
            viewHolder.userID.setText(b.uID);
            viewHolder.userLine.setText(b.uLine);


        } else { //Cargamos el otro layout para los mensajes de otros clientes
            convertView = LayoutInflater.from(context).inflate(R.layout.item_bubble_left, null);
            viewHolder.userID = convertView.findViewById(R.id.uID);
            viewHolder.userLine = convertView.findViewById(R.id.uLine);
            viewHolder.userIcon = convertView.findViewById(R.id.uIcon);

            convertView.setTag(viewHolder);

            viewHolder.userID.setText(b.uID);
            viewHolder.userLine.setText(b.uLine);
        }

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        //devuelve la cantidad de diferentes de vistas que puede tener el ArrayAdapter
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        //En este método comprobamos si el nuevo mensaje que se va a añadir al ListView
        //debe "inflar" un layout u otro
        //si el mensaje es del propio cliente devuelve 0
        if(ChatActivity.getBubbles().get(position).myMessage) {
            return 0;
        }
        //si es de otro cliente devuelve 1
        return 1;
    }
}
