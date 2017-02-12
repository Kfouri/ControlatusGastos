package com.kfouri.controlatusgastos.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kfouri.controlatusgastos.Utils.BaseDatos;
import com.kfouri.controlatusgastos.Beans.CategoriaModel;
import com.kfouri.controlatusgastos.Utils.ExpandableHeightListView;
import com.kfouri.controlatusgastos.Beans.Gasto;
import com.kfouri.controlatusgastos.Adapters.GastoCustomAdapter;
import com.kfouri.controlatusgastos.Beans.GastoModel;
import com.kfouri.controlatusgastos.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GastoActivity extends AppCompatActivity  {

    List<CategoriaModel> categoriaModelArray = new ArrayList<CategoriaModel>();
    List<GastoModel> gastoModelArray = new ArrayList<GastoModel>();
    ArrayList<Gasto> gastoArray = new ArrayList<Gasto>();
    private BaseDatos db = new BaseDatos(this);
    GastoCustomAdapter gastoAdapter;
    Spinner spnCategorias;
    private DatePicker DPFecha;
    private EditText ETImporte;
    private EditText ETObservacion;
    private TextView TVTotal;
    GastoModel gastoDetails;
    ExpandableHeightListView gastoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gasto);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DPFecha = (DatePicker) findViewById(R.id.fecha_gasto);
        
        spnCategorias = (Spinner) findViewById(R.id.spn_categorias);
        ETImporte = (EditText) findViewById(R.id.importe);
        ETObservacion = (EditText) findViewById(R.id.observacion);

        loadSpinnerData();

        /*
        TVTotal = (TextView) findViewById(R.id.total);

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

        loadSpinnerData();
        TVTotal.setText("0.00");
        */


    }

    /*
    private DatePicker.OnDateChangedListener dateSetListener = new DatePicker.OnDateChangedListener()
    {

        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            Calendar c = Calendar.getInstance();
            c.set(year, monthOfYear, dayOfMonth);

            Toast.makeText(GastoActivity.this, year + "/" + monthOfYear + "/" + dayOfMonth, Toast.LENGTH_SHORT).show();
        }
    };
    */

    private void loadSpinnerData()
    {
        List<String> categorias = new ArrayList<String>();
        categoriaModelArray = db.getCategorias();

        if (categoriaModelArray != null)
        {
            for (int i = 0; i < categoriaModelArray.size(); ++i)
            {
                categorias.add(categoriaModelArray.get(i).descripcion);
            }

            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, categorias);

            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            spnCategorias.setAdapter(dataAdapter);
        }
        else
        {
            new AlertDialog.Builder(GastoActivity.this)
                    .setTitle("Listado Categorías")
                    .setMessage("Error, No hay Categorías cargadas")
                    .setPositiveButton(getString(R.string.cerrar), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                GastoActivity.this.finish();
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }


    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        //actualizarLista();

    }

    void actualizarLista()
    {

        Calendar calendar = Calendar.getInstance();

        float xtotal = 0;

        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND, 0);


        long LFecha = calendar.getTimeInMillis();

        gastoModelArray = db.getGastosFecha(LFecha);

        gastoArray.clear();

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

            gastoAdapter = new GastoCustomAdapter(GastoActivity.this, R.layout.row_listado_gastos, gastoArray);
            gastoList.setItemsCanFocus(false);
            gastoList.setAdapter(gastoAdapter);

            TVTotal.setText(getString(R.string.total)+" "+xtotal);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nuevo_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_save:

                if (ETImporte.getText().toString().equals(""))
                {
                    Toast.makeText(GastoActivity.this, getString(R.string.errorImporteVacio), Toast.LENGTH_SHORT).show();
                }
                else if (ETImporte.getText().toString().equals("."))
                {
                    Toast.makeText(GastoActivity.this, getString(R.string.errorImporteNoValido), Toast.LENGTH_SHORT).show();
                }
                else if (Float.parseFloat(ETImporte.getText().toString()) == 0.0)
                {
                    Toast.makeText(GastoActivity.this, getString(R.string.errorImporteCero), Toast.LENGTH_SHORT).show();
                }
                else if (ETImporte.getText().toString().equals("0."))
                {
                    Toast.makeText(GastoActivity.this, getString(R.string.errorImporteCero), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    gastoDetails = new GastoModel();
                    updateModelFromLayout();
                    db.crearGasto(gastoDetails);

                    ETImporte.setText("");
                    ETObservacion.setText("");

                    Toast.makeText(GastoActivity.this, getString(R.string.gastoGuardado), Toast.LENGTH_SHORT).show();
                    //actualizarLista();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void updateModelFromLayout()
    {

        Calendar calendar = Calendar.getInstance();
        calendar.set(DPFecha.getYear(), DPFecha.getMonth(), DPFecha.getDayOfMonth(),0,0,0);
        calendar.set(Calendar.MILLISECOND, 0);

        long LFecha = calendar.getTimeInMillis();

        gastoDetails.fecha = LFecha;
        gastoDetails.categoria = spnCategorias.getItemAtPosition(spnCategorias.getSelectedItemPosition()).toString();
                //spnCategorias.getSelectedItem().toString();
        gastoDetails.importe = Float.parseFloat(ETImporte.getText().toString());
        gastoDetails.descripcion = ETObservacion.getText().toString();

    }
}
