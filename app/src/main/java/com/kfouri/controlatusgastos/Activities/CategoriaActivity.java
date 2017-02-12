package com.kfouri.controlatusgastos.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.kfouri.controlatusgastos.Utils.BaseDatos;
import com.kfouri.controlatusgastos.Beans.Categoria;
import com.kfouri.controlatusgastos.Adapters.CategoriaCustomAdapter;
import com.kfouri.controlatusgastos.Beans.CategoriaModel;
import com.kfouri.controlatusgastos.R;

import java.util.ArrayList;
import java.util.List;

public class CategoriaActivity extends AppCompatActivity {


    CategoriaModel categoriaDetails;

    ListView categoriaList;
    CategoriaCustomAdapter categoriaAdapter;
    ArrayList<Categoria> userArray = new ArrayList<Categoria>();
    private BaseDatos db = new BaseDatos(this);
    List<CategoriaModel> categoriaModelArray = new ArrayList<CategoriaModel>();
    private EditText descripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        descripcion = (EditText) findViewById(R.id.descripcion);

        categoriaList = (ListView) findViewById(R.id.listView);
        categoriaList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    final int position, long id) {
                Log.i("List View Clicked", "**********");
                Toast.makeText(CategoriaActivity.this, "List View Clicked:" + position, Toast.LENGTH_LONG).show();
            }
        });


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        categoriaModelArray = db.getCategorias();

        userArray.clear();

        if (categoriaModelArray != null)
        {
            for (int i = 0; i < categoriaModelArray.size(); ++i) {
                userArray.add(new Categoria(categoriaModelArray.get(i).descripcion, categoriaModelArray.get(i).id));

            }

            categoriaAdapter = new CategoriaCustomAdapter(CategoriaActivity.this, R.layout.row_listado_categoria, userArray);
            categoriaList.setItemsCanFocus(false);
            categoriaList.setAdapter(categoriaAdapter);
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

        switch (id) {
            case R.id.action_save:

                categoriaDetails = new CategoriaModel();
                updateModelFromLayout();

                if (db.existeCategoria(categoriaDetails.descripcion.toUpperCase()))
                {
                    Toast.makeText(CategoriaActivity.this, getString(R.string.errorCategoriaYaExiste), Toast.LENGTH_LONG).show();
                }
                else
                {

                    db.crearCategoria(categoriaDetails);
                    descripcion.setText("");

                    onResume();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void updateModelFromLayout()
    {

        categoriaDetails.descripcion = descripcion.getText().toString();

    }
}
