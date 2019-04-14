package dla.cn.file_manager.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class FileOpenHelper extends SQLiteOpenHelper {

    public FileOpenHelper(@Nullable Context context) {
        super(context, SqliteConfig.DB_NAME, null, SqliteConfig.DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        FileTaskBean.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
