package com.crizacio.controldeteccion;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class DispositivosVinculados extends AppCompatActivity {
    public static final String DEVICE_NAME = "com.crizacio.controldeteccion.devicename";
    public static final String DEVICE_ADDRESS = "com.crizacio.controldeteccion.deviceaddress";

    private BluetoothAdapter BA;
    private Set<BluetoothDevice>pairedDevices;
    ListView lst_Dispositivos;
    List<Dispositivo> dispositivos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispositivosvinculados);

        BA = BluetoothAdapter.getDefaultAdapter();
        lst_Dispositivos = (ListView)findViewById(R.id.lstDispositivos);
        dispositivos = new ArrayList<>();

        list();

        lst_Dispositivos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Dispositivo dispo = dispositivos.get(i);
                Intent intent = new Intent(DispositivosVinculados.this, MainActivity.class);
                intent.putExtra(DEVICE_NAME, dispo.getNombre());
                intent.putExtra(DEVICE_ADDRESS, dispo.getDireccion());
                startActivity(intent);
            }
        });
    }

    @SuppressLint("MissingPermission")
    public void list(){
        pairedDevices = BA.getBondedDevices();
        for (BluetoothDevice bt : pairedDevices) {
            Dispositivo dispositivo = new Dispositivo(bt.getName(), bt.getAddress());
            dispositivos.add(dispositivo);
        };
        Toast.makeText(getApplicationContext(), "Seleccione un dispositivo",Toast.LENGTH_SHORT).show();
        ListaDispositivo dispositivoAdapter = new ListaDispositivo(this, dispositivos);
        lst_Dispositivos.setAdapter(dispositivoAdapter);
    }

}
// CODIGO BASE
//  https://www.tutorialspoint.com/android/android_bluetooth.htm