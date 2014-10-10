package me.jaxbot.wear.vprwear;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.List;


public class MyActivity extends Activity {

    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        final Context ctx = this;
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
                        NotificationManager mNotificationManager = (NotificationManager)
                                ctx.getSystemService(Context.NOTIFICATION_SERVICE);
                        Notification notif = new Notification.Builder(ctx)
                                .setContentTitle("You are at Vespr.")
                                .setSmallIcon(R.drawable.ic_launcher)
                                .build();
                        mNotificationManager.notify(NOTIFICATION_ID, notif);
                    }
                    break;
                }
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode("{\"cn\": num}", BarcodeFormat.QR_CODE, 256, 128);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            ((ImageView) findViewById(R.id.imageView1)).setImageBitmap(bmp);

            NotificationManager mNotificationManager = (NotificationManager)
                    this.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notif = new Notification.Builder(this)
                    .setContentTitle("Vprwear")
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
