package cbcgroup.cbc.Insumos;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
    private final Context ctx;
    public ArrayList<ListInsumo> insumos;
    private final ArrayList<ListInsumo> filterList;
    private CustomFilter filter;
    private int List=1;
    private final CBC cbc;
    private final ConnSQLiteHelper con;

    public AdapterInsumos(Context ctx, ArrayList<ListInsumo> insumos, int list) {
        this.ctx = ctx;
        this.insumos = insumos;
        this.filterList = insumos;
        this.List=list;
        cbc=new CBC( ctx );
        con =  new ConnSQLiteHelper( ctx);
        SQLite sql = new SQLite();
    }

    @NonNull
    @Override
    public listHolderInsumos onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v=null;
        if(List==1) v= LayoutInflater.from(parent.getContext()).inflate( R.layout.list1,parent,false );
        if(List==2) v= LayoutInflater.from(parent.getContext()).inflate( R.layout.list2_subitem,parent,false );
        if(List==3)v = LayoutInflater.from(parent.getContext()).inflate( R.layout.list3_onedate,parent,false );
        if(List==4) v= LayoutInflater.from(parent.getContext()).inflate( R.layout.list4,parent,false );

        return new listHolderInsumos( v,List);
    }

    @Override
    public void onBindViewHolder(listHolderInsumos holder, int position)
    {
            //LiST 1: INSUMOS -> npedido,nombre cliente
            //LiST 2: INSUMOS SUB ITEM -> modelo, numero de serie
            //LiST 3: TECNICOS SUPER ADMIN -> nombre de todos los tec
            //LIST 4: TECNICOS PEDIDOS -> Pedidos tecnicos, Npedido,cliente
        if(List==1)
            {
                holder.numPedido.setText( insumos.get( position ).getNumPedido() );
                holder.nombreCliente.setText( insumos.get( position ).getNombreCliente() );

                holder.itemClickListener( new ItemClickListener() {
                    @Override
                    public void onItemClick(int numPoss) {
                        final String npedido = insumos.get( numPoss ).numPedido;
                        final String nombrecliente= insumos.get(numPoss).nombreCliente;
                        Toast.makeText( ctx, "Pedido Nº: "+npedido, Toast.LENGTH_SHORT ).show();
                        ctx.startActivity( new Intent( ctx, InsumosSubItem.class ).putExtra( "npedido",npedido ).putExtra( "nomcliente",nombrecliente ) );
                    }
                } );
            }else if(List==2)
            {
                holder.numSerie.setText( insumos.get( position ).getNumSerie() );
                holder.modelo.setText( insumos.get( position ).getModelo());
                holder.itemClickListener( new ItemClickListener() {
                    @Override
                    public void onItemClick(int numPoss) {
                        final String modelo = insumos.get( numPoss ).modelo;
                        final String numSerie= insumos.get(numPoss).numSerie;
                        Toast.makeText( ctx, "Pedido Nº: "+numSerie+modelo, Toast.LENGTH_SHORT ).show();
                        ctx.startActivity( new Intent( ctx, CamActivity.class ).putExtra( "modelo",modelo ).putExtra( "numSerie",numSerie ) );
                    }
                } );
            }else if(List==3)
            {

                holder.oneDate.setText( insumos.get( position ).getOneDate() );
                holder.itemClickListener( new ItemClickListener() {
                    @Override
                    public void onItemClick(int numPoss) {
                        String nameTec= insumos.get( numPoss ).oneDate;
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
                holder.numPedido.setText( insumos.get( position ).getNumPedido() );
                holder.nombreCliente.setText( insumos.get( position ).getNombreCliente() );
                holder.categoria.setText(insumos.get( position ).getCategoria());
                holder.horaVence.setText(insumos.get( position ).getHoraVence());

                final String npedido = insumos.get( position ).getNumPedido();
                SQLiteDatabase db = con.getReadableDatabase();
                String SQL="SELECT Ingreso FROM "+ dbTecnicos.TABLE+ " WHERE nParte='"+npedido+"'";
                Cursor resp=db.rawQuery( SQL,null);
                if(resp.moveToPosition( 0))
                {
                    if(!resp.getString( 0 ).equals( "" )) holder.categoria.setBackgroundColor( Color.RED );
                }
                db.close();

                holder.itemClickListener( new ItemClickListener() {
                    @Override
                    public void onItemClick(int numPoss) {
                        final String npedido = insumos.get( numPoss ).numPedido;
                        final String nombrecliente= insumos.get(numPoss).nombreCliente;
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
