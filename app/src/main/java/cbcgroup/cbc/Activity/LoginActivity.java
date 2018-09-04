package cbcgroup.cbc.Activity;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

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


/***************************************************************************************************/
//Autor: Angel Vazquez
//Empresa: CBC GROUP
//Fecha:31/08/2018
//Version 15
/***************************************************************************************************/
public class LoginActivity extends AppCompatActivity implements View.OnClickListener,CfgCbc
{
    private TextInputEditText userName,userPassword;
    private Button btnLogin;
    private String URL=CfgCbc.ULRLOGIN;
    private final static String TAG="LoginActivity";
    private CheckBox check;
    CBC cbc;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );
        userName=findViewById( R.id.edtUserName );
        userPassword=findViewById( R.id.edtUserPassword );
        btnLogin=findViewById( R.id.btnLoginConfirm );
        btnLogin.setOnClickListener( this );
        check=findViewById( R.id.login_checkbox );
        cbc= new CBC(LoginActivity.this);
        if(cbc.getSession())HomeStart();
    }

    @Override
    public void onClick(View btnPush)
    {
            if(btnPush==btnLogin && !userName.getText().toString().matches( "" ) && !userPassword.getText().toString().matches( "" ))
            {
                cbc.debug(TAG,getString( R.string.ConfirmUserPassword ));
                cbc.progressDialog( "Logeando...","Espere por favor..." );
                Auth();


            }else{ cbc.msg( getString(R.string.ErrorUserPassword) ); cbc.debug( TAG,"btn Login->No se rellenaron todos los campos" );}
    }

    void Auth()
    {
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
                            //Acceso por CBC. Consulto si existe.

                            JSONObject response = new JSONObject(s);
                            JSONArray res=response.getJSONArray( "Login" );
                            JSONObject obj = res.getJSONObject(0);
                            if(obj.getString( "acceso" ).equals( "true" ))
                            {
                                String mail=obj.getString( "mail" );
                                String nombre=obj.getString( "nombre" );
                                String sector=obj.getString( "sector" );
                                String iduser=obj.getString( "iduser" );
                                HomeStart();

                                cbc.setUserName( nombre );
                                cbc.setUserSector( sector);
                                cbc.setUserId(iduser );
                                cbc.setUserEmail( mail );
                                if(check.isChecked()) cbc.setSession( true );
                                else cbc.setSession( false );
                            /*    Log.w(TAG,"respo"+nombre+sector);
                                if(mail.matches( "" )) mail=cbc.spaceRemplace(edtUserName.getText().toString(),"_" )+"@appcbcgroup.com.ar";
                                if(edtPassword.getText().toString().length()<=4) password=edtPassword.getText().toString()+"password";
                                else password=edtPassword.getText().toString();
                                signIn( mail,password,nombre,sector );*/
                                cbc.debug( TAG,mail+nombre+sector );
                            }else cbc.msg( "No se puede autentificar, verifique los datos ingresados" );

                        } catch (Exception error) {}
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
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


    void HomeStart()
    {
        Intent homeStart=new Intent( LoginActivity.this,HomeActivity.class );
        homeStart.addFlags(  Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
        startActivity( homeStart );
        finish();
    }

}
