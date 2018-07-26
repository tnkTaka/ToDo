package local.hal.st21.android.todo60213;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.support.v4.app.ShareCompat;


import java.util.*;
import java.text.*;

public class TodoEditActivity extends AppCompatActivity {

    private int _mode = TodoListActivity.MODE_INSERT;

    private int _done = 0;

    private int _idNo = 0;

    private String _nowTime = "";

    private int _actionBarState = 0;

    private int _seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_edit);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(
            new SeekBar.OnSeekBarChangeListener() {
                //ツマミがドラッグされると呼ばれる
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    _seekBar = seekBar.getProgress();
                }

                //ツマミがタッチされた時に呼ばれる
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                //ツマミがリリースされた時に呼ばれる
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    _seekBar = seekBar.getProgress();
                }
        });

        // 現在日時の取得
        Date now = new Date(System.currentTimeMillis());
        // 日時のフォーマットオブジェクト作成
        DateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        // フォーマット
        _nowTime = formatter.format(now);

        Intent intent = getIntent();
        _mode = intent.getIntExtra("mode", TodoListActivity.MODE_INSERT);

        if(_mode == TodoListActivity.MODE_INSERT) {

            _actionBarState = 1;

            TextView tvTitleEdit = findViewById(R.id.tvTitleEdit);
            tvTitleEdit.setText(R.string.tv_title_insert);

            TextView tvDate = (TextView)findViewById(R.id.tvDate);
            tvDate.setText(_nowTime);

        } else {
            _idNo = intent.getIntExtra("idNo", 0);

            _actionBarState = 2;

            DatabaseHelper helper = new DatabaseHelper(TodoEditActivity.this);
            SQLiteDatabase db = helper.getWritableDatabase();
            try {
                Todo todoData = DatabaseAccess.findByPK(db, _idNo);

                EditText etInputTitle = findViewById(R.id.etInputTitle);
                etInputTitle.setText(todoData.getName());

                TextView tvDate = (TextView)findViewById(R.id.tvDate);
                tvDate.setText(todoData.getDeadline());

                seekBar.setProgress(todoData.getPriority());

                ToggleButton toggle = (ToggleButton) findViewById(R.id.tbDone);
                if(todoData.getDone() == 0){
                    toggle.setChecked(true);
                }else if(todoData.getDone() == 1){
                    toggle.setChecked(false);
                }

                _done = todoData.getDone();

                EditText etInputContent = findViewById(R.id.etInputContent);
                etInputContent.setText(todoData.getNote());
            }
            catch(Exception ex) {
                Log.e("ERROR", ex.toString());
            }
            finally {
                db.close();
            }
        }

        // idがtoggleのトグルボタンを取得
        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.tbDone);
        // toggleButtonのオンオフが切り替わった時の処理を設定
        toggleButton.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener(){
                    public void onCheckedChanged(CompoundButton comButton, boolean isChecked){
                        // Toast表示する文字列をトグルボタンのオンオフで変える
                        String state = "";
                        if (isChecked) {
                            state = "未完了に変更しました";
                            _done = 0;
                        } else {
                            state = "完了に変更しました";
                            _done = 1;
                        }
                        Toast.makeText(TodoEditActivity.this, state, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        if (_actionBarState == 1){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.insert_actionbar_menu,menu);
            return true;
        }else{
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.update_actionbar_menu,menu);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        EditText etInputTitle = findViewById(R.id.etInputTitle);
        String inputTitle = etInputTitle.getText().toString();
        TextView tvDate = (TextView)findViewById(R.id.tvDate);
        String inputDate = tvDate.getText().toString();
        EditText etInputContent = findViewById(R.id.etInputContent);
        String inputContent = etInputContent.getText().toString();

        SharedPreferences settings = getSharedPreferences("TaskDone",MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        int itemId = item.getItemId();
        switch (itemId){
            case R.id.action_bar_Insert:
                if(inputTitle.equals("")) {
                    Toast.makeText(TodoEditActivity.this, R.string.msg_input_title, Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseHelper helper = new DatabaseHelper(TodoEditActivity.this);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    try {
                        if(_mode == TodoListActivity.MODE_INSERT ) {
                            DatabaseAccess.insert(db, inputTitle, inputDate, _seekBar, _done, inputContent);
                        } else {
                            DatabaseAccess.update(db, _idNo, inputTitle, inputDate, _seekBar, _done, inputContent);
                        }
                    } catch(Exception ex) {
                        Log.e("ERROR", ex.toString());
                    } finally {
                        db.close();
                    }
                    finish();
                }
                break;
            case  R.id.action_bar_Delete:
                int idNo = _idNo;
                Bundle extras = new Bundle();
                extras.putInt("idNo",idNo);
                DeleteDialogFragment dialog = new DeleteDialogFragment();
                dialog.setArguments(extras);
                FragmentManager manager = getSupportFragmentManager();
                dialog.show(manager,"DeleteDialogFragment");
                break;
            case  android.R.id.home:
                finish();
                break;
            case R.id.menuShare:
                ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(TodoEditActivity.this);
                builder.setSubject(inputTitle);
                builder.setText("期限："+inputDate+"\n詳細："+inputContent+"");
                builder.setType("text/plain");
                builder.startChooser();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DatePickerDialogDateSetListener implements DatePickerDialog.OnDateSetListener{
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
            String msg = + year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日に変更しました";
            Toast.makeText(TodoEditActivity.this, msg, Toast.LENGTH_SHORT).show();

            TextView tvDate = (TextView)findViewById(R.id.tvDate);
            tvDate.setText(String.format("%d年%02d月%02d日",year,monthOfYear + 1,dayOfMonth));
        }
    }

    public void onDateButtonClick(View view){
        TextView tvDate = (TextView)findViewById(R.id.tvDate);
        String date = tvDate.getText().toString();

        if(date.equals("")){
            Calendar cal = Calendar.getInstance();
            int nowYear = cal.get(Calendar.YEAR);
            int nowMonth = cal.get(Calendar.MONTH);
            int nowDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(TodoEditActivity.this, new DatePickerDialogDateSetListener(), nowYear,nowMonth,nowDayOfMonth);
            dialog.show();
        }else{
            int dy = Integer.valueOf(date.substring(0,4));
            int dm = Integer.valueOf(date.substring(5,7));
            int dd = Integer.valueOf(date.substring(8,10));
            DatePickerDialog dialog = new DatePickerDialog(TodoEditActivity.this, new DatePickerDialogDateSetListener(),dy,dm -1 ,dd);
            dialog.show();
        }

    }

}