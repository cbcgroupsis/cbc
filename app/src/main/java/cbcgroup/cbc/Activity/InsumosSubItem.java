package cbcgroup.cbc.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

public class InsumosSubItem extends AppCompatActivity {

    private static final String TAG = "INSUMOS-SUB-ITEM" ;
    private String URL = "http://tecnicos.cbcgroup.com.ar:80/Test/app_android/v14/insumosSubItem.php";
    private android.support.v7.widget.SearchView searchView;
    private RecyclerView recyclerView;
    ListInsumo insumos = new ListInsumo();
    AdapterInsumos adapter;
    CBC cbc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_insumos_sub_item );
        searchView=findViewById( R.id.mSearch );
        recyclerView=findViewById( R.id.myRecycler );
        recyclerView.setLayoutManager( new LinearLayoutManager( InsumosSubItem.this ) );
        recyclerView.setItemAnimator( new DefaultItemAnimator() );
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        cbc=new CBC(InsumosSubItem.this);
        setSupportActionBar( toolbar );
        Bundle extra=getIntent().getExtras();
        if(extra!=null)
        {
            getSupportActionBar().setTitle("Insumos - Npedido: "+extra.getString( "npedido"));
            cbc.setInsumosNpedidos(extra.getString( "npedido"));
            cbc.setInsumosClientes(extra.getString("nomcliente"));
            //instalar jaja
        }
        if(cbc.Internet()) Programa();
        else Toast.makeText( this,"NO TIENE ACCESO A INTERNET O SU CONNCECION ES MUY LENTA",Toast.LENGTH_LONG ).show();


    }

    void List(String  res)
    {

        final ArrayList<ListInsumo> list=new ArrayList<>();
        list.clear();
        Log.w("INSUMOS","LIST_RESPONSE"+res);
        try {
            JSONObject response = new JSONObject( res );
            JSONArray jsonInsumos = response.getJSONArray( "insumosSubItem" );
            for(int a=0;a<jsonInsumos.length();a++)
            {
                JSONObject obj=jsonInsumos.getJSONObject( a );
                insumos=new ListInsumo();
                insumos.setNumSerie(obj.getString( "serie" ));
                insumos.setModelo(obj.getString( "modelo" ));
                list.add(insumos);
            }
            adapter=new AdapterInsumos(InsumosSubItem.this,list,2);
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
        cbc.progressDialog( "Cargando Pedidos...","Espere por favor..." );
        RequestQueue requestQueue = Volley.newRequestQueue(InsumosSubItem.this);
        StringRequest stringRequest = new StringRequest( Request.Method.POST, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String s)
                    {
                        cbc.progressDialogCancel();
                        Log.w(TAG,"Resp:"+s);
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
                        Toast.makeText( InsumosSubItem.this, "Error: "+volleyError.toString(), Toast.LENGTH_SHORT ).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new Hashtable<>();
                params.put("Content-Type","application/json; charset=utf-8");
                params.put("dato",getIntent().getExtras().getString( "npedido" ));
                return params;
            }

        };
        requestQueue.add(stringRequest);
    }

}
