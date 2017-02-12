package com.kfouri.controlatusgastos.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kfouri.controlatusgastos.Adapters.GridviewAdapter;
import com.kfouri.controlatusgastos.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class PrincipalActivity extends Activity {

    private GridviewAdapter mAdapter;
    private ArrayList<String> listCountry;
    private ArrayList<Integer> listFlag;

    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (prefs.getLong("TiempoBackup", 0)==0)
        {
            SharedPreferences.Editor editor = prefs.edit();
            Date date = new Date(System.currentTimeMillis());
            editor.putLong("TiempoBackup", date.getTime());
            editor.commit();
        }

        Date UltimaFecha = new Date(prefs.getLong("TiempoBackup", 0));

        Date hoy = new Date(System.currentTimeMillis());
        long diff = hoy.getTime() - UltimaFecha.getTime();
        if (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)>=7)
        {
            Toast.makeText(getApplicationContext(), getString(R.string.hacerBackup), Toast.LENGTH_LONG).show();
        }

        prepareList();

        mAdapter = new GridviewAdapter(this,listCountry, listFlag);

        gridView = (GridView) findViewById(R.id.gridView1);
        gridView.setAdapter(mAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                if (position==0)
                {
                    Intent intent = new Intent(PrincipalActivity.this, NuevoGastoActivity.class);
                    //intent.putExtra("id", id);
                    startActivityForResult(intent, 0);
                }
                else if (position==1)
                {
                    Intent intent = new Intent(PrincipalActivity.this, CategoriaActivity.class);
                    //intent.putExtra("id", id);
                    startActivityForResult(intent, 0);
                }
                else  if (position==2)
                {
                    Intent intent = new Intent(PrincipalActivity.this, NuevoListadoActivity.class);
                    startActivityForResult(intent, 0);
                }
                else if (position==3)
                {
                    Intent intent = new Intent(PrincipalActivity.this, BackupActivity.class);
                    startActivityForResult(intent, 0);

                }
                else if (position==4)
                {
                    String texto;
                    texto = getString(R.string.app_name)+"\n";
                    texto = texto +getString(R.string.compartir)+"\n";
                    texto = texto +"https://play.google.com/store/apps/details?id=com.kfouri.controlatusgastos";

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, texto);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                }
                else if (position==5)
                {
                    Intent intent1 = new Intent("android.intent.action.VIEW",Uri.parse("market://search?q=pub:MAKP+Team"));
                    startActivity(intent1);
                }

            }
        });

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void prepareList()
    {
        listCountry = new ArrayList<String>();

        listCountry.add(getString(R.string.NuevoGasto));
        listCountry.add(getString(R.string.NuevasCategorias));
        listCountry.add(getString(R.string.Listado));
        listCountry.add(getString(R.string.Backup));
        listCountry.add(getString(R.string.Compartir));

        listCountry.add(getString(R.string.MasApp));


        listFlag = new ArrayList<Integer>();
        listFlag.add(R.drawable.logo2);
        listFlag.add(R.drawable.config);
        listFlag.add(R.drawable.listado);
        listFlag.add(R.drawable.backup);
        listFlag.add(R.drawable.compartir);

        listFlag.add(R.drawable.market);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_share: {
                compartir();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void compartir()
    {
        String texto;
        texto = getString(R.string.app_name)+"\n";
        texto = texto +getString(R.string.compartir_txt)+"\n";
        texto = texto +"https://play.google.com/store/apps/details?id=com.kfouri.justintime";

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, texto);
        sendIntent.setType("text/plain");
        //sendIntent.setType("text/html");
        startActivity(sendIntent);
    }
}
