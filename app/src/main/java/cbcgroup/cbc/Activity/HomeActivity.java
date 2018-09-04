package cbcgroup.cbc.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import cbcgroup.cbc.Clases.CBC;
;
import cbcgroup.cbc.Fragment.HomeFragment;
import cbcgroup.cbc.Fragment.InsumosFragment;
import cbcgroup.cbc.Fragment.ListTecnicosSuperAdmin;
import cbcgroup.cbc.Fragment.WebDct;
import cbcgroup.cbc.R;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,InsumosFragment.OnFragmentInteractionListener,HomeFragment.OnFragmentInteractionListener,ListTecnicosSuperAdmin.OnFragmentInteractionListener,WebDct.OnFragmentInteractionListener{


    private static final String TAG = "HomeActivity" ;
    //private FirebaseAuth firebaseAuth;
    //private FirebaseAuth.AuthStateListener authStateListener;
    private NavigationView navigationView ;
    private TextView edtNombreMenu,edtEmailMenu;
    private CBC cbc;
    private boolean webUse=false,fragmentUse=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        navigationView  =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Home();
        cbc= new CBC(HomeActivity.this);

        FloatingActionButton fab = (FloatingActionButton) findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make( view, "Pantalla en construccion", Snackbar.LENGTH_LONG )
                        .setAction( "Action", null ).show();
            }
        } );

        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( this );
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        if (drawer.isDrawerOpen( GravityCompat.START )) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.home, menu );
        menuInfo();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id=item.getItemId();
        if (id == R.id.home)Home();
        /********************LOGISTICA***************************/
        else if (id == R.id.logistica_insumos)InsumosFragment();
        /******************* Tecnicos *************************/
        else if(id==R.id.tecnicos_tecnicos)
        {
            if(cbc.getdUserSector().equals( "super admin" ) || cbc.getdUserSector().equals( "direccion") ||  cbc.getdUserSector().equals( "comercial"))TecnicosFragment();
            else startActivity( new Intent( HomeActivity.this,TecnicosActivity.class ));
        }
        /******************* Cliente  ************************/
        else if(id== R.id.cliente_pedidos_insumos) Web_Redirect("insumos");
        else if(id== R.id.cliente_tecnicos)Web_Redirect("tecnicos");
        else if(id== R.id.cliente_contadores) pantalla_en_construccion();
        else if(id== R.id.cliente_preferencia)Web_Redirect("preferencias");

        /****************** Extras ***************************/
        else if (id == R.id.web_dct)WebDct();
        else if (id == R.id.option_menu)
        {
            pantalla_en_construccion();
        }else if(id == R.id.reportar) startActivity( new Intent( HomeActivity.this,ReporteDeErroresActivity.class ) );
        else if (id == R.id.login_close)
        {
         //   firebaseAuth.signOut();
            cbc.setSession( false );
            startActivity( new Intent(HomeActivity.this,LoginActivity.class).addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP ));
            finish();
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void pantalla_en_construccion()
    {
        Toast.makeText(HomeActivity.this,"PANTALLA EN CONSTRUCCION",Toast.LENGTH_LONG).show();
    }

    /*************************************************FIREBASE ************************************/
   /* private void inicialize()
    {
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser!=null)
                {
                    Bundle extra= getIntent().getExtras();
                    if(extra!=null)InsumosFragment();

                    Log.w( TAG,"onAuthStateChanged - Sign_in  uid"+ firebaseUser.getUid());
                    Log.w( TAG,"onAuthStateChanged - Sign_in email"+ firebaseUser.getEmail());
                }else
                {
                    Log.w( TAG,"onAuthStateChanged - Sign_Out ");

                }
            }
        };
    }
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener( authStateListener );

        // Home();

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        firebaseAuth.removeAuthStateListener( authStateListener );
    }*/
    /***********************************************************************************************/

    void menuInfo()
    {
        edtEmailMenu=findViewById( R.id.edtMailMenu );
        edtNombreMenu=findViewById( R.id.edtNombreMenu);
        edtNombreMenu.setText( cbc.getdUserName()+"-"+ cbc.getdUserSector() );
        edtEmailMenu.setText(cbc.getdUserEmail());

        //Dependiendo del tipo de usuario, muestro diferentes menus.
        if (( cbc.getdUserSector().equals( "super admin" )) || ( cbc.getdUserSector().equals( "direccion" )) ||  ( cbc.getdUserSector().equals( "comercial" ))) {
            navigationView.getMenu().setGroupVisible( R.id.menu_logistica, true );
            navigationView.getMenu().setGroupVisible( R.id.menu_cliente, true );
            navigationView.getMenu().setGroupVisible( R.id.menu_tecnicos, true );
            // cliente = true;
        } else if (cbc.getdUserSector().equals( "tecnicos" )) {
            navigationView.getMenu().setGroupVisible( R.id.menu_tecnicos, true );
        } else if (cbc.getdUserSector() .equals( "cac" ) || cbc.getdUserSector() .equals( "deposito" )) {
            navigationView.getMenu().setGroupVisible( R.id.menu_logistica, true );
        } else if (cbc.getdUserSector().equals( "Cliente" )) {
            navigationView.getMenu().setGroupVisible( R.id.menu_cliente, true );
            //  Web();
            // cliente = true;
            // cliente_aux = true;
        } else if (cbc.getdUserSector().equals( "administracion" )) {

        } else if (cbc.getdUserSector().equals( "comercial" )) {

        } else if (cbc.getdUserSector().equals( "compras" )) {

        } else if (cbc.getdUserSector().equals( "deposito" )) {

        } else if (cbc.getdUserSector().equals( "reacondicionado" )) {

        } else if (cbc.getdUserSector().equals( "sueldos" )) {

        } else if (cbc.getdUserSector().equals( "supervisor" )) {

        } else if (cbc.getdUserSector().equals( "usuarios interior" )) {

        }


    }
    void WebDct()
    {
        Toast.makeText( this, "WEB-DCT", Toast.LENGTH_SHORT ).show();
        webUse=true;
        fragmentUse=true;
        WebDct webdct= new WebDct();
        webdct.setEnterTransition( new Slide( Gravity.BOTTOM ) );
        webdct.setExitTransition( new Slide( Gravity.BOTTOM ) );
        FragmentTransaction transition= getSupportFragmentManager().beginTransaction();
        transition.replace(R.id.contenedor,webdct);
        transition.addToBackStack(null);
        transition.commit();

    }
   void TecnicosFragment()
    {
        Toast.makeText( this, "Tecnicos", Toast.LENGTH_SHORT ).show();
        fragmentUse=true;
        ListTecnicosSuperAdmin tecc = new ListTecnicosSuperAdmin();
        tecc.setEnterTransition( new Slide( Gravity.RIGHT ) );
        tecc.setExitTransition(new Slide(Gravity.BOTTOM));
        FragmentTransaction transition= getSupportFragmentManager().beginTransaction();
        transition.replace(R.id.contenedor,tecc);
        transition.addToBackStack(null);
        transition.commit();
    }



    void HomeFragmento()
    {
        Toast.makeText( this, "HOME", Toast.LENGTH_SHORT ).show();
        HomeFragment home = new HomeFragment();
        home.setEnterTransition( new Slide( Gravity.TOP ) );
        home.setExitTransition(new Slide(Gravity.BOTTOM));
        FragmentTransaction transition= getSupportFragmentManager().beginTransaction();
        transition.replace(R.id.contenedor,home);
        transition.addToBackStack(null);
        transition.commit();

    }

    void InsumosFragment()
    {
        Toast.makeText( this, "INSUMOS", Toast.LENGTH_SHORT ).show();
        fragmentUse=true;
        InsumosFragment  insumos = new InsumosFragment();
        insumos.setEnterTransition( new Slide( Gravity.RIGHT ) );
        insumos.setExitTransition(new Slide(Gravity.BOTTOM));
        FragmentTransaction transition= getSupportFragmentManager().beginTransaction();
        transition.replace(R.id.contenedor,insumos);
        transition.addToBackStack(null);
        transition.commit();

    }
    //Redireccionamiento del WEB DCT
    void Web_Redirect(String redirect)
    {
        //Inicializo primero el fragment webdct, para activar la sesion.
        WebDct();

        WebDct webdct= new WebDct();
        //Cargo la url a la que quiero redireccionar.
        Bundle bundle=new Bundle( );
        bundle.putString( "dato_aux","https://tecnicos.cbcgroup.com.ar/test/pedidosweb/"+redirect+".aspx?" +
                "id=" +cbc.getdUserName() +
                "&id1="+ cbc.getdUserPassword());
        webdct.setArguments( bundle );
        FragmentTransaction transition= getSupportFragmentManager().beginTransaction();
        transition.replace(R.id.contenedor,webdct);
        transition.addToBackStack(null);
        transition.commit();
    }

    void Home()
    {

        webUse=false;
        fragmentUse=false;
        HomeFragmento();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    /**********************************************************************************************/

    //Deteccion del boton retroceder.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        WebView mWebView = findViewById( R.id.web_frag_dct);
        //Detecto el evento cuando se apreta el boton de rertroceder.
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            //Pregunto si la vista del WEB DCT se encuentra actiavada.
            if(webUse)
            {
                if (mWebView.canGoBack())    mWebView.goBack();
                else Home();
            }else if(fragmentUse) Home();
            else finish();


        }
        Log.w(TAG,"AFUUERA AFUERA");
        return false;
     /*   if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (mWebView.canGoBack()) {
                        mWebView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
*/


    }
}
