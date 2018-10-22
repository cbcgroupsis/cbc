package cbcgroup.cbc.Insumos;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import java.util.ArrayList;

import cbcgroup.cbc.Activity.CamActivity;
import cbcgroup.cbc.Activity.InsumosSubItem;
import cbcgroup.cbc.Activity.TecnicoIn;
import cbcgroup.cbc.Activity.TecnicosActivity;
import cbcgroup.cbc.Activity.TecnicosOut;
import cbcgroup.cbc.Clases.CBC;
import cbcgroup.cbc.R;
import cbcgroup.cbc.dbLocal.ConnSQLiteHelper;
import cbcgroup.cbc.dbLocal.SQLite;
import cbcgroup.cbc.dbLocal.Tablas.dbNombresTecSa;
import cbcgroup.cbc.dbLocal.Tablas.dbTecnicos;

public class AdapterInsumos extends RecyclerView.Adapter<listHolderInsumos> implements Filterable
{
    private static final String TAG = "AdapterInsumos";
    private String URL = "http://tecnicos.cbcgroup.com.ar/Test/app_android/test.php?";
    private Context ctx;
    ArrayList<ListInsumo> insumos,filterList;
    private CustomFilter filter;
    private int List=1;
    private int pos=0;
    private CBC cbc;
    private SQLite sql;
    private ConnSQLiteHelper con;

    public AdapterInsumos(Context ctx, ArrayList<ListInsumo> insumos, int list) {
        this.ctx = ctx;
        this.insumos = insumos;
        this.filterList = insumos;
        this.List=list;
        cbc=new CBC( ctx );
        con =  new ConnSQLiteHelper( ctx);
        sql = new SQLite();
    }

    @NonNull
    @Override
    public listHolderInsumos onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v=null;
        if(List==1 ||List==4) v= LayoutInflater.from(parent.getContext()).inflate( R.layout.list1,null );
        if(List==2) v= LayoutInflater.from(parent.getContext()).inflate( R.layout.list2_subitem,null );
        if(List==3)v = LayoutInflater.from(parent.getContext()).inflate( R.layout.list3_onedate,null );

        listHolderInsumos holder= new listHolderInsumos( v,List);

