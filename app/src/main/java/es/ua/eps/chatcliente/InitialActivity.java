package es.ua.eps.chatcliente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class InitialActivity extends AppCompatActivity {
    private Button buttonConnect; //botón para entrar en la sala
    private EditText editIP; //EditText para introducir la IP del servidor
    private EditText editPort; //EditText para introducir el puerto de conexión

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial); //cargamos el layout del activity

        //Instanciamos las vistas del acivity
        buttonConnect = findViewById(R.id.buttonConnect);
        editIP = findViewById(R.id.editIP);
        editPort = findViewById(R.id.editPort);

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //A través de un Intent lanzamos el activity del chat y le pasamos los datos del servidor
                Intent intent = new Intent(InitialActivity.this, ChatActivity.class);
                intent.putExtra("IP_SERVER", editIP.getText().toString());
                intent.putExtra("PORT", editPort.getText().toString());
                startActivity(intent);
            }
        });
    }
}
