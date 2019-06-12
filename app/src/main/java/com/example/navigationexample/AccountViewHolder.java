package com.example.navigationexample;

import android.accounts.Account;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class AccountViewHolder extends RecyclerView.ViewHolder {
    private TextView text_view_cuenta;
    private TextView text_view_tipo;

    public AccountViewHolder(@NonNull View itemView) {
        super(itemView);
        text_view_cuenta = (TextView)itemView.findViewById(R.id.textViewLabelAccountCuenta);
        text_view_tipo = (TextView)itemView.findViewById(R.id.textViewLabelAccountTipo);
    }

    public void cargarCuentaEnHolder(Account l) {
        text_view_cuenta.setText(l.name);
        text_view_tipo.setText(l.type);
    }
}
