package local.hal.st21.android.todo60213;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DatabaseAccess {

    public static Cursor findAll(SQLiteDatabase db, int division, int value) {
        //全ての場合
        String sql = "SELECT _id, name, deadline, done, note FROM tasks ORDER BY deadline DESC";

        switch (division){
            case 1:
                switch (value){
                    //優先度高い
                    case 1:
                        sql = "SELECT _id, name, deadline, done, note FROM tasks WHERE done = 0 ORDER BY priority DESC";
                        break;
                    //優先度低い
                    case 2:
                        sql = "SELECT _id, name, deadline, done, note FROM tasks WHERE done = 0 ORDER BY priority ASC";
                        break;
                }
                break;
            case 2:
                switch (value){
                    //未完了の場合
                    case 1:
                        sql = "SELECT _id, name, deadline, done, note FROM tasks WHERE done = 0 ORDER BY deadline ASC";
                        break;
                    //完了の場合
                    case 2:
                        sql = "SELECT _id, name, deadline, done, note FROM tasks WHERE done = 1 ORDER BY deadline DESC";
                        break;
                    //全ての場合
                    case 3:
                        sql = "SELECT _id, name, deadline, done, note FROM tasks ORDER BY deadline DESC";
                        break;
                }
                break;
            case 3:
                switch (value){
                    //今日のタスク
                    case 1:
                        sql = "SELECT _id, name, deadline, done, note FROM tasks WHERE deadline = strftime('%Y年%m月%d日', CURRENT_TIMESTAMP) ORDER BY priority DESC";
                        break;
                    //今週のタスク
                    case 2:
                        sql = "SELECT _id, name, deadline, done, note FROM tasks WHERE deadline BETWEEN strftime('%Y年%m月%d日',CURRENT_TIMESTAMP) AND strftime('%Y年%m月%d日',CURRENT_TIMESTAMP, '7 days') ORDER BY priority DESC";
                        break;
                }
                break;
        }

        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    public static Todo findByPK(SQLiteDatabase db, int id) {
        String sql = "SELECT _id, name, deadline, priority, done, note FROM tasks WHERE _id = " + id ;
        Cursor cursor = db.rawQuery(sql, null);
        Todo result = null;
        if(cursor.moveToFirst()) {

            int idxName = cursor.getColumnIndex("name");
            int idxDeadline = cursor.getColumnIndex("deadline");
            int idxPriority = cursor.getColumnIndex("priority");
            int idxDone = cursor.getColumnIndex("done");
            int idxNote = cursor.getColumnIndex("note");

            String name = cursor.getString(idxName);
            String deadline = cursor.getString(idxDeadline);
            int priority = cursor.getInt(idxPriority);
            int done = cursor.getInt(idxDone);
            String note = cursor.getString(idxNote);

            result = new Todo();
            result.setId(id);
            result.setName(name);
            result.setDeadline(deadline);
            result.setPriority(priority);
            result.setDone(done);
            result.setNote(note);
        }
        return result;
    }

    public static int update(SQLiteDatabase db, int id, String name, String deadline, int priority, int done, String note) {
        String sql = "UPDATE tasks SET name = ?, deadline = ?, priority = ?,done = ?, note = ? WHERE _id = ?";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindString(1, name);
        stmt.bindString(2, deadline);
        stmt.bindLong(3, priority);
        stmt.bindLong(4, done);
        stmt.bindString(5, note);
        stmt.bindLong(6, id);
        int result = stmt.executeUpdateDelete();
        return result;
    }

    public static long insert(SQLiteDatabase db, String name, String deadline, int priority, int done, String note) {
        String sql = "INSERT INTO tasks (name, deadline, priority, done, note) VALUES (?, ?, ?, ?, ?)";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindString(1, name);
        stmt.bindString(2, deadline);
        stmt.bindLong(3, priority);
        stmt.bindLong(4, done);
        stmt.bindString(5, note);
        long id = stmt.executeInsert();
        return id;
    }

    public static int delete(SQLiteDatabase db, int id) {
        String sql = "DELETE FROM tasks WHERE _id = ?";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindLong(1, id);
        int result = stmt.executeUpdateDelete();
        return result;
    }

    public static int contextCompletionUpdate(SQLiteDatabase db, int id) {
        String sql = "UPDATE tasks SET done = 1 WHERE _id = ?";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindLong(1, id);
        int result = stmt.executeUpdateDelete();
        return result;
    }

    public static int contextInCompletionUpdate(SQLiteDatabase db, int id) {
        String sql = "UPDATE tasks SET done = 0 WHERE _id = ?";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindLong(1, id);
        int result = stmt.executeUpdateDelete();
        return result;
    }
}
