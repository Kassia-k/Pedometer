package kassia.k.pedometer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    Intent intentService;
    BroadcastReceiver receiver;
    boolean flag = true;
    String serviceData;

    @Bind(R.id.btn_service_on_off) Button serviceBtn;
    @Bind(R.id.step_count) TextView stepCount;
    @Bind(R.id.step_detect) TextView stepDetect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        intentService = new Intent(this, PedometerService.class);
        receiver = new MainReceiver();
    }

    @OnClick(R.id.btn_service_on_off)
    void serviceOnOff(){
        if(flag) {
            Log.i("git", "서비스시작");
            serviceBtn.setText("서비스종료");

            try {
                IntentFilter mainFilter = new IntentFilter();
                mainFilter.addAction("kassia.k.step");
                mainFilter.addAction("kassia.k.detector");
                registerReceiver(receiver, mainFilter);

                startService(intentService);
                Toast.makeText(getApplicationContext(), "서비스시작", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        } else {
            Log.i("git", "서비스종료");
            serviceBtn.setText("서비스시작");
            stepCount.setText("0");
            stepDetect.setText("0");

            try {
                unregisterReceiver(receiver);
                stopService(intentService);
                Toast.makeText(getApplicationContext(), "서비스종료", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        flag = !flag;
    }


    class MainReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("kassia.k.step")) {
                serviceData = intent.getStringExtra("serviceStepData");
                Log.i("git", "넘어온 Counter 값 :: "+serviceData);
                        stepCount.setText(serviceData);

            } else if(action.equals("kassia.k.detector")) {
                serviceData = intent.getStringExtra("serviceDetectorData");
                Log.i("git", "넘어온 Detector 값 :: "+serviceData);
                        stepDetect.setText(serviceData);
            }
        }
    }


    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }
}