        return holder;
    }

    @Override
    public void onBindViewHolder(listHolderInsumos holder, int position)
    {
            //LiST 1: INSUMOS -> npedido,nombre cliente
            //LiST 2: INSUMOS SUB ITEM -> modelo, numero de serie
            //LiST 3: TECNICOS SUPER ADMIN -> nombre de todos los tec
            //LIST 4: TECNICOS PEDIDOS -> Pedidos tecnicos, Npedido,cliente
            pos=position;
            if(List==1)
            {
                holder.numPedido.setText( insumos.get( pos ).getNumPedido() );
                holder.nombreCliente.setText( insumos.get( pos ).getNombreCliente() );


                holder.itemClickListener( new ItemClickListener() {
                    @Override
                    public void onItemClick(View v, int numPoss) {
                        final String npedido = insumos.get( numPoss ).numPedido.toString();
                        final String nombrecliente=insumos.get(numPoss).nombreCliente.toString();
                        Toast.makeText( ctx, "Pedido Nº: "+npedido, Toast.LENGTH_SHORT ).show();
                        ctx.startActivity( new Intent( ctx, InsumosSubItem.class ).putExtra( "npedido",npedido ).putExtra( "nomcliente",nombrecliente ) );
                    }
                } );
            }else if(List==2)
            {
                holder.numSerie.setText( insumos.get( pos ).getNumSerie() );
                holder.modelo.setText( insumos.get( pos ).getModelo());
                holder.itemClickListener( new ItemClickListener() {
                    @Override
                    public void onItemClick(View v, int numPoss) {
                        final String modelo = insumos.get( numPoss ).modelo.toString();
                        final String numSerie=insumos.get(numPoss).numSerie.toString();
                        Toast.makeText( ctx, "Pedido Nº: "+numSerie+modelo, Toast.LENGTH_SHORT ).show();
                        ctx.startActivity( new Intent( ctx, CamActivity.class ).putExtra( "modelo",modelo ).putExtra( "numSerie",numSerie ) );
                    }
                } );
            }else if(List==3)
            {

                holder.oneDate.setText( insumos.get(pos).getOneDate() );
                holder.itemClickListener( new ItemClickListener() {
                    @Override
                    public void onItemClick(View v, int numPoss) {
                        String nameTec= insumos.get( numPoss ).oneDate.toString();
                        Toast.makeText( ctx, nameTec, Toast.LENGTH_SHORT ).show();
                        cbc.setTecSa(nameTec );
                        ctx.startActivity( new Intent( ctx,TecnicosActivity.class ).putExtra( "idTec",idTec( nameTec )) );

                    }
                } );
                /*holder.onLogClick( new ItemClickListener() {
                    @Override
                    public void onItemClick(View v, int numPoss) {
                            Toast.makeText( ctx,"APRETADO" + insumos.get( numPoss ).oneDate.toString(),Toast.LENGTH_LONG ).show();
                    }
                } );*/
            }else if(List==4)
            {
                holder.numPedido.setText( insumos.get( pos ).getNumPedido() );
                holder.nombreCliente.setText( insumos.get( pos ).getNombreCliente() );


                holder.itemClickListener( new ItemClickListener() {
                    @Override
                    public void onItemClick(View v, int numPoss) {
                        final String npedido = insumos.get( numPoss ).numPedido.toString();
                        final String nombrecliente=insumos.get(numPoss).nombreCliente.toString();
                        Toast.makeText( ctx, "Pedido Nº: "+npedido, Toast.LENGTH_SHORT ).show();
                        /***/
                        SQLiteDatabase db = con.getReadableDatabase();
                        String SQL="SELECT Ingreso FROM "+ dbTecnicos.TABLE+ " WHERE nParte='"+npedido+"'";
                        Cursor resp=db.rawQuery( SQL,null);
                        if(resp.moveToPosition( 0))
                        {

                            if(!resp.getString( 0 ).equals( "" ))
                            {
                                //Tecnicos Out
                                ctx.startActivity(new Intent( ctx, TecnicosOut.class ).putExtra( "nameTecSa",cbc.getTecSa() ).putExtra( "npedido",npedido ));
                            }else
                                {
                                    //tecnicos In.
                                    ctx.startActivity(new Intent( ctx, TecnicoIn.class ).putExtra( "nameTecSa",cbc.getTecSa() ).putExtra( "npedido",npedido ));
                                }


                        }
                        db.close();
                        /***/
                        //if(cbc.getIngresoTecnico() && cbc.getIngresoNpedido().equals( npedido ))ctx.startActivity(new Intent( ctx, TecnicosOut.class ).putExtra( "nameTecSa",cbc.getTecSa() ).putExtra( "npedido",npedido ));
                       //else ctx.startActivity(new Intent( ctx, TecnicoIn.class ).putExtra( "nameTecSa",cbc.getTecSa() ).putExtra( "npedido",npedido ));
                    }
                } );
            }

    }

    @Override
    public int getItemCount() {
        return insumos.size();
    }

    @Override
    public Filter getFilter()
    {
        if(filter==null)
        {
            filter= new CustomFilter(filterList,this,List);
        }
        return filter;
    }

    private String idTec(String condicion)
    {
        String idTec="";
        SQLiteDatabase db = con.getReadableDatabase();
        String SQL="SELECT idTec FROM "+ dbNombresTecSa.TABLE + " WHERE Nombres='"+condicion+"';";
        Cursor resp=db.rawQuery( SQL,null);
        if(resp.moveToPosition( 0 )) idTec= resp.getString(0);
        db.close();
        return  idTec;
    }
}
