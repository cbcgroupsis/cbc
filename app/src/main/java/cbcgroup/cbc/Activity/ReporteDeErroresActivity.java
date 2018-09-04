package cbcgroup.cbc.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Hashtable;
import java.util.Map;

import cbcgroup.cbc.R;

public class ReporteDeErroresActivity extends AppCompatActivity implements View.OnClickListener {
    String URL="http://tecnicos.cbcgroup.com.ar/test/app_android/v14/reportes.php";
    SharedPreferences info;
    String nombre;
    String mail;
    String mensaje;
    EditText editText;
    Button btnEnviar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_reporte_de_errores );
        editText=findViewById( R.id.mensajeReporet );
        info=getSharedPreferences( "userInfo",MODE_PRIVATE );
        btnEnviar=findViewById( R.id.btnRepor );
        btnEnviar.setOnClickListener( this );
    }

    @Override
    public void onClick(View v)
    {
        if(v==btnEnviar)
        {
            nombre=info.getString( "name" ,"");
            mail=info.getString("mail","");
            mensaje=editText.getText().toString();

            if(!mensaje.matches( "" ) )
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_DARK);
                alertDialogBuilder.setMessage("Desea enviar reporte?");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton("Si",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)

                            {
                                Internet( nombre,mensaje,mail );

                            }
                        });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Toast.makeText(ReporteDeErroresActivity.this, "Cancelado", Toast.LENGTH_SHORT).show();

                    }
                });
                AlertDialog alertDialog=alertDialogBuilder.create();
                alertDialog.show();
            }else Toast.makeText( ReporteDeErroresActivity.this,"Ingrese un texto",Toast.LENGTH_LONG ).show();
        }
    }



    void Internet(final String nombre, final String mensaje, final String mail)
    {

        StringRequest stringRequest = new StringRequest( Request.Method.POST, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String s)
                    {
                        Toast.makeText(ReporteDeErroresActivity.this, "Mensaje Enviado", Toast.LENGTH_LONG).show();
                        Intent home=new Intent( ReporteDeErroresActivity.this,HomeActivity.class );
                        home.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                        ReporteDeErroresActivity.this.startActivity( home );
                        finish();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        Toast.makeText( ReporteDeErroresActivity.this,volleyError.toString(),Toast.LENGTH_LONG ).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new Hashtable<>();
                params.put("name",nombre);
                params.put("mail",mail);
                params.put("mensaje",mensaje);
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
