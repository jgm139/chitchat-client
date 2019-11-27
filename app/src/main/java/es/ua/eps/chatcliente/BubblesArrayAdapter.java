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
    private Context context;

    private static class ViewHolder {
        private TextView userID;
        private TextView userLine;
        private ImageView userIcon;
    }

    public BubblesArrayAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Bubble b = (Bubble) getItem(position);
        ViewHolder viewHolder = new ViewHolder();


        if(getItemViewType(position) == 0) { //You
            convertView = LayoutInflater.from(context).inflate(R.layout.item_bubble_right, null);
            viewHolder.userID = convertView.findViewById(R.id.uID);
            viewHolder.userLine = convertView.findViewById(R.id.uLine);
            viewHolder.userIcon = convertView.findViewById(R.id.uIcon);

            convertView.setTag(viewHolder);

            viewHolder.userID.setText(b.uID);
            viewHolder.userLine.setText(b.uLine);


        } else { //Someone else
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
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if(ChatActivity.getBubbles().get(position).myMessage) {
            return 0;
        }

        return 1;
    }
}
