package com.example.navigationexample;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

public class AccountsActivity extends AppCompatActivity {

    private static final String[] PERMISSIONS =
            {Manifest.permission.GET_ACCOUNTS};
    private static final int CODIGO_PERMISOS_GET_ACCOUNTS = 1001;
    private static final int[] IDENTIFICADORES = {
            CODIGO_PERMISOS_GET_ACCOUNTS
    };
    private static final int CODIGO_PETICION_GET_ACCOUNTS = 2001;

    private boolean usuarioPermitir;
    private RecyclerView recView;
    /**
     * CuentasActivity (Pendiente de definir)
     * <p>
     * TODO Falta la actividad 4, hacer un layout para un una lista con
     * RecyclerView con dos columnas, NOMBRE y TIPO (de momento vacía), Haced
     * sólo la cabecera
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        solicitarPermisos(PERMISSIONS, IDENTIFICADORES);
    }

    public void solicitarPermisos(String[] permisos, int[] identificador) {
        // Cada vez que se requiere una solicitud de permisos hay que solicitar todos a la vez
        boolean granted = true;
        for (String permiso : permisos) {
            granted = granted & (ContextCompat.checkSelfPermission(this,
                    permiso)
                    == PackageManager.PERMISSION_GRANTED);
        }
        if (!granted) {
            for (int i = 0; i < permisos.length; i++) {
                String permiso = permisos[i];
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        permiso)) {

                    Log.d("MIAPP", "Hay que mostrar una explicación al usuario para el permiso: " + permiso);
                    // TODO Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    usuarioPermitir = false;

                    // No se puede solicitar cada permiso por separado, hay que solicitarlos siempre
                    // como bloque.
//                } else {
                    // No explanation needed, we can request the permission.
//                    ActivityCompat.requestPermissions(this,
//                            permisos,
//                            identificador[0]);
                }
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permisos,
                    identificador[0]);
            usuarioPermitir = true;
        }
    }

    // Resultado de las acciones solicitadas al sistema
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("MIAPP", "Ver el resultado de solicitar el permiso.");
        switch (requestCode) {
            case CODIGO_PERMISOS_GET_ACCOUNTS: {
                if (grantResults.length > 0) {
                    for (int i : grantResults) {
                        // If request is cancelled, the result arrays are empty.
                        if (i == PackageManager.PERMISSION_GRANTED) {

                            Log.d("MIAPP", "Tengo permisos para acceder a las cuentas.");
                            // permission was granted, yay! Do the
                            // related task you need to do.

                        } else {

                            usuarioPermitir = usuarioPermitir & false;
                            Log.d("MIAPP", "No tengo permisos para acceder a las cuentas.");

                            // TODO permission denied, boo! Disable the
                            // functionality that depends on this permission.
                        }
                    }
                    if (usuarioPermitir) {
                        mostrarCuentas();
                    }
                }
                return;
            }
            // other 'case' lines to check for other
            // PERMISSIONS this app might request
        }
    }

    private void mostrarCuentas() {
        AccountManager am = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        for (Account ac : am.getAccounts()) {
            Log.d(this.getClass().getCanonicalName(), ac.toString());
        }
        recView = (RecyclerView) findViewById(R.id.recView);

        AdapterAccounts adaptador = new AdapterAccounts(am.getAccounts());

        recView.setAdapter(adaptador);

        recView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }
}
