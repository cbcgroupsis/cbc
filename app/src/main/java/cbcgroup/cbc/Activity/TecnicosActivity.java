package cbcgroup.cbc.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class TecnicosActivity  extends AppCompatActivity
{

    String URL = "http://tecnicos.cbcgroup.com.ar/test/app_android/v14/hoja_de_reparacion.php?";
    private android.support.v7.widget.SearchView searchView;
    private RecyclerView recyclerView;
    ListInsumo insumos = new ListInsumo();
    AdapterInsumos adapter;
    CBC cbc;
    private String TAG="Tecnicos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_tecnicos );
        searchView=findViewById( R.id.mSearch );
        recyclerView=findViewById( R.id.myRecycler );
        recyclerView.setLayoutManager( new LinearLayoutManager( TecnicosActivity.this ) );
        recyclerView.setItemAnimator( new DefaultItemAnimator() );
        cbc=new CBC(TecnicosActivity.this);
        if(cbc.Internet()) Programa();
        else Toast.makeText( this,"NO TIENE ACCESO A INTERNET O SU CONNCECION ES MUY LENTA, VUELVA ATRAS Y INTENTE NUEVAMENTE",Toast.LENGTH_LONG ).show();
    }
    void List(String  res)
    {
        final ArrayList<ListInsumo> list=new ArrayList<>();
        list.clear();
        try {
            JSONObject response = new JSONObject( res );
            JSONArray jsonInsumos = response.getJSONArray( "lista_de_hoja_reparacion" );
            for(int a=0;a<jsonInsumos.length();a++)
            {
                JSONObject obj=jsonInsumos.getJSONObject( a );
                insumos=new ListInsumo();
                insumos.setNumPedido(obj.getString( "idparte" ));
                insumos.setNombreCliente(obj.getString( "cliente" ));
                Log.w(TAG,"Resss:"+obj.get( "idparte" ));
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
        String nameTec="";
        if(extra!=null) nameTec=extra.getString( "nombreTecSa" );
        else nameTec=cbc.getdUserName();
        RequestQueue requestQueue = Volley.newRequestQueue(TecnicosActivity.this);
        StringRequest stringRequest = new StringRequest( Request.Method.POST, URL+"name_tec="+cbc.spaceRemplace( nameTec,"%20" ),
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
                params.put("Content-Type","application/json; charset=utf-8");
                //   params.put("name_tec",cbc.getdUserName());
                return params;
            }

        };
        requestQueue.add(stringRequest);
    }

}
