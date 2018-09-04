package cbcgroup.cbc.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
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

import cbcgroup.cbc.Clases.CBC;
import cbcgroup.cbc.Insumos.AdapterInsumos;
import cbcgroup.cbc.Insumos.ListInsumo;
import cbcgroup.cbc.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListTecnicosSuperAdmin.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListTecnicosSuperAdmin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListTecnicosSuperAdmin extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    public static final String TAG = "TecnicosSuperAdmin_Fragment";
    private String URL="http://tecnicos.cbcgroup.com.ar/test/app_android/v14/tecnicosSuperAdmin.php";
    private android.support.v7.widget.SearchView searchView;
    private RecyclerView recyclerView;
    ListInsumo insumos = new ListInsumo();
    AdapterInsumos adapter;
    CBC cbc;

    public ListTecnicosSuperAdmin() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListTecnicosSuperAdmin.
     */
    // TODO: Rename and change types and number of parameters
    public static ListTecnicosSuperAdmin newInstance(String param1, String param2) {
        ListTecnicosSuperAdmin fragment = new ListTecnicosSuperAdmin();
        Bundle args = new Bundle();
        args.putString( ARG_PARAM1, param1 );
        args.putString( ARG_PARAM2, param2 );
        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        if (getArguments() != null) {
            mParam1 = getArguments().getString( ARG_PARAM1 );
            mParam2 = getArguments().getString( ARG_PARAM2 );
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate( R.layout.fragment_list_tecnicos_super_admin, container, false );
        searchView=view.findViewById( R.id.mSearch );
        recyclerView=view.findViewById( R.id.myRecycler );
        recyclerView.setLayoutManager( new LinearLayoutManager( getActivity() ) );
        recyclerView.setItemAnimator( new DefaultItemAnimator() );
        cbc=new CBC(getActivity());
        Programa();
        return view;
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
    void List(String  res)
    {

        final ArrayList<ListInsumo> list=new ArrayList<>();
        list.clear();
        Log.w("INSUMOS","LIST_RESPONSE"+res);
        try {
            JSONObject response = new JSONObject( res );
            JSONArray jsonInsumos = response.getJSONArray( "Tecnicos" );
            for(int a=0;a<jsonInsumos.length();a++)
            {
                JSONObject obj=jsonInsumos.getJSONObject( a );
                insumos=new ListInsumo();
                insumos.setOneDate(obj.getString( "nombre" ));
                list.add(insumos);
            }
            adapter=new AdapterInsumos(getActivity(),list,3);
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
        cbc.progressDialog( "Cargando lista de tecnicos...","Espere por favor..." );
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest( Request.Method.POST, URL,
                new Response.Listener<String>()
                {
                    @SuppressLint("LongLogTag")
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
}