package cbcgroup.cbc.Clases;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Hashtable;
import java.util.Map;

import cbcgroup.cbc.Activity.HomeActivity;
import cbcgroup.cbc.Activity.TecnicosOut;
import cbcgroup.cbc.dbLocal.ConnSQLiteHelper;
import cbcgroup.cbc.dbLocal.SQLite;
import cbcgroup.cbc.dbLocal.Tablas.dbInsumos;
import cbcgroup.cbc.dbLocal.Tablas.dbInsumosSinInternet;
import cbcgroup.cbc.dbLocal.Tablas.dbTecSinInternet;

public class NetWorkStateChecker extends BroadcastReceiver {

    //context and database helper object
    private String URLIMAGEN = "http://tecnicos.cbcgroup.com.ar/test/app_android/v14/imagenTecnico.php";
    String URL = "http://tecnicos.cbcgroup.com.ar/Test/app_android/imagen.php";
    private Context context;
    private ConnSQLiteHelper con;
    private SQLite sql;
    CBC cbc;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        con =  new ConnSQLiteHelper(context);
        sql = new SQLite();
        cbc = new CBC(context);
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //if there is a network
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
              //  Toast.makeText(context,"hay internet",Toast.LENGTH_LONG).show();

                    //if(cbc.getdUserSector()=="deposito" || cbc.getdUserSector()=="super admin"  || cbc.getdUserSector()=="comercial")uploadImage();
                    //if(cbc.getdUserSector()=="tecnicos" || cbc.getdUserSector()=="super admin"  || cbc.getdUserSector()=="comercial")uploadImage2();
                        Log.w("SINCONEXCION","hay Internet");
                    uploadImage2();
                    uploadImage();

            }Log.w("SINCONEXCION","NO HAY Internet");
        }Log.w("SINCONEXCION","NO HAY hay Internet");
    }


    Map<String,String> Mostrar()
    {
        SQLiteDatabase db = con.getWritableDatabase();
        Map<String, String> params = new Hashtable<>();
        String SQL="SELECT id_tec,serie,id_parte,mensaje,copias,copiasColor,viaje,foto,Espera FROM "+ dbTecSinInternet.TABLE;
        Cursor resp=db.rawQuery( SQL,null );
        if(resp==null) return null;
            if(resp.moveToPosition( 0 ))
            {
                Log.w("SINCONEXCION","idTec->"+resp.getString( 0 ));
                Log.w("SINCONEXCION","serie->"+resp.getString( 1 ));
                Log.w("SINCONEXCION","idparte->"+resp.getString( 2 ));
                Log.w("SINCONEXCION","mensaje->"+resp.getString( 3 ));
                Log.w("SINCONEXCION","copias->"+resp.getString( 4 ));
                Log.w("SINCONEXCION","copiasColor->"+resp.getString( 5 ));
                Log.w("SINCONEXCION","viaje->"+resp.getString( 6 ));
                Log.w("SINCONEXCION","foto->"+resp.getString( 7 ));
                Log.w("SINCONEXCION","Espera->"+resp.getString( 8 ));

                params.put("foto", resp.getString( 7 ));
                params.put("nparte",resp.getString( 2 ));
                params.put("id_tecnico",resp.getString( 0 ));
                params.put("serie",resp.getString( 1 ));
                params.put("id_parte", resp.getString( 2 ));
                params.put("mensaje",resp.getString( 3 ));
                params.put("copias",resp.getString( 4 ));
                params.put("copiasColor",resp.getString( 5 ));
                params.put("viaje",resp.getString( 6 ));

            }

        db.close();
        return params;
    }

    private void uploadImage() {
        //final ProgressDialog loading = ProgressDialog.show(this, "Subiendo...", "Espere por favor...", false, false);
        SQLiteDatabase db = con.getWritableDatabase();
        String SQL = "";
        String idParte="";
        SQL = "SELECT id_tec,serie,id_parte,mensaje,copias,copiasColor,viaje,foto,Espera FROM " + dbTecSinInternet.TABLE;
        Cursor resp = db.rawQuery( SQL, null );
        if (resp.moveToPosition( 0 )) {

            idParte = resp.getString( 2 );
        }
        db.close();
        if (resp.getCount() != 0 && resp!=null)
        {
            StringRequest stringRequest = new StringRequest( Request.Method.POST, URLIMAGEN,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            SQLiteDatabase db = con.getWritableDatabase();
                            String idParte = "";
                            String SQL = "";
                            SQL = "SELECT id_tec,serie,id_parte,mensaje,copias,copiasColor,viaje,foto,Espera FROM " + dbTecSinInternet.TABLE;
                            Cursor resp = db.rawQuery( SQL, null );
                            if (resp.moveToPosition( 0 )) {

                                idParte = resp.getString( 2 );
                            }

                            db.close();
                            if (resp.getCount() != 0) {
                                db = con.getWritableDatabase();
                                SQL = "DELETE FROM " + dbTecSinInternet.TABLE + " WHERE id_parte='" + idParte + "';";
                                try {
                                    db.execSQL( SQL );
                                    Log.w( "SINCONEXCION", "FILA BORRADA" );
                                    uploadImage();
                                    db.close();


                                } catch (Exception e) {
                                    Log.w( "SINCONEXCION", e.toString() );
                                }
                                db.close();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                        }
                    } ) {
                @Override
                protected Map<String, String> getParams() {
                    return Mostrar();
                }
            };
            stringRequest.setRetryPolicy( new DefaultRetryPolicy(
                    5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ) );
            RequestQueue requestQueue = Volley.newRequestQueue( context );
            requestQueue.add( stringRequest );
        }
    }

    /******************************************************/
    Map<String,String> Mostrar2()
    {
        SQLiteDatabase db = con.getWritableDatabase();
        Map<String, String> params = new Hashtable<>();
        String SQL="SELECT Nombre,Serie,Foto,nPedido FROM "+ dbInsumosSinInternet.TABLE;
        Cursor resp=db.rawQuery( SQL,null );
        if(resp==null) return null;
        if(resp.moveToPosition( 0 ))
        {
            params.put("name", resp.getString( 0 ));
            params.put("serie",resp.getString( 1 ));
            params.put("foto", resp.getString( 2 ));
            params.put("nombre", resp.getString( 3 ));

        }

        db.close();
        return params;
    }
    private void uploadImage2() {
        //final ProgressDialog loading = ProgressDialog.show(this, "Subiendo...", "Espere por favor...", false, false);
        SQLiteDatabase db = con.getWritableDatabase();
        String SQL = "";
        String idParte="";
        SQL="SELECT Nombre,Serie,Foto FROM "+ dbInsumosSinInternet.TABLE;
        Cursor resp = db.rawQuery( SQL, null );
        if (resp.moveToPosition( 0 )) {

            idParte = resp.getString( 0 );
        }
        db.close();
        if (resp.getCount() != 0 && resp!=null)
        {
            StringRequest stringRequest = new StringRequest( Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            SQLiteDatabase db = con.getWritableDatabase();
                            String idParte = "";
                            String SQL = "";
                            SQL="SELECT Nombre,nPedido,Serie,Foto FROM "+ dbInsumosSinInternet.TABLE;
                            Cursor resp = db.rawQuery( SQL, null );
                            if (resp.moveToPosition( 0 )) {

                                idParte = resp.getString( 1 );
                            }

                            db.close();
                            if (resp.getCount() != 0) {
                                db = con.getWritableDatabase();
                                SQL = "DELETE FROM " + dbInsumosSinInternet.TABLE + " WHERE nPedido='" + idParte + "';";
                                try {
                                    db.execSQL( SQL );
                                    Log.w( "SINCONEXCION", "FILA BORRADA" );
                                    uploadImage2();
                                    db.close();


                                } catch (Exception e) {
                                    Log.w( "SINCONEXCION", e.toString() );
                                }
                                db.close();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                        }
                    } ) {
                @Override
                protected Map<String, String> getParams() {
                    return Mostrar2();
                }
            };
            stringRequest.setRetryPolicy( new DefaultRetryPolicy(
                    5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ) );
            RequestQueue requestQueue = Volley.newRequestQueue( context );
            requestQueue.add( stringRequest );
        }
    }
}