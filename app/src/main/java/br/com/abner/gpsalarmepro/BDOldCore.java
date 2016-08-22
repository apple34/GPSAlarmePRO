package br.com.abner.gpsalarmepro;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by AbnerAdmin on 02/10/2015.
 */
public class BDOldCore extends SQLiteOpenHelper{
    private static final String NOME_BDOld = "OldTable";
    private static final int VERSAO_BDOld = 4;

    public BDOldCore(Context context) {
        super(context, NOME_BDOld, null, VERSAO_BDOld);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE OLdTable(" +
                "_id integer primary key autoincrement," +
                "nome,"+
                "endereco text,"+
                "latitude real," +
                "longitude real," +
                "ativo long," +
                "distancia long," +
                "dias_da_semana text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase bd, int oldVersion, int newVersion) {
        bd.execSQL("DROP TABLE OldTable");
        onCreate(bd);
    }
}
