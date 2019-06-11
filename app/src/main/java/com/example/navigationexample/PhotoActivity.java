package com.example.navigationexample;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
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

    private static final String SAVED_NAME = "IMAGEN";
    private static final String PREF_IMAGEN = "PREFS_IMAGEN";
    private static final String PREF_FILE = "PREFS_PHOTO";

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

        registerForContextMenu(imageView);
        if (savedInstanceState != null) {
            rutaFoto = savedInstanceState.getString(SAVED_NAME, null);
        } else {
            SharedPreferences sp = this.getSharedPreferences( PREF_FILE, Context.MODE_PRIVATE);
            if( null != sp) {
                rutaFoto = sp.getString( PREF_IMAGEN, null);
            }
        }
        if (null != rutaFoto) {
            File fichero = new File(rutaFoto);
            if (fichero.isFile() && fichero.canRead() && (0 < fichero.length())) {
                photoUri = Uri.fromFile(fichero);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPref = this.getSharedPreferences( PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PREF_IMAGEN, rutaFoto);
        editor.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_NAME, getFilePath());
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
                    botonFotos.setEnabled(false);
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
            botonFotos.setEnabled(true);
            usuarioPermitir = true;
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
            desactivarModoEstricto(); // Desactivamos el modo estricto pues no nos permite escribir
            // en el directorio seleccionado.
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

    // Seleccionar una foto
    public void seleccionarFoto(View view) {
        Log.d("MIAPP", "Quiere seleccionar una foto.");

        Intent intent = new Intent();
        // con Intent.ACTION_PICK fuerza que sea la Galería, puede que no tenga instalada una app.
        // intent.setAction(Intent.ACTION_PICK);
        // con Intent.ACTION_GET_CONTENT pido cualquier app que pueda realizar la acción, como el
        // FileManager.
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        startActivityForResult(intent, CODIGO_PETICION_SELECCIONAR_FOTO);
    }


    // Resultado de las acciones solicitadas al sistema
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
                        File fichero = new File(rutaFoto);
                        if (fichero.isFile() && fichero.canRead() && (0 < fichero.length())) {
                            photoUri = Uri.fromFile(fichero);
                            this.imageView.setImageURI(photoUri);
                            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, photoUri));
                        } else {
                            Log.d("MIAPP", "La foto recibida está vacia o no es accesible.");
                        }
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

    private void desactivarModoEstricto() {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
            }
        }
    }

    private String getFilePath() {
        // https://stackoverflow.com/questions/5657411/android-getting-a-file-uri-from-a-content-uri#answer-12603415
        String filePath = null;
        if (photoUri != null && "content".equals(photoUri.getScheme())) {
            Cursor cursor = this.getContentResolver().query(photoUri, new String[]{android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
            cursor.moveToFirst();
            filePath = cursor.getString(0);
            cursor.close();
        } else {
            filePath = photoUri.getPath();
        }
        Log.d("MIAPP", "Chosen path = " + filePath);

        return filePath;
    }

    // Gestión del context menú.
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (null != photoUri) {
            String filePath = getFilePath();
            File fichero = new File(filePath);
            if (null != fichero && fichero.isFile() && fichero.canRead()) {
                super.onCreateContextMenu(menu, v, menuInfo);
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.context_image_menu, menu);
            } else {
                Log.d("MIAPP", "La foto recibida no es accesible.");
            }
        } else {
            Log.d("MIAPP", "No hay foto accesible.");
        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        //find out which menu item was pressed
        switch (item.getItemId()) {
            case R.id.borrar_imagen_context:
                return doBorrarImagen();
            default:
                return false;
        }
    }

    private boolean doBorrarImagen() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_image_borrar)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("MIAPP", "Borrar la imagen.");
                        String filePath = getFilePath();
                        File fichero = new File(filePath);
                        fichero.delete();
                        if (fichero.exists()) {
                            try {
                                fichero.getCanonicalFile().delete();
                            } catch (IOException e) {
                                Log.e("MIAPP", "No se puede borrar la imagen.", e);
                            }
                            if (fichero.exists()) {
                                getApplicationContext().deleteFile(fichero.getName());
                            }
                        }
                        if (!fichero.exists()) {
                            imageView.setImageURI(null);
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, photoUri));
                            photoUri = null;
                        } else {
                            Log.d("MIAPP", "No he logrado borrar la imagen.");
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        Log.d("MIAPP", "Cancel: Borrar la imagen.");
                    }
                });
        // Create the AlertDialog object
        builder.create();
        builder.show();

        return true;
    }
}
