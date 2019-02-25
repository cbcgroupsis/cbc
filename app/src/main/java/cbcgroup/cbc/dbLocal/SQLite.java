package cbcgroup.cbc.dbLocal;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Map;

public class SQLite
{
    private static final String TAG = "SQLITE";

    public void Add(SQLiteDatabase db, String TABLA, Map Params)
    {
        Log.w(TAG,"Add row");                                                                  //Debug...Muestro en la consola
        String SQL="INSERT INTO "+TABLA+" ("+ InsertCampos( Params )+ ") VALUES ("+InsertValues( Params )+")";                     //Inserto los comandos SQL con los campos y sus respectivos valores.
        try{db.execSQL( SQL );}                                                                     //Ejecuto el script SQL.
        catch(Exception error){Log.w(TAG,"Error Add:"+error.toString());}                      //Debug...Muestro el mensaje de error que genera.
        db.close();                                                                                 //Cierro la conexion.
    }

    private String InsertValues(Map dato)
    {
        String camposSql="";
        for(int a=0;a<dato.size()-1;a++)
        {
            camposSql+= "'"+dato.get(  dato.keySet().toArray()[a].toString() )+"',";
        }
        camposSql+=" '"+dato.get(  dato.keySet().toArray()[dato.size()-1].toString() )+"' ";
        return camposSql;
    }

    private String InsertCampos(Map dato)
    {
        String camposSql="";
        for(int a=0;a<dato.size()-1;a++)
        {
            camposSql+=dato.keySet().toArray()[a].toString()+", ";
        }
        camposSql+=dato.keySet().toArray()[dato.size()-1].toString();
        return camposSql;
    }

    public void DeleteTabla(SQLiteDatabase db,String TABLA)                                         //Funciona
    {
        Log.w(TAG,"Delete tabla Db");                                                                  //Debug...
        String SQL="DELETE FROM "+TABLA;
        try
        {
            db.execSQL( SQL );
        }catch (Exception ignored){}

        db.close();
    }
}
