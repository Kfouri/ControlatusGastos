package com.kfouri.controlatusgastos.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kfouri.controlatusgastos.Utils.BaseDatos;
import com.kfouri.controlatusgastos.Beans.Categoria;
import com.kfouri.controlatusgastos.Beans.CategoriaModel;
import com.kfouri.controlatusgastos.R;

import java.util.ArrayList;

public class CategoriaCustomAdapter extends ArrayAdapter<Categoria>
{

    Context context;
    int layoutResourceId;
    ArrayList<Categoria> data = new ArrayList<Categoria>();

    public CategoriaCustomAdapter(Context context, int layoutResourceId, ArrayList<Categoria> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        CategoriaHolder holder = null;

        if (row == null)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new CategoriaHolder();
            holder.textId = (TextView) row.findViewById(R.id.categoria_id);
            holder.textDescripcion = (TextView) row.findViewById(R.id.textView1);
            holder.btnEdit = (Button) row.findViewById(R.id.button1);
            holder.btnDelete = (Button) row.findViewById(R.id.button2);
            row.setTag(holder);
        }
        else
        {
            holder = (CategoriaHolder) row.getTag();
        }

        final Categoria categoria = data.get(position);
        holder.textId.setText(String.valueOf(categoria.getId()));
        holder.textDescripcion.setText(categoria.getDescripcion());
        final CategoriaHolder finalHolder1 = holder;

        final BaseDatos db = new BaseDatos(context);

        holder.btnEdit.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

                    alertDialog.setTitle(context.getResources().getString(R.string.categoria_alert_dialog_editar_titulo));
                    alertDialog.setMessage(context.getResources().getString(R.string.categoria_alert_dialog_editar_descripcion));

                    final EditText input = new EditText(context);

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);

                    input.setLayoutParams(lp);
                    alertDialog.setView(input);
                    //alertDialog.setIcon(R.drawable.key);
                    input.setText(finalHolder1.textDescripcion.getText());
                    input.setSelection(input.getText().length());

                    alertDialog.setPositiveButton(context.getResources().getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    CategoriaModel cateModel = new CategoriaModel();
                                    cateModel.id = Long.parseLong(finalHolder1.textId.getText().toString());
                                    cateModel.descripcion = input.getText().toString();

                                    db.modificarCategoria(cateModel,finalHolder1.textDescripcion.getText().toString());

                                    categoria.setDescripcion(input.getText().toString());
                                    data.remove(categoria);
                                    data.add(categoria);
                                    notifyDataSetChanged();
                                    Toast.makeText(context.getApplicationContext(), context.getString(R.string.toastModificarCategoria),Toast.LENGTH_LONG).show();
                                }
                            });

                    alertDialog.setNegativeButton("NO",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                    alertDialog.show();

            }
        });

        final CategoriaHolder finalHolder = holder;
        holder.btnDelete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getResources().getString(R.string.categoria_alert_dialog_texto))
                .setCancelable(false)
                        .setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {

                                if (db.isEmptyCategoriaEnGastos(finalHolder.textDescripcion.getText().toString()))
                                {
                                    db.eliminarCategoria(Long.parseLong(finalHolder.textId.getText().toString()), finalHolder.textDescripcion.getText().toString());
                                    data.remove(categoria);
                                    notifyDataSetChanged();
                                    Toast.makeText(context.getApplicationContext(), context.getString(R.string.toastEliminarCategoria),Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    Toast.makeText(context.getApplicationContext(), context.getString(R.string.toastErrorEliminarCategoria),Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        return row;

    }

    static class CategoriaHolder {
        TextView textId;
        TextView textDescripcion;
        Button btnEdit;
        Button btnDelete;
    }

}
