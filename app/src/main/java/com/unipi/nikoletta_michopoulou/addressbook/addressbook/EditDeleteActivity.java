package com.unipi.nikoletta_michopoulou.addressbook.addressbook;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

public class EditDeleteActivity extends AppCompatActivity {
    SQLiteDatabase db;
    EditText Text,Text2,Text3,Text4;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_delete);
        //title bar name
        getSupportActionBar().setTitle("Edit or Delete");
        //stop scrollview from autofocus
        ScrollView view = (ScrollView)findViewById(R.id.scrollView);
        view.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
    }

    @Override
    public void onResume(){
        super.onResume();
        //create or open database
        db= openOrCreateDatabase("Contacts", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS Informations(user_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name VARCHAR, lastname VARCHAR, tel VARCHAR, email VARCHAR);");
        //retrive id that AddContact has send
        String my_id=getIntent().getStringExtra("id");
        id = Integer.valueOf(my_id);
        //find common button-row id
        Cursor cursor =db.rawQuery("SELECT * FROM Informations WHERE user_id='"+id+"'",null);
        //four editText
        Text = (EditText)findViewById(R.id.Text);
        Text2 = (EditText)findViewById(R.id.Text2);
        Text3 = (EditText)findViewById(R.id.Text3);
        Text4 = (EditText)findViewById(R.id.Text4);
        //sets each editText value to each one of the columns of the specific database row
        if(cursor.getCount() >= 1){
            while(cursor.moveToNext()) {
                Text.setText(cursor.getString(1), TextView.BufferType.EDITABLE);
                Text2.setText(cursor.getString(2),TextView.BufferType.EDITABLE);
                Text3.setText(cursor.getString(3),TextView.BufferType.EDITABLE);
                Text4.setText(cursor.getString(4),TextView.BufferType.EDITABLE);
            }
        }
        else{
            //if something goes wrong
            showmessage("Sorry","No Results");
        }

    }
    //delete's button function delay 1.5 sec and returns to ContactsActivity
    public void delete_contact(View view){
        db.execSQL("DELETE FROM Informations WHERE user_id='"+id+"'");
        showmessage("Success","Your contact has been deleted");
        //some delay to show showmessage first and then return to ContactsActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(EditDeleteActivity.this, ContactsActivity.class);
        startActivity(intent);
            }
        }, 1500);
    }
    //edit's button function to update database with new values
    public void edit_contact(View view){
        String name=Text.getText().toString();
        String lastname=Text2.getText().toString();
        String tel=Text3.getText().toString();
        String email=Text4.getText().toString();
        if(name.matches("")){
            showmessage("Error","Please fill the name!");
        }
        else{
        db.execSQL("UPDATE Informations SET name = '"+name+"', lastname='"+lastname+"', tel='"+tel+"', email='"+email+"' WHERE user_id = '"+id+"'");
        showmessage("Success","Your contact has been updated");
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(EditDeleteActivity.this, ContactsActivity.class);
                startActivity(intent);
            }
        }, 1500);
    }
    //popup information window
    public void showmessage(String title,String text ){
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setCancelable(true);
        builder.setMessage(text);
        builder.show();

    }
    //refresh Contacts when change/delete contacts Back button
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            finish();
            Intent intent = new Intent(EditDeleteActivity.this,
                    ContactsActivity.class);
            startActivity(intent);
            finishAfterTransition();
        }
        return super.onKeyDown(keyCode, event);
    }
}
