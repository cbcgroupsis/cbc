package cbcgroup.cbc.dbLocal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cbcgroup.cbc.dbLocal.Tablas.dbInsumos;
import cbcgroup.cbc.dbLocal.Tablas.dbInsumosSinInternet;
import cbcgroup.cbc.dbLocal.Tablas.dbNombresTecSa;
import cbcgroup.cbc.dbLocal.Tablas.dbTecSinInternet;
import cbcgroup.cbc.dbLocal.Tablas.dbTecnicos;

public class ConnSQLiteHelper extends SQLiteOpenHelper
{

    public ConnSQLiteHelper(Context context)
    {
        super( context, "db_CBC", null, 2 );
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL( dbInsumos.CREATE_TABLE );
        db.execSQL( dbTecSinInternet.CREATE_TABLE );
        db.execSQL( dbNombresTecSa.CREATE_TABLE );
        db.execSQL( dbInsumosSinInternet.CREATE_TABLE );
        db.execSQL( dbTecnicos.CREATE_TABLE );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if(oldVersion!=newVersion)
        {
            db.execSQL( "DROP TABLE IF EXISTS "+ dbTecSinInternet.TABLE );
            db.execSQL( "DROP TABLE IF EXISTS "+ dbNombresTecSa.TABLE );
            db.execSQL( "DROP TABLE IF EXISTS "+ dbInsumos.TABLE );
            db.execSQL( "DROP TABLE IF EXISTS "+ dbInsumosSinInternet.TABLE );
            db.execSQL( "DROP TABLE IF EXISTS "+ dbTecnicos.TABLE );
            onCreate( db );
        }
    }
}
