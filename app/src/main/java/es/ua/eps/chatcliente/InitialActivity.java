package es.ua.eps.chatcliente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class InitialActivity extends AppCompatActivity {
    private Button buttonConnect;
    private EditText editIP;
    private EditText editPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        buttonConnect = findViewById(R.id.buttonConnect);
        editIP = findViewById(R.id.editIP);
        editPort = findViewById(R.id.editPort);

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialActivity.this, ChatActivity.class);
                intent.putExtra("IP_SERVER", editIP.getText().toString());
                intent.putExtra("PORT", editPort.getText().toString());
                startActivity(intent);
            }
        });
    }
}
