package balen.simulator.balen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;




public class MainActivity extends AppCompatActivity {


    private EditText device;
    private EditText latitude;
    private EditText longtitude;
    private EditText temperature;
    private Button submitButton;
    private Button cancleButton;
    private RadioButton openRadio;
    private RadioButton closeRadio;
    private RadioGroup doorGroup;
    private BroadcastReceiver mReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        device = findViewById(R.id.deviceField);
        longtitude = findViewById(R.id.longField);
        latitude = findViewById(R.id.latField);
        temperature = findViewById(R.id.fieldTemperature);
        submitButton = findViewById(R.id.buttonSubmitToken);
        cancleButton = findViewById(R.id.buttonCancelToken);
        openRadio = findViewById(R.id.radioOpen);
        closeRadio = findViewById(R.id.radioClose);
        doorGroup = findViewById(R.id.radioGroup);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("submit clicked");

                SimulatorData data = new SimulatorData();
                data.setDeviceId(String.valueOf(device.getText().toString()));
                data.setTemperature(Double.valueOf(temperature.getText().toString()));
                data.setLatitude(Double.valueOf(latitude.getText().toString()));
                data.setLongtitude(Double.valueOf(longtitude.getText().toString()));
//                data.setBattrey(String.valueOf(mReceiver.getClass().toString()));
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
//    @Override
//    protected void onStart() {
//        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
//        super.onStart();
//    }
//    @Override
//    protected void onStop() {
//        unregisterReceiver(mReceiver);
//        super.onStop();
//    }
//
//    private class BatteryBroadcastReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
//        }
//    }
}
