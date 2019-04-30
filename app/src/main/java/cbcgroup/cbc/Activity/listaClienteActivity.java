package cbcgroup.cbc.Activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

import cbcgroup.cbc.Clases.CBC;
import cbcgroup.cbc.Insumos.AdapterInsumos;
import cbcgroup.cbc.Insumos.ListInsumo;
import cbcgroup.cbc.R;
import cbcgroup.cbc.dbLocal.ConnSQLiteHelper;
import cbcgroup.cbc.dbLocal.Tablas.dbGraficos;
import cbcgroup.cbc.dbLocal.Tablas.dbTecnicos;

public class listaClienteActivity extends AppCompatActivity
{
    private static final String TAG = "listaCliente-";
    private android.support.v7.widget.SearchView searchView;
    private RecyclerView recyclerView;
    private ListInsumo insumos = new ListInsumo();
    private AdapterInsumos adapter;
    private ImageButton actualizar;
    private ConnSQLiteHelper con;
    private CBC cbc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_lista_cliente );
        con = new ConnSQLiteHelper(this);
        searchView=findViewById( R.id.mSearch );
        recyclerView=findViewById( R.id.myRecycler );
        actualizar=findViewById( R.id.actualizar );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        recyclerView.setItemAnimator( new DefaultItemAnimator() );
     //   Lista();

        Log.w(TAG,getIntent().getStringExtra( "type" ));
        ProgramaSinConexion();

    }

    private void Lista()
    {
        final ArrayList<ListInsumo> list=new ArrayList<>();
        list.clear();


        for(int i=0;i<10;i++)
        {

                insumos=new ListInsumo();
                insumos.setNumPedido("1");
                insumos.setNombreCliente( "angel");
                insumos.setCategoria( "7");
                insumos.setHoraVence( "angel");
                list.add(insumos);


        }
        adapter=new AdapterInsumos(this,list,1);
        recyclerView.setAdapter(adapter);
    }


    private void ProgramaSinConexion()
    {


        final ArrayList<ListInsumo> list=new ArrayList<>();
        list.clear();
        SQLiteDatabase db = con.getReadableDatabase();
        String SQL="SELECT np,e,ns,m FROM "+ dbGraficos.TABLE +" WHERE e='"+getIntent().getStringExtra( "type" )+"' AND ty='"+getIntent().getStringExtra( "activity" )+"'";
        Cursor resp=db.rawQuery( SQL,null);
        Log.w(TAG,"contador:"+resp.getCount());
        for(int i=0;i<resp.getCount();i++)
        {
            if(resp.moveToPosition( i ))
            {

                insumos=new ListInsumo();
                insumos.setNumPedido( resp.getString( 0 ) );
                insumos.setNombreCliente( resp.getString( 1 )+"\n\r"+resp.getString( 3 ));
                insumos.setCategoria( String.valueOf( getIntent().getStringExtra( "type" ).charAt( 0 ) ) );
                insumos.setHoraVence( resp.getString( 2 ) );
                list.add(insumos);

            }
        }

        adapter=new AdapterInsumos(this,list,4);
        recyclerView.setAdapter(adapter);
        db.close();
        if(resp.getCount()==0)
        {
            if(getIntent().getStringExtra( "activity" ).equals( "insumos" ))Toast.makeText( this, "No hay insumos "+getIntent().getStringExtra( "type" ), Toast.LENGTH_SHORT ).show();
            else Toast.makeText( this, "No hay servicio tecnico "+getIntent().getStringExtra( "type" ), Toast.LENGTH_SHORT ).show();
            finish();
        }
    }

}
