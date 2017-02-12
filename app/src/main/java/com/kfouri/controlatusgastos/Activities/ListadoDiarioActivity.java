package com.kfouri.controlatusgastos.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class ListadoDiarioActivity extends AppCompatActivity {

    private TextView TVTotalMensual;
    private DatePicker DPFecha;

    private BaseDatos db;
    List<GastoModel> gastoModelArrayMensual = new ArrayList<GastoModel>();
    ArrayList<Gasto> gastoArrayMensual = new ArrayList<Gasto>();
    GastoCustomAdapter gastoAdapterMensual;
    ExpandableHeightListView gastoListMensual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_diario);

        TVTotalMensual = (TextView) findViewById(R.id.totalMensual);
        DPFecha = (DatePicker) findViewById(R.id.fecha_gasto);

        db = new BaseDatos(ListadoDiarioActivity.this);

        gastoListMensual = (ExpandableHeightListView)findViewById(R.id.listViewMensual);
        gastoListMensual.setExpanded(true);

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
        gastoAdapterMensual = new GastoCustomAdapter(ListadoDiarioActivity.this, R.layout.row_listado_gastos_mensual, gastoArrayMensual);
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
}
