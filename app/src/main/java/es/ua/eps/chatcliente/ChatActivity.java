package es.ua.eps.chatcliente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private String messageToSend; //mensaje que envía el cliente
    private Button buttonSend; // botón para enviar mensaje
    private EditText toSend; //vista donde se escribe el mensaje
    private ListView lv; //vista donde se visualizan todos los mensajes

    private static List<Bubble> bubbles; //lista donde se almacenan los mensajes
    private BubblesArrayAdapter adapter; //adaptador que da formato a cada elemento de la lista

    private ClientThread clientThread; //hilo de lectura de mensajes del cliente
    private DataOutputStream out; //flujo de datos de salida
    private Socket socket; //socket con el que conectarse con el servidor
    private int SERVER_PORT; //puerto que usará el socket para conextarse
    private String SERVER_IP; //IP del servidor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity); //cargamos el layout del activity

        Intent intent_received = getIntent(); //obtenemos el intent que trajo este activity
        SERVER_IP = intent_received.getStringExtra("IP_SERVER"); //obtenemos la IP del servidor de los extras del intent
        SERVER_PORT = Integer.parseInt(intent_received.getStringExtra("PORT")); //obtenemos el puerto de los extras del intent

        lv = findViewById(android.R.id.list); //instanciamos la vista de la lista
        toSend = findViewById(R.id.toSend); //instanciamos la vista del editText
        buttonSend = findViewById(R.id.buttonSend); //instanciamos el botón

        ip(); //obtenemos la IP del cliente

        lv.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL); //hace un scroll al final de la lista para ver un nuevo elemento añadido
        bubbles = new ArrayList<Bubble>(); //inicializamos el array de mensajes
        adapter = new BubblesArrayAdapter(this, R.layout.item_bubble_left, bubbles); //inicializamos el adaptador de la lista con su layout
        lv.setAdapter(adapter); //asignamos el adaptador a la lista

        clientThread = new ClientThread(); //instanciamos el hilo del cliente
        clientThread.execute(); //ejecutamos el hilo del cliente

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    messageToSend = toSend.getText().toString(); //recogemos el mensaje escrito por el usuario

                    new WriteThread().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR); //forzamos la ejecución de un segundo hilo para enviar el mensaje
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            out.close(); //cerramos el envío de datos
            socket.close(); //cerramos el socket
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static List<Bubble> getBubbles() {
        return bubbles;
    } //devuelve la lista de mensajes

    private String ip() { //obtiene la ip del cliente
        WifiManager wifiManager;
        String ip;

        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        ip = getIpFormat(wifiManager.getConnectionInfo().getIpAddress());

        Log.d("INFORMATION", "IP Client: " + ip);

        return ip;
    }

    private static String getIpFormat(int code) { //formateamos la IP
        String result;

        result = String.format("%d.%d.%d.%d", (code & 0xff), (code >> 8 & 0xff), (code >> 16 & 0xff), (code >> 24 & 0xff));

        return result;
    }


    private class ClientThread extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT); //instanciamos el socket con la IP del servidor y el puerto
                new ReadThread().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR); //forzamos la ejecución de un asynctask para la lectura de mensajes recibidos
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }
    }

    private class WriteThread extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                Log.d("DebugApp", "Escribiendo nuevo mensaje en el chat");
                out = new DataOutputStream(socket.getOutputStream()); //instanciamos un flujo de datos de salida para el socket
                if (socket != null) {
                    out.writeUTF(messageToSend); //escribimos en el flujo el mensaje a enviar
                    out.flush(); //enviamos el mensaje
                }
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            toSend.setText(""); //vaciamos el texto

            Bubble b = new Bubble("You", messageToSend, true); //creamos una intancia del objeto Bubble indicando
                                                                                //que el cliente ha ennviado un mensaje
            bubbles.add(b); //añadimos la burbuja del mensaje a la lista de mensajes
            adapter.notifyDataSetChanged(); //indicamos un cambio en el ListView
        }
    }

    private class ReadThread extends AsyncTask {
        private DataInputStream input; //es el flujo de datos de entrada
        private String read; //es el mensaje que ha llegado

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                this.input = new DataInputStream(socket.getInputStream()); //instanciamos el flujo de datos a través del socket
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (!socket.isClosed()) { //mientras el socket esté abierto
                try {
                    Log.d("DebugApp", "Leyendo mensajes en el servidor");
                    read = input.readUTF(); //leemos un mensaje del flujo de datos de entrada
                    this.publishProgress(); //indicamos que se ha producido un cambio en la interfaz
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            String[] r = read.split("\\$"); //formateamos el mensaje para sacar la IP del cliente y su mensaje
            Bubble b = new Bubble(r[0], r[1], false); //instanciamos un Bubble para el mensaje de otro cliente

            bubbles.add(b); //añadimos el mensaje a la lista de mensajes
            adapter.notifyDataSetChanged(); //indicamos que se ha producido un cambio en la lista
        }
    }
}
