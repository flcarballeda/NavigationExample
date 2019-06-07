package com.example.navigationexample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class PhotoActivity extends AppCompatActivity {

    private static final String[] permissions = {Manifest.permission.CAMERA};
    private static final int CODIGO_PERMISOS_SELECCIONAR_FOTO = 1001;
    private static final int CODIGO_PETICION_SELECCIONAR_FOTO = 2002;

    private ImageView imageView;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        imageView = findViewById( R.id.imageViewIdPhoto);
    }

    public void hacerFoto(View view) {
        Log.d( "MIAPP", "Quiere hacer una foto.");
    }

    public void seleccionarFoto(View view) {
        Log.d( "MIAPP", "Quiere seleccionar una foto.");

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                Log.d( "MIAPP", "Hay que mostrar una explicación al usuario.");
                // TODO Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        CODIGO_PERMISOS_SELECCIONAR_FOTO);

                // CODIGO_PERMISOS_SELECCIONAR_FOTO is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d( "MIAPP", "Ver el resultado de solicitar el permiso.");
        switch (requestCode) {
            case CODIGO_PERMISOS_SELECCIONAR_FOTO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d( "MIAPP", "Tengo permisos para seleccionar la imagen.");
                    // permission was granted, yay! Do the
                    // related task you need to do.
                    Intent intent = new Intent();
                    intent.setAction( Intent.ACTION_PICK);
                    intent.setType( "image/*");

                    startActivityForResult( intent, CODIGO_PETICION_SELECCIONAR_FOTO);

                } else {

                    Log.d( "MIAPP", "No tengo permisos para seleccionar la foto.");
                    // TODO permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d( "MIAPP", "Vamos a ver que ha ocurrido al seleccionar la foto.");
        switch (resultCode) {
            case RESULT_OK : {
                Log.d( "MIAPP", "Me deja seleccionar la foto.");
                if (null != data) {
                    photoUri = data.getData();
                    this.imageView.setImageURI( photoUri);
                    imageView.setScaleType( ImageView.ScaleType.FIT_XY);
                } // TODO No tiene sentido que si se ha seleccionado la foto no vengan datos.
            } break;
            case RESULT_CANCELED : {
                Log.d( "MIAPP", "No ha seleccionado una foto.");
            } break;
        }
    }
}
