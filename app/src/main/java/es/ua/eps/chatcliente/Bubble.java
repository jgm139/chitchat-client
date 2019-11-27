package es.ua.eps.chatcliente;

import android.graphics.Color;
import java.util.Random;

public class Bubble {
    public String uID;
    public String uLine;
    public boolean myMessage;
    public int uIcon;
    public int uIconColour;

    public Bubble (String id, String line, boolean myMessage) {
        this.uID = id;
        this.uLine = line;
        this.myMessage = myMessage;
    }

    public void setuIcon(int uIcon) {
        Random rnd = new Random();
        this.uIconColour = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }
}
