package com.example.hjg.note;

import android.app.Activity;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends ListActivity {
    private SimpleCursorAdapter adapter;
    private NotesDB db;
    private SQLiteDatabase dbRead;

    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_EDIT_NOTE = 2;
    /*实现OnClickListner接口，添加日志按钮的监听*/
    private View.OnClickListener btnAddNote_clickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivityForResult(new Intent(MainActivity.this,AtyEditNote.class),REQUEST_CODE_ADD_NOTE);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*操作数据库*/
        db = new NotesDB(this);
        dbRead = db.getReadableDatabase();

        adapter = new SimpleCursorAdapter(this,R.layout.notes_list_cell,null,
                new String[] {
                        NotesDB.COLUMN_NAME_NOTE_NAME,NotesDB.COLUMN_NAME_NOTE_DATE},
                new int[]{
                        R.id.tvDate});
        setListAdapter(adapter);
        refreshNotesListView();
        findViewById(R.id.btnAddNote).setOnClickListener(
                btnAddNote_clickHandler);
    }

    /*复写方法，笔记列表中的笔记条目被点击时被调用，打开编辑笔记页面，同时传入当前笔记的信息*/
    protected void onListItemClick(ListView l, View v, int position,long id){
        //获取当前笔记的Cursor对象
        Cursor c = adapter.getCursor();
        c.moveToPosition(position);

        //显示Intent开启编辑笔记页面
        Intent i = new Intent(MainActivity.this,AtyEditNote.class);

        //传入笔记id,  name,  content
        i.putExtra(AtyEditNote.EXTRA_NOTE_ID,
                c.getInt(c.getColumnIndex(NotesDB.COLUMN_NAME_ID)));
        i.putExtra(AtyEditNote.EXTRA_NOTE_NAME,
                c.getString(c.getColumnIndex(NotesDB.COLUMN_NAME_NOTE_NAME)));
        i.putExtra(AtyEditNote.EXTRA_NOTE_CONTENT,
                c.getString(c.getColumnIndex(NotesDB.COLUMN_NAME_NOTE_CONTENT)));

        // 有返回的开启Activity
        startActivityForResult(i, REQUEST_CODE_EDIT_NOTE);

        super.onListItemClick(l, v, position, id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        switch (requestCode){
            case REQUEST_CODE_ADD_NOTE:
            case REQUEST_CODE_EDIT_NOTE:
                if (resultCode == Activity.RESULT_OK){
                    refreshNotesListView();
                }
                break;
            default:
                break;
        }

        super.onActivityResult(requestCode,resultCode,data);
    }

    /*刷新笔记列表，内容从数据库中查询*/
    private void refreshNotesListView() {
        adapter.changeCursor(dbRead.query(NotesDB.TABLE_NAME_NOTES,null,null,null,null,null,null));

    }
}
