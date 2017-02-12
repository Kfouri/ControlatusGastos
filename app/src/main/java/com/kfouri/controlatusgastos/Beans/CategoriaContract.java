package com.kfouri.controlatusgastos.Beans;

import android.provider.BaseColumns;

/**
 * Created by MSI on 25/10/2015.
 */
public final class CategoriaContract
{
    public CategoriaContract()
    {}

    public static abstract class Categoria implements BaseColumns {
        public static final String TABLE_NAME = "categoria";
        public static final String COLUMN_DESCRIPCION = "descripcion";
    }
}
