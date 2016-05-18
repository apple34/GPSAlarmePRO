package br.com.abner.naopassedalinha3;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;

/**
 * Created by RavDellV2 on 12/05/2016.
 */
public class MyAdapter extends BaseAdapter{
    private LayoutInflater mInflater;
    private List<String> textoNomes;
    private List<String> textoEnderecos;

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
        view = mInflater.inflate(R.layout.itens, null);

        TextView textoNome = (TextView) view.findViewById(R.id.textoNome);
        textoNome.setText(textoNomes.get(position));
        //textoNome.setText(Html.fromHtml("<b>"+textoNomes.get(position)+"</b>")+" - "+Html.fromHtml("<font color=\"red\">"+textoEnderecos.get(position)+"</font>"));
        TextView textoEndereco = (TextView) view.findViewById(R.id.textoEndereco);
        textoEndereco.setText(textoEnderecos.get(position));

        return view;
    }

    private class ItemSuporte {

        TextView txt1Title;
        TextView txtTitle;
    }
}
