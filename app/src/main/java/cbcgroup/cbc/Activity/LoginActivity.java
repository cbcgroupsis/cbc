package cbcgroup.cbc.Activity;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Hashtable;
import java.util.Map;
import cbcgroup.cbc.Clases.CBC;
import cbcgroup.cbc.Clases.CfgCbc;
import cbcgroup.cbc.R;

/***************************************************************************************************
//Autor: Angel Vazquez
//Empresa: CBC GROUP
//Fecha:31/08/2018
//Version 15
***************************************************************************************************/
public class LoginActivity extends AppCompatActivity implements View.OnClickListener,CfgCbc
{
    private TextInputEditText userName,userPassword;
    private Button btnLogin;
    private String URL=CfgCbc.ULRLOGIN;
    private final static String TAG="LoginActivity";
    private CheckBox sesion;
    private CBC cbc;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );
        userName=findViewById( R.id.edtUserName );
        userPassword=findViewById( R.id.edtUserPassword );
        btnLogin=findViewById( R.id.btnLoginConfirm );
        btnLogin.setOnClickListener( this );
        sesion=findViewById( R.id.login_checkbox );
        cbc= new CBC(LoginActivity.this);
        if(cbc.getSession())HomeStart();

    }

    @Override
    public void onClick(View btnPush)
    {
        if(btnLogin==btnPush && ReadDataIsNull( userName,userPassword ))
        {
            SignIn();
        }
    }

    private void SignIn()
    {
        cbc.progressDialog( "Logeando...","Espere por favor..." );
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        StringRequest stringRequest = new StringRequest( Request.Method.POST, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String s)
                    {
                        cbc.progressDialogCancel();
                        try
                        {
                            JSONObject response = new JSONObject(s);
                            JSONArray res=response.getJSONArray( "Login" );
                            JSONObject obj = res.getJSONObject(0);
                            if(obj.getString( "acceso" ).equals( "true" ))
                            {
                                if(sesion.isChecked()) cbc.setSession( true );
                                else cbc.setSession( false );
                                cbc.setUserName(obj.getString( "nombre" ) );
                                cbc.setUserSector(obj.getString( "sector" ));
                                cbc.setUserId(obj.getString( "iduser" ) );
                                cbc.setUserEmail(obj.getString( "mail" ) );
                                HomeStart();
                            }else cbc.msg( "No se puede autentificar, verifique los datos ingresados" );

                        } catch (Exception error)
                        {

                            Log.w(TAG,"VolleyError->"+error);
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        cbc.progressDialogCancel();

                        cbc.debug( TAG,"voleyError->"+volleyError.toString() );
                        cbc.msg( volleyError.toString() );

                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new Hashtable<>();
                params.put("user_name",userName.getText().toString());
                params.put("user_password",userPassword.getText().toString());
                return params;
            }

        };
        requestQueue.add(stringRequest);
    }


    private void HomeStart()
    {
        Intent homeStart=new Intent( LoginActivity.this,HomeActivity.class );
        homeStart.addFlags(  Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
        startActivity( homeStart );
        finish();
    }
    private boolean ReadDataIsNull(TextInputEditText user,TextInputEditText password)
    {
        final String User=user.getText().toString();
        final String Password=password.getText().toString();
        if(User.matches( "" ) ||Password.matches( "" ))
        {
            Log.w(TAG,"ReadUserData-> Ingrese todos los campos");
            Toast.makeText( this, "Ingrese todos los campos", Toast.LENGTH_SHORT ).show();
            return false;
        }else return true;
    }

}

