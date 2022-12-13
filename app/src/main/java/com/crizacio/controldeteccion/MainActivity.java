package com.crizacio.controldeteccion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btn_Estado, btn_Panico;
    ListView lst_Eventos;

    Handler bluetoothIn;
    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread mConnectedThread;

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static String address = null;
    private static String name = null;
    List<Evento> eventos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DEFINICION DE OBJETOS
        btn_Estado = (Button) findViewById(R.id.btnEstado);
        btn_Panico = (Button) findViewById(R.id.btnPanico);
        lst_Eventos = (ListView) findViewById(R.id.lstEventos);
        eventos = new ArrayList<>();

        createNotificationChannel();

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;
                    if (readMessage.equals("5")) {
                        addNotification();
                    }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();
    }
    String notify_canal = "meme";
    int notify_ide = 1;
    private void addNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, notify_canal)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("ALERTA DE MOVIMIENTO")
                .setContentText("RAPIDO, QUE TENGO MIEDO")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notify_ide, builder.build());
        notify_ide++;
        AddEvento("Alerta", "Movimiento detectado");
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = notify_canal;
            String description = "ALERTINHASKKKXD";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(notify_canal, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public void AddEvento(String titulo, String descripcion) {
        Evento evento = new Evento(titulo, descripcion);
        eventos.add(evento);
        ListaEvento eventoAdapter = new ListaEvento(this, eventos);
        lst_Eventos.setAdapter(eventoAdapter);
    }

    boolean estadoActual = false;
    public void metEstado(View view) {
        if (estadoActual) {
            mConnectedThread.write("0");
            Toast.makeText(getBaseContext(), "Sistema inactivo", Toast.LENGTH_SHORT).show();
            estadoActual = false;
            btn_Estado.setText("Inactivo");
        } else {
            mConnectedThread.write("1");
            Toast.makeText(getBaseContext(), "Sistema activo", Toast.LENGTH_SHORT).show();
            estadoActual = true;
            btn_Estado.setText("Activo");
        }
        AddEvento("Monitorizacion", "" + estadoActual);
    }
    boolean panicoActual = false;
    public void metPanico(View view) {
        if (panicoActual) {
            mConnectedThread.write("3");
            Toast.makeText(getBaseContext(), "PANICO! activado", Toast.LENGTH_SHORT).show();
            panicoActual = false;
            btn_Panico.setText("En panico...");
        } else {
            mConnectedThread.write("4");
            Toast.makeText(getBaseContext(), "PANICO! desactivado", Toast.LENGTH_SHORT).show();
            panicoActual = true;
            btn_Panico.setText("Panico");
        }
        AddEvento("Panico!", "" + panicoActual);
    }

    @SuppressLint("MissingPermission")
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
        address = intent.getStringExtra(DispositivosVinculados.DEVICE_ADDRESS);
        name = intent.getStringExtra(DispositivosVinculados.DEVICE_NAME);
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }
        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) { }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
        mConnectedThread.write("x");
        AddEvento("Conexion: " + name, address);
    }
    @Override
    public void onPause()
    {
        super.onPause();
        try {
            btSocket.close();
        } catch (IOException e2) { }
    }
    @SuppressLint("MissingPermission")
    private void checkBTState() {
        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            byte[] buffer = new byte[256];
            int bytes;
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}
// CODIGO BASE
//  https://github.com/patriotaSJ/Bluetooth/tree/master/Android/app/src/main/java/com/tatoado/ramabluewingood
//  https://github.com/patriotaSJ/Bluetooth/blob/master/Android/app/src/main/java/com/tatoado/ramabluewingood/MainActivity.java