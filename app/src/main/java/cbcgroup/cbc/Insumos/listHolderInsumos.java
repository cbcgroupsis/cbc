package cbcgroup.cbc.Insumos;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import cbcgroup.cbc.R;


public class listHolderInsumos extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView numPedido,nombreCliente,numSerie,modelo,oneDate;
    ItemClickListener itemClickListener;
    public listHolderInsumos(View itemView,int List)
    {
        super( itemView );
        if(List==1 || List==4)
        {
            numPedido=itemView.findViewById( R.id.insumosNpedido );
            nombreCliente=itemView.findViewById( R.id.insumosNombreCliente );
        }else if(List==2)
        {
            numSerie=itemView.findViewById( R.id.insumosNumSerie );
            modelo=itemView.findViewById( R.id.insumosModelo );
        }else if(List==3)
        {
            oneDate=itemView.findViewById( R.id.list3OneDate);
        }

        itemView.setOnClickListener( this );

    }

    @Override
    public void onClick(View v)
    {
        this.itemClickListener.onItemClick( v,getLayoutPosition() );
    }
    public void onLogClick(ItemClickListener ic){this.itemClickListener=ic;}
    public void itemClickListener(ItemClickListener ic)
    {
        this.itemClickListener=ic;
    }

}
