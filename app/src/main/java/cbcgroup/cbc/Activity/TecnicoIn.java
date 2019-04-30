package cbcgroup.cbc.Activity;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;

import cbcgroup.cbc.Clases.CBC;
import cbcgroup.cbc.Insumos.AdapterInsumos;
import cbcgroup.cbc.Insumos.ListInsumo;
import cbcgroup.cbc.R;
import cbcgroup.cbc.dbLocal.ConnSQLiteHelper;
import cbcgroup.cbc.dbLocal.SQLite;
import cbcgroup.cbc.dbLocal.Tablas.dbGraficos;
import cbcgroup.cbc.dbLocal.Tablas.dbNombresTecSa;
import cbcgroup.cbc.dbLocal.Tablas.dbTecnicos;

public class TecnicoIn extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = "TecnicosInActivity";
    private static final String CHANNEL_ID = "test";
    private int NOTIFICATION_ID =0 ;
    String URL = "http://tecnicos.cbcgroup.com.ar/test/app_android/v14/hoja_de_reparacion.php?";
    String URL2 = "http://tecnicos.cbcgroup.com.ar/test/app_android/v14/tecnicos.php";
    TextView serie,sector,fecha,modelo,inconveniente,fechaVencimiento;
    Button button;
    String nombre;
    Bundle extra;
    CBC cbc;
    NotificationCompat.Builder builder;

    private SQLite sql;
    private ConnSQLiteHelper con;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_tecnico_in );
        modelo=findViewById( R.id.tec_modelo );
        modelo.setMovementMethod(new ScrollingMovementMethod());
        serie=findViewById( R.id.tec_subItem_serie );
        sector=findViewById( R.id.tec_subItem_sector);
        fecha=findViewById( R.id.tec_subItem_fecha);
        inconveniente=findViewById( R.id.tec_subItem_inconveniente);
        button=findViewById( R.id.ingresar );
        fechaVencimiento=findViewById( R.id.tec_subItem_fechaVencimiento );
        button.setOnClickListener( this );
        cbc= new CBC(TecnicoIn.this);
        con =  new ConnSQLiteHelper( this);
        sql = new SQLite();
        builder = new NotificationCompat.Builder(this);
        extra=getIntent().getExtras();
        CompleteInfo();
    }

    @Override
    public void onClick(View v)
    {
        if(v==button)
        {

            if(pedidosIsNull())IngresoPedido();
            else
                {
                    Toast.makeText(this,"Por favor cierre servicio tecnico abierto.",Toast.LENGTH_LONG).show();
                    sendNotificaction();
                }
            /*DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            Log.w("HORA",date);*/

        }
    }

    void IngresoPedido()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_DARK);
        alertDialogBuilder.setMessage("Desea Ingresar al pedido tecnico?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Si", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)

            {

                ingresoQuery();

            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(TecnicoIn.this, "Cancelado", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog=alertDialogBuilder.create();
        alertDialog.show();
    }

    void ingresoQuery()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(TecnicoIn.this);
        StringRequest stringRequest = new StringRequest( Request.Method.POST, URL2,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String s)
                    {
                        //cbc.setIngresoTecnico(true);
                        //cbc.setIngresonpedido( extra.getString( "npedido" ));
                        Ingreso();
                        sendNotificaction();
                        Toast.makeText( TecnicoIn.this, "Se ingreso correctamente!", Toast.LENGTH_SHORT ).show();
                        startActivity( new Intent(TecnicoIn.this,HomeActivity.class ).addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP ));
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        Log.i( TAG,volleyError.toString());
                        Toast.makeText( TecnicoIn.this, "Error: "+volleyError.toString(), Toast.LENGTH_SHORT ).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new Hashtable<>();
                params.put("Content-Type","application/json; charset=utf-8");
                params.put("llegada","true");
                params.put("id_tecnico",cbc.getdUserId() );
                params.put("serie",serie.getText().toString());
                params.put("id_parte", extra.getString( "npedido" ));
                return params;
            }

        };
        requestQueue.add(stringRequest);
    }

    void CompleteInfo()
    {
        String nparte= extra.getString( "npedido" );
        SQLiteDatabase db = con.getReadableDatabase();
        String SQL="SELECT nParte,Cliente,nSerie,Sector,FechaVence,Fecha,Modelo,Inconveniente,Ingreso FROM "+ dbTecnicos.TABLE+ " WHERE nParte='"+nparte+"'";
        Cursor resp=db.rawQuery( SQL,null);
        if(resp.moveToPosition( 0))
        {
            serie.setText(resp.getString(2));
            sector.setText(resp.getString(3));
            fechaVencimiento.setText(resp.getString(4) );
            fecha.setText(resp.getString(5));
            modelo.setText(resp.getString(6));
            inconveniente.setText(resp.getString(7) );

            Log.w("LISTATEST","ingreso->"+resp.getString(8));
            if(!resp.getString( 8 ).equals( "" ))
            {
                Log.w("LISTATEST","ingreso->VALOR NO NULL");
            }else Log.w("LISTATEST","ingreso->VALOR NULL");


        }

        db.close();
    }
    private void Ingreso()
    {
        String nparte= extra.getString( "npedido" );
        SQLiteDatabase db = con.getReadableDatabase();
        String SQL="UPDATE "+dbTecnicos.TABLE+" SET Ingreso='1' WHERE nParte='"+ nparte+"'";
        try
        {
            db.execSQL( SQL );
        }catch (Exception e)
        {
            Log.w("LSITATEST","error->"+e.toString());
        }

        db.close();
    }



    private void pedidosSinCerrar()
    {
        SQLiteDatabase db = con.getReadableDatabase();
        String SQL="SELECT nParte,Cliente "+dbTecnicos.TABLE+" WHERE Ingreso='1'";
        try
        {
            Cursor resp=db.rawQuery( SQL,null );
            for(int a=0;a<resp.getCount();a++)
            {
                if(resp.moveToPosition( a ))
                {
                    String nParte=resp.getString(0);
                    String cliente=resp.getString( 1 );

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(R.mipmap.ic_cbc)
                            .setContentTitle("My notification")
                            .setContentText("Much longer text that cannot fit one line...")
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText("Much longer text that cannot fit one line..."))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                }
            }

        }catch (Exception e)
        {
            Log.w("LSITATEST","error->"+e.toString());
        }

        db.close();
    }
    private void sendNotificaction()
    {
        SQLiteDatabase db = con.getReadableDatabase();
        String SQL="SELECT nParte,Cliente FROM Tecnicos WHERE Ingreso='1';";
        try
        {
            Cursor resp=db.rawQuery( SQL,null );
            for(int a=0;a<resp.getCount();a++)
            {
                if(resp.moveToPosition( a ))
                {
                    Log.w("INGRESO","resp->"+resp.getString( 0 ));
                    Intent notificationIntent = new Intent(this, TecnicosOut.class);
                    notificationIntent.putExtra("npedido",resp.getString( 0 ));
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            builder.setSmallIcon(R.mipmap.ic_cbc)
                            .setAutoCancel(true)
                            .setColor(getResources().getColor(R.color.colorPrimary))
                            .setGroup("CBCGROUP")
                            .setPriority(2)
                            .setContentText("Se encuentra sin cerrar, por favor termine de cerrar el parte.")
                            .setContentIntent(PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                            .setWhen(System.currentTimeMillis())
                            .setContentTitle("El parte "+resp.getString( 0 )+" de "+resp.getString( 1 ))
                            .setDefaults(Notification.DEFAULT_ALL);
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                    //notificationManager.notify(++NOTIFICATION_ID,builder.build());
                    notificationManager.notify(NOTIFICATION_ID,builder.build());


                }
            }


        }catch(Exception e)
        {
            Log.w(TAG,"Error Notificacion->"+e);
        }
        db.close();
    }

    private boolean pedidosIsNull()
    {
        SQLiteDatabase db = con.getReadableDatabase();
        String SQL="SELECT nParte FROM Tecnicos WHERE Ingreso='1';";
        Cursor resp=db.rawQuery( SQL,null);

        if(resp.getCount()==0)
        {
            db.close();
            return true;
        }
        else
            {
                db.close();
                return false;
            }

    }


}
