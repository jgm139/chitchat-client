package es.ua.eps.chatcliente;

import android.graphics.Color;
import java.util.Random;

public class Bubble {
    public String uID; //identificador del dueño del mensaje
    public String uLine; //el mensaje
    public boolean myMessage; //indica si el mensaje es del propio cliente o de otro
    public int uIcon;
    public int uIconColour;

    public Bubble (String id, String line, boolean myMessage) { //constructor para inicializar variables
        this.uID = id;
        this.uLine = line;
        this.myMessage = myMessage;
    }

    public void setuIcon(int uIcon) {
        Random rnd = new Random();
        this.uIconColour = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }
}
