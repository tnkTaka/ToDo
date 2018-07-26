package local.hal.st21.android.todo60213;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;

import java.util.*;
import java.text.*;


public class TodoListActivity extends AppCompatActivity  {

    static final int MODE_INSERT = 1;
    static final int MODE_EDIT = 2;
    private ListView _lvToDoList;

    private int _division;
    private int _value;


    private String _state;

    private String _today;

    private static final int _name = 1;
    private static final int _deadline = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 現在日時の取得
        Date now = new Date(System.currentTimeMillis());
        // 日時のフォーマットオブジェクト作成
        DateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        // フォーマット
        _today = formatter.format(now);
        Log.e("初期動作　今日",""+_today+"");

        setContentView(R.layout.activity_to_do_list);

        SharedPreferences settings = getSharedPreferences("TaskDone",MODE_PRIVATE);
        _division = settings.getInt("division", _division);
        _value = settings.getInt("value", _value);
        _state = settings.getString("state",_state);

        _lvToDoList = findViewById(R.id.lvToDoList);
        _lvToDoList.setOnItemClickListener(new ListItemClickListener());
        registerForContextMenu(_lvToDoList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_list,menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu,view,menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu,menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        TextView tvTag = (TextView)findViewById(R.id.tvTag);
        tvTag.setText(_state);

        DatabaseHelper helper = new DatabaseHelper(TodoListActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = DatabaseAccess.findAll(db,_division,_value);
        String[] from = {"name", "deadline"};
        int[] to = {android.R.id.text1, android.R.id.text2};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(TodoListActivity.this, android.R.layout.simple_list_item_2, cursor, from, to, 0);
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                switch (columnIndex){
                    case _name:
                        if(cursor.getInt(cursor.getColumnIndex("done")) == 1){
                            ((TextView)view).setTextColor(Color.rgb(169,169,169));
                        }

                        return false;
                    case _deadline:
                        TextView deadline = (TextView)view;

                        //表示処理
                        if(cursor.getString(columnIndex).equals(_today)){
                            deadline.setText("期限： 今日");
                            ((TextView)view).setTextColor(Color.RED);
                        }else{
                            deadline.setText("期限：" + cursor.getString(columnIndex));
                        }

                        if(cursor.getInt(cursor.getColumnIndex("done")) == 1){
                            ((TextView)view).setTextColor(Color.rgb(169,169,169));
                        }
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });

        _lvToDoList.setAdapter(adapter);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        DatabaseHelper helper = new DatabaseHelper(TodoListActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();

        int listId = (int)info.id;
        int itemID = item.getItemId();
        switch (itemID){
            //完了
            case R.id.menuContextCompletion:
                try {
                    DatabaseAccess.contextCompletionUpdate(db,listId);
                } catch(Exception ex) {
                    Log.e("ERROR", ex.toString());
                } finally {
                    db.close();
                }
                onResume();
                Toast.makeText(TodoListActivity.this, R.string.menu_context_completion, Toast.LENGTH_SHORT).show();
                break;
            //未完
            case R.id.menuContextInCompletion:
                try {
                    DatabaseAccess.contextInCompletionUpdate(db,listId);
                } catch(Exception ex) {
                    Log.e("ERROR", ex.toString());
                } finally {
                    db.close();
                }
                onResume();
                Toast.makeText(TodoListActivity.this, R.string.menu_context_inCompletion, Toast.LENGTH_SHORT).show();
                break;
            //その他
            case R.id.menuContextOther:
                Intent intent = new Intent(TodoListActivity.this, TodoEditActivity.class);
                intent.putExtra("mode", MODE_EDIT);
                intent.putExtra("idNo", listId);
                startActivity(intent);
                onResume();
                Toast.makeText(TodoListActivity.this, R.string.menu_context_other, Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        SharedPreferences settings = getSharedPreferences("TaskDone",MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        int itemId = item.getItemId();
        switch (itemId){
            //優先度高い
            case R.id.menuHigh:
                _division = 1;
                _value = 1;
                _state = "優先度の高い順";
                editor.putInt("division", _division);
                editor.putInt("value", _value);
                editor.putString("state",_state);
                onResume();
                break;

            //優先度低い
            case R.id.menuLow:
                _division = 1;
                _value = 2;
                _state = "優先度の低い順";
                editor.putInt("division", _division);
                editor.putInt("value", _value);
                editor.putString("state",_state);
                onResume();
                break;

            //未完了の場合
            case R.id.menuIncomplete:
                _division = 2;
                _value = 1;
                _state = "未完了なタスク";
                editor.putInt("division", _division);
                editor.putInt("value", _value);
                editor.putString("state",_state);
                onResume();
                break;

            //完了の場合
            case R.id.menuComplete:
                _division = 2;
                _value = 2;
                _state = "完了したタスク";
                editor.putInt("division", _division);
                editor.putInt("value", _value);
                editor.putString("state",_state);
                onResume();
                break;

            //全ての場合
            case R.id.menuAll:
                _division = 2;
                _value = 3;
                _state = "全てのタスク";
                editor.putInt("division", _division);
                editor.putInt("value", _value);
                editor.putString("state",_state);
                onResume();
                break;

            //今日のタスク
            case R.id.menuToday:
                _division = 3;
                _value = 1;
                _state = "期限が今日のタスク";
                editor.putInt("division", _division);
                editor.putInt("value", _value);
                editor.putString("state",_state);
                onResume();
                break;

            //今週のタスク
            case R.id.menuWeek:
                _division = 3;
                _value = 2;
                _state = "期限が今週のタスク";
                editor.putInt("division", _division);
                editor.putInt("value", _value);
                editor.putString("state",_state);
                onResume();
                break;
        }

        TextView tvTag = (TextView)findViewById(R.id.tvTag);
        tvTag.setText(_state);
        editor.commit();
        return super.onOptionsItemSelected(item);
    }

    public void onNewButtonClick(View view) {
        Intent intent = new Intent(TodoListActivity.this, TodoEditActivity.class);
        intent.putExtra("mode", MODE_INSERT);
        startActivity(intent);
    }


    private class ListItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            Cursor item = (Cursor) parent.getItemAtPosition(position);
            int idxId = item.getColumnIndex("_id");
            int idNo =  item.getInt(idxId);

            Intent intent = new Intent(TodoListActivity.this, TodoEditActivity.class);
            intent.putExtra("mode", MODE_EDIT);
            intent.putExtra("idNo", idNo);
            startActivity(intent);
        }
    }
}