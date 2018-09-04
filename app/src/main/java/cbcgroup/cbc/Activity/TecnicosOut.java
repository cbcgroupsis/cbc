package cbcgroup.cbc.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.widget.EditText;
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

import cbcgroup.cbc.Clases.CBC;
import cbcgroup.cbc.R;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class TecnicosOut extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = "TecnicosInActivity";
    private String URL = "http://tecnicos.cbcgroup.com.ar/test/app_android/v14/hoja_de_reparacion.php?";
    private String URL2 = "http://tecnicos.cbcgroup.com.ar/test/app_android/v14/tecnicos.php";
    private String URLIMAGEN = "http://tecnicos.cbcgroup.com.ar/test/app_android/v14/imagenTecnico.php";
    private TextView serie,sector,fecha,modelo,fechaVencimiento;
    private EditText tareaRealizada,copias,copiasColor,viaje;
    private ImageButton imgTec;
    private Button button;
    private String nombre;
    private Bundle extra;
    private CBC cbc;

    //////////////////Sacar fotos /////////////////
    private ContentValues values;
    private Uri imageUri;
    private static final int PICTURE_RESULT = 122;
    ///////////////// CAM RESULT /////////////////
    private Bitmap thumbnail;
    private String imageurl;
    /////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_tecnicos_out );
        modelo=findViewById( R.id.tec_modelo );
        serie=findViewById( R.id.tec_subItem_serie );
        sector=findViewById( R.id.tec_subItem_sector);
        fecha=findViewById( R.id.tec_subItem_fecha);
        tareaRealizada=findViewById( R.id.tarea_realizada);
        copias=findViewById( R.id.contador );
        copiasColor=findViewById( R.id.contadorColor );
        viaje=findViewById( R.id.Tviaje );
        button=findViewById( R.id.cerrarPedido );
        fechaVencimiento=findViewById( R.id.tec_subItem_fechaVencimiento );
        imgTec=findViewById( R.id.imgTecnico );
        button.setOnClickListener( this );
        imgTec.setOnClickListener( this );
        cbc= new CBC(TecnicosOut.this);
        extra=getIntent().getExtras();
        if(cbc.Internet()) completeInfo();
        else Toast.makeText( this,"NO TIENE ACCESO A INTERNET O SU CONNCECION ES MUY LENTA, VUELVA ATRAS Y INTENTE NUEVAMENTE",Toast.LENGTH_LONG ).show();
    }

    @Override
    public void onClick(View v)
    {
        if(v==button)
        {
            if(tareaRealizada.length()>=5)
            {
                if (!copiasColor.getText().toString().matches( "" )) copiasColor.setText( "0" );
                if (!copias.getText().toString().matches( "" )) copias.setText( "0" );
                if (!viaje.getText().toString().matches( "" ))
                {
                    CerrarPedido();
                   // SubirImagen();

                } else
                    Toast.makeText( this, "Ingrese el Tiempo de viaje", Toast.LENGTH_SHORT ).show();
            }else Toast.makeText( this, "Ingrese la tarea realizada", Toast.LENGTH_SHORT ).show();
        }

        if(v==imgTec)
        {

            Log.w(TAG,"btnCam  IMAGENfunciona");
            if(Permisos())SacarFoto();

        }
    }
    void completeInfo()
    {
        cbc.progressDialog( "Cargando Informacion...","Espere por favor." );
        RequestQueue requestQueue = Volley.newRequestQueue(TecnicosOut.this);
        StringRequest stringRequest = new StringRequest( Request.Method.POST, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String s)
                    {
                        cbc.progressDialogCancel();
                        Log.w(TAG,"Resp:"+s);
                        try
                        {
                            JSONObject response= new JSONObject( s );
                            JSONArray tecSubItem= response.getJSONArray( "hoja_reparacion" );
                            JSONObject obj=tecSubItem.getJSONObject(0 );
                            modelo.setText(obj.getString( "modelo" ));
                            serie.setText(obj.getString( "serie" ));
                            sector.setText(obj.getString( "sector" ));
                            fecha.setText(obj.getJSONObject( "fecha" ).getString("date"));
                            fechaVencimiento.setText(obj.getJSONObject( "fechaVence" ).getString("date") );
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
                        cbc.progressDialogCancel();
                        Log.i( TAG,volleyError.toString());
                        Toast.makeText( TecnicosOut.this, "Error: "+volleyError.toString(), Toast.LENGTH_SHORT ).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new Hashtable<>();
                if(cbc.getdUserSector().equals( "super admin" )) nombre=extra.getString( "nameTecSa" );
                else nombre=cbc.getdUserName();
                params.put("Content-Type","application/json; charset=utf-8");
                params.put("name_tec",nombre );
                params.put("id_parte", extra.getString( "npedido" ));
                return params;
            }

        };
        requestQueue.add(stringRequest);
    }

    void CerrarPedido()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_DARK);
        alertDialogBuilder.setMessage("Desea Cerrar el pedido tecnico?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Si", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)

            {
                regresoQuery();

            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(TecnicosOut.this, "Cancelado", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog=alertDialogBuilder.create();
        alertDialog.show();
    }


    void regresoQuery()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(TecnicosOut.this);
        StringRequest stringRequest = new StringRequest( Request.Method.POST, URL2,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String s)
                    {
                        cbc.setIngresoTecnico(false);
                        cbc.setIngresonpedido( extra.getString( "" ));
                        Toast.makeText( TecnicosOut.this, "Se cerro el pedido correctamente!", Toast.LENGTH_SHORT ).show();
                        SubirImagen();

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        Log.i( TAG,volleyError.toString());
                        Toast.makeText( TecnicosOut.this, "Error: "+volleyError.toString(), Toast.LENGTH_SHORT ).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new Hashtable<>();
                //params.put("Content-Type","application/json; charset=utf-8");
                params.put("id_tecnico",cbc.getdUserId() );
                params.put("serie",serie.getText().toString());
                params.put("id_parte", extra.getString( "npedido" ));
                params.put("mensaje",tareaRealizada.getText().toString());
                params.put("copias",copias.getText().toString());
                params.put("copiasColor",copiasColor.getText().toString());
                params.put("viaje",viaje.getText().toString());
                return params;
            }

        };
        requestQueue.add(stringRequest);
    }

    /******************************* FOTO *************************************/
    boolean Permisos()
    {
        if (ActivityCompat.checkSelfPermission( TecnicosOut.this, CAMERA ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission( TecnicosOut.this, WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions( new String[]{WRITE_EXTERNAL_STORAGE,CAMERA}, 101 );
            }
            Toast.makeText( TecnicosOut.this, "No tiene activado los permisos para usar la camara", Toast.LENGTH_SHORT ).show();
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



                uploadImage();



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
                            imgTec.setImageBitmap(thumbnail );
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
        final ProgressDialog loading= new ProgressDialog(TecnicosOut.this,ProgressDialog.THEME_HOLO_DARK );
        loading.setMessage( "Espere por favor..." );
        loading.setTitle( "Subiendo..." );
        loading.setCancelable( false );
        loading.setButton( DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(TecnicosOut.this, "Cancelado", Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }
        } );
        loading.show();
        StringRequest stringRequest = new StringRequest( Request.Method.POST, URLIMAGEN,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String s)
                    {
                        loading.dismiss();
                        Toast.makeText(TecnicosOut.this, "Imagen Subida", Toast.LENGTH_LONG).show();
                        startActivity( new Intent(TecnicosOut.this,HomeActivity.class ).putExtra( "homeStart","homeStart" ).addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP ));
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        loading.dismiss();
                        Toast.makeText(TecnicosOut.this, "Error: No se pudo subir la imagen", Toast.LENGTH_LONG).show();
                        //Toast.makeText( Cam.this,volleyError.toString(),Toast.LENGTH_LONG ).show();

                    }
                })
        {
            @Override
            protected Map<String, String> getParams(){
                Bitmap foto;

                foto = Bitmap.createScaledBitmap(thumbnail, 500, 500, true);
                String imagen = getStringImagen( foto);

                Map<String, String> params = new Hashtable<>();
                params.put("foto", imagen);
                params.put("nparte",extra.getString( "npedido" ));
                params.put("idtecnico",cbc.getdUserId());
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

}
