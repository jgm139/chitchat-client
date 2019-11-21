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
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatActivity extends AppCompatActivity {

    private Socket socket;
    private String messageToSend;
    private ClientThread clientThread;
    private Button buttonSend;
    private EditText toSend;
    private TextView containerMessages;
    private DataOutputStream out;

    private int SERVER_PORT;
    private String SERVER_IP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_activity);

        Intent intent_received = getIntent();

        SERVER_IP = intent_received.getStringExtra("IP_SERVER");
        SERVER_PORT = Integer.parseInt(intent_received.getStringExtra("PORT"));

        ipserver();

        this.clientThread = new ClientThread();
        this.clientThread.execute();

        this.toSend = findViewById(R.id.toSend);
        this.buttonSend = findViewById(R.id.buttonSend);
        this.containerMessages = findViewById(R.id.containerMessages);

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

    private void ipserver() {
        WifiManager wifiManager;
        String ip;

        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        ip = getIpFormat(wifiManager.getConnectionInfo().getIpAddress());

        Log.d("INFORMATION", "IP Client: " + ip);
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
            containerMessages.append(read + "\n");
        }
    }
}
