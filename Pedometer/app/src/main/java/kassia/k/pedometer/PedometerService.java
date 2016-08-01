package kassia.k.pedometer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

public class PedometerService extends Service implements SensorEventListener{

    private long lastTime = 0;
    private float speed;
    private float lastX;
    private float lastY;
    private float lastZ;

    private float x, y, z;
    private static final int SHAKE_THRESHOLD = 1500;

    private SensorManager sensorManager;
    private Sensor accelerormeterSensor;
    private Sensor counterSensor;
    private Sensor detectorSensor;

    private int stepCounter = 0;
    private int counterSteps = 0;
    private int stepDetector = 0;

    NotificationManager Notifi_M;
    Notification Notifi ;

    public PedometerService() {}

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("git", "service onCreate()");
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        counterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        detectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        //Sensor.TYPE_STEP_COUNTER // 만보계 센서를 나타내는 상수 (4.4)
//        Sensor.TYPE_STEP_DETECTOR // 걸음 감지 센서를 나타내는 상수(4.4)
        //SENSOR_DELAY_NORMAL
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("git", "service onStartCommand()");
        if(counterSensor != null)
            sensorManager.registerListener(this, counterSensor, SensorManager.SENSOR_DELAY_UI);
        if(detectorSensor != null)
            sensorManager.registerListener(this, detectorSensor, SensorManager.SENSOR_DELAY_UI);

        // 센서 읽어오는 속도 (빠른순)
//        SensorManager.SENSOR_DELAY_FASTEST;   // 0ms 최대한 빠르게
//        SensorManager.SENSOR_DELAY_GAME;      // 20,000ms 게임에 적합한 속도
//        SensorManager.SENSOR_DELAY_UI;        // 60,000ms UI 수정에 적합한 속도
//        SensorManager.SENSOR_DELAY_NORMAL;    // 200,000ms 화면 방향 변화를 모니터링하기에 적합한 속도

        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notiReg();

//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private void notiReg(){
        Intent intent = new Intent(PedometerService.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(PedometerService.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notifi = new Notification.Builder(getApplicationContext())
                .setContentTitle("클릭하면 앱으로 돌아갑니다.")
                .setContentText("만보기 앱")
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker("만보기!!")
                .setContentIntent(pendingIntent)
                .build();

        //확인하면 자동으로 알림이 제거 되도록
//        Notifi.flags = Notification.FLAG_AUTO_CANCEL;
        Notifi_M.notify( 777 , Notifi);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("git", "service onDestroy()");
        if(sensorManager != null)
            sensorManager.unregisterListener(this);

        if(Notifi_M != null)
            Notifi_M.cancel(777);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String msg = "";
        Intent myFilterRes;
        switch (event.sensor.getType()) {
            case Sensor.TYPE_STEP_COUNTER:
                Log.d("git", "TYPE_STEP_COUNTER :: "+event.values[0]);
                if(counterSteps < 1) {
                    counterSteps = (int)event.values[0];
                }
                stepCounter = (int)event.values[0] - counterSteps;

                myFilterRes = new Intent("kassia.k.step");
                msg = Integer.toString(stepCounter);
                myFilterRes.putExtra("serviceStepData", msg);
                sendBroadcast(myFilterRes);
                break;

            case Sensor.TYPE_STEP_DETECTOR:
                Log.d("git", "TYPE_STEP_DETECTOR :: "+event.values[0]);
                stepDetector++;
                myFilterRes = new Intent("kassia.k.detector");
                msg = Integer.toString(stepDetector);
                myFilterRes.putExtra("serviceDetectorData", msg);
                sendBroadcast(myFilterRes);
                break;

        }





//        if(event.sensor.getType() == accelerormeterSensor.TYPE_ACCELEROMETER) {
////            long currentTime = System.currentTimeMillis();
//            long currentTime = event.timestamp / 1000000L;
//            long gabOfTime = (currentTime - lastTime);
//
//            Log.i("git", "gabOfTime :: "+gabOfTime);
//            if(gabOfTime > 100) {
//                lastTime = currentTime;
//
//                x = event.values[0];
//                y = event.values[1];
//                z = event.values[2];
//
//                speed = Math.abs(x+y+z - lastX-lastY-lastZ) / gabOfTime * 10000;
//                Log.i("git", "speed :: "+speed);
//
//                if(speed > SHAKE_THRESHOLD) {
//                    Intent myFilterRes = new Intent("kassia.k.step");
//
//                    values.Step = count++;
//
//                    String msg = values.Step + "";
//                    myFilterRes.putExtra("serviceData", msg);
//
//                    sendBroadcast(myFilterRes);
//                }
//
//                lastX = event.values[0];
//                lastX = event.values[1];
//                lastX = event.values[2];
//            }
//        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
}
