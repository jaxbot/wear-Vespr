package me.jaxbot.wear.vprwear;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.List;


public class MyActivity extends Activity {

    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onPause()
    {
       saveNumber();
       super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        final Context ctx = this;

        final EditText txtNumber = (EditText)(findViewById(R.id.editText));

        final WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        registerReceiver(new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context c, Intent intent)
            {
                List<ScanResult> results = wifi.getScanResults();
                for (int i = 0; i < results.size(); i++) {
                    if (results.get(i).SSID.equals("Vespr-Guest"))
                    {
                        showVesprNotification();
                    }
                    break;
                }
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        SharedPreferences pref = ctx.getSharedPreferences("U", 0);
        txtNumber.setText(String.valueOf(pref.getInt("number", 12345)));


        showVesprNotification();
    }

    void showVesprNotification()
    {
        String num = ((EditText) (findViewById(R.id.editText))).getText().toString();

        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode("{cn:" + num + "}", BarcodeFormat.QR_CODE, 128, 128);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width + 100, height + 80, Bitmap.Config.RGB_565);
            for (int x = 0; x < width + 100; x++) {
                for (int y = 0; y < height + 80; y++) {
                    bmp.setPixel(x, y, (x % 2 == 0 && y % 2 == 0) ? Color.MAGENTA : Color.WHITE);
                }
            }
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x + 40, y + 10, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            ((ImageView) findViewById(R.id.imageView1)).setImageBitmap(bmp);

            NotificationManager mNotificationManager = (NotificationManager)
                    this.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notif = new Notification.Builder(this)
                    .setContentTitle("Vprwear")
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVibrate(new long[]{ 100, 100, 100, 100 })
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setLargeIcon(bmp)
                    .setStyle(new Notification.BigPictureStyle()
                            .bigPicture(bmp))
                    .build();
            mNotificationManager.notify(NOTIFICATION_ID, notif);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    void saveNumber()
    {
        SharedPreferences pref = this.getSharedPreferences("U", 0);
        SharedPreferences.Editor edit = pref.edit();
        edit.putInt("number", Integer.valueOf(((EditText) (findViewById(R.id.editText))).getText().toString()));
        edit.commit();
    }

}
