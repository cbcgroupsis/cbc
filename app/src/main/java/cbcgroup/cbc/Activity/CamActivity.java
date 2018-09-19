package cbcgroup.cbc.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.util.Map;

import cbcgroup.cbc.Clases.CBC;
import cbcgroup.cbc.R;
import cbcgroup.cbc.dbLocal.ConnSQLiteHelper;
import cbcgroup.cbc.dbLocal.SQLite;
import cbcgroup.cbc.dbLocal.Tablas.dbInsumosSinInternet;
import cbcgroup.cbc.dbLocal.Tablas.dbTecSinInternet;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class CamActivity extends AppCompatActivity implements View.OnClickListener
{
    private TextView txvModelo,txvSerie,txvCliente;
    private ImageButton imgInsumos;
    private Button btnEnviar;
    private String TAG ="CAM";
    private CBC cbc;
    String URL = "http://tecnicos.cbcgroup.com.ar/Test/app_android/imagen.php";
    //////////////////Sacar fotos /////////////////
    private ContentValues values;
    private Uri imageUri;
    private static final int PICTURE_RESULT = 122;
    ///////////////// CAM RESULT /////////////////
    private Bitmap thumbnail;
    private String imageurl;
    /////////////////////////////////////////////
    String nombre,serie;
    private SQLite sql;
    private ConnSQLiteHelper con;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_cam );
        txvCliente=findViewById( R.id.txvCliente );
        txvModelo=findViewById( R.id.txvModelo );
        txvSerie=findViewById( R.id.txvSerie );
        imgInsumos=findViewById( R.id.imgInsumos );
        btnEnviar=findViewById( R.id.btnEnviarFoto );
        btnEnviar.setOnClickListener( this );
        imgInsumos.setOnClickListener( this );
        cbc = new CBC(CamActivity.this);
        con =  new ConnSQLiteHelper( this,"bdtecSinInternet",null, 1);
        sql = new SQLite();
        CargarInfo();


    }
    @Override
    public void onClick(View v)
    {
        if(v==btnEnviar)
        {
            Log.w(TAG,"btnCam funciona");
            SubirImagen();

        }
        if(v==imgInsumos)
        {
            Log.w(TAG,"btnCam  IMAGENfunciona");
            if(Permisos()) SacarFoto();
        }
    }
    void CargarInfo()
    {
        Bundle extra= getIntent().getExtras();
        if(extra!=null)
        {
            Log.w(TAG,"INTENT"+extra.getString( "numSerie" )+extra.getString( "modelo" ));
            txvSerie.setText( cbc.getInsumosNpedidos() +" - "+cbc.spaceRemplace( extra.getString( "numSerie" ),"" ) );
            txvModelo.setText( cbc.spaceRemplace(extra.getString( "modelo" ),"") );
            txvCliente.setText( cbc.spaceRemplace( cbc.getInsumosClientes(),"" ) );
        }

    }

    boolean Permisos()
    {
        if (ActivityCompat.checkSelfPermission( CamActivity.this, CAMERA ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission( CamActivity.this, WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions( new String[]{WRITE_EXTERNAL_STORAGE,CAMERA}, 101 );
            }
            Toast.makeText( CamActivity.this, "No tiene activado los permisos para usar la camara", Toast.LENGTH_SHORT ).show();
           return false;
        }else return true;
    }
    void SacarFoto()
    {
        values = new ContentValues();
        values.put( MediaStore.Images.Media.TITLE, "cbc" );
        values.put( MediaStore.Images.Media.DESCRIPTION, System.currentTimeMillis() );
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values );
        Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
        intent.putExtra( MediaStore.EXTRA_OUTPUT, imageUri );
        startActivityForResult( intent, PICTURE_RESULT );
    }
    void SubirImagen()
    {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_DARK);
        alertDialogBuilder.setMessage("Desea Subir Imagen?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Si", new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            if(cbc.Internet()) uploadImage();
            else GuardarInformacion();

            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(CamActivity.this, "Cancelado", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog=alertDialogBuilder.create();
        alertDialog.show();

    }
    /***********************************************************************************************/
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case PICTURE_RESULT:
                if (requestCode == PICTURE_RESULT)
                {
                    if (resultCode == Activity.RESULT_OK)
                    {
                        try
                        {
                            thumbnail = MediaStore.Images.Media.getBitmap( getContentResolver(), imageUri );
                            imgInsumos.setImageBitmap(thumbnail );
                            //Obtiene la ruta donde se encuentra guardada la imagen.
                            imageurl = getRealPathFromURI( imageUri );
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery( contentUri, proj, null, null, null );
        int column_index = cursor.getColumnIndexOrThrow( MediaStore.Images.Media.DATA );
        cursor.moveToFirst();
        return cursor.getString( column_index );
    }
    /***********************************************************************************************/
    public String getStringImagen(Bitmap bmp)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
    private void uploadImage()
    {
        //final ProgressDialog loading = ProgressDialog.show(this, "Subiendo...", "Espere por favor...", false, false);
        final ProgressDialog loading= new ProgressDialog(CamActivity.this,ProgressDialog.THEME_HOLO_DARK );
        loading.setMessage( "Espere por favor..." );
        loading.setTitle( "Subiendo..." );
        loading.setCancelable( false );
        loading.setButton( DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(CamActivity.this, "Cancelado", Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }
        } );
        loading.show();
        StringRequest stringRequest = new StringRequest( Request.Method.POST, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String s)
                    {
                        loading.dismiss();
                        Toast.makeText(CamActivity.this, "Imagen Subida", Toast.LENGTH_LONG).show();
                        startActivity( new Intent(CamActivity.this,HomeActivity.class ).putExtra( "homeStart","homeStart" ).addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP ));
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        loading.dismiss();
                        //Toast.makeText(CamActivity.this, "Error: No se pudo subir la imagen", Toast.LENGTH_LONG).show();

                        //Toast.makeText( Cam.this,volleyError.toString(),Toast.LENGTH_LONG ).show();
                        GuardarInformacion();

                    }
                })
        {
            @Override
            protected Map<String, String> getParams(){
                Bitmap foto;

                foto = Bitmap.createScaledBitmap(thumbnail, 500, 500, true);
                String imagen = getStringImagen( foto);

                Map<String, String> params = new Hashtable<>();
                Bundle extra= getIntent().getExtras();
                params.put("foto", imagen);
                params.put("serie",extra.getString( "numSerie" ));
                params.put("name", cbc.getdUserName());
                params.put("nombre",cbc.getInsumosNpedidos() ); //npedido
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void GuardarInformacion()
    {
        Bitmap foto;
        String imagen="";
        Bundle extra= getIntent().getExtras();
        if(thumbnail!=null)
        {
            foto = Bitmap.createScaledBitmap(thumbnail, 500, 500, true);
            imagen = getStringImagen( foto);
        }
        Map<String, String> params = new Hashtable<>();
        params.clear();
        params.put( dbInsumosSinInternet.CAMPO_FOTO, imagen);
        params.put(dbInsumosSinInternet.CAMPO_SERIE,extra.getString( "numSerie" ));
        params.put(dbInsumosSinInternet.CAMPO_NOMBRE, cbc.getdUserName());
        params.put(dbInsumosSinInternet.CAMPO_NPEDIDO,cbc.getInsumosNpedidos());
        SQLiteDatabase db=con.getWritableDatabase();
        sql.Add(db, dbInsumosSinInternet.TABLE,params);
        startActivity( new Intent(this,HomeActivity.class ).putExtra( "homeStart","homeStart" ).addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP ));
    }
}
