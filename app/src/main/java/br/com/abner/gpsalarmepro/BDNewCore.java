package br.com.abner.gpsalarmepro;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by AbnerAdmin on 10/09/2015.
 */
public class BDNewCore extends SQLiteOpenHelper {
    private static final String NOME_BD = "NewTable";
    private static final int VERSAO_BD = 2;

    public BDNewCore(Context context){
        super(context,NOME_BD,null,VERSAO_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase bd) {
        bd.execSQL("CREATE TABLE NewTable(" +
                "_id integer primary key autoincrement," +
                "nome text,"+
                "endereco text,"+
                "latitude real," +
                "longitude real," +
                "ativo long," +
                "distancia long);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase bd, int oldVersion, int newVersion) {
        bd.execSQL("DROP TABLE NewTable");
        onCreate(bd);
    }
}
