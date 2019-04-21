package dla.cn.file_manager.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class FileTaskDao {
    SQLiteOpenHelper sqLiteOpenHelper;

    public FileTaskDao(Context context) {
        sqLiteOpenHelper = new FileOpenHelper(context);
    }

    // 插入一个task
    public boolean insertOne(FileTaskBean fileTaskBean) {
        if(fileTaskBean == null){
            return  false;
        }

        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
        long result = db.insert(FileTaskBean.TABLE_NAME, null, fileTaskBean.toContentValues());
        return result != -1;
    }

    // 更新一个task
    public boolean updateOne(FileTaskBean fileTaskBean) {
        if(fileTaskBean == null){
            return  false;
        }

        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
        int i = db.update(FileTaskBean.TABLE_NAME, fileTaskBean.toContentValues(), "_id=?",  new String[]{String.valueOf(fileTaskBean.get_id())});
        return i == 1;
    }

    // 删除一个task
    public boolean deleteOne(FileTaskBean fileTaskBean) {
        if(fileTaskBean == null){
            return  false;
        }

        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
        int i = db.delete(FileTaskBean.TABLE_NAME, "_id=?", new String[]{String.valueOf(fileTaskBean.get_id())});
        return  i == 1;
    }

    // 查询一个task
    public FileTaskBean selectOne(String networkpath) {
        FileTaskBean fileTaskBean = new FileTaskBean();
        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();

        Cursor cursor = db.query(
                FileTaskBean.TABLE_NAME,
                new String[]{FileTaskBean._ID, FileTaskBean.FILENAME, FileTaskBean.NETWORKPATH, FileTaskBean.FILESIZE, FileTaskBean.DOWNLOADSIZE, FileTaskBean.FILEMD5},
                 FileTaskBean.NETWORKPATH+ "=?",
                new String[]{networkpath},
                null,
                null,
                null
        );

        if (cursor.moveToNext()){
            int _idIndex = cursor.getColumnIndex(FileTaskBean._ID);
            fileTaskBean.set_id(Integer.parseInt(cursor.getString(_idIndex)));

            int fileNameIndex = cursor.getColumnIndex(FileTaskBean.FILENAME);
            fileTaskBean.setFilename(cursor.getString(fileNameIndex));

            int networkpathIndex = cursor.getColumnIndex(FileTaskBean.NETWORKPATH);
            fileTaskBean.setNetworkpath(cursor.getString(networkpathIndex));

            int filesizeIndex = cursor.getColumnIndex(FileTaskBean.FILESIZE);
            fileTaskBean.setFilesize(Long.parseLong(cursor.getString(filesizeIndex)));

            int downloadsizeIndex = cursor.getColumnIndex(FileTaskBean.DOWNLOADSIZE);
            fileTaskBean.setFilesize(Long.parseLong(cursor.getString(downloadsizeIndex)));

            int filemd5Index = cursor.getColumnIndex(FileTaskBean.FILEMD5);
            fileTaskBean.setFilemd5(cursor.getString(filemd5Index));

            return fileTaskBean;
        }

        return null;
    }

    // 查询所有的task
    public ArrayList<FileTaskBean> selectAll() {
        ArrayList<FileTaskBean> fileTaskBeans = new ArrayList<>();

        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(
                FileTaskBean.TABLE_NAME,
                new String[]{FileTaskBean._ID, FileTaskBean.FILENAME, FileTaskBean.NETWORKPATH, FileTaskBean.FILESIZE, FileTaskBean.DOWNLOADSIZE, FileTaskBean.FILEMD5},
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()){
            FileTaskBean fileTaskBean = new FileTaskBean();
            int _idIndex = cursor.getColumnIndex(FileTaskBean._ID);
            fileTaskBean.set_id(Integer.parseInt(cursor.getString(_idIndex)));

            int fileNameIndex = cursor.getColumnIndex(FileTaskBean.FILENAME);
            fileTaskBean.setFilename(cursor.getString(fileNameIndex));

            int networkpathIndex = cursor.getColumnIndex(FileTaskBean.NETWORKPATH);
            fileTaskBean.setNetworkpath(cursor.getString(networkpathIndex));

            int filesizeIndex = cursor.getColumnIndex(FileTaskBean.FILESIZE);
            fileTaskBean.setFilesize(Long.parseLong(cursor.getString(filesizeIndex)));

            int downloadsizeIndex = cursor.getColumnIndex(FileTaskBean.DOWNLOADSIZE);
            fileTaskBean.setFilesize(Long.parseLong(cursor.getString(downloadsizeIndex)));

            int filemd5Index = cursor.getColumnIndex(FileTaskBean.FILEMD5);
            fileTaskBean.setFilemd5(cursor.getString(filemd5Index));

            fileTaskBeans.add(fileTaskBean);
        }

        return fileTaskBeans;
    }
}
