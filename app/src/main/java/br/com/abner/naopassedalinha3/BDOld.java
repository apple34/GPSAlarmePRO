package br.com.abner.naopassedalinha3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AbnerAdmin on 02/10/2015.
 */
public class BDOld {
    private SQLiteDatabase bd;

    public BDOld(Context context){
        BDOldCore auxBd = new BDOldCore(context);
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

        bd.insert("OldTable", null, valores);
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

        bd.update("OldTable", valores, "_id = ?", new String[]{"" + marcadores.getId()});
    }


    public void deletar(double lat/*, double lng*/){
        Log.i("DENTRO DELETE", "lat: " + lat/*+", lng: "+lng*/);
        bd.delete("OldTable", "latitude =" + lat/* + " and longitude = "+ lng*/, null);
    }

    public void deletarAllLines(){
        bd.delete("OldTable", null, null);
    }

    public List<Marcadores> buscar(){
        List<Marcadores> list = new ArrayList<>();
        String[] colunas = new String[]{"_id", "nome", "endereco", "latitude", "longitude", "ativo", "distancia"};

        Cursor cursor = bd.query("OldTable", colunas, null, null, null, null, null);

        if(cursor.getCount() > 0){
            cursor.moveToFirst();

            do{

                Marcadores marcadores = new Marcadores();
                marcadores.setId(cursor.getLong(0));
                marcadores.setNome(cursor.getString(1));
                marcadores.setEndereco(cursor.getString(2));
                marcadores.setLatitude(cursor.getDouble(3));
                marcadores.setLongitude(cursor.getDouble(4));
                marcadores.setAtivo(cursor.getLong(5));
                marcadores.setDistancia(cursor.getLong(6));
                list.add(marcadores);

            }while(cursor.moveToNext());
        }

        return(list);
    }

    public List<Marcadores> buscaUltimo(){
        List<Marcadores> list = new ArrayList<>();
        String[] colunas = new String[]{"_id", "endereco", "latitude", "longitude", "ativo", "distancia"};

        Cursor cursor = bd.query("OldTable", colunas, "_id = (SELECT MAX(_id) FROM OldTable)"
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
