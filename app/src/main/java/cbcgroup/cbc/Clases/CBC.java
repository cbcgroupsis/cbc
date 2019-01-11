package cbcgroup.cbc.Clases;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class CBC
{
    /**
     * asdasd
     */
    private final Context ctx;
    private ProgressDialog loading;
    /**/
    private final SharedPreferences getstorage;
    private final SharedPreferences insumos;
    private SharedPreferences.Editor storage;
    /**/
    public CBC(Context ctx)
    {
        this.ctx=ctx;
        getstorage=ctx.getSharedPreferences( "userInfo",Context.MODE_PRIVATE );
        insumos=ctx.getSharedPreferences( "insumos",Context.MODE_PRIVATE );

    }
    public void msg(String msg){Toast.makeText( ctx, msg, Toast.LENGTH_LONG ).show();}
    public void debug(String TAG,String msg){Log.w(TAG,msg);}
    public void progressDialog(String titulo,String mensaje){loading=ProgressDialog.show(ctx, titulo, mensaje, false, false);}
    public void progressDialogCancel(){loading.dismiss();}
    /** cbc-new **/
    /////////////////////////////////////////////////////////////////////////////////////////////////
    public Boolean Internet()
    {
        try
        {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 correo.cbcgroup.com.ar");
            int val           = p.waitFor();
            return (val == 0);
        } catch (Exception e){e.printStackTrace();}
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public void Msj(String msj){Toast.makeText( ctx, msj, Toast.LENGTH_LONG ).show();}
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public String spaceRemplace(String dato,String remplace)
    {
        StringBuilder aux= new StringBuilder();
        char[] strAux=dato.toCharArray();
        for(int a=0;a<dato.length();a++)
        {
            if(strAux[a]==' ')
            {
                aux.append( remplace );
            }else aux.append( Character.toString( strAux[a] ) );
        }
        return aux.toString();
    }

    private void SaveStorage(String response)
    {
        try {

            File f = new File( insumos +".TXT");
            f.delete();
            String FILE_NAME = insumos +".txt";
            FileOutputStream fos = ctx.openFileOutput(FILE_NAME,Context.MODE_PRIVATE);
            fos.write( response.getBytes());
            fos.close();
        } catch (Exception ignored) {}
    }
    /***********************************************************************************************/

    public void setInsumos(String response)
    {
        SaveStorage( response );
    }
    public void setUserName(String name)
    {
        storage=getstorage.edit();
        storage.putString( "userName",name );
        storage.apply();
    }
    public void setUserId(String name)
    {
        storage=getstorage.edit();
        storage.putString( "userId",name );
        storage.apply();
    }
    public void setUserPassword(String name)
    {
        storage=getstorage.edit();
        storage.putString( "userPassword",name );
        storage.apply();
    }
    public void setUserSector(String sector)
    {
        storage=getstorage.edit();
        storage.putString( "userSector",sector );
        storage.apply();
    }
    public void setTecSa(String sector)
    {
        storage=getstorage.edit();
        storage.putString( "tecSa",sector );
        storage.apply();
    }
    public void setUserEmail(String email)
    {
        storage=getstorage.edit();
        storage.putString( "userEmail",email );
        storage.apply();
    }
    public String getTecSa()
    {
        return getstorage.getString( "tecSa","" );
    }
    public void setIngresoTecnico(Boolean ingreso)
    {
        storage=getstorage.edit();
        storage.putBoolean( "tecIngreso",ingreso);
        storage.apply();
    }
    public void setIngresonpedido(String ingreso)
    {
        storage=getstorage.edit();
        storage.putString ("ingresoNpedido",ingreso);
        storage.apply();
    }
    public void setSession(boolean bool)
    {
        storage=getstorage.edit();
        storage.putBoolean ("Session",bool);
        storage.apply();
    }
    public Boolean getSession()
    {
        return getstorage.getBoolean( "Session",false );
    }

    public String getdUserName()
    {
        return getstorage.getString( "userName","" );
    }
    public String getdUserId()
    {
        return getstorage.getString( "userId","" );
    }
    public String getdUserPassword()
    {
        return getstorage.getString( "userPassword","" );
    }
    public String getdUserSector()
    {
        return getstorage.getString( "userSector","" );
    }
    public String getdUserEmail()
    {
        return getstorage.getString( "userEmail","" );
    }



    public void setInsumosNpedidos(String npedidos)
    {
        storage=getstorage.edit();
        storage.putString( "npedidos",npedidos );
        storage.apply();
    }
    public void setInsumosClientes(String npedidos)
    {
        storage=getstorage.edit();
        storage.putString( "clientes",npedidos );
        storage.apply();
    }
    public String getInsumosNpedidos()
    {
        return getstorage.getString( "npedidos","" );
    }
    public String getInsumosClientes()
    {
        return getstorage.getString( "clientes","" );
    }




}
