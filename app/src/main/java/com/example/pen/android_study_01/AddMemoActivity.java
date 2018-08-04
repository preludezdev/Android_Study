package com.example.pen.android_study_01;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddMemoActivity extends AppCompatActivity {

    Button bt1, bt2;
    EditText et1,et2,et3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memo);

        et1 = findViewById(R.id.editText1);
        et2 = findViewById(R.id.editText2);
        et3 = findViewById(R.id.editText3);

        bt1 = findViewById(R.id.button1);
        bt2 = findViewById(R.id.button2);

        //취소
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //확인
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = "'"+et1.getText().toString()+"'";
                String content = "'"+et2.getText().toString()+"'";
                String detailContent = "'"+et3.getText().toString()+"'";
                DBHelper.getInstance(AddMemoActivity.this).insertMemo(name, content, detailContent);

                Intent addIntent = new Intent();
                setResult(3000,addIntent);
                finish(); //현재 액티비티(AddMemoActivity) 종료
            }
        });
    }
}
