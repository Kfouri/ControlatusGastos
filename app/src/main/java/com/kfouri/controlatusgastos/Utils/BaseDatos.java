package com.kfouri.controlatusgastos.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kfouri.controlatusgastos.Beans.CategoriaContract;
import com.kfouri.controlatusgastos.Beans.CategoriaModel;
import com.kfouri.controlatusgastos.Beans.GastoContract;
import com.kfouri.controlatusgastos.Beans.GastoModel;

import java.util.ArrayList;
import java.util.List;

public class BaseDatos extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "base.db";
	
	private static final String SQL_CREATE_GASTOS = "CREATE TABLE " + GastoContract.Gasto.TABLE_NAME + " (" +
			GastoContract.Gasto._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
			GastoContract.Gasto.COLUMN_FECHA + " INTEGER," +
			GastoContract.Gasto.COLUMN_CATEGORIA + " TEXT," +
			GastoContract.Gasto.COLUMN_IMPORTE + " REAL," +
			GastoContract.Gasto.COLUMN_DESCRIPCION + " TEXT " + " )";
	
	private static final String SQL_DELETE_GASTOS =
		    "DROP TABLE IF EXISTS " + GastoContract.Gasto.TABLE_NAME;

	private static final String SQL_CREATE_CATEGORIAS = "CREATE TABLE " + CategoriaContract.Categoria.TABLE_NAME + " (" +
			CategoriaContract.Categoria._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
			CategoriaContract.Categoria.COLUMN_DESCRIPCION + " TEXT" + " )";

	private static final String SQL_DELETE_CATEGORIAS =
			"DROP TABLE IF EXISTS " + CategoriaContract.Categoria.TABLE_NAME;

	public BaseDatos(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(SQL_CREATE_GASTOS);
		db.execSQL(SQL_CREATE_CATEGORIAS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_GASTOS);
		db.execSQL(SQL_DELETE_CATEGORIAS);

		onCreate(db);
	}


	private CategoriaModel populateCategoriaModel(Cursor c) {
		CategoriaModel model = new CategoriaModel();
		model.id = c.getLong(c.getColumnIndex(GastoContract.Gasto._ID));
		model.descripcion = c.getString(c.getColumnIndex(CategoriaContract.Categoria.COLUMN_DESCRIPCION));

		return model;
	}

	private GastoModel populateGastoModel(Cursor c) {
		GastoModel model = new GastoModel();
		model.id = c.getLong(c.getColumnIndex(GastoContract.Gasto._ID));
		model.fecha = c.getLong(c.getColumnIndex(GastoContract.Gasto.COLUMN_FECHA));
		model.categoria = c.getString(c.getColumnIndex(GastoContract.Gasto.COLUMN_CATEGORIA));
		model.importe = c.getFloat(c.getColumnIndex(GastoContract.Gasto.COLUMN_IMPORTE));
		model.descripcion = c.getString(c.getColumnIndex(GastoContract.Gasto.COLUMN_DESCRIPCION));
		
		return model;
	}

	private GastoModel populateGastoModelMensual(Cursor c) {
		GastoModel model = new GastoModel();
		model.categoria = c.getString(c.getColumnIndex(GastoContract.Gasto.COLUMN_CATEGORIA));
		model.importe = c.getFloat(c.getColumnIndex(GastoContract.Gasto.COLUMN_IMPORTE));

		return model;
	}

	private GastoModel populateGastoModelAnual(Cursor c) {
		GastoModel model = new GastoModel();
		model.Mes = c.getString(c.getColumnIndex(GastoContract.Gasto.COLUMN_MES));
		model.categoria = c.getString(c.getColumnIndex(GastoContract.Gasto.COLUMN_CATEGORIA));
		model.importe = c.getFloat(c.getColumnIndex(GastoContract.Gasto.COLUMN_IMPORTE));

		return model;
	}

	private ContentValues populateGastoContent(GastoModel model) {
		ContentValues values = new ContentValues();

        values.put(GastoContract.Gasto.COLUMN_FECHA, model.fecha);
        values.put(GastoContract.Gasto.COLUMN_CATEGORIA, model.categoria);
        values.put(GastoContract.Gasto.COLUMN_IMPORTE, model.importe);
        values.put(GastoContract.Gasto.COLUMN_DESCRIPCION, model.descripcion);

        return values;
	}

	private ContentValues populateCategoriaContent(CategoriaModel model) {
		ContentValues values = new ContentValues();

		values.put(CategoriaContract.Categoria.COLUMN_DESCRIPCION, model.descripcion);

		return values;
	}

	public long crearCategoria(CategoriaModel model)
	{
		ContentValues values = populateCategoriaContent(model);
		return getWritableDatabase().insert(CategoriaContract.Categoria.TABLE_NAME, null, values);
	}

	public boolean existeCategoria(String descripcion)
	{
		SQLiteDatabase db = this.getReadableDatabase();

		String select = "SELECT count(1) cnt " +
				" FROM " + CategoriaContract.Categoria.TABLE_NAME +
				" WHERE upper(" + CategoriaContract.Categoria.COLUMN_DESCRIPCION +")='"+descripcion+"'";

		Cursor c = db.rawQuery(select, null);

		int cnt=0;

		while (c.moveToNext())
		{
			cnt = c.getInt(c.getColumnIndex("cnt"));
		}

		if (cnt>0)
		{
			return true;
		}
		else
		{
			return false;
		}

	}

	public long crearGasto(GastoModel model)
	{
		ContentValues values = populateGastoContent(model);
        return getWritableDatabase().insert(GastoContract.Gasto.TABLE_NAME, null, values);
	}
	
	public long modificarGasto(GastoModel model)
	{
		ContentValues values = populateGastoContent(model);
        return getWritableDatabase().update(GastoContract.Gasto.TABLE_NAME, values, GastoContract.Gasto._ID + " = ?", new String[]{String.valueOf(model.id)});
	}

	public long modificarCategoria(CategoriaModel model,String categoriaAntigua)
	{
		ContentValues values = populateCategoriaContent(model);
		ContentValues gastoValues = new ContentValues();

		gastoValues.put(GastoContract.Gasto.COLUMN_CATEGORIA, model.descripcion);

		getWritableDatabase().update(GastoContract.Gasto.TABLE_NAME, gastoValues, GastoContract.Gasto.COLUMN_CATEGORIA + " = ?", new String[] { categoriaAntigua });

		return getWritableDatabase().update(CategoriaContract.Categoria.TABLE_NAME, values, CategoriaContract.Categoria._ID + " = ?", new String[]{String.valueOf(model.id)});
	}

	public List<GastoModel> getGastosFecha(long fecha)
	{
		SQLiteDatabase db = this.getReadableDatabase();

		String select = "SELECT * FROM " + GastoContract.Gasto.TABLE_NAME + " WHERE " + GastoContract.Gasto.COLUMN_FECHA + " = " + fecha + " order by " + GastoContract.Gasto.COLUMN_CATEGORIA;

		Cursor c = db.rawQuery(select, null);

		List<GastoModel> gastoList = new ArrayList<GastoModel>();

		while (c.moveToNext()) {
			gastoList.add(populateGastoModel(c));
		}

		if (!gastoList.isEmpty()) {
			return gastoList;
		}

		return null;
	}

	public List<GastoModel> getGastosMesDetallado(long fechaDesde,long fechaHasta)
	{
		SQLiteDatabase db = this.getReadableDatabase();

		String select = "SELECT * " +
				        "FROM " + GastoContract.Gasto.TABLE_NAME +
				        " WHERE "+GastoContract.Gasto.COLUMN_FECHA+" between " + fechaDesde + " and " + fechaHasta +
				        " order by " + GastoContract.Gasto.COLUMN_FECHA +","+ GastoContract.Gasto.COLUMN_CATEGORIA;

		Cursor c = db.rawQuery(select, null);

		List<GastoModel> gastoList = new ArrayList<GastoModel>();

		while (c.moveToNext())
		{
			gastoList.add(populateGastoModel(c));
		}

		if (!gastoList.isEmpty())
		{
			return gastoList;
		}

		return null;
	}

	public Cursor getTodosGastos()
	{
		SQLiteDatabase db = this.getReadableDatabase();

		String select = "SELECT * " +
				        "FROM " + GastoContract.Gasto.TABLE_NAME +
				        " order by fecha, categoria";

		Cursor c = db.rawQuery(select, null);

		return c;
	}

	public List<GastoModel> getGastosMes(long fechaDesde,long fechaHasta)
	{
		SQLiteDatabase db = this.getReadableDatabase();

		String select = "SELECT categoria,sum(importe) importe " +
				"FROM " + GastoContract.Gasto.TABLE_NAME +
				" WHERE fecha between " + fechaDesde + " and " + fechaHasta +
				" group by categoria order by " + GastoContract.Gasto.COLUMN_CATEGORIA;

		Cursor c = db.rawQuery(select, null);

		List<GastoModel> gastoList = new ArrayList<GastoModel>();

		while (c.moveToNext())
		{
			gastoList.add(populateGastoModelMensual(c));
		}

		if (!gastoList.isEmpty())
		{
			return gastoList;
		}

		return null;
	}

	public GastoModel getGasto(long id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
        String select = "SELECT * FROM " + GastoContract.Gasto.TABLE_NAME + " WHERE " + GastoContract.Gasto._ID + " = " + id;
		
		Cursor c = db.rawQuery(select, null);
		
		if (c.moveToNext()) {
			return populateGastoModel(c);
		}
		
		return null;
	}

	public CategoriaModel getCategoria(long id)
	{
		SQLiteDatabase db = this.getReadableDatabase();

		String select = "SELECT * FROM " + CategoriaContract.Categoria.TABLE_NAME + " WHERE " + CategoriaContract.Categoria._ID + " = " + id;

		Cursor c = db.rawQuery(select, null);

		if (c.moveToNext()) {
			return populateCategoriaModel(c);
		}

		return null;
	}

	public List<GastoModel> getGastos() {
		SQLiteDatabase db = this.getReadableDatabase();
		
        String select = "SELECT * FROM " + GastoContract.Gasto.TABLE_NAME+ " order by "+GastoContract.Gasto.COLUMN_FECHA+" desc";
		
		Cursor c = db.rawQuery(select, null);
		
		List<GastoModel> gastoList = new ArrayList<GastoModel>();
		
		while (c.moveToNext()) {
			gastoList.add(populateGastoModel(c));
		}
		
		if (!gastoList.isEmpty()) {
			return gastoList;
		}
		
		return null;
	}

	public List<GastoModel> getGastosAnual(long fechaDesde,long fechaHasta)
	{
		SQLiteDatabase db = this.getReadableDatabase();

		float sumaMensual;
		String MesAnterior;

		String select = "SELECT strftime('%m', fecha / 1000, 'unixepoch') mes,categoria,sum(importe) importe " +
				        "FROM " + GastoContract.Gasto.TABLE_NAME +
				        " WHERE fecha between " + fechaDesde + " and " + fechaHasta +
				        " group by strftime('%m', fecha / 1000, 'unixepoch'),categoria " +
				        "order by mes," + GastoContract.Gasto.COLUMN_CATEGORIA;

		Cursor c = db.rawQuery(select, null);

		List<GastoModel> gastoList = new ArrayList<GastoModel>();

		sumaMensual = 0;
		MesAnterior = "X";

		while (c.moveToNext())
		{
			if (sumaMensual == 0)
			{
				sumaMensual = c.getFloat(c.getColumnIndex(GastoContract.Gasto.COLUMN_IMPORTE));
			}
			else
			{
				if (MesAnterior.equals(c.getString(c.getColumnIndex(GastoContract.Gasto.COLUMN_MES))))
				{
					sumaMensual = sumaMensual + c.getFloat(c.getColumnIndex(GastoContract.Gasto.COLUMN_IMPORTE));
				}
				else
				{
					GastoModel model = new GastoModel();
					model.Mes = "";
					model.categoria = "Total";
					model.importe = sumaMensual;

					gastoList.add(model);
					sumaMensual = c.getFloat(c.getColumnIndex(GastoContract.Gasto.COLUMN_IMPORTE));
				}
			}
			MesAnterior = c.getString(c.getColumnIndex(GastoContract.Gasto.COLUMN_MES));
			gastoList.add(populateGastoModelAnual(c));
		}

		GastoModel model = new GastoModel();
		model.Mes = "";
		model.categoria = "Total";
		model.importe = sumaMensual;

		gastoList.add(model);

		if (!gastoList.isEmpty())
		{
			return gastoList;
		}

		return null;
	}

	public List<CategoriaModel> getCategorias() {
		SQLiteDatabase db = this.getReadableDatabase();

		String select = "SELECT * FROM " + CategoriaContract.Categoria.TABLE_NAME+ " order by "+CategoriaContract.Categoria.COLUMN_DESCRIPCION;

		Cursor c = db.rawQuery(select, null);

		List<CategoriaModel> categoriaList = new ArrayList<CategoriaModel>();

		while (c.moveToNext()) {
			categoriaList.add(populateCategoriaModel(c));
		}

		if (!categoriaList.isEmpty()) {
			return categoriaList;
		}

		return null;
	}

	public boolean isEmptyCategoriaEnGastos(String descripcion)
	{
		SQLiteDatabase db = this.getReadableDatabase();

		String select = "SELECT count(1) cnt " +
				        " FROM " + GastoContract.Gasto.TABLE_NAME +
				        " WHERE "+GastoContract.Gasto.COLUMN_CATEGORIA +"='"+descripcion+"'";

		Cursor c = db.rawQuery(select, null);

		int cnt=0;

		while (c.moveToNext())
		{
		    cnt = c.getInt(c.getColumnIndex("cnt"));
		}

		if (cnt>0)
		{
			return false;
		}
		else
		{
			return true;
		}

	}

	public int eliminarGasto(long id)
	{
		return getWritableDatabase().delete(GastoContract.Gasto.TABLE_NAME, GastoContract.Gasto._ID + " = ?", new String[] { String.valueOf(id) });
	}

	public int eliminarCategoria(long id,String descripcion)
	{

		ContentValues gastoValues = new ContentValues();

		gastoValues.put(GastoContract.Gasto.COLUMN_CATEGORIA, "");

		getWritableDatabase().update(GastoContract.Gasto.TABLE_NAME, gastoValues, GastoContract.Gasto.COLUMN_CATEGORIA + " = ?", new String[] { descripcion });

		return getWritableDatabase().delete(CategoriaContract.Categoria.TABLE_NAME, CategoriaContract.Categoria._ID + " = ?", new String[] { String.valueOf(id) });
	}

}
