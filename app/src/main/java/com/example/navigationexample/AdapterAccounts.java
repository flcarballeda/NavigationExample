package com.example.navigationexample;

import android.accounts.Account;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AdapterAccounts extends RecyclerView.Adapter<AccountViewHolder> {

    private Account[] cuentas;
    public AdapterAccounts(Account[] cuentas) {
        this.cuentas = cuentas;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        AccountViewHolder viewHolder = null;

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        View itemView = inflater.inflate(R.layout.recyclerview_account_line, viewGroup, false);
        viewHolder = new AccountViewHolder(itemView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder viewHolder, int i) {
        Account cuenta = cuentas[i];
        viewHolder.cargarCuentaEnHolder(cuenta);
    }

    @Override
    public int getItemCount() {
        return cuentas.length;
    }
}
