package dla.cn.file_manager.sqlite;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class FileTaskBean {
    // 表名
    public final static String TABLE_NAME = "task_info";

    // 文件任务表字段
    public final static String _ID = "_id";
    public final static String FILENAME = "filename";
    public final static String NETWORKPATH = "networkpath";
    public final static String FILESIZE = "filesize";
    public final static String DOWNLOADSIZE = "downloadsize";
    public final static String FILEMD5 = "filemd5";

    // 自动增加ID
    private int _id;
    // 文件名称
    private String filename;
    // 文件在互联网上的路径
    private String networkpath;
    // 文件的大小
    private long filesize;
    // 文件已经下载的大小
    private long downloadsize;
    // 文件的md5值
    private String filemd5;

    public static void createTable(SQLiteDatabase db) {
        String createTableSql = "CREATE TABLE IF NOT EXISTS task_info(\n" +
                "\t" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\t" + FILENAME + " VARCHAR(128) ,\n" +
                "\t" + NETWORKPATH + " VARCHAR(1024) ,\n" +
                "\t" + FILESIZE + " INTEGER,\n" +
                "\t" + DOWNLOADSIZE + " INTEGER,\n" +
                "\t" + FILEMD5 + " VARCHAR(512) \n" +
                ")";
        Log.d("FileTask:Sql", createTableSql);

        try{
            db.execSQL(createTableSql);
        }catch (Exception e){
            Log.d("FileTask:Sql", e.getMessage());
        }
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
//        contentValues.put(_ID, this.get_id());
        contentValues.put(FILENAME, this.getFilename());
        contentValues.put(NETWORKPATH, this.getNetworkpath());
        contentValues.put(FILESIZE, this.getFilesize());
        contentValues.put(DOWNLOADSIZE, this.getDownloadsize());
        contentValues.put(FILEMD5, this.getFilemd5());

        return contentValues;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getNetworkpath() {
        return networkpath;
    }

    public void setNetworkpath(String networkpath) {
        this.networkpath = networkpath;
    }

    public long getFilesize() {
        return filesize;
    }

    public void setFilesize(long filesize) {
        this.filesize = filesize;
    }

    public long getDownloadsize() {
        return downloadsize;
    }

    public void setDownloadsize(long downloadsize) {
        this.downloadsize = downloadsize;
    }

    public String getFilemd5() {
        return filemd5;
    }

    public void setFilemd5(String filemd5) {
        this.filemd5 = filemd5;
    }
}
