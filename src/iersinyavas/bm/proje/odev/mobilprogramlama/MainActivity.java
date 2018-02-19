package iersinyavas.bm.proje.odev.mobilprogramlama;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener{
	
	// Kullanýlacak bileþenler tanýmlanýyor
	TextView textViewX, textViewY, textViewZ, durum;
	SensorManager sensorManager;
	BroadcastReceiver broadcastReceiver;
	
	@Override
	protected void onResume() {
		super.onResume();
		
		//Gönderilen yayýn burada alýnmaktadýr.Telefon düz mü, yan mý, veya dik mi bu belirtiliyor.
		if(broadcastReceiver == null)
			broadcastReceiver = new BroadcastReceiver() {
				
				@Override
				public void onReceive(Context context, Intent intent) {
					durum.setText(""+intent.getExtras().get("durum"));							
				}
			};
			
			registerReceiver(broadcastReceiver, new IntentFilter("Durumlar"));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Tüm bileþenlerin layout dosyasýndaki idleri tanýmlanýyor.
		setContentView(R.layout.main_activity);
		textViewX = (TextView) findViewById(R.id.textViewX);
		textViewY = (TextView) findViewById(R.id.textViewY);
		textViewZ = (TextView) findViewById(R.id.textViewZ);
		durum = (TextView) findViewById(R.id.durum);
		
		//Proje için gerekli ivme sensörü tanýmlamasý yapýlýyor.
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		Intent intent = new Intent("Durumlar");
		//Sensörün anlýk dinlenmesi durumunda textviewlere yazdýrmasý saðlanýyor.
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
			textViewX.setText("X Ekseni : "+Float.toString(event.values[0]));
			textViewY.setText("Y Ekseni : "+Float.toString(event.values[1]));
			textViewZ.setText("Z Ekseni : "+Float.toString(event.values[2]));
			
			//Projede birde cihazýn titreþim özelliði kullanýlýyor.
			Vibrator titresim = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			
			//Telefon düz, yan ve dik durmadýðý zaman titreme yapmaktadýr.Düz zemin olmadýðý zaman titreme yapýyor.
			//Bunun için gerekli koþul ifadeleri
			//Ayrýca telefonun durumunu cihazda textview içerisinde göstermek için yayýn göndermektedir.
			if(((event.values[0]<1 && event.values[0]>-1)&&(event.values[1]<1 && event.values[1]>-1)&&(event.values[2]>9 || event.values[2]<-9)))
			{
				intent.putExtra("durum", "Telefon düz duruyor");
				sendBroadcast(intent);
			}
			else if(((event.values[1]<1 && event.values[1]>-1)&&(event.values[2]<1 && event.values[2]>-1)&&(event.values[0]>9 || event.values[0]<-9)))
			{
				intent.putExtra("durum", "Telefon yan duruyor");
				sendBroadcast(intent);
			}
			else if(((event.values[0]<1 && event.values[0]>-1)&&(event.values[2]<1 && event.values[2]>-1)&&(event.values[1]>9 || event.values[1]<-9)))
			{
				intent.putExtra("durum", "Telefon dik duruyor");
				sendBroadcast(intent);
			}
			else titresim.vibrate(250);
			
		}
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

		
	}

	
}
