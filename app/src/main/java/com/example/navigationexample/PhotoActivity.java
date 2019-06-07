package com.example.navigationexample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoActivity extends AppCompatActivity {

    private static final String PREFIJO_FOTO = "CursoPicture";
    private static final String SUFIJO_FOTO = ".jpg";
    private static final String[] PERMISSIONS =
            {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int CODIGO_PERMISOS_HACER_FOTO = 1001;
    private static final int CODIGO_PERMISOS_ESCRIBIR_EXTERNO = 1002;
    private static final int[] IDENTIFICADORES = {
            CODIGO_PERMISOS_HACER_FOTO,
            CODIGO_PERMISOS_ESCRIBIR_EXTERNO
    };
    private static final int CODIGO_PETICION_HACER_FOTO = 2001;
    private static final int CODIGO_PETICION_SELECCIONAR_FOTO = 2002;

    private ImageView imageView;
    private ImageView botonFotos;
    private Uri photoUri;
    private boolean usuarioPermitir;
    private String rutaFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        imageView = findViewById(R.id.imageViewIdPhoto);
        botonFotos = findViewById(R.id.imageView3);

        usuarioPermitir = true;
        solicitarPermisos(PERMISSIONS, IDENTIFICADORES);
    }

    public void solicitarPermisos(String[] permisos, int[] identificador) {
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

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{permiso},
                            identificador[i]);
                }
            }
        } else {
            for (int i = 0; i < permisos.length; i++) {
                String permiso = permisos[i];

                ActivityCompat.requestPermissions(this,
                        new String[]{permiso},
                        identificador[i]);
            }
        }
    }

    public void hacerFoto(View view) {
        Log.d("MIAPP", "Quiere hacer una foto.");
        // CODIGO_PETICION_HACER_FOTO is an
        // app-defined int constant. The callback method gets the
        // result of the request.
        if (usuarioPermitir) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoUri = crearFicheroImagen();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, CODIGO_PETICION_HACER_FOTO);
        }
    }

    private Uri crearFicheroImagen() {
        Uri resultado = null;

        String momentoActual = new SimpleDateFormat(
                "yyyyMMdd_HHmmss").format(new Date());
        String nombreFichero = PREFIJO_FOTO + momentoActual + SUFIJO_FOTO;
        rutaFoto = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getPath() + File.separator +
                nombreFichero;
        File fichero = new File(rutaFoto);
        try {
            if (fichero.createNewFile()) {
                resultado = Uri.fromFile(fichero);
                Log.d("MIAPP", "Ruta foto: '" + rutaFoto + "'.");
            } else {
                Log.d("MIAPP", "Fichero no creado: '" + rutaFoto + "'");
            }
        } catch (IOException ex) {
            Log.e("MIAPP", "Error al crear el fichero: '" + rutaFoto + "'", ex);
        }

        return resultado;
    }

    public void seleccionarFoto(View view) {
        Log.d("MIAPP", "Quiere seleccionar una foto.");

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");

        startActivityForResult(intent, CODIGO_PETICION_SELECCIONAR_FOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("MIAPP", "Ver el resultado de solicitar el permiso.");
        switch (requestCode) {
            case CODIGO_PERMISOS_HACER_FOTO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d("MIAPP", "Tengo permisos para hacer una foto.");
                    // permission was granted, yay! Do the
                    // related task you need to do.

                } else {

                    usuarioPermitir = usuarioPermitir & false;
                    Log.d("MIAPP", "No tengo permisos para hacer una foto.");
                    botonFotos.setEnabled(false);

                    // TODO permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case CODIGO_PERMISOS_ESCRIBIR_EXTERNO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d("MIAPP", "Tengo permisos para hacer una foto.");
                    // permission was granted, yay! Do the
                    // related task you need to do.

                } else {

                    usuarioPermitir = usuarioPermitir & false;
                    Log.d("MIAPP", "No tengo permisos para hacer una foto.");
                    botonFotos.setEnabled(false);

                    // TODO permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // PERMISSIONS this app might request
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case CODIGO_PETICION_SELECCIONAR_FOTO: {
                Log.d("MIAPP", "Vamos a ver que ha ocurrido al seleccionar la foto.");
                switch (resultCode) {
                    case RESULT_OK: {
                        Log.d("MIAPP", "Me deja seleccionar la foto.");
                        if (null != data) {
                            photoUri = data.getData();
                            this.imageView.setImageURI(photoUri);
                            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        } // TODO No tiene sentido si se ha seleccionado la foto que no vengan datos.
                    }
                    break;
                    case RESULT_CANCELED: {
                        Log.d("MIAPP", "No ha seleccionado una foto.");
                    }
                    break;
                }
            }
            break;
            case CODIGO_PETICION_HACER_FOTO: {
                Log.d("MIAPP", "Vamos a ver que ha ocurrido al hacer la foto.");
                switch (resultCode) {
                    case RESULT_OK: {
                        Log.d("MIAPP", "Me deja hacer la foto.");
                        this.imageView.setImageURI(photoUri);
                        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, photoUri));
                    }
                    break;
                    case RESULT_CANCELED: {
                        Log.d("MIAPP", "No ha tomado una foto.");
                    }
                    break;
                }
            }
            break;
        }
    }
}
