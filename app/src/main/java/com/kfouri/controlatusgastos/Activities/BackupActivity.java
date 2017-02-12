package com.kfouri.controlatusgastos.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kfouri.controlatusgastos.Beans.GastoContract;
import com.kfouri.controlatusgastos.R;
import com.kfouri.controlatusgastos.Utils.BaseDatos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


public class BackupActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnUpload, btnDownload;
    private Button btnExportarExcel,btnExportarExcelMail;
    private BaseDatos db = new BaseDatos(this);

    private final String DIR = "/";
    private File f;
    private int buffKey = 0;
    private int selected = 0;
    private TextView UltimoBackup,Carpeta;

    private String fileName = "TodoList.xls";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnDownload = (Button) findViewById(R.id.btnDownload);
        btnUpload = (Button) findViewById(R.id.btnUploadPhoto);
        btnExportarExcel = (Button) findViewById(R.id.btnExportarExcel);
        btnExportarExcelMail = (Button) findViewById(R.id.btnExportarExcelMail);

        btnUpload.setOnClickListener(this);
        btnDownload.setOnClickListener(this);
        btnExportarExcel.setOnClickListener(this);
        btnExportarExcelMail.setOnClickListener(this);

        UltimoBackup = (TextView) findViewById(R.id.ultimo_backup);
        Carpeta = (TextView) findViewById(R.id.carpeta);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Date fecha=new Date(prefs.getLong("TiempoBackup", 0));

        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);

        UltimoBackup.setText(getString(R.string.UltimaFechaBackup)+" "+formato.format(fecha));
    }

    @Override
    public void onClick(View v)
    {
        if (v == btnDownload)
        {
            exportDB();
        }
        else if (v == btnUpload)
        {
            importDB();
        }
        else if (v == btnExportarExcel)
        {
            if (Excel())
            {
                File sdCard = Environment.getExternalStorageDirectory();

                File file = new File(sdCard.getAbsolutePath() + "/ControlaTusGastos", fileName);

                Carpeta.setText(getString(R.string.carpeta) + " " + sdCard.getAbsolutePath() + "/ControlaTusGastos/"+ fileName);

                Toast.makeText(getApplicationContext(), "Finish!!!",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.ms-excel");
                startActivity(intent);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Error!!!",Toast.LENGTH_SHORT).show();
            }
        }
        else if (v == btnExportarExcelMail)
        {
            if (Excel())
            {
                File sdCard = Environment.getExternalStorageDirectory();
                Carpeta.setText(getString(R.string.carpeta) + " " + sdCard.getAbsolutePath() + "/ControlaTusGastos/"+ fileName);

                Toast.makeText(getApplicationContext(), "Finish!!!",Toast.LENGTH_SHORT).show();

                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("application/image");
                //emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{strEmail});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,getString(R.string.app_name));
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Backup "+getString(R.string.app_name));
                emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + sdCard.getAbsolutePath() + "/ControlaTusGastos/"+ fileName));
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Error!!!",Toast.LENGTH_SHORT).show();
            }
        }
    }


    private boolean Excel()
    {

        Cursor cursor = db.getTodosGastos();

        //Saving file in external storage
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath() + "/ControlaTusGastos");

        //create directory if not exist
        if(!directory.isDirectory()){
            directory.mkdirs();
        }

        //file path
        File file = new File(directory, fileName);

        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));
        WritableWorkbook workbook;

        try {
            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet(getString(R.string.app_name), 0);

            try {
                sheet.addCell(new Label(0, 0, getString(R.string.fecha))); // column and row
                sheet.addCell(new Label(1, 0, getString(R.string.Categoria)));
                sheet.addCell(new Label(2, 0, getString(R.string.Importe)));
                sheet.addCell(new Label(3, 0, getString(R.string.Descripcion)));

                if (cursor.moveToFirst()) {
                    do {
                        long fecha = cursor.getLong(cursor.getColumnIndex(GastoContract.Gasto.COLUMN_FECHA));
                        String categoria= cursor.getString(cursor.getColumnIndex(GastoContract.Gasto.COLUMN_CATEGORIA));
                        float importe = cursor.getFloat(cursor.getColumnIndex(GastoContract.Gasto.COLUMN_IMPORTE));
                        String descripcion = cursor.getString(cursor.getColumnIndex(GastoContract.Gasto.COLUMN_DESCRIPCION));

                        int i = cursor.getPosition() + 1;

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(fecha);
                        int mYear = calendar.get(Calendar.YEAR);
                        int mMonth = calendar.get(Calendar.MONTH)+1;
                        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                        String strFecha = mDay+"/"+mMonth+"/"+mYear;

                        sheet.addCell(new Label(0, i, strFecha));
                        sheet.addCell(new Label(1, i, categoria));
                        sheet.addCell(new Label(2, i, String.valueOf(importe)));
                        sheet.addCell(new Label(3, i, descripcion));

                    } while (cursor.moveToNext());
                }
                //closing cursor
                cursor.close();
            } catch (RowsExceededException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }
            workbook.write();
            try {
                workbook.close();

                return true;

            } catch (WriteException e) {
                e.printStackTrace();
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }


    private void ExcelMail()
    {

    }

    private List<File> getListFiles(File parentDir)
    {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        for (File file : files)
        {
            if (file.isDirectory())
            {
                inFiles.addAll(getListFiles(file));
            }
            else
            {
                if(file.getName().endsWith(".db"))
                {
                    inFiles.add(file);
                }
            }
        }
        return inFiles;
    }

    private void importDB()
    {


        List<File> files = getListFiles(new File(Environment.getExternalStorageDirectory() + "//ControlaTusGastos"));

        List<String> listItems = new ArrayList<String>();

        for (File file : files)
        {
            listItems.add(file.getName());
        }

        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(BackupActivity.this);
        builder.setTitle(getString(R.string.Restaurar));
        builder.setIcon(R.drawable.backup);
        builder.setCancelable(false);
        builder.setSingleChoiceItems(items, selected, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog,int which)
            {
                buffKey = which;
            }
        });

        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        String ItemSeleccionado = items[buffKey].toString();
                        selected = buffKey;

                        RestaurarBase(ItemSeleccionado);

                    }
                });
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(BackupActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void RestaurarBase(String archivo)
    {
        try
        {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite())
            {
                String currentDBPath = "//data//"+getApplicationContext().getPackageName()+"//databases//"+BaseDatos.DATABASE_NAME;
                String backupDBPath = "//ControlaTusGastos//"+archivo;
                File backupDB = new File(data, currentDBPath);
                File currentDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getApplicationContext(), "Import Successful!",Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "Import Failed!", Toast.LENGTH_SHORT).show();
        }

    }

    private void exportDB()
    {
        try
        {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite())
            {
                File folder = new File(Environment.getExternalStorageDirectory() + "//ControlaTusGastos");
                boolean success = true;
                if (!folder.exists())
                {
                    success = folder.mkdir();
                }

                if (success)
                {
                    Date hoy = Calendar.getInstance().getTime();
                    DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                    String today = formatter.format(hoy);

                    String currentDBPath = "//data//"+getApplicationContext().getPackageName()+"//databases//" + BaseDatos.DATABASE_NAME;

                    String base = BaseDatos.DATABASE_NAME.substring(0,BaseDatos.DATABASE_NAME.length()-3);
                    String backupDBPath = "//ControlaTusGastos//"+base+"_"+today+".db";

                    File currentDB = new File(data, currentDBPath);
                    File backupDB = new File(sd, backupDBPath);

                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                    SharedPreferences.Editor editor = prefs.edit();
                    Date date = new Date(System.currentTimeMillis());
                    editor.putLong("TiempoBackup", date.getTime());
                    editor.commit();

                    Toast.makeText(getApplicationContext(), "Backup Successful!", Toast.LENGTH_SHORT).show();

                    SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);

                    UltimoBackup.setText(getString(R.string.UltimaFechaBackup) + " " + formato.format(date));
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Error al crear la carpeta", Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "Backup Failed!"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
