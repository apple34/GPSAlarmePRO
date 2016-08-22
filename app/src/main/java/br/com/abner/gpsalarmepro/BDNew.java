package br.com.abner.gpsalarmepro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AbnerAdmin on 10/09/2015.
 */
public class BDNew {

    private SQLiteDatabase bd;

    public BDNew(Context context){
        BDNewCore auxBd = new BDNewCore(context);
        bd = auxBd.getWritableDatabase();
    }


    public void inserir(Marcadores marcadores){
        ContentValues valores = new ContentValues();
        //valores.put("_id", marcadores.getId());
        valores.put("nome", marcadores.getNome());
        valores.put("endereco", marcadores.getEndereco());
        valores.put("latitude", marcadores.getLatitude());
        valores.put("longitude", marcadores.getLongitude());
        valores.put("ativo", marcadores.getAtivo());
        valores.put("distancia", marcadores.getDistancia());
        valores.put("dias_da_semana", marcadores.getDiasDaSemana());

        bd.insert("NewTable", null, valores);
    }


    public void atualizar(Marcadores marcadores){
        ContentValues valores = new ContentValues();
        valores.put("_id", marcadores.getId());
        valores.put("nome", marcadores.getNome());
        valores.put("endereco", marcadores.getEndereco());
        valores.put("latitude", marcadores.getLatitude());
        valores.put("longitude", marcadores.getLongitude());
        valores.put("ativo", marcadores.getAtivo());
        valores.put("distancia", marcadores.getDistancia());
        valores.put("dias_da_semana", marcadores.getDiasDaSemana());

        bd.update("NewTable", valores, "_id = ?", new String[]{"" + marcadores.getId()});
    }


    public void deletar(double lat/*, double lng*/){
        Log.i("DENTRO DELETE", "lat: " + lat/*+", lng: "+lng*/);
        bd.delete("NewTable", "latitude =" + lat/* + " and longitude = "+ lng*/, null);
    }

    public void deletarAllLines(){
        bd.delete("NewTable", null, null);
    }


    public List<Marcadores> buscar(){
        List<Marcadores> list = new ArrayList<>();
        String[] colunas = new String[]{"_id", "nome", "endereco", "latitude", "longitude", "ativo",
                "distancia", "dias_da_semana"};

        Cursor cursor = bd.query("NewTable", colunas, null, null, null, null, null);
                if(cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    do {

                        Marcadores marcadores = new Marcadores();
                        marcadores.setId(cursor.getLong(0));
                        marcadores.setNome(cursor.getString(1));
                        marcadores.setEndereco(cursor.getString(2));
                        marcadores.setLatitude(cursor.getDouble(3));
                        marcadores.setLongitude(cursor.getDouble(4));
                        marcadores.setAtivo(cursor.getLong(5));
                        marcadores.setDistancia(cursor.getLong(6));
                        marcadores.setDiasDaSemana(cursor.getString(7));
                        list.add(marcadores);

                    } while (cursor.moveToNext());
                }
        return(list);
    }

    public List<Marcadores> buscaUltimo(){
        List<Marcadores> list = new ArrayList<>();
        String[] colunas = new String[]{"_id", "endereco", "latitude", "longitude", "ativo", "distancia"};

        Cursor cursor = bd.query("NewTable", colunas, "_id = (SELECT MAX(_id) FROM NewTable)"
                , null, null, null, null);

        if(cursor.getCount() > 0){
            cursor.moveToFirst();

            do{

                Marcadores marcadores = new Marcadores();
                marcadores.setId(cursor.getLong(0));
                marcadores.setEndereco(cursor.getString(1));
                marcadores.setLatitude(cursor.getDouble(2));
                marcadores.setLongitude(cursor.getDouble(3));
                marcadores.setAtivo(cursor.getLong(4));
                marcadores.setDistancia(cursor.getLong(5));
                list.add(marcadores);

            }while(cursor.moveToNext());
        }

        return(list);
    }
}
