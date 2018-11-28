package cbcgroup.cbc.Activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import cbcgroup.cbc.Clases.CBC;
import cbcgroup.cbc.Insumos.AdapterInsumos;
import cbcgroup.cbc.Insumos.ListInsumo;
import cbcgroup.cbc.R;
import cbcgroup.cbc.dbLocal.ConnSQLiteHelper;
import cbcgroup.cbc.dbLocal.SQLite;
import cbcgroup.cbc.dbLocal.Tablas.dbInsumos;
import cbcgroup.cbc.dbLocal.Tablas.dbNombresTecSa;
import cbcgroup.cbc.dbLocal.Tablas.dbTecnicos;

public class TecnicosActivity  extends AppCompatActivity
{

    private String URL = "http://tecnicos.cbcgroup.com.ar/test/app_android/v14/prueba/servicioTecnico.php";
    private android.support.v7.widget.SearchView searchView;
    private RecyclerView recyclerView;
    private ListInsumo insumos = new ListInsumo();
    private AdapterInsumos adapter;
    private ImageButton actualizar;
    private CBC cbc;
    private SQLite sql;
    private ConnSQLiteHelper con;
    private String TAG="Tecnicos";
    private String idTec="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_tecnicos );
        searchView=findViewById( R.id.mSearch );
        recyclerView=findViewById( R.id.myRecycler );
        actualizar=findViewById( R.id.actualizar );
        recyclerView.setLayoutManager( new LinearLayoutManager( TecnicosActivity.this ) );
        recyclerView.setItemAnimator( new DefaultItemAnimator() );
        con =  new ConnSQLiteHelper( this);
        sql = new SQLite();
        cbc=new CBC(TecnicosActivity.this);
        if(TableValues()) Programa();
        else ProgramaSinConexion();
        actualizar.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
             if(cbc.Internet())Programa();
                else Toast.makeText( TecnicosActivity.this,"No tiene Acceso Internet para Actualizar la Lista",Toast.LENGTH_LONG ).show();
            }
        } );

    }
    void List(String  res)
    {
        final ArrayList<ListInsumo> list=new ArrayList<>();
        list.clear();
        try {
            JSONObject response = new JSONObject( res );
            JSONArray jsonInsumos = response.getJSONArray( "lista_de_hoja_reparacion" );
            SQLiteDatabase db=con.getWritableDatabase();
            for(int a=0;a<jsonInsumos.length();a++)
            {
                JSONObject obj=jsonInsumos.getJSONObject( a );

                String npedido=obj.getString( "idparte" );
                String inconveniente=obj.getString( "inconveniente" );
                String fVence=obj.getString( "fechaVence" );
                String fecha=obj.getString( "fecha" );
                String cliente=obj.getString( "cliente" );
                String modelo=obj.getString( "modelo" );
                String nSerie=obj.getString( "serie" );
                String sector= obj.getString( "sector" );
                String ingreso=obj.getString("ingreso");
                String categoria=obj.getString("categoria");
                SincronizarDbLocal(npedido,cliente,nSerie,sector,fVence,fecha,ingreso,modelo,inconveniente,categoria);
                insumos=new ListInsumo();
                insumos.setNumPedido(npedido);
                insumos.setNombreCliente( cliente );
                insumos.setCategoria( categoria );
                list.add(insumos);

            }
            adapter=new AdapterInsumos(TecnicosActivity.this,list,4);
            recyclerView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void Search()
    {
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                adapter.getFilter().filter(query);
                return true;
            }
        });
        recyclerView.setAdapter(adapter);
    }

    void Programa()
    {
        cbc.progressDialog( "Cargando Pedidos Tecnicos...","Espere por favor..." );
        Bundle extra= getIntent().getExtras();

        if(extra!=null)
        {
            idTec=extra.getString( "idTec" );
        }
        else idTec=cbc.getdUserId();
        RequestQueue requestQueue = Volley.newRequestQueue(TecnicosActivity.this);
        StringRequest stringRequest = new StringRequest( Request.Method.POST, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String s)
                    {
                        cbc.progressDialogCancel();
                        Log.w(TAG,"Resp:"+s);
                        if(s.equals( "null" ))
                        {
                            Toast.makeText( TecnicosActivity.this, "No hay pedidos tecnicos.", Toast.LENGTH_SHORT ).show(); finish();
                        }
                        SQLiteDatabase db=con.getWritableDatabase();
                        sql.DeleteTabla( db, dbTecnicos.TABLE );
                        cbc.progressDialogCancel();
                        List( s );
                        Search();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        cbc.progressDialogCancel();
                        Log.i( TAG,volleyError.toString());
                        Toast.makeText( TecnicosActivity.this, "Error: "+volleyError.toString(), Toast.LENGTH_SHORT ).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new Hashtable<>();
                params.put("idTec",idTec);
                return params;
            }

        };
        requestQueue.add(stringRequest);
    }

    /************** DB **************************/
    private void ProgramaSinConexion()
    {
        Bundle extra= getIntent().getExtras();
        if(extra!=null) idTec=extra.getString( "idTec" );
        else idTec=cbc.getdUserId();
        final ArrayList<ListInsumo> list=new ArrayList<>();
        list.clear();
        SQLiteDatabase db = con.getReadableDatabase();

        String SQL="SELECT nParte,Cliente,Categoria FROM "+ dbTecnicos.TABLE + " WHERE Ingreso!=2 AND idTec='"+idTec+"'";

        Cursor resp=db.rawQuery( SQL,null);
        Log.w("LIST","lista:"+resp.getCount());
        for(int i=0;i<resp.getCount();i++)
        {
            if(resp.moveToPosition( i ))
            {
                insumos=new ListInsumo();
                insumos.setNumPedido(resp.getString( 0 ));
                insumos.setNombreCliente( resp.getString( 1 ) );
                insumos.setCategoria( resp.getString( 2 ) );
                list.add(insumos);

            }
        }
        adapter=new AdapterInsumos(this,list,4);
        recyclerView.setAdapter(adapter);
        db.close();
        Search();
    }
    private void SincronizarDbLocal(String nParte,String Cliente,String Serie,String Sector, String Fvence, String fecha, String Ingreso,String Modelo, String Inconveniente,String categoria)
    {
        Bundle extra= getIntent().getExtras();
        if(extra!=null) idTec=extra.getString( "idTec" );
        else idTec=cbc.getdUserId();
        SQLiteDatabase db=con.getWritableDatabase();
        Map params = new Hashtable(  );
        params.clear();
        params.put( dbTecnicos.CAMPO_NPARTE,nParte);
        params.put( dbTecnicos.CAMPO_CLIENTE,Cliente);
        params.put( dbTecnicos.CAMPO_NSERIE,Serie);
        params.put( dbTecnicos.CAMPO_SECTOR,Sector);
        params.put( dbTecnicos.CAMPO_FECHAVENCE,Fvence);
        params.put( dbTecnicos.CAMPO_FECHA,fecha);
        params.put( dbTecnicos.CAMPO_INGRESO,Ingreso);
        params.put( dbTecnicos.CAMPO_INCONVENIENTE,Inconveniente);
        params.put( dbTecnicos.CAMPO_MODELO,Modelo);
        params.put(dbTecnicos.CAMPO_IDTEC,idTec);
        params.put(dbTecnicos.CAMPO_CATEGORIA,categoria);
        sql.Add(db,dbTecnicos.TABLE,params);

    }
    boolean TableValues()
    {
        Bundle extra= getIntent().getExtras();
        if(extra!=null) idTec=extra.getString( "idTec" );
        else idTec=cbc.getdUserId();
        SQLiteDatabase db = con.getWritableDatabase();
        String SQL = "";
        String idParte="";
        SQL = "SELECT * FROM " + dbTecnicos.TABLE + " WHERE idTec='"+idTec+"'";
        Cursor resp = db.rawQuery( SQL, null );
        if (resp.moveToPosition( 0 )) {

            idParte = resp.getString( 0 );
        }
        db.close();
        if (resp.getCount() != 0 && resp!=null)
        {
            return false;
        }else return true;
    }


}
