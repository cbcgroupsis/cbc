package cbcgroup.cbc.Activity;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

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
    private final static String TAG="LoginActivity";
    private CheckBox sesion;
    private CBC cbc;
    private LinearLayout linearLayout;
    private String token;
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
        linearLayout=findViewById( R.id.linearLayout );
        linearLayout.setOnClickListener( this );
        cbc= new CBC(LoginActivity.this);
        if(cbc.getSession())HomeStart();                                                            //Verifico si la sesion esta guardad.Si es asi, directamente voy a la pantalla principal (home)
        token= FirebaseInstanceId.getInstance().getToken();                                         //Lectura del identificador unico del celular.



    }

    @Override
    public void onClick(View btnPush)
    {
        if(btnLogin==btnPush && ReadDataIsNull( userName,userPassword ))                            //Verifico si el bonton de logear se apreto y si los datos ingresados no estan vacios.
        {
            SignIn();                                                                               //Llamo a la funcion "SignIn" para la autentificacion.
        }
        if(linearLayout==btnPush)                                                                   //Utilizo esta sentencia para ocultar el teclado.
        {
            View view = this.getCurrentFocus();                                                     //Guardo en una variable si se hizo algun foco en algun Editext.
            if (view != null)                                                                       //Si no se hizo foco en alguno de eso, oculto el teclado.
            {
                InputMethodManager imm = (InputMethodManager)getSystemService(LoginActivity.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void SignIn()                                                                           //En esta funcion autentifico las credenciales del usuario.
    {
        cbc.progressDialog( "Logeando...","Espere por favor..." );                   //Dialogo en pantalla indicando al usuario que se esta autentificando la informacion ingresada.
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);             //Creo una nueva cola de solicitud.
        StringRequest stringRequest = new StringRequest( Request.Method.POST, CfgCbc.ULRLOGIN,                  //Creo una String de solicitud, en el cual cargo la URL y El metodo Post.
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String s)                                                //Respuesta de la solicitud en formato String.
                    {
                        cbc.progressDialogCancel();                                                 //Cierro el dialogo.
                        try                                                                         //Utilizo un Try/Catch por si la respuesta es null para no tener problemas con la aplicacion.
                        {
                            JSONObject response = new JSONObject(s);                                 //Convierto el String en un Objeto.
                            JSONArray res=response.getJSONArray( "Login" );                   //Busco en el objeto el key "Login" y convierto todos los objetos hijos en un arreglo.
                            JSONObject obj = res.getJSONObject(0);                            //Obtengo los valores de la posicion 0 del arreglo.
                            if(obj.getString( "acceso" ).equals( "true" ))                    //Busco en la posicion 0 el valor del objeto "acceso". Si el valor es "true" significa que la autentificacion fue correcta.
                            {
                                if(sesion.isChecked()) cbc.setSession( true );                      //Verifico si el usuario quiere guardar la sesion, si es asi guardo la sesion.
                                else cbc.setSession( false );                                       //caso contrario, indico que el usuario no quiere guardar la sesion.
                                cbc.setUserName(obj.getString( "nombre" ) );                  //Obtengo el valor del objeto "nombre" y lo guardo en la memoria.
                                cbc.setUserSector(obj.getString( "sector" ));                 //Obtengo el valor del objeto "sector" y lo guardo en la memoria.
                                cbc.setUserId(obj.getString( "iduser" ) );                    //Obtengo el valor del objeto "iduser" y lo guardo en la memoria.
                                cbc.setUserEmail(obj.getString( "mail" ) );                   //Obtengo el valor del objeto "mail" y lo guardo en la memoria.
                                HomeStart();                                                        //Llamo al metodo HomeStart y inicializo la actividad Home.
                            }else cbc.msg( "No se puede autentificar, verifique los datos ingresados" ); //Caso de que la autentificacion fuera erronea, indico que los datos ingresados fueron incorrectos.
                        } catch (Exception error)                                                   //
                        {
                            Log.w(TAG,"VolleyError->"+error);                                  //En caso de haber un error, lo muestro por consola.
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)                            //Error al hacer la solicitud a la URL indicada
                    {
                        cbc.progressDialogCancel();                                                 //Cierro el dialogo.
                        cbc.debug( TAG,"voleyError->"+volleyError.toString() );               //Muestro en la consola el error.
                        cbc.msg( volleyError.toString() );                                          //Muestro al usuario a travez de la pantalla el error.
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new Hashtable<>();                                     //Parametros a enviar.
                params.put("user_name",userName.getText().toString());                              //nombre del usuario
                params.put("user_password",userPassword.getText().toString());                      //contraseña.
                params.put("token",token);                                                          //Envio el identificador unico del celular.
                return params;                                                                      //Envio los parametros por el metodo post.
            }

        };
        requestQueue.add(stringRequest);                                                            //Agrego una solicitud
    }

    private void HomeStart()                                                                        //Con este metodo cierro la actividad actual (pantalla de login) y inicializo la actividad home(pantalla principal).
    {
        Intent homeStart=new Intent( LoginActivity.this,HomeActivity.class );         //Creo un Intent indicando que el destino como la actividad HomeActivity.
        homeStart.addFlags(  Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );    //Agrego flags de borrar todas las actividades que se encuetra en la pila.
        startActivity( homeStart );                                                                 //Inicio la actividad de Home.
        finish();                                                                                   //Finalizo la actividad.
    }
    private boolean ReadDataIsNull(TextInputEditText user,TextInputEditText password)               //Con este metodo detecto si los campos ingresados por el usuario son nulo o vacio.
    {
        final String User=user.getText().toString();                                                //Creo una variable del tipo String y tomo el texto del nombre del usuario.
        final String Password=password.getText().toString();                                        //Creo una variable del tipo String y tomo el texto de la contraseña del usuario.
        if(User.matches( "" ) ||Password.matches( "" ))                                //Verificio si alguno de las 2 variables son nulas o vacias.
        {
            Log.w(TAG,"ReadUserData-> Ingrese todos los campos");                              //Muestro por consola un mensaje de "error".
            Toast.makeText( this, "Ingrese todos los campos", Toast.LENGTH_SHORT ).show();//Indico en la pantalla al usuario que no completo todos los campos.
            return false;                                                                           //Retorno un false indicando que los cambos estan vacios.
        }else return true;                                                                          //Retorno un true indicando que los campos se han rellenado con un nombre de usuario y contraseña.
    }

}

