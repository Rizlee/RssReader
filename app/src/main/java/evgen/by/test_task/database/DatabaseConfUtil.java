package evgen.by.test_task.database;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;
import java.sql.SQLException;

import evgen.by.test_task.entity.RssItem;

/**
 * Created by evgen on 04.05.2016.
 */
public class DatabaseConfUtil extends OrmLiteConfigUtil {
    private static final Class<?>[] classes = new Class[]{RssItem.class};
    public static void main(String[] args) throws IOException, SQLException {
        writeConfigFile("ormlite_config.txt",classes);

    }
}
