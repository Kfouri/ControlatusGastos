package com.kfouri.controlatusgastos.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kfouri.controlatusgastos.Adapters.GastoCustomAdapter;
import com.kfouri.controlatusgastos.Utils.BaseDatos;
import com.kfouri.controlatusgastos.Beans.Gasto;
import com.kfouri.controlatusgastos.Beans.GastoModel;
import com.kfouri.controlatusgastos.Utils.ExpandableHeightListView;
import com.kfouri.controlatusgastos.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ListadoGastosActivity extends AppCompatActivity {

    private DatePicker DPFecha;
    ExpandableHeightListView gastoList;
    ExpandableHeightListView gastoListMensual;
    List<GastoModel> gastoModelArray = new ArrayList<GastoModel>();
    List<GastoModel> gastoModelArrayMensual = new ArrayList<GastoModel>();

    ArrayList<Gasto> gastoArray = new ArrayList<Gasto>();
    ArrayList<Gasto> gastoArrayMensual = new ArrayList<Gasto>();

    private BaseDatos db = new BaseDatos(this);
    GastoCustomAdapter gastoAdapter;
    GastoCustomAdapter gastoAdapterMensual;
    private TextView TVTotal;
    private TextView TVTotalMensual;
    private Button BTNBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_gastos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DPFecha = (DatePicker) findViewById(R.id.fecha_gasto);
        TVTotal = (TextView) findViewById(R.id.total);
        TVTotalMensual = (TextView) findViewById(R.id.totalMensual);

        BTNBuscar = (Button) findViewById(R.id.botonBuscar);

        gastoList = (ExpandableHeightListView) findViewById(R.id.listView);
        gastoList.setExpanded(true);

        gastoList.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });


        gastoListMensual = (ExpandableHeightListView) findViewById(R.id.listViewMensual);
        gastoListMensual.setExpanded(true);

        gastoListMensual.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        BTNBuscar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                actualizarLista();
            }

        });

        actualizarLista();
        actualizarListaMensual();

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    void actualizarListaMensual()
    {
        TVTotalMensual.setText(getString(R.string.totalMensual)+" 0.00");
        Calendar calendar = Calendar.getInstance();

        float xtotal = 0;
        calendar.set(DPFecha.getYear(),DPFecha.getMonth(),1);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND, 0);


        long FechaDesde = calendar.getTimeInMillis();
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));

        long FechaHasta = calendar.getTimeInMillis();

        gastoModelArrayMensual = db.getGastosMes(FechaDesde,FechaHasta);

        gastoArrayMensual.clear();
        gastoAdapterMensual = new GastoCustomAdapter(ListadoGastosActivity.this, R.layout.row_listado_gastos, gastoArrayMensual);
        gastoListMensual.setAdapter(gastoAdapterMensual);
        gastoAdapterMensual.notifyDataSetChanged();

        if (gastoModelArrayMensual != null)
        {

            for (int i = 0; i < gastoModelArrayMensual.size(); ++i)
            {
                String xcategoria = gastoModelArrayMensual.get(i).categoria;
                float ximporte = gastoModelArrayMensual.get(i).importe;
                xtotal = xtotal + ximporte;

                gastoArrayMensual.add(new Gasto(0,
                        0,
                        xcategoria,
                        ximporte,
                        ""));

            }

            gastoListMensual.setItemsCanFocus(false);
            gastoListMensual.setAdapter(gastoAdapterMensual);

            TVTotalMensual.setText(getString(R.string.total)+" "+xtotal);
        }
    }

    void actualizarLista()
    {

        TVTotal.setText(getString(R.string.total)+" 0.00");

        Calendar calendar = Calendar.getInstance();

        float xtotal = 0;
        calendar.set(DPFecha.getYear(),DPFecha.getMonth(),DPFecha.getDayOfMonth());
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND, 0);


        long LFecha = calendar.getTimeInMillis();

        gastoModelArray = db.getGastosFecha(LFecha);

        gastoArray.clear();
        gastoAdapter = new GastoCustomAdapter(ListadoGastosActivity.this, R.layout.row_listado_gastos, gastoArray);
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

            TVTotal.setText(getString(R.string.total)+" "+xtotal);
        }

    }
}
