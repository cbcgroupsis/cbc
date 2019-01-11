package cbcgroup.cbc.Insumos;

import android.widget.Filter;

import java.util.ArrayList;

class CustomFilter extends Filter
{
    private final AdapterInsumos adapter;
    private final ArrayList<ListInsumo> filterList;
    private final int list;
    public CustomFilter(ArrayList<ListInsumo> filterList, AdapterInsumos adapterInsumos,int list)
    {
        this.filterList = filterList;
        this.adapter=adapterInsumos;
        this.list=list;

    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results= new FilterResults();

        if (constraint != null && constraint.length() > 0) {
            constraint = constraint.toString().toUpperCase();
            ArrayList<ListInsumo> filteredInsumo = new ArrayList<>();

            for (int i = 0; i < filterList.size(); i++) {
                //CHECK
                //LiST 1: INSUMOS -> npedido,nombre cliente
                //LiST 2: INSUMOS SUB ITEM -> modelo, numero de serie
                if(list==1 || list==4)
                {
                    if (filterList.get( i ).getNumPedido().toUpperCase().contains( constraint )) {
                        filteredInsumo.add( filterList.get( i ) );
                    } else if (filterList.get( i ).getNombreCliente().toUpperCase().contains( constraint )) {
                        filteredInsumo.add( filterList.get( i ) );
                    }
                }else if(list==2)
                {
                    if (filterList.get( i ).getNumSerie().toUpperCase().contains( constraint )) {
                        filteredInsumo.add( filterList.get( i ) );
                    } else if (filterList.get( i ).getModelo().toUpperCase().contains( constraint )) {
                        filteredInsumo.add( filterList.get( i ) );
                    }
                }else if(list==3)
                {
                    if (filterList.get( i ).getOneDate().toUpperCase().contains( constraint )) {
                        filteredInsumo.add( filterList.get( i ) );
                    }
                }
            }

            results.count = filteredInsumo.size();
            results.values = filteredInsumo;
        } else {
            results.count = filterList.size();
            results.values = filterList;

        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results)
    {
        adapter.insumos = (ArrayList<ListInsumo>) results.values;
        adapter.notifyDataSetChanged();
    }
}
