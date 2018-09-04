package cbcgroup.cbc.Clases;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class CBC
{
    Context ctx;
    ProgressDialog loading;
    /**/
    SharedPreferences getstorage,insumos;
    SharedPreferences.Editor storage,insumosEdit;
    private String neuText="";
    /**/
    public CBC(Context ctx)
    {
        this.ctx=ctx;
        getstorage=ctx.getSharedPreferences( "userInfo",Context.MODE_PRIVATE );
        storage=getstorage.edit();
        insumos=ctx.getSharedPreferences( "insumos",Context.MODE_PRIVATE );
        insumosEdit=insumos.edit();
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
            boolean reachable = (val == 0);
            return reachable;
        } catch (Exception e){e.printStackTrace();}
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public void Msj(String msj){Toast.makeText( ctx, msj, Toast.LENGTH_LONG ).show();}
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public String spaceRemplace(String dato,String remplace)
    {
        String aux="";
        char[] strAux=dato.toCharArray();
        for(int a=0;a<dato.length();a++)
        {
            if(strAux[a]==' ')
            {
                aux+=remplace;
            }else aux+=Character.toString( strAux[a] );
        }
        return aux;
    }
    public String spaceEncoode(String dato)
    {
        String aux="";
        char[] strAux=dato.toCharArray();
        for(int a=0;a<dato.length();a++)
        {
            if(strAux[a]==' ')
            {
                aux+="%20";
            }else aux+=Character.toString( strAux[a] );
        }
        return aux;
    }
    /**********************************************************************************************/
    public String ReadStorage(String filename )
    {
        try {
            String FILE_NAME = filename+".txt";
            FileInputStream fin = ctx.openFileInput(FILE_NAME);
            int size;
            while ((size = fin.read()) != -1) {neuText += Character.toString((char) size); }
        } catch (Exception error) {}
        return  neuText;
    }

    public void SaveStorage(String filename,String response)
    {
        try {

            File f = new File(filename+".TXT");
            f.delete();
            String FILE_NAME = filename+".txt";
            String content = response;


            FileOutputStream fos = ctx.openFileOutput(FILE_NAME,Context.MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.close();
        } catch (Exception error) {}
    }
    /***********************************************************************************************/
    public void contInsumos(int cont)
    {
        insumosEdit.putInt( "contInsumos",cont );
        insumosEdit.commit();
    }

    public void setInsumos(String response)
    {
        SaveStorage( "insumos",response );
    }
    public void setUserName(String name)
    {
        storage.putString( "userName",name );
        storage.commit();
    }
    public void setUserId(String name)
    {
        storage.putString( "userId",name );
        storage.commit();
    }
    public void setUserPassword(String name)
    {
        storage.putString( "userPassword",name );
        storage.commit();
    }
    public void setUserSector(String sector)
    {
        storage.putString( "userSector",sector );
        storage.commit();
    }
    public void setTecSa(String sector)
    {
        storage.putString( "tecSa",sector );
        storage.commit();
    }
    public void setUserEmail(String email)
    {
        storage.putString( "userEmail",email );
        storage.commit();
    }
    public String getTecSa()
    {
        return getstorage.getString( "tecSa","" );
    }
    public void setIngresoTecnico(Boolean ingreso)
    {
        storage.putBoolean( "tecIngreso",ingreso);
        storage.commit();
    }
    public void setIngresonpedido(String ingreso)
    {
        storage.putString ("ingresoNpedido",ingreso);
        storage.commit();
    }
    public void setSession(boolean bool)
    {
        storage.putBoolean ("Session",bool);
        storage.commit();
    }
    public Boolean getSession()
    {
        return getstorage.getBoolean( "Session",false );
    }

    public String getIngresoNpedido()
    {
        return getstorage.getString( "ingresoNpedido","" );
    }

    public Boolean getIngresoTecnico()
    {
        return getstorage.getBoolean( "tecIngreso",false );
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
    public String getdInsumos()
    {
        return ReadStorage( "insumos" );
    }
    public int getdContInsumos()
    {
        return insumos.getInt( "contInsumos",0 );
    }



    public void setInsumosNpedidos(String npedidos)
    {
        storage.putString( "npedidos",npedidos );
        storage.commit();
    }
    public void setInsumosClientes(String npedidos)
    {
        storage.putString( "clientes",npedidos );
        storage.commit();
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
