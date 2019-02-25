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
import android.database.SQLException;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

import cbcgroup.cbc.Clases.CBC;
import cbcgroup.cbc.R;
import cbcgroup.cbc.dbLocal.ConnSQLiteHelper;
import cbcgroup.cbc.dbLocal.SQLite;
import cbcgroup.cbc.dbLocal.Tablas.dbInsumos;
import cbcgroup.cbc.dbLocal.Tablas.dbInsumosSinInternet;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class CamActivity extends AppCompatActivity implements View.OnClickListener
{
    private TextView txvModelo,txvSerie,txvCliente;
    private ImageButton imgInsumos;
    private Button btnEnviar;
    private final String TAG ="CamActivity";
    private CBC cbc;
    private Uri imageUri;
    private static final int PICTURE_RESULT = 122;
    ///////////////// CAM RESULT /////////////////
    private Bitmap thumbnail;
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
        con =  new ConnSQLiteHelper( this);
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
    private void CargarInfo()
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

    private boolean Permisos()
    {
        if (ActivityCompat.checkSelfPermission( CamActivity.this, CAMERA ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission( CamActivity.this, WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions( new String[]{WRITE_EXTERNAL_STORAGE,CAMERA}, 101 );
            }
            Toast.makeText( CamActivity.this, "No tiene activado los permisos para usar la camara", Toast.LENGTH_SHORT ).show();
           return false;
        }else return true;
    }
    private void SacarFoto()
    {
        //////////////////Sacar fotos /////////////////
        ContentValues values = new ContentValues();
        values.put( MediaStore.Images.Media.TITLE, "cbc" );
        values.put( MediaStore.Images.Media.DESCRIPTION, System.currentTimeMillis() );
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values );
        Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
        intent.putExtra( MediaStore.EXTRA_OUTPUT, imageUri );
        startActivityForResult( intent, PICTURE_RESULT );
    }
    private void SubirImagen()
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
                            String imageurl = getRealPathFromURI( imageUri );
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }

                    /***/
                }
                break;
            case 0:
                if (requestCode == 0) {

                    // si tout s'est bien passee
                    if (resultCode == CamActivity.RESULT_OK) {

                        // contents est la valeur contenue dans notre code barre ou QR

                        String contents = data.getStringExtra("SCAN_RESULT");

                        Toast.makeText(this,"Valeur decryptee : "+contents, Toast.LENGTH_LONG).show();
                        Log.w(TAG,contents);

                    }

                    // si operation annulee
                    if(resultCode == CamActivity.RESULT_CANCELED){

                        Toast.makeText(this,"Operation annulee", Toast.LENGTH_LONG).show();

                    }

                }
                break;


        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null && Objects.requireNonNull( cursor ).moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow( MediaStore.Images.Media.DATA );
            res = cursor.getString( column_index );
        }
        if (cursor != null) {
            cursor.close();
        }
        return res;
    }
    /***********************************************************************************************/
    private String getStringImagen(Bitmap bmp)
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
        String URL="http://tecnicos.cbcgroup.com.ar/Test/app_android/produccion/api/android.php/Insumos/uploadImg";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest( Request.Method.POST, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String s)
                    {
                        Log.w(TAG,"resp:"+s);
                        loading.dismiss();
                        try {
                            Log.w("RESPUESTACAM","resp:"+s);
                            JSONObject response = new JSONObject(s);
                            JSONArray res=response.getJSONArray( "resp" );
                            JSONObject obj= res.getJSONObject( 0 );
                            if(obj.getBoolean( "status" )) {
                                Toast.makeText( CamActivity.this, "Imagen Subida", Toast.LENGTH_LONG ).show();
                                if (thumbnail != null)
                                    getContentResolver().delete( imageUri, null, null );
                                startActivity( new Intent( CamActivity.this, HomeActivity.class ).putExtra( "homeStart", "homeStart" ).addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP ) );
                            }else
                                {
                                    GuardarInformacion();
                                }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        loading.dismiss();
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
                params.put("img", imagen);
                params.put("serie",extra.getString( "numSerie" ));
                params.put("nameTec", cbc.getdUserName());
                params.put("npedido",cbc.getInsumosNpedidos() ); //npedido
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
        db=con.getWritableDatabase();
        String SQL = "DELETE FROM " + dbInsumos.TABLE+ " WHERE nPedido='" + cbc.getInsumosNpedidos() + "';";
        try {
            db.execSQL( SQL );
            Log.w( TAG, "FILA BORRADA" );
        } catch (SQLException e) {
            Log.w( TAG, e.toString() );
        }
        db.close();

        startActivity( new Intent(this,HomeActivity.class ).putExtra( "homeStart","homeStart" ).addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP ));
    }
}
