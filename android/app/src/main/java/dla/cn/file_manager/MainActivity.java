package dla.cn.file_manager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import dla.cn.file_manager.sqlite.FileTaskBean;
import dla.cn.file_manager.sqlite.FileTaskDao;

public class MainActivity extends AppCompatActivity {
    FileTaskDao fileTaskDao = new FileTaskDao(this);
    String path = "http://192.168.1.8:8080/test.zip";
    ArrayList<String> datas ;
    MyAdapter myAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        datas = new ArrayList<>();
        datas.add("你好");
        myAdapter = new MyAdapter(datas,this);

        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(myAdapter);

        this.setDBDatas();
    }

    public void setDatas(ArrayList<String> arrayList) {
        this.datas.clear();
        for(int i =0; i < arrayList.size(); i++){
            this.datas.add(arrayList.get(i));
        }

        this.myAdapter.notifyDataSetChanged();
    }

    public void setDBDatas() {
        ArrayList<FileTaskBean> fileTaskBeans = fileTaskDao.selectAll();
        ArrayList<String> nDatas = new ArrayList<>();
        if(null == fileTaskBeans){
            return;
        }

        for (int i = 0; i < fileTaskBeans.size(); i++) {
            nDatas.add(fileTaskBeans.get(i).getFilename());
        }

        this.setDatas(nDatas);
    }

    public void add(View v) {
        FileTaskBean fileTaskBean = new FileTaskBean();
        fileTaskBean.setFilemd5("md5");
        fileTaskBean.setFilesize(123);
        fileTaskBean.setNetworkpath(path);
        fileTaskBean.setFilename("test.zip");
        boolean b = fileTaskDao.insertOne(fileTaskBean);

        if (b) {
            this.setDBDatas();
        }

        Toast.makeText(this, b ? "添加成功" : "添加失败", Toast.LENGTH_SHORT).show();
    }

    public void update(View v) {
        FileTaskBean fileTaskBean = fileTaskDao.selectOne(path);
        fileTaskBean.setFilename("dla.zip");
        boolean b = fileTaskDao.updateOne(fileTaskBean);

        if (b) {
            this.setDBDatas();
        }

        Toast.makeText(this, b ? "修改成功" : "修改失败", Toast.LENGTH_SHORT).show();
    }

    public void query(View v) {
        FileTaskBean fileTaskBean = fileTaskDao.selectOne(path);
        Toast.makeText(this, fileTaskBean != null ? fileTaskBean.getFilename() : null, Toast.LENGTH_SHORT).show();
    }

    public void delete(View v) {
        FileTaskBean fileTaskBean = fileTaskDao.selectOne(path);
        boolean b = fileTaskDao.deleteOne(fileTaskBean);

        if (b) {
            this.setDBDatas();
        }

        Toast.makeText(this, b ? "删除成功" : "删除失败", Toast.LENGTH_SHORT).show();
    }
}
