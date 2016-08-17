package br.com.abner.gpsalarmepro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.abner.gpsalarmepro.R;

/**
 * Created by RavDellV2 on 12/05/2016.
 */
public class MyAdapter extends BaseAdapter{
    private LayoutInflater mInflater;
    private List<String> textoNomes;
    private List<String> textoEnderecos;
    private List<Boolean> booleanAtivos;

    public MyAdapter(Context context, List<String> textoNomes, List<String> textoEnderecos, List<Boolean> booleanAtivos) {
        this.textoEnderecos = textoEnderecos;
        this.textoNomes = textoNomes;
        this.booleanAtivos = booleanAtivos;
        mInflater = LayoutInflater.from(context);
    }

    public MyAdapter(Context context, List<String> textoNomes, List<String> textoEnderecos) {
        this.textoEnderecos = textoEnderecos;
        this.textoNomes = textoNomes;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return textoEnderecos.size();
    }

    @Override
    public Object getItem(int position) {
        return textoEnderecos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        view = mInflater.inflate(R.layout.itens, parent, false);

        TextView textoNome = (TextView) view.findViewById(R.id.textoNome);
        textoNome.setText(textoNomes.get(position));

        TextView textoEndereco = (TextView) view.findViewById(R.id.textoEndereco);
        textoEndereco.setText(textoEnderecos.get(position));

        return view;
    }

    private class ItemSuporte {

        TextView txt1Title;
        TextView txtTitle;
    }
}
