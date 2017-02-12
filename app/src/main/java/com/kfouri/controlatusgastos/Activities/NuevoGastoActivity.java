package com.kfouri.controlatusgastos.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kfouri.controlatusgastos.Adapters.GastoCustomAdapter;
import com.kfouri.controlatusgastos.Beans.Gasto;
import com.kfouri.controlatusgastos.Beans.GastoModel;
import com.kfouri.controlatusgastos.R;
import com.kfouri.controlatusgastos.Utils.BaseDatos;
import com.kfouri.controlatusgastos.Utils.ExpandableHeightListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NuevoGastoActivity extends AppCompatActivity {

    private DatePicker DPFecha;
    private Button BTNBuscar;
    public  TextView TVTotal;
    ExpandableHeightListView gastoList;

    List<GastoModel> gastoModelArray = new ArrayList<GastoModel>();
    GastoCustomAdapter gastoAdapter;
    private BaseDatos db = new BaseDatos(this);
    ArrayList<Gasto> gastoArray = new ArrayList<Gasto>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_gasto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TVTotal = (TextView) findViewById(R.id.total);
        DPFecha = (DatePicker) findViewById(R.id.fecha_gasto);
        BTNBuscar = (Button) findViewById(R.id.botonBuscar);
        BTNBuscar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                actualizarLista();
            }

        });

        gastoList = (ExpandableHeightListView) findViewById(R.id.listView);
        gastoList.setExpanded(true);

        gastoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3)
            {
                //String value = (String) adapter.getItemAtPosition(position);
                Gasto gasto = (Gasto)adapter.getItemAtPosition(position);

                Toast.makeText(getApplicationContext(), getString(R.string.Descripcion)+": "+gasto.getDescripcion(),Toast.LENGTH_LONG).show();
            }
        });

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


    }

    void actualizarLista()
    {
        TVTotal.setText("0.00");

        Calendar calendar = Calendar.getInstance();

        float xtotal = 0;
        calendar.set(DPFecha.getYear(), DPFecha.getMonth(), DPFecha.getDayOfMonth());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);


        long LFecha = calendar.getTimeInMillis();

        gastoModelArray = db.getGastosFecha(LFecha);

        gastoArray.clear();
        gastoAdapter = new GastoCustomAdapter(NuevoGastoActivity.this, R.layout.row_listado_gastos, gastoArray);
        gastoList.setAdapter(gastoAdapter);
        gastoAdapter.notifyDataSetChanged();

        if (gastoModelArray != null)
        {

            for (int i = 0; i < gastoModelArray.size(); ++i)
            {
                long xid = gastoModelArray.get(i).id;
                long xfecha = gastoModelArray.get(i).fecha;
                String xcategoria = gastoModelArray.get(i).categoria;
                float ximporte = gastoModelArray.get(i).importe;
                xtotal = xtotal + ximporte;
                String xdescripcion = gastoModelArray.get(i).descripcion;

                gastoArray.add(new Gasto(xid,
                        xfecha,
                        xcategoria,
                        ximporte,
                        xdescripcion));

            }


            gastoList.setItemsCanFocus(false);
            gastoList.setAdapter(gastoAdapter);

            TVTotal.setText(""+xtotal);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_agregar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_add:

                Intent intent = new Intent(NuevoGastoActivity.this, GastoActivity.class);
                startActivityForResult(intent, 0);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        actualizarLista();

    }
}
