package cbcgroup.cbc.Activity;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import cbcgroup.cbc.Clases.CBC;
import cbcgroup.cbc.Clases.NetworkSchedulerService;
import cbcgroup.cbc.Fragment.ClientesFragment;
import cbcgroup.cbc.Fragment.HomeFragment;
import cbcgroup.cbc.Fragment.InsumosFragment;
import cbcgroup.cbc.Fragment.ListTecnicosSuperAdmin;
import cbcgroup.cbc.Fragment.WebDct;
import cbcgroup.cbc.Fragment.mapRutaDiaFragment;
import cbcgroup.cbc.R;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        InsumosFragment.OnFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener,
        ListTecnicosSuperAdmin.OnFragmentInteractionListener,
        mapRutaDiaFragment.OnFragmentInteractionListener,
        ClientesFragment.OnFragmentInteractionListener,
        WebDct.OnFragmentInteractionListener{


    private static final String TAG = "TAG_HomeActivity";
    private NavigationView navigationView ;
    private CBC cbc;
    private boolean webUse=false,fragmentUse=true;

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        scheduleJob();
        navigationView  =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Home();
        cbc= new CBC(HomeActivity.this);

        FloatingActionButton fab = findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make( view, "Pantalla en construccion", Snackbar.LENGTH_LONG )
                        .setAction( "Action", null ).show();
            }
        } );

        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();

        NavigationView navigationView = findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( this );



    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById( R.id.drawer_layout );
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
        if (id == R.id.home)
                if(cbc.getdUserSector()=="Cliente") clienteDCT();
                else Home();
        /********************LOGISTICA***************************/
        else if (id == R.id.logistica_insumos)InsumosFragment();
        /******************* Tecnicos *************************/
        else if(id==R.id.tecnicos_tecnicos)
        {
            if(cbc.getdUserSector().equals( "super admin" ) || cbc.getdUserSector().equals( "direccion") ||  cbc.getdUserSector().equals( "comercial") || cbc.getdUserSector().equals( "supervisor tecnico") )TecnicosFragment();
            else startActivity( new Intent( HomeActivity.this,TecnicosActivity.class ));
        }else if(id==R.id.ruta_dia)
        {
            RutaDia();
        }

        /******************* Cliente  ************************/
        else if(id== R.id.cliente_pedidos_insumos) Web_Redirect("insumos");
        else if(id== R.id.cliente_tecnicos)Web_Redirect("tecnicos");
        else if(id== R.id.cliente_contadores) pantalla_en_construccion();
        else if(id== R.id.cliente_preferencia)clienteDCT();

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

    private void menuInfo()
    {
        TextView edtEmailMenu = findViewById( R.id.edtMailMenu );
        TextView edtNombreMenu = findViewById( R.id.edtNombreMenu );
        edtNombreMenu.setText( cbc.getdUserName()+"-"+ cbc.getdUserSector() );
        edtEmailMenu.setText(cbc.getdUserEmail());

        //Dependiendo del tipo de usuario, muestro diferentes menus.
        if ((cbc.getdUserSector().equals( "super admin" )) || (cbc.getdUserSector().equals( "direccion" )) || (cbc.getdUserSector().equals( "comercial" ))) {
            navigationView.getMenu().setGroupVisible( R.id.menu_logistica, true );
            navigationView.getMenu().setGroupVisible( R.id.menu_cliente, true );
            navigationView.getMenu().setGroupVisible( R.id.menu_tecnicos, true );
            // cliente = true;
        } else {
            switch (cbc.getdUserSector()) {
                case "tecnicos":
                case "usuarios interior":
                    navigationView.getMenu().setGroupVisible( R.id.menu_tecnicos, true );
                    break;
                case "cac":
                case "deposito":
                    navigationView.getMenu().setGroupVisible( R.id.menu_logistica, true );
                    break;
                case "Cliente":
                    navigationView.getMenu().setGroupVisible( R.id.menu_cliente, true );
                    clienteDCT();
                    // cliente = true;
                    // cliente_aux = true;
                    break;
            }
        }


    }
    private void WebDct()
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
   private void TecnicosFragment()
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



    private void HomeFragmento()
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

    private void InsumosFragment()
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
    private void Web_Redirect(String redirect)
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

    private void RutaDia()
    {


        Toast.makeText( this, "RUTA DEL DIA", Toast.LENGTH_SHORT ).show();
        webUse=true;
        fragmentUse=true;
        mapRutaDiaFragment  webdct= new mapRutaDiaFragment();
        if (( cbc.getdUserSector().equals( "super admin" )) || ( cbc.getdUserSector().equals( "direccion" )) ||  ( cbc.getdUserSector().equals( "comercial" ))) {


            Bundle bundle = new Bundle();
            bundle.putString( "dato_aux", "https://tecnicos.cbcgroup.com.ar/test/app_android/Desarrollo/web/html/map.html?nameTec=superAdmin" );
            webdct.setArguments( bundle );
        }
        FragmentTransaction transition= getSupportFragmentManager().beginTransaction();
        transition.replace(R.id.contenedor,webdct);
        transition.addToBackStack(null);
        transition.commit();
    }
    private void clienteDCT()
    {
        Toast.makeText( this, "WEB-DCT", Toast.LENGTH_SHORT ).show();
        webUse=true;
        fragmentUse=true;
        ClientesFragment webdct= new ClientesFragment();
        webdct.setEnterTransition( new Slide( Gravity.BOTTOM ) );
        webdct.setExitTransition( new Slide( Gravity.BOTTOM ) );
        FragmentTransaction transition= getSupportFragmentManager().beginTransaction();
        transition.replace(R.id.contenedor,webdct);
        transition.addToBackStack(null);
        transition.commit();
    }
    private void Home()
    {

        webUse=false;
        fragmentUse=false;

        HomeFragmento();
    }

    private void UltimoInicioDeSesion()
    {
        Log.w(TAG,"ultimoInicioDeSesion");
    }

    @Override
    public void onFragmentInteraction() {

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
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scheduleJob() {
        JobInfo myJob = new JobInfo.Builder(0, new ComponentName(this, NetworkSchedulerService.class))
                .setRequiresCharging(true)
                .setMinimumLatency(1000)
                .setOverrideDeadline(2000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService( Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(myJob);
    }

    @Override
    protected void onStop() {
        // A service can be "started" and/or "bound". In this case, it's "started" by this Activity
        // and "bound" to the JobScheduler (also called "Scheduled" by the JobScheduler). This call
        // to stopService() won't prevent scheduled jobs to be processed. However, failing
        // to call stopService() would keep it alive indefinitely.
        stopService(new Intent(this, NetworkSchedulerService.class));
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start service and provide it a way to communicate with this class.
        Intent startServiceIntent = new Intent(this, NetworkSchedulerService.class);
        startService(startServiceIntent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}