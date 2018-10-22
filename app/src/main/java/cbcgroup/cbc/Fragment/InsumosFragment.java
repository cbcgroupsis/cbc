package cbcgroup.cbc.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import cbcgroup.cbc.Activity.TecnicosOut;
import cbcgroup.cbc.Clases.CBC;
import cbcgroup.cbc.Insumos.AdapterInsumos;
import cbcgroup.cbc.Insumos.ListInsumo;
import cbcgroup.cbc.R;
import cbcgroup.cbc.dbLocal.ConnSQLiteHelper;
import cbcgroup.cbc.dbLocal.SQLite;
import cbcgroup.cbc.dbLocal.Tablas.dbInsumos;

public class InsumosFragment extends Fragment
{
    private OnFragmentInteractionListener mListener;
    public static final String TAG = "TecnicosSuperAdmin_Fragment";
    private String URL = "http://tecnicos.cbcgroup.com.ar/Test/app_android/v14/prueba/insumosPrueba.php";
    private android.support.v7.widget.SearchView searchView;
    private RecyclerView recyclerView;
    ListInsumo insumos = new ListInsumo();
    AdapterInsumos adapter;
    CBC cbc;
    /**db*/
   private SQLite sql;
   private ConnSQLiteHelper con;


    public InsumosFragment() { }
    @Override
    public void onCreate(Bundle savedInstanceState){super.onCreate( savedInstanceState );}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate( R.layout.fragment_insumos, container, false );
        con =  new ConnSQLiteHelper( getContext());
        sql = new SQLite();
        searchView=view.findViewById( R.id.mSearch );
        recyclerView=view.findViewById( R.id.myRecycler );
        recyclerView.setLayoutManager( new LinearLayoutManager( getActivity() ) );
        recyclerView.setItemAnimator( new DefaultItemAnimator() );
        cbc=new CBC(getActivity());
        if(cbc.Internet()) Programa();
        else
            {
                Toast.makeText( getContext(),"Usted esta trabajando sin conexion",Toast.LENGTH_LONG ).show();
                ProgramaSinConexion();
            }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach( context );
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException( context.toString()
                    + " must implement OnFragmentInteractionListener" );
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    void List(String  res)
    {

        final ArrayList<ListInsumo> list=new ArrayList<>();
        list.clear();
        Log.w("INSUMOS","LIST_RESPONSE"+res);
        try {
            JSONObject response = new JSONObject( res );
            JSONArray jsonInsumos = response.getJSONArray( "insumos" );
            for(int a=0;a<jsonInsumos.length();a++)
            {
                JSONObject obj=jsonInsumos.getJSONObject( a );
            //    insumos=new ListInsumo();
            //    insumos.setNumPedido(obj.getString( "npedido" ));
           //     insumos.setNombreCliente(obj.getString( "cliente" ));
                SincronizarDbLocal(obj.getString( "npedido" ),obj.getString( "cliente" ),obj.getString( "serie"),obj.getString( "modelo" ) );
             //   list.add(insumos);
            }
          ProgramaSinConexion();

          //  adapter=new AdapterInsumos(getActivity(),list,1);
          //  recyclerView.setAdapter(adapter);
        } catch (JSONException e)
        {
            e.printStackTrace();
            ProgramaSinConexion();
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
        cbc.progressDialog( "Cargando Pedidos de Insumos...","Espere por favor." );
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest( Request.Method.POST, URL,
                new Response.Listener<String>()
                {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onResponse(String s)
                    {
                        SQLiteDatabase db=con.getWritableDatabase();
                        sql.DeleteTabla( db,dbInsumos.TABLE );
                        cbc.progressDialogCancel();
                        Log.w(TAG,"Resp:"+s);
                        List( s );
                        Search();
                    }
                },
                new Response.ErrorListener()
                {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        cbc.progressDialogCancel();
                        Log.i( TAG,volleyError.toString());
                        Toast.makeText( getActivity(), "Error: "+volleyError.toString(), Toast.LENGTH_SHORT ).show();
                    }
                })
        {



            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new Hashtable<>();
                params.put("Content-Type","application/json; charset=utf-8");
                //     params.put("dato",getActivity().getIntent().getExtras().getString( "npedido" ));
                return params;
            }

        };
        requestQueue.add(stringRequest);
    }

    private void SincronizarDbLocal(String nPedido,String cliente,String serie,String modelo)
    {
        SQLiteDatabase db=con.getWritableDatabase();
        Map params = new Hashtable(  );
        params.clear();
        params.put( dbInsumos.CAMPO_NPEDIDO,nPedido);
        params.put(dbInsumos.CAMPO_Cliente,cliente);
        params.put(dbInsumos.CAMPO_SERIE,serie);
        params.put(dbInsumos.CAMPO_MODELO,modelo);
        sql.Add(db,dbInsumos.TABLE,params);

    }
    private void ProgramaSinConexion()
    {

        final ArrayList<ListInsumo> list=new ArrayList<>();
        list.clear();
        SQLiteDatabase db = con.getReadableDatabase();
        String SQL="SELECT nPedido,Cliente FROM "+ dbInsumos.TABLE+" GROUP BY nPedido,Cliente";
        Cursor resp=db.rawQuery( SQL,null);
        for(int i=0;i<resp.getCount();i++)
        {
            if(resp.moveToPosition( i ))
            {
                insumos=new ListInsumo();
                insumos.setNumPedido(resp.getString(0));
                insumos.setNombreCliente(resp.getString( 1 ));
                list.add(insumos);
            }
        }
        adapter=new AdapterInsumos(getActivity(),list,1);
        recyclerView.setAdapter(adapter);
        db.close();
        Search();
    }
}

