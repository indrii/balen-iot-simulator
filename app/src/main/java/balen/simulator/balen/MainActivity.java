package balen.simulator.balen;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class MainActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private static final Object RequestPermissionsCode = 1;
    Context context;
    private GoogleApiClient googleApiClient;
    private EditText device;
    private TextView latitude;
    private TextView longtitude;
    private TextView temperature;
    private TextView humadity;
    private Button submitButton;
    private Button cancleButton;
    private RadioButton openRadio;
    private RadioButton closeRadio;
    private RadioButton radioRem;
    private RadioButton radioTidak;
    private RadioGroup doorGroup;
    private RadioGroup brakeGroup;
    private TextView battrey;
    private TextView bensin;
    private SeekBar seekbar;
    private SeekBar seekbarHuma;
    private SeekBar seekbarBensin;
    private ToggleButton submitToggle;
    private TextView speedText;
    private TextView imei;
    private FusedLocationProviderClient fusedLocationProviderClient;
    Thread t;
    private LocationRequest mLocationRequest;
    private final  int min = 50;
    private boolean mRequestingLocationUpdates = true;
    private boolean suspendPublisher = false;
    private static final long INTERVAL = 1000 * 2;
    private static final long FASTEST_INTERVAL = 1000 * 1;
//    static double distance = 0;
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            battrey.setText(String.valueOf(level));
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        device = (EditText) findViewById(R.id.deviceField);
        longtitude = (TextView) findViewById(R.id.longField);
        latitude = (TextView) findViewById(R.id.latField);
        temperature = (TextView) findViewById(R.id.textTemp);
        openRadio = (RadioButton) findViewById(R.id.radioOpen);
        closeRadio = (RadioButton) findViewById(R.id.radioClose);
        radioRem = (RadioButton) findViewById(R.id.radioRem);
        radioTidak = (RadioButton) findViewById(R.id.radioTidak);
        doorGroup = (RadioGroup) findViewById(R.id.radioGroup);
        brakeGroup = (RadioGroup) findViewById(R.id.radioBrake);
        battrey = (TextView) findViewById(R.id.battreyText);
        bensin = (TextView) findViewById(R.id.textBensin);
        seekbar = (SeekBar) findViewById(R.id.seekBarTemp);
        seekbarHuma = (SeekBar) findViewById(R.id.seekBarHuma);
        seekbarBensin = (SeekBar) findViewById(R.id.seekBarBensin);
        submitToggle = (ToggleButton) findViewById(R.id.toggleBtn);
        speedText = (TextView) findViewById(R.id.txtSpeed);
        humadity = (TextView) findViewById(R.id.txtHuma);
        imei = (TextView) findViewById(R.id.txtIMEI);


        submitToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                if (b){
                    if(!t.isAlive()){
                        t.start();

                    }
                    submitToggle.setBackgroundColor(Color.rgb(251,89,19));

                    suspendPublisher = false;
                }else{
                    latitude.setText("");
                    longtitude.setText("");
                    temperature.setText("");
                    humadity.setText("");
                    openRadio.setChecked(false);
                    closeRadio.setChecked(false);
                    radioRem.setChecked(false);
                    radioTidak.setChecked(false);
                    device.setText("");
                    bensin.setText("");

                    if (imei != null){
                        imei.setText("");
                    }
                    speedText.setText("");
                    seekbar.setProgress(50);
                    seekbarHuma.setProgress(50);
                    seekbarBensin.setProgress(0);
                    if(t.isAlive()){
                        //t.stop();

                    }
                    submitToggle.setBackgroundColor(Color.rgb(15,229,213));

                    suspendPublisher = true;
                }

            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int buffer = progress - min;
                temperature.setText(""+ buffer + "");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });
        seekbarHuma.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                int buffer = progress - min;
                humadity.setText(""   + buffer + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekbarBensin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                bensin.setText(""+ i +"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);



        createLocationRequest();

        MainActivity diz = this;

        t = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {

                    try {
                        Thread.sleep(2000);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if ((ActivityCompat.checkSelfPermission(diz, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(diz, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(diz, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED)) {
                                 requestPermissions();
                                }else {

                                    if (!suspendPublisher){
                                        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(diz, new OnSuccessListener<Location>() {
                                            @Override
                                            public void onSuccess(Location location) {
                                                // Location location = locationResult.getLastLocation();
                                                System.out.println("===LOCATION CALLBACK===");
                                                System.out.println(location);
                                                System.out.println("=======================");

                                                if (location != null) {
                                                    System.out.println("===GET LOCATION SUCCESS===");
                                                    System.out.println(location.getLongitude());
                                                    System.out.println(location.getLatitude());
                                                    System.out.println(new Date(location.getTime()));
                                                    System.out.println("=========================");

                                                    Double speedInKPH = Double.valueOf(location.getSpeed()*18/5);
                                                    DecimalFormat df= new DecimalFormat("#.#");
                                                    df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));

                                                    latitude.setText(String.valueOf(location.getLatitude()));
                                                    longtitude.setText(String.valueOf(location.getLongitude()));
                                                    speedText.setText(String.valueOf(df.format(speedInKPH)));



                                                }

                                            }
                                        });
                                    }

                                }

                                if (!suspendPublisher){

                                    if(imei != null){
                                        imei.setText(String.valueOf(getImeiNumber()));
                                    }

                                    SimulatorData data = new SimulatorData();
                                    data.setDeviceId(String.valueOf(device.getText().toString()));

                                    if (temperature.getText() != null && !temperature.getText().toString().isEmpty()) {
                                        data.setTemperature(Double.valueOf(temperature.getText().toString()));
                                    }
                                    if (latitude.getText() != null && !latitude.getText().equals("")) {
                                        data.setLatitude(Double.valueOf(latitude.getText().toString()));
                                    }
                                    if (longtitude.getText() != null && !longtitude.getText().equals("")) {
                                        data.setLongitude(Double.valueOf(longtitude.getText().toString()));
                                    }

                                    if(imei != null){
                                        data.setImei(String.valueOf(imei.getText().toString()));
                                    }

                                    if (humadity.getText() != null && !humadity.getText().toString().isEmpty()) {
                                        data.setHumadity(Double.valueOf(humadity.getText().toString()));
                                    }

                                    data.setBattery(Double.valueOf(battrey.getText().toString()));


                                    if (bensin.getText() !=null && !bensin.getText().toString().isEmpty()){
                                        data.setBensin(Double.valueOf(bensin.getText().toString()));
                                    }

                                    if (speedText.getText() != null && !speedText.getText().equals("")){
                                        data.setSpeed(Double.valueOf(speedText.getText().toString()));
                                    }
//=========================================================Door Group========================================================================================
                                    String checked = null;

                                    if (doorGroup.getCheckedRadioButtonId() == closeRadio.getId()){
                                      checked = "CLOSE";
                                    } else if (doorGroup.getCheckedRadioButtonId() == openRadio.getId()){
                                        checked = "OPEN";
                                    }

                                    data.setDoorState(checked);
//=========================================================Brake Group=======================================================================
                                    String checkedBrake = null;
                                    if (brakeGroup.getCheckedRadioButtonId() == radioRem.getId()){
                                        checkedBrake = "REM";
                                    } else if (brakeGroup.getCheckedRadioButtonId() == radioTidak.getId()){
                                        checkedBrake = "Jalan";
                                    }

                                    data.setBrake(checkedBrake);

//===================================~~~~~~~~~~~~~~~~~~~~~Json~~~~~~~~~~~~~~~=====================================================================
                                    ObjectMapper mapper = new ObjectMapper();

                                    try {
                                        String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
                                        System.out.println(jsonResult);
                                        new PublisherTask().execute(jsonResult);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };


    }

    private String getImeiNumber(){
        final TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            return  tm.getDeviceId();
        }else {
            return tm.getSubscriberId();
        }
    }


 // -----------------------------------------------------GPS---------------------------------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));


    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onStop(){
        //fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        if (googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }

        unregisterReceiver(this.mBatInfoReceiver);
        super.onStop();
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        googleApiClient, mLocationRequest, this);
            }catch (SecurityException e){

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

//    private LocationCallback mLocationCallback = new LocationCallback() {
//        @Override
//        public void onLocationResult(LocationResult locationResult) {
//            super.onLocationResult(locationResult);
//
//            locationResult.getLocations();
//            System.out.println("===LOCATION CALLBACK===");
//
//            Location oldLocation = null;
//            Location newLocation = null;
//
//            for (Location loc : locationResult.getLocations()) {
//                System.out.println(loc);
//                oldLocation = newLocation;
//                newLocation = loc;
//            }
//
//            Location location = locationResult.getLastLocation();
//            System.out.println("===LAST LOCATION===");
//            System.out.println(location);
//            System.out.println("=======================");
//
//            if (location != null) {
//
//                if(oldLocation != null && newLocation != null){
//                    speed = calcuSpeed(newLocation, oldLocation);
//                } else if (location.getSpeed() == 0.0f){
//                    speed = location.getSpeed();
//                }
//
//                System.out.println("===GET LOCATION SUCCESS===");
//                System.out.println(location.getLongitude());
//                System.out.println(location.getLatitude());
//                System.out.println(location.getSpeed());
//                System.out.println(new Date(location.getTime()));
//                System.out.println("=========================");
//
//                latitude.setText(String.valueOf(location.getLatitude()));
//                longtitude.setText(String.valueOf(location.getLongitude()));
//                speedText.setText(String.valueOf(location.getSpeed()));
//
//
//
//            }
//
//
//        }
//    };



    @Override
    public void onLocationChanged(Location location) {

    }

//    public double calcuSpeed(Location mCurrentLocation, Location mOldLocation){
//
//        double newlat = mCurrentLocation.getLatitude();
//        double newLon = mCurrentLocation.getLongitude();
//
//        double oldLat = mOldLocation.getLatitude();
//        double oldLon = mOldLocation.getLongitude();
//
//        if (mCurrentLocation.hasSpeed()){
//            return mCurrentLocation.getSpeed();
//        }else {
//            double radius = 6371000;
//            double dLat = Math.toRadians(newlat-oldLat);
//            double dLon = Math.toRadians(newLon-oldLon);
//            double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
//                        Math.cos(Math.toRadians((newlat))) * Math.cos(Math.toRadians(oldLat)) *
//                                Math.sin(dLon/2) * Math.sin(dLon/2);
//            double c = 2 * Math.asin(Math.sqrt(a));
//            double distance = Math.round(radius * c);
//
//            double timeDiffrent = mCurrentLocation.getTime() - mOldLocation.getTime();
//            return distance/timeDiffrent;
//
//        }
//    };



//----------------------------------------------------send data to queque----------------------------------------------------------
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
                channel.basicPublish(getResources().getString(R.string.amqp_exchange), "null", MessageProperties.TEXT_PLAIN,strings[0].getBytes("UTF-8"));
                channel.close();
                conn.close();
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
