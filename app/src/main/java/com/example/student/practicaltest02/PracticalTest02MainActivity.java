package com.example.student.practicaltest02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {

    EditText portEditText;
    EditText addressEditText;
    EditText commandEditText;

    Button connectButton;
    Button executeButton;

    TextView responseTextView;

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;

    class ConnectClickLsn implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            serverThread = new ServerThread(7000);
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }
    }

    class ExecuteClickLsn implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String clientAddress = addressEditText.getText().toString();
            String clientPort = portEditText.getText().toString();
            if (clientAddress == null || clientAddress.isEmpty()
                    || clientPort == null || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }
            String argument = commandEditText.getText().toString();
            String commandType = commandSelectSpinner.getSelectedItem().toString();

            responseTextView.setText("");
            clientThread = new ClientThread(clientAddress, Integer.valueOf(clientPort), argument, commandType, responseTextView);
            clientThread.start();

        }
    }

    Spinner commandSelectSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        portEditText = findViewById(R.id.id_port_edit_text);
        addressEditText = findViewById(R.id.id_address_edit_text);
        commandEditText = findViewById(R.id.id_command_arg_edit_text);

        connectButton = findViewById(R.id.id_button_connect);
        executeButton = findViewById(R.id.id_button_execute);

        responseTextView = findViewById(R.id.response_edit_text);

        commandSelectSpinner = findViewById(R.id.information_type_spinner);

        connectButton.setOnClickListener(new ConnectClickLsn());
        executeButton.setOnClickListener(new ExecuteClickLsn());
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}
