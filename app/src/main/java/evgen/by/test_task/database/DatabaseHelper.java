package evgen.by.test_task.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import evgen.by.test_task.R;
import evgen.by.test_task.entity.RssItem;

/**
 * Created by evgen on 04.05.2016.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "rss.db";
    private static final int DATABASE_VESION = 1;

    private Dao<RssItem, Integer> itemDao = null;
    private RuntimeExceptionDao<RssItem, Integer> itemRuntimeDao = null;

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VESION, R.raw.ormlite_config);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            Log.d("helpme","yes");
            TableUtils.createTable(connectionSource, RssItem.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try {
            Log.d("helpme","yes");
            TableUtils.dropTable(connectionSource,RssItem.class,true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public Dao<RssItem,Integer> getDao() throws SQLException {
        if (itemDao == null){
            itemDao = getDao(RssItem.class);
        }
        return  itemDao;
    }

    public RuntimeExceptionDao<RssItem, Integer> getItemRuntimeExceptionDao(){
        if (itemRuntimeDao == null){
            itemRuntimeDao = getRuntimeExceptionDao(RssItem.class);
        }
        return  itemRuntimeDao;
    }

}
