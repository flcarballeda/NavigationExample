package com.example.navigationexample;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

//IMPORTANTE ANIADIR LOS PERMISOS DE RED <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
//LISTA COMPLETA DE PERMISOS https://stackoverflow.com/a/36937109/4067559 PELIGROSOS Y NO
public class NetworkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);

        checkNetwork();
    }
    private void checkNetwork() {
        RadioButton rbci = findViewById(R.id.radioButtonIdInternet);
        RadioButton rbsi = findViewById(R.id.radioButtonIdNoInternet);

        boolean hayred = hayInternet(this);
        rbci.setChecked(hayred);
        rbsi.setChecked(!hayred);

        RadioButton rbwifi = findViewById(R.id.radioButtonIdUsesWifi);
        RadioButton rbmovil = findViewById(R.id.radioButtonIdUsesMobile);
        RadioButton rbo = findViewById(R.id.radioButtonIdUsesOthers);

        if (hayred) {
            boolean redwifi = isWifiAvailable(this);
            boolean redmovil = hayConexion3G4G(this);

            rbwifi.setChecked(redwifi);
            rbmovil.setChecked(redmovil);

            //caso especial, puede haber internet por bridge, ethernet, u otra
            rbo.setChecked(!redmovil&&!redwifi);
        } else {
            rbwifi.setChecked(false);
            rbmovil.setChecked(false);
            rbo.setChecked(false);
        }
    }

    //metodo copiado de https://stackoverflow.com/a/34741193/4067559

    public static boolean isWifiAvailable (Context context) {
        boolean br = false;
        ConnectivityManager cm = null;
        NetworkInfo ni = null;

        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        ni = cm.getActiveNetworkInfo();
        br = ((null != ni) && (ni.isConnected()) && (ni.getType() == ConnectivityManager.TYPE_WIFI));

        return br;
    }


    public static boolean hayConexion3G4G (Context context) {
        boolean br = false;
        ConnectivityManager cm = null;
        NetworkInfo ni = null;

        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        ni = cm.getActiveNetworkInfo();
        br = ((null != ni) && (ni.isConnected()) && (ni.getType() == ConnectivityManager.TYPE_MOBILE));

        return br;
    }

    public static boolean hayInternet(Context context) {

        boolean hay_internet = false;

        ConnectivityManager con_manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = con_manager.getActiveNetworkInfo();

        if (ni!=null)
        {
            hay_internet = ni.isAvailable()&&ni.isConnected();
        }
        return hay_internet;
    }

    public void comprobar (View v) {
        checkNetwork();
    }
}
