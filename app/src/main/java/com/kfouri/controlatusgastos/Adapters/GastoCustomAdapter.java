package com.kfouri.controlatusgastos.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.kfouri.controlatusgastos.Activities.NuevoGastoActivity;
import com.kfouri.controlatusgastos.Beans.Gasto;
import com.kfouri.controlatusgastos.R;
import com.kfouri.controlatusgastos.Utils.BaseDatos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class GastoCustomAdapter extends ArrayAdapter<Gasto>
{

    Context context;
    int layoutResourceId;
    ArrayList<Gasto> data = new ArrayList<Gasto>();

    public GastoCustomAdapter(Context context, int layoutResourceId, ArrayList<Gasto> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;

        this.data = data;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        GastoHolder holder = null;

        if (row == null)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new GastoHolder();
            holder.gasto_id = (TextView) row.findViewById(R.id.gasto_id);
            holder.gasto_fecha = (TextView) row.findViewById(R.id.gasto_fecha);
            holder.gasto_categoria = (TextView) row.findViewById(R.id.gasto_categoria);
            holder.gasto_importe = (TextView) row.findViewById(R.id.gasto_importe);
            holder.gasto_mes = (TextView) row.findViewById(R.id.gasto_mes);

            holder.btnDelete = (Button) row.findViewById(R.id.button2);
            row.setTag(holder);
        }
        else
        {
            holder = (GastoHolder) row.getTag();
        }

        final Gasto gasto = data.get(position);
        holder.gasto_id.setText(String.valueOf(gasto.getId()));

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(gasto.getFecha());

        holder.gasto_fecha.setText(String.valueOf(formatter.format(calendar.getTime())));

        holder.gasto_categoria.setText(String.valueOf(gasto.getCategoria()));
        holder.gasto_importe.setText(String.valueOf(gasto.getImporte()));
        holder.gasto_mes.setText(String.valueOf(gasto.getMes()));

        final BaseDatos db = new BaseDatos(context);

        final GastoHolder finalHolder = holder;
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                String mensaje = context.getResources().getString(R.string.gasto_alert_dialog_texto);
                String texto = mensaje.replace("#",String.valueOf(gasto.getCategoria()) + " - "+String.valueOf(gasto.getImporte()));

                builder.setMessage(texto)
                        .setCancelable(false)
                        .setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                db.eliminarGasto(Long.parseLong(finalHolder.gasto_id.getText().toString()));

                                //String importe_temp = ((NuevoGastoActivity) context).TVTotal.getText().toString();

                                 float importe_temp = Float.parseFloat(((NuevoGastoActivity) context).TVTotal.getText().toString());

                                 float res = importe_temp - gasto.getImporte();
                                 ((NuevoGastoActivity) context).TVTotal.setText("" + res);


                                /*
                                if (((NuevoListadoActivity)context).active)
                                {
                                    float importe_temp = Float.parseFloat(((NuevoListadoActivity) context).TVTotalMensual.getText().toString());

                                    float res = importe_temp - gasto.getImporte();
                                    ((NuevoListadoActivity) context).TVTotalMensual.setText("" + res);
                                }
*/
                                data.remove(gasto);
                                notifyDataSetChanged();

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

        if (holder.gasto_id.getText().equals("-1"))
        {
            holder.gasto_id.setVisibility(View.GONE);
            holder.gasto_fecha.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }

        if (holder.gasto_categoria.getText().equals("Total"))
        {
            holder.gasto_importe.setTypeface(null, Typeface.BOLD);
            holder.gasto_categoria.setTypeface(null, Typeface.BOLD);
        }
        else
        {
            holder.gasto_importe.setTypeface(null, Typeface.NORMAL);
            holder.gasto_categoria.setTypeface(null, Typeface.NORMAL);
        }

        return row;

    }

    static class GastoHolder {
        TextView gasto_id;
        TextView gasto_fecha;
        TextView gasto_categoria;
        TextView gasto_importe;
        TextView gasto_mes;

        Button btnDelete;
    }

}
