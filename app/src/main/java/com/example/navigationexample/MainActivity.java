package com.example.navigationexample;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;//el menú lateral
    private boolean menu_visible;//para gestionar si está visible o no el menú lateral

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.menu_visible = false;

        //iniciamos el menú
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //muestra el boton de para atrás (por defecto)
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);//personalizo con el del menu
        //asignamos listener del menú lateral
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navview);
        navigationView.setNavigationItemSelectedListener(this); //escucho los eventos de esta clase aquí
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id_item = item.getItemId();
        switch (id_item) {
            case android.R.id.home:
                if (menu_visible) {
                    drawerLayout.closeDrawers();
                    menu_visible = false;
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                    menu_visible = true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        String menu = item.getTitle().toString();
        int npi = item.getOrder();//obtengo el número del punto de interés

        Intent intent = null;
        switch (npi) {
            case 1 : {
                intent = new Intent(this, MainActivity.class);
            } break;
            case 2 : {
                intent = new Intent(this, NetworkActivity.class);
            } break;
            case 3 : {
                intent = new Intent(this, PhotoActivity.class);
            } break;
            case 4 : {
                intent = new Intent( this, AccountsActivity.class);
            } break;
            default: {}
        }
        if (null != intent) {
            startActivity( intent);
        }

        Log.d(getClass().getCanonicalName(), "Ha tocado la opción " + menu + " " +npi);
        drawerLayout.closeDrawers();
        menu_visible = false;

        return false;
    }
}
