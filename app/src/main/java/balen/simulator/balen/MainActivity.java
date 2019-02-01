package balen.simulator.balen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


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

                if (openRadio.isChecked()) {
                    data.setDoor(String.valueOf(openRadio.getText().toString()));
                } else if (closeRadio.isChecked()) {
                    data.setDoor(String.valueOf(closeRadio.getText().toString()));
                }


                ObjectMapper mapper = new ObjectMapper();

                try {
                    String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
                    System.out.println(jsonResult);
                } catch (JsonProcessingException e) {
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

}
