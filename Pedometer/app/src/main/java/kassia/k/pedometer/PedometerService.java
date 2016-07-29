package kassia.k.pedometer;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

public class PedometerService extends Service implements SensorEventListener{

    int count = values.Step;

    private long lastTime = 0;
    private float speed;
    private float lastX;
    private float lastY;
    private float lastZ;

    private float x, y, z;
    private static final int SHAKE_THRESHOLD = 1500;

    private SensorManager sensorManager;
    private Sensor accelerormeterSensor;

    public PedometerService() {}

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("git", "service onCreate()");
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //Sensor.TYPE_STEP_COUNTER
        //SENSOR_DELAY_NORMAL
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("git", "service onStartCommand()");
        if(accelerormeterSensor != null)
            sensorManager.registerListener(this, accelerormeterSensor, SensorManager.SENSOR_DELAY_GAME);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("git", "service onDestroy()");
        if(sensorManager != null)
            sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == accelerormeterSensor.TYPE_ACCELEROMETER) {
//            long currentTime = System.currentTimeMillis();
            long currentTime = event.timestamp / 1000000L;
            long gabOfTime = (currentTime - lastTime);

            Log.i("git", "gabOfTime :: "+gabOfTime);
            if(gabOfTime > 100) {
                lastTime = currentTime;

                x = event.values[0];
                y = event.values[1];
                z = event.values[2];

                speed = Math.abs(x+y+z - lastX-lastY-lastZ) / gabOfTime * 10000;
                Log.i("git", "speed :: "+speed);

                if(speed > SHAKE_THRESHOLD) {
                    Intent myFilterRes = new Intent("kassia.k.step");

                    values.Step = count++;

                    String msg = values.Step + "";
                    myFilterRes.putExtra("serviceData", msg);

                    sendBroadcast(myFilterRes);
                }

                lastX = event.values[0];
                lastX = event.values[1];
                lastX = event.values[2];
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
}
