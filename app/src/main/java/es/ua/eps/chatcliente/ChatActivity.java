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

    private String messageToSend;
    private Button buttonSend;
    private EditText toSend;
    private ListView lv;

    private static List<Bubble> bubbles;
    private BubblesArrayAdapter adapter;

    private ClientThread clientThread;
    private DataOutputStream out;
    private Socket socket;
    private int SERVER_PORT;
    private String SERVER_IP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        Intent intent_received = getIntent();
        SERVER_IP = intent_received.getStringExtra("IP_SERVER");
        SERVER_PORT = Integer.parseInt(intent_received.getStringExtra("PORT"));

        lv = findViewById(android.R.id.list);
        toSend = findViewById(R.id.toSend);
        buttonSend = findViewById(R.id.buttonSend);

        ip();

        lv.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        bubbles = new ArrayList<Bubble>();
        adapter = new BubblesArrayAdapter(this, R.layout.item_bubble_left, bubbles);
        lv.setAdapter(adapter);

        clientThread = new ClientThread();
        clientThread.execute();

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    messageToSend = toSend.getText().toString();

                    new WriteThread().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static List<Bubble> getBubbles() {
        return bubbles;
    }

    private String ip() {
        WifiManager wifiManager;
        String ip;

        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        ip = getIpFormat(wifiManager.getConnectionInfo().getIpAddress());

        Log.d("INFORMATION", "IP Client: " + ip);

        return ip;
    }

    private static String getIpFormat(int code) {
        String result;

        result = String.format("%d.%d.%d.%d", (code & 0xff), (code >> 8 & 0xff), (code >> 16 & 0xff), (code >> 24 & 0xff));

        return result;
    }


    private class ClientThread extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                new ReadThread().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                out = new DataOutputStream(socket.getOutputStream());
                if (socket != null) {
                    out.writeUTF(messageToSend);
                    out.flush();
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
            toSend.setText("");

            Bubble b = new Bubble("You", messageToSend, true);
            bubbles.add(b);
            adapter.notifyDataSetChanged();
        }
    }

    private class ReadThread extends AsyncTask {
        private DataInputStream input;
        private String read;

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                this.input = new DataInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (!socket.isClosed()) {
                try {
                    Log.d("DebugApp", "Leyendo mensajes en el servidor");
                    read = input.readUTF();
                    this.publishProgress();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            String[] r = read.split("\\$");
            Bubble b = new Bubble(r[0], r[1], false);

            bubbles.add(b);
            adapter.notifyDataSetChanged();
        }
    }
}
