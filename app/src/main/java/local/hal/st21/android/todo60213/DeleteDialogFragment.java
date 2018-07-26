package local.hal.st21.android.todo60213;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

public class DeleteDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("削除");
        builder.setMessage("よろしいですか？");
        builder.setPositiveButton("OK", new DialogButtonClickListener());
        builder.setNegativeButton("NO", new DialogButtonClickListener());
        AlertDialog dialog = builder.create();
        return dialog;
    }

    private class DialogButtonClickListener implements DialogInterface.OnClickListener{

        @Override
        public void onClick(DialogInterface dialog, int which){
            Bundle extras = getArguments();
            int idNo = 0;
            if(extras != null){
                idNo = extras.getInt("idNo");
            }
            Activity parent = getActivity();
            String msg = "";
            switch(which){
                case DialogInterface.BUTTON_POSITIVE:
                    msg = "削除しました。";
                    DatabaseHelper helper = new DatabaseHelper(parent);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    Toast.makeText(parent, msg, Toast.LENGTH_SHORT).show();
                    try{
                        DatabaseAccess.delete(db, idNo);
                    }
                    catch(Exception ex) {
                        Log.e("ERROR",ex.toString());
                    }
                    finally {
                        db.close();
                    }
                    parent.finish();
                    break;
                    case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    }
}