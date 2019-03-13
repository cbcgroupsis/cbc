package cbcgroup.cbc.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.ScriptGroup;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import cbcgroup.cbc.Clases.CBC;
import cbcgroup.cbc.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ClientesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClientesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClientesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ProgressDialog progress;
    private String dato_aux;
    private String urll;
    SharedPreferences menu;
    private ProgressBar progressBar;
    private WebView mWebView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int upCont=0;
    private OnFragmentInteractionListener mListener;
    private CBC cbc;
    public ClientesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClientesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClientesFragment newInstance(String param1, String param2) {
        ClientesFragment fragment = new ClientesFragment();
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
        cbc = new CBC(this.getContext());
        String idUseR=cbc.getdUserId();
        Log.w("IDUSER",idUseR);
        urll="https://tecnicos.cbcgroup.com.ar/test/app_android/proyectos/informesInsumos/html/informes.html?type=cliente&iduser="+idUseR;
        View view= inflater.inflate( R.layout.fragment_clientes, container, false );

        progressBar = view.findViewById( R.id.progressBar );
        mWebView =  view.findViewById( R.id.web_frag_dct );
        Programa_principal();

        swipeRefreshLayout = view.findViewById( R.id.swipe_web_dct );
        swipeRefreshLayout.setColorSchemeColors( 0x2196F3 );
        ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener;
        swipeRefreshLayout.getViewTreeObserver().addOnScrollChangedListener( mOnScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener()
        {

            @Override
            public void onScrollChanged() {
                if (mWebView.getScrollY() == 0)
                {
                    upCont++;
                    if(upCont>2)
                    {
                        swipeRefreshLayout.setEnabled( true );
                        upCont=1;
                    }
                }
                else
                    swipeRefreshLayout.setEnabled(false);

            }
        });
        swipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                swipeRefreshLayout.setRefreshing( true );
                (new Handler(  )).postDelayed( new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing( false );
                        Programa_principal( );
                    }
                },2000 );

            }
        } );
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

    private void Programa_principal()
    {

        progressBar.getIndeterminateDrawable().setColorFilter(0xff2196F3, PorterDuff.Mode.SRC_IN );
        progressBar.getProgressDrawable().setColorFilter(0xff2196F3, PorterDuff.Mode.SRC_IN );
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls( true );
        webSettings.setDisplayZoomControls(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                view.loadUrl(url);

                return true;
            }
        });
        mWebView.loadUrl(urll);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress)
            {
                progressBar.setProgress(0);
                getActivity().setProgress(progress * 1000);
                progressBar.incrementProgressBy(progress);
                if (progress == 100) progressBar.setVisibility(View.GONE);
                else   progressBar.setVisibility(View.VISIBLE);
            }
        });
    }



}
