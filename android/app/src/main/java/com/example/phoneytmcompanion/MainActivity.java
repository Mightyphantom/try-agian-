package com.example.phoneytmcompanion;
import android.app.Activity; import android.content.Intent; import android.os.Bundle; import android.provider.Settings; import android.widget.*;
public class MainActivity extends Activity {
 public void onCreate(Bundle b){super.onCreate(b); LinearLayout l=new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); l.setPadding(40,40,40,40);
 TextView t=new TextView(this); t.setText("Phone YTM Companion\n\nEnable Notification Access, then play music in YouTube Music."); t.setTextSize(20); l.addView(t);
 Button x=new Button(this); x.setText("Open Notification Access"); x.setOnClickListener(v->startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))); l.addView(x); setContentView(l);}
}