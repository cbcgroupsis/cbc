package cbcgroup.cbc.Fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import java.util.List;
import java.util.Map;

import cbcgroup.cbc.Activity.listaClienteActivity;
import cbcgroup.cbc.Clases.CBC;
import cbcgroup.cbc.Insumos.ListInsumo;
import cbcgroup.cbc.R;
import cbcgroup.cbc.dbLocal.ConnSQLiteHelper;
import cbcgroup.cbc.dbLocal.SQLite;
import cbcgroup.cbc.dbLocal.Tablas.dbGraficos;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

public class GrafClientTecFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "fragGrafTec" ;
    private PieChartView chart;
    private PieChartData data;
    private boolean hasLabels = true;
    private boolean hasLabelsOutside = false;
    private boolean hasCenterCircle = true;
    private boolean hasCenterText1 = true;
    private boolean hasCenterText2 = false;
    private boolean isExploded = false;
    private boolean hasLabelForSelected = false;
    private SQLite sql;
    private ConnSQLiteHelper con;
    private CBC cbc;
    private Button realizado,aprobado,solicitado;

    private OnFragmentInteractionListener mListener;

    public GrafClientTecFragment() {
        // Required empty public constructor
    }

    public static GrafClientTecFragment newInstance(String param1, String param2) {
        GrafClientTecFragment fragment = new GrafClientTecFragment();
        Bundle args = new Bundle();

        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =inflater.inflate( R.layout.fragment_graf_client_tec, container, false );
        chart = (PieChartView) v.findViewById(R.id.chart);
        cbc=new CBC(getContext());
        sql = new SQLite();
        con = new ConnSQLiteHelper( getContext() );
        chart.setOnValueTouchListener(new PlaceholderFragment.ValueTouchListener());
        realizado=v.findViewById( R.id.Realizado );
        aprobado=v.findViewById( R.id.Aprobado );
        solicitado=v.findViewById( R.id.Solicitado );

        realizado.setOnClickListener(this);
        aprobado.setOnClickListener( this );
        solicitado.setOnClickListener( this );
        if(cbc.Internet())data();
        else ProgramaSinInternet();
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction( uri );
        }
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

    @Override
    public void onClick(View v){
        if(realizado==v)
        {
            startActivity( new Intent(getContext(),listaClienteActivity.class).putExtra( "type","Realizado" ).putExtra( "activity","tec" ) );
        }else if(aprobado==v)
        {
            startActivity( new Intent(getContext(),listaClienteActivity.class).putExtra( "type","Aprobado" ).putExtra( "activity","tec" ) );
        }else if(solicitado==v)
        {
            startActivity( new Intent(getContext(),listaClienteActivity.class).putExtra( "type","Solicitado" ).putExtra( "activity","tec" ) );
        }

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public static class PlaceholderFragment extends android.app.Fragment {



        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            View rootView = inflater.inflate(R.layout.fragment_pie_chart, container, false);

            return rootView;
        }

        static class ValueTouchListener implements PieChartOnValueSelectListener {

            @Override
            public void onValueSelected(int arcIndex, SliceValue value) {

            }

            @Override
            public void onValueDeselected() {
                // TODO Auto-generated method stub

            }

        }



    }

    private void generateData( int solicitado,int aprobado,int realizado) {

        List<SliceValue> values = new ArrayList<SliceValue>();
        values.add( new SliceValue( solicitado,ChartUtils.COLOR_GREEN,1 ) );
        values.add( new SliceValue( aprobado,ChartUtils.COLOR_RED,2 ) );
        values.add( new SliceValue( realizado,ChartUtils.COLOR_ORANGE,3 ) );
        data = new PieChartData(values);
        data.setHasLabels(hasLabels);
        data.setHasLabelsOnlyForSelected(hasLabelForSelected);
        data.setHasLabelsOutside(hasLabelsOutside);
        data.setHasCenterCircle(hasCenterCircle);

        if (isExploded) data.setSlicesSpacing(24);
        if (hasCenterText1)
        {
            data.setCenterText1("Servicio Tecnico");
            data.setCenterText1Color( Color.parseColor("#ffffff"));
            data.setCenterText1FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,(int) getResources().getDimension(R.dimen.pie_chart_text1_size)));
        }
        chart.setPieChartData(data);
    }

    private void data()
    {
        cbc.progressDialog( "Cargando...","Espere por favor..." );                   //Dialogo en pantalla indicando al usuario que se esta autentificando la informacion ingresada.
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String url="https://tecnicos.cbcgroup.com.ar/test/app_android/Proyectos/Api/informes.php";
        StringRequest stringRequest = new StringRequest( Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String s)
                    {
                        cbc.progressDialogCancel();
                        //Log.w(TAG,"respuesta:"+s);

                        try {
                            JSONObject response = new JSONObject(s);
                            JSONArray res=response.getJSONArray( "Info" );                   //Busco en el objeto el key "Login" y convierto todos los objetos hijos en un arreglo.
                            int solicitado=0,aprobados=0,realizado=0;

                            Map<String,String> params = new Hashtable(  );
                            params.clear();
                            SQLiteDatabase db=con.getWritableDatabase();
                            DeleteTabla( db,dbGraficos.TABLE,"tec" );
                            for(int x=0;x<res.length();x++)
                            {

                                db=con.getWritableDatabase();
                                JSONObject obj = res.getJSONObject(x);
                                Log.w(TAG,obj.getString( "e" ));
                                if(obj.getString( "e" ).equals( "Aprobado" ))aprobados+=1;
                                else if(obj.getString( "e" ).equals( "Realizado" ))realizado+=1;
                                else if(obj.getString( "e" ).equals( "Solicitado" ))solicitado+=1;


                                params.put( dbGraficos.CAMPOS_e,obj.getString( "e" ));
                                params.put( dbGraficos.CAMPOS_m,obj.getString( "m" ));
                                params.put( dbGraficos.CAMPOS_np,obj.getString( "np" ));
                                params.put( dbGraficos.CAMPOS_ns,obj.getString( "ns" ));
                                params.put( dbGraficos.CAMPOS_type,"tec" );
                                sql.Add(db,dbGraficos.TABLE,params);


                            }
                            Log.w(TAG,"result:"+aprobados+"\t"+realizado+"\t"+solicitado);
                            generateData(realizado,aprobados,solicitado);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)                            //Error al hacer la solicitud a la URL indicada
                    {
                        cbc.progressDialogCancel();

                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new Hashtable<>();                                     //Parametros a enviar.
                params.put("id",cbc.getdUserId());
                params.put("s","tec");
                params.put("t",cbc.getdUserSector());
                return params;                                                                      //Envio los parametros por el metodo post.

            }

        };
        requestQueue.add(stringRequest);                                                            //Agrego una solicitud

    }

    private void ProgramaSinInternet()
    {

        final ArrayList<ListInsumo> list=new ArrayList<>();
        list.clear();
        SQLiteDatabase db = con.getReadableDatabase();
        String SQL="SELECT e FROM "+ dbGraficos.TABLE + " WHERE ty='tec'";
        Cursor resp=db.rawQuery( SQL,null);
        int solicitado=0,aprobados=0,realizado=0;
        for(int i=0;i<resp.getCount();i++)
        {
            if(resp.moveToPosition( i ))
            {

                if(resp.getString( 0 ).equals( "Aprobado" ))aprobados+=1;
                else if(resp.getString( 0 ).equals( "Realizado" ))realizado+=1;
                else if(resp.getString( 0 ).equals( "Solicitado" ))solicitado+=1;
            }
        }
        generateData(realizado,aprobados,solicitado);
        db.close();
    }

    public void DeleteTabla(SQLiteDatabase db,String TABLA,String condicion)                                         //Funciona
    {
        Log.w(TAG,"Delete tabla Db");                                                                  //Debug...
        String SQL="DELETE FROM "+TABLA + " WHERE  ty='"+condicion+"'";
        try
        {
            db.execSQL( SQL );
        }catch (Exception e){}

        db.close();
    }

}
