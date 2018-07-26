package local.hal.st21.android.todo60213;

import android.util.Log;

import java.sql.Timestamp;

public class Todo {

    private int _id;
    private int _idNo;
    private String _name;
    private String _deadline;
    private int _done;
    private int _priority;
    private String _note;
    private Timestamp _updateAt;

    //以下アクセサメソッド

    public int getId() {
        return _id;
    }
    public void setId(int id) {
        _id = id;
    }

    public String getName() {
        return _name;
    }
    public void setName(String name) {
        _name = name;
    }

    public String getDeadline() {
        return _deadline;
    }
    public void setDeadline(String deadline) {
        _deadline = deadline;
    }

    public int getPriority() {
        return _priority;
    }
    public void setPriority(int priority) {_priority = priority;}

    public int getDone() {
        return _done;
    }
    public void setDone(int done) {_done = done;}

    public String getNote() {
        return _note;
    }
    public void setNote(String note) {_note = note;}
}
