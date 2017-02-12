package com.kfouri.controlatusgastos.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kfouri.controlatusgastos.Adapters.GastoCustomAdapter;
import com.kfouri.controlatusgastos.Adapters.GastoMensualCustomAdapter;
import com.kfouri.controlatusgastos.Beans.Gasto;
import com.kfouri.controlatusgastos.Beans.GastoModel;
import com.kfouri.controlatusgastos.R;
import com.kfouri.controlatusgastos.Utils.BaseDatos;
import com.kfouri.controlatusgastos.Utils.ExpandableHeightListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NuevoListadoActivity extends AppCompatActivity {

    private TextView TVTotalDiario;
    public TextView TVTotalMensual;
    private TextView TVTotalAnual;

    private DatePicker DPFecha;

    private Button BTNBuscar;
    private Button BTNBuscar_tab2;
    private Button BTNBuscar_tab3;

    private Spinner SPAnio;
    private Spinner SPMes;
    private Spinner SPAnio3;

    private BaseDatos db;

    List<GastoModel> gastoModelArrayDiario = new ArrayList<GastoModel>();
    ArrayList<Gasto> gastoArrayDiario = new ArrayList<Gasto>();
    GastoCustomAdapter gastoAdapterDiario;
    ExpandableHeightListView gastoListDiario;

    List<GastoModel> gastoModelArrayMensual = new ArrayList<GastoModel>();
    ArrayList<Gasto> gastoArrayMensual = new ArrayList<Gasto>();
    GastoCustomAdapter gastoAdapterMensual;
    ExpandableHeightListView gastoListMensual;
    List<GastoModel> gastoModelArray = new ArrayList<GastoModel>();
    ArrayList<Gasto> gastoArray = new ArrayList<Gasto>();
    GastoMensualCustomAdapter gastoAdapter;

    List<GastoModel> gastoModelArrayAnual = new ArrayList<GastoModel>();
    ArrayList<Gasto> gastoArrayAnual = new ArrayList<Gasto>();
    GastoCustomAdapter gastoAdapterAnual;
    ExpandableHeightListView gastoListAnual;
    ExpandableHeightListView gastoListDetallado;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nuevo_listado);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Tab2 contents
        final TabHost tabHost=(TabHost)findViewById(R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec spec1=tabHost.newTabSpec("Tab1");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator(getString(R.string.diario), null);

        TabHost.TabSpec spec2=tabHost.newTabSpec("Tab2");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator(getString(R.string.mensual), null);

        TabHost.TabSpec spec3=tabHost.newTabSpec("Tab3");
        spec3.setContent(R.id.tab3);
        spec3.setIndicator(getString(R.string.anual), null);

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        tabHost.addTab(spec3);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String arg0) {

                setTabColor(tabHost);
            }
        });
        setTabColor(tabHost);


        //------------------TAB 1-------------------------------------------

        TVTotalDiario = (TextView) findViewById(R.id.totalDiario_tab1);
        TVTotalMensual = (TextView) findViewById(R.id.totalMensual_tab2);
        TVTotalAnual = (TextView) findViewById(R.id.totalMensual_tab3);

        DPFecha = (DatePicker) findViewById(R.id.fecha_gasto_tab1);

        BTNBuscar = (Button) findViewById(R.id.botonBuscar_tab1);
        BTNBuscar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                actualizarListaDiario();
            }

        });

        db = new BaseDatos(NuevoListadoActivity.this);

        gastoListDiario = (ExpandableHeightListView)findViewById(R.id.listViewDiario_tab1);
        gastoListDiario.setExpanded(true);

        gastoListMensual = (ExpandableHeightListView)findViewById(R.id.listViewDiario_tab2);
        gastoListMensual.setExpanded(true);

        gastoListAnual = (ExpandableHeightListView)findViewById(R.id.listViewAnual_tab3);
        gastoListAnual.setExpanded(true);

        actualizarListaDiario();

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //------------------TAB 2-------------------------------------------


        SPMes=(Spinner)findViewById(R.id.spn_mes_tab2);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(getApplicationContext(), R.array.months, R.layout.spinner_item);


        SPMes.setAdapter(adapter);

        SPAnio=(Spinner)findViewById(R.id.spn_anio_tab2);

        ArrayAdapter<CharSequence> adapter2=ArrayAdapter.createFromResource(getApplicationContext(), R.array.year, R.layout.spinner_item);
        SPAnio.setAdapter(adapter2);

        BTNBuscar_tab2 = (Button) findViewById(R.id.botonBuscar_tab2);
        BTNBuscar_tab2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                actualizarListaMensual();
            }

        });

        AdView mAdViewMensual = (AdView) findViewById(R.id.adViewMensual);
        AdRequest adRequestMensual = new AdRequest.Builder().build();
        mAdViewMensual.loadAd(adRequestMensual);

        Date date= new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);

        SPMes.setSelection(month);

        gastoListDetallado = (ExpandableHeightListView) findViewById(R.id.listViewtab22);
        gastoListDetallado.setExpanded(true);

        gastoListDetallado.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
                //String value = (String) adapter.getItemAtPosition(position);
                Gasto gasto = (Gasto) adapter.getItemAtPosition(position);

                Toast.makeText(getApplicationContext(), getString(R.string.Descripcion) + ": " + gasto.getDescripcion(), Toast.LENGTH_LONG).show();
            }
        });

        actualizarListaMensual();

        //------------------TAB 3-------------------------------------------


        SPAnio3 = (Spinner)findViewById(R.id.spn_anio_tab3);
        ArrayAdapter<CharSequence> adapterAnual = ArrayAdapter.createFromResource(getApplicationContext(), R.array.year, R.layout.spinner_item);
        SPAnio3.setAdapter(adapterAnual);

        BTNBuscar_tab3 = (Button) findViewById(R.id.botonBuscar_tab3);
        BTNBuscar_tab3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                actualizarListaAnual();
            }

        });

        actualizarListaAnual();
    }

    void actualizarListaAnual()
    {
        TVTotalAnual.setText(getString(R.string.totalAnual) + " 0.00");

        int xanio = Integer.parseInt(SPAnio.getSelectedItem().toString());

        Calendar calendar = Calendar.getInstance();
        calendar.set(xanio, 0, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long FechaDesde = calendar.getTimeInMillis();

        Calendar calendarHasta = Calendar.getInstance();
        calendarHasta.set(xanio, 11, 31);
        calendarHasta.set(Calendar.HOUR_OF_DAY, 0);
        calendarHasta.set(Calendar.MINUTE, 0);
        calendarHasta.set(Calendar.SECOND, 0);
        calendarHasta.set(Calendar.MILLISECOND, 0);

        long FechaHasta = calendarHasta.getTimeInMillis();

        gastoModelArrayAnual = db.getGastosAnual(FechaDesde,FechaHasta);

        gastoArrayAnual.clear();
        gastoAdapterAnual = new GastoCustomAdapter(NuevoListadoActivity.this, R.layout.row_listado_gastos_anual, gastoArrayAnual);
        gastoListAnual.setAdapter(gastoAdapterAnual);
        gastoAdapterAnual.notifyDataSetChanged();

        float xtotal = 0;

        if (gastoModelArrayAnual != null)
        {

            for (int i = 0; i < gastoModelArrayAnual.size(); ++i)
            {
                String xcategoria = gastoModelArrayAnual.get(i).categoria;
                float ximporte = gastoModelArrayAnual.get(i).importe;
                String mes = gastoModelArrayAnual.get(i).Mes;

                if (mes.equals("01"))
                {
                    mes = getString(R.string.Enero);
                }
                else if (mes.equals("02"))
                {
                    mes = getString(R.string.Febrero);
                }
                else if (mes.equals("03"))
                {
                    mes = getString(R.string.Marzo);
                }
                else if (mes.equals("04"))
                {
                    mes = getString(R.string.Abril);
                }
                else if (mes.equals("05"))
                {
                    mes = getString(R.string.Mayo);
                }
                else if (mes.equals("06"))
                {
                    mes = getString(R.string.Junio);
                }
                else if (mes.equals("07"))
                {
                    mes = getString(R.string.Julio);
                }
                else if (mes.equals("08"))
                {
                    mes = getString(R.string.Agosto);
                }
                else if (mes.equals("09"))
                {
                    mes = getString(R.string.Septiembre);
                }
                else if (mes.equals("10"))
                {
                    mes = getString(R.string.Octubre);
                }
                else if (mes.equals("11"))
                {
                    mes = getString(R.string.Noviembre);
                }
                else if (mes.equals("12"))
                {
                    mes = getString(R.string.Diciembre);
                }

                xtotal = xtotal + ximporte;

                gastoArrayAnual.add(new Gasto(0,
                        0,
                        xcategoria,
                        ximporte,
                        "",
                        mes));

            }

            gastoListAnual.setItemsCanFocus(false);
            gastoListAnual.setAdapter(gastoAdapterAnual);

            TVTotalAnual.setText(getString(R.string.totalAnual) + " " + xtotal);
            TVTotalAnual.setVisibility(View.GONE);
        }

    }

    void actualizarListaMensual()
    {

        int xanio = Integer.parseInt(SPAnio.getSelectedItem().toString());

        //getString(R.string.totalMensual)
        TVTotalMensual.setText("0.00");

        Calendar calendar = Calendar.getInstance();

        float xtotal = 0;
        calendar.set(xanio, SPMes.getSelectedItemPosition(), 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long FechaDesde = calendar.getTimeInMillis();

        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        long FechaHasta = calendar.getTimeInMillis();

        gastoModelArrayMensual = db.getGastosMes(FechaDesde,FechaHasta);

        gastoArrayMensual.clear();
        gastoAdapterMensual = new GastoCustomAdapter(NuevoListadoActivity.this, R.layout.row_listado_gastos_mensual, gastoArrayMensual);
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

            //getString(R.string.totalMensual)+" "+
            TVTotalMensual.setText(""+xtotal);
        }


        gastoModelArray = db.getGastosMesDetallado(FechaDesde, FechaHasta);

        gastoArray.clear();
        gastoAdapter = new GastoMensualCustomAdapter(NuevoListadoActivity.this, R.layout.row_listado_gastos, gastoArray);
        gastoListDetallado.setAdapter(gastoAdapter);
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


            gastoListDetallado.setItemsCanFocus(false);
            gastoListDetallado.setAdapter(gastoAdapter);

        }
    }

    void actualizarListaDiario()
    {
        TVTotalDiario.setText(getString(R.string.totalDiario) + " 0.00");
        Calendar calendar = Calendar.getInstance();

        float xtotal = 0;
        calendar.set(DPFecha.getYear(), DPFecha.getMonth(), DPFecha.getDayOfMonth());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);


        long FechaDesde = calendar.getTimeInMillis();
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));

        //long FechaHasta = calendar.getTimeInMillis();
        long FechaHasta = FechaDesde;

        gastoModelArrayDiario = db.getGastosMes(FechaDesde,FechaHasta);

        gastoArrayDiario.clear();
        gastoAdapterDiario = new GastoCustomAdapter(NuevoListadoActivity.this, R.layout.row_listado_gastos_mensual, gastoArrayDiario);
        gastoListDiario.setAdapter(gastoAdapterDiario);
        gastoAdapterDiario.notifyDataSetChanged();

        if (gastoModelArrayDiario != null)
        {

            for (int i = 0; i < gastoModelArrayDiario.size(); ++i)
            {
                String xcategoria = gastoModelArrayDiario.get(i).categoria;
                float ximporte = gastoModelArrayDiario.get(i).importe;
                xtotal = xtotal + ximporte;

                gastoArrayDiario.add(new Gasto(0,
                        0,
                        xcategoria,
                        ximporte,
                        ""));

            }

            gastoListDiario.setItemsCanFocus(false);
            gastoListDiario.setAdapter(gastoAdapterDiario);

            TVTotalDiario.setText(getString(R.string.totalDiario)+" "+xtotal);
        }
    }

    public void setTabColor(TabHost tabhost) {

        for(int i=0;i<tabhost.getTabWidget().getChildCount();i++) {
            TextView tv = (TextView)  tabhost.getTabWidget().getChildAt(i).findViewById(android.R.id.title); //unselected
            tv.setTextColor(Color.parseColor("#ffffff"));
        }

        TextView tv = (TextView)  tabhost.getCurrentTabView().findViewById(android.R.id.title); //unselected
        tv.setTextColor(Color.parseColor("#ffffff"));
    }

}
