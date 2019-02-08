package balen.simulator.balen;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class MainActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener {

    private static final Object RequestPermissionsCode = 1;
    private GoogleApiClient googleApiClient;
    private EditText device;
    private EditText latitude;
    private EditText longtitude;
    private EditText temperature;
    private Button submitButton;
    private Button cancleButton;
    private RadioButton openRadio;
    private RadioButton closeRadio;
    private RadioGroup doorGroup;
    private TextView battrey;


    private FusedLocationProviderClient fusedLocationProviderClient;

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
            battrey.setText(String.valueOf(level)+"%");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        device = findViewById(R.id.deviceField);
        longtitude = (EditText) findViewById(R.id.longField);
        latitude = (EditText) findViewById(R.id.latField);
        temperature = findViewById(R.id.fieldTemperature);
        submitButton = findViewById(R.id.buttonSubmitToken);
        cancleButton = findViewById(R.id.buttonCancelToken);
        openRadio = findViewById(R.id.radioOpen);
        closeRadio = findViewById(R.id.radioClose);
        doorGroup = findViewById(R.id.radioGroup);
        battrey = findViewById(R.id.battreyText);

        this.registerReceiver(this.mBatInfoReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("submit clicked");

                SimulatorData data = new SimulatorData();
                data.setDeviceId(String.valueOf(device.getText().toString()));

                if (temperature.getText()!=null){
                    data.setTemperature(Double.valueOf(temperature.getText().toString()));
                }
                if (latitude.getText()!=null){
                    data.setLatitude(Double.valueOf(latitude.getText().toString()));
                }
                if (longtitude.getText()!=null){
                    data.setLongtitude(Double.valueOf(longtitude.getText().toString()));
                }

                data.setBattery(String.valueOf(battrey.getText().toString()));
//                data.getDateTime().toString();
//                data.getBattrey().toString();

                if (openRadio.isChecked()) {
                    data.setDoor(String.valueOf(openRadio.getText().toString()));
                } else if (closeRadio.isChecked()) {
                    data.setDoor(String.valueOf(closeRadio.getText().toString()));
                }


                ObjectMapper mapper = new ObjectMapper();

                try {
                    String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
                    System.out.println(jsonResult);
                    new PublisherTask().execute(jsonResult);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latitude.setText("");
                longtitude.setText("");
                temperature.setText("");
                openRadio.setChecked(false);
                closeRadio.setChecked(false);
                device.setText("");
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop(){
        if (googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
        super.onStop();
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions();
        }else {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
//                                SimulatorData data = new SimulatorData();
                                latitude.setText(String.valueOf(location.getLatitude()));
                                longtitude.setText(String.valueOf(location.getLongitude()));
                            }
                        }
                    });
        }
    }
    
    private void requestPermissions(){
        ActivityCompat.requestPermissions(MainActivity.this, new 
                String[]{ACCESS_FINE_LOCATION}, (Integer) RequestPermissionsCode);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("MainActivity", "Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.e("MainActivity","Connection failed :" + connectionResult.getErrorCode());
    }

    private class PublisherTask extends AsyncTask<String, Void, Long> {

        Channel channel;
        Connection conn;

        PublisherTask() {
            enableStrictMode();
        }


        @Override
        protected Long doInBackground(String... strings) {
            try {
                final ConnectionFactory factory = new ConnectionFactory();
                // "guest"/"guest" by default, limited to localhost connections
                factory.setUsername("mevkewre");
                factory.setPassword("S6J6kdp7uYepVqMUIBTJ2daZuGXyF6nc");
                factory.setHost("caterpillar.rmq.cloudamqp.com");
                factory.setPort(5672);
                factory.setVirtualHost("mevkewre");
                conn = factory.newConnection();
                channel = conn.createChannel();
                channel.exchangeDeclare("(AMQP default)", "direct", true);
                channel.queueDeclare("DEV.BALENA_REQUEST",true,false,false,null);
                channel.queueBind("DEV.BALENA_REQUEST", "(AMQP default)", "null" );
                channel.basicPublish("(AMQP default)", "null", MessageProperties.TEXT_PLAIN,strings[0].getBytes("UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            return 1L;
        }

        private void enableStrictMode() {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

}
