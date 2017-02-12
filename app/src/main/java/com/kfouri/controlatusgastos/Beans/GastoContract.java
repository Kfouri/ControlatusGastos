package com.kfouri.controlatusgastos.Beans;

import android.provider.BaseColumns;

public final class GastoContract {
	
	public GastoContract() {}
	
	public static abstract class Gasto implements BaseColumns {
		public static final String TABLE_NAME = "gasto";
		public static final String COLUMN_FECHA = "fecha";
		public static final String COLUMN_IMPORTE = "importe";
		public static final String COLUMN_CATEGORIA = "categoria";
		public static final String COLUMN_DESCRIPCION = "descripcion";
		public static final String COLUMN_MES = "mes";
	}
	
}
