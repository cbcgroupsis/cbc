package cbcgroup.cbc.Clases;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.SyncStateContract;
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

import cbcgroup.cbc.dbLocal.ConnSQLiteHelper;
import cbcgroup.cbc.dbLocal.Tablas.dbInsumosSinInternet;
import cbcgroup.cbc.dbLocal.Tablas.dbTecSinInternet;

public class NetworkSchedulerService extends JobService implements
        ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = "TESTINTERNET";
    private static final String URLIMAGEN = "https://tecnicos.cbcgroup.com.ar/test/app_android/v14/imagenTecnico.php";
    private static final String URL = "https://tecnicos.cbcgroup.com.ar/Test/app_android/imagen.php";
    private Context context;
    private ConnSQLiteHelper con;
    private CBC cbc;
    private ConnectivityReceiver mConnectivityReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service created");
        this.context = this;
        con =  new ConnSQLiteHelper(context);
        cbc = new CBC(context);
        mConnectivityReceiver = new ConnectivityReceiver(this);
    }



    /**
     * When the app's NetworkConnectionActivity is created, it starts this service. This is so that the
     * activity and this service can communicate back and forth. See "setUiCallback()"
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return START_NOT_STICKY;
    }


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, "onStartJob" + mConnectivityReceiver);
        registerReceiver(mConnectivityReceiver, new IntentFilter(Constants.CONNECTIVITY_ACTION));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "onStopJob");
        unregisterReceiver(mConnectivityReceiver);
        return true;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected)
    {
        String message;
        if(isConnected)
        {
            //message="Conectado a internet";
               uploadImage2();
              uploadImage();
        }
        //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }

    Map<String,String> Mostrar()
    {
        SQLiteDatabase db = con.getWritableDatabase();
        Map<String, String> params = new Hashtable<>();
        String SQL="SELECT id_tec,serie,id_parte,mensaje,copias,copiasColor,viaje,foto,Espera FROM "+ dbTecSinInternet.TABLE;
        @SuppressLint("Recycle") Cursor resp=db.rawQuery( SQL,null );
        if(resp==null) return null;
        if(resp.moveToPosition( 0 ))
        {
            /*Parametros a enviar*/
            Log.w(TAG,"Enviando...");
            params.put("id_tecnico",resp.getString( 0 ));
            params.put("serie",resp.getString( 1 ));
            params.put("nparte",resp.getString( 2 ));
            params.put("id_parte", resp.getString( 2 ));
            params.put("mensaje",resp.getString( 3 ));
            params.put("copias",resp.getString( 4 ));
            params.put("copiasColor",resp.getString( 5 ));
            params.put("viaje",resp.getString( 6 ));
            params.put("foto", resp.getString( 7 ));
        }

        db.close();
        return params;
    }

    private void uploadImage() {
        //final ProgressDialog loading = ProgressDialog.show(this, "Subiendo...", "Espere por favor...", false, false);
        SQLiteDatabase db = con.getWritableDatabase();
        String SQL = "SELECT id_tec,serie,id_parte,mensaje,copias,copiasColor,viaje,foto,Espera FROM " + dbTecSinInternet.TABLE;
        @SuppressLint("Recycle") Cursor resp = db.rawQuery( SQL, null );
        resp.moveToPosition( 0 );
        db.close();
        if (resp.getCount() != 0)
        {
            StringRequest stringRequest = new StringRequest( Request.Method.POST, URLIMAGEN,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            SQLiteDatabase db = con.getWritableDatabase();
                            String idParte = "";
                            String SQL = "SELECT id_tec,serie,id_parte,mensaje,copias,copiasColor,viaje,foto,Espera FROM " + dbTecSinInternet.TABLE;
                            @SuppressLint("Recycle") Cursor resp = db.rawQuery( SQL, null );
                            if (resp.moveToPosition( 0 )) {

                                idParte = resp.getString( 2 );
                            }

                            db.close();
                            if (resp.getCount() != 0) {
                                db = con.getWritableDatabase();
                                SQL = "DELETE FROM " + dbTecSinInternet.TABLE + " WHERE id_parte='" + idParte + "';";
                                try {
                                    db.execSQL( SQL );
                                    Log.w( TAG, "FILA BORRADA" );
                                    uploadImage();
                                    db.close();


                                } catch (Exception e) {
                                    Log.w( TAG, e.toString() );
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
        @SuppressLint("Recycle") Cursor resp=db.rawQuery( SQL,null );
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
    @SuppressLint("Recycle")
    private void uploadImage2() {
        //final ProgressDialog loading = ProgressDialog.show(this, "Subiendo...", "Espere por favor...", false, false);
        Cursor resp;
        try (SQLiteDatabase db = con.getWritableDatabase()) {
            String SQL = "SELECT Nombre,Serie,Foto FROM " + dbInsumosSinInternet.TABLE;
            resp = db.rawQuery( SQL, null );
            resp.moveToPosition( 0 );
            db.close();
        }
        if (resp.getCount() != 0)
        {
            StringRequest stringRequest = new StringRequest( Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            SQLiteDatabase db = con.getWritableDatabase();
                            String idParte = "";
                            String SQL="SELECT Nombre,nPedido,Serie,Foto FROM "+ dbInsumosSinInternet.TABLE;
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
                                    Log.w( TAG, "FILA BORRADA" );
                                    uploadImage2();
                                    db.close();


                                } catch (Exception e) {
                                    Log.w( TAG, e.toString() );
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
