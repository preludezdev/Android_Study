package com.example.pen.android_study_01;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddMemoActivity extends AppCompatActivity {

    Button bt1, bt2;
    EditText et1,et2,et3;

    MainActivity aActivity = (MainActivity)MainActivity.aActivity;

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
                String title = "'"+et1.getText().toString()+"'";
                String content = "'"+et2.getText().toString()+"'";
                String detail = "'"+et3.getText().toString()+"'";

                C.db.execSQL("INSERT INTO myMemoTable (title,content,detail) VALUES(" + title + "," + content + "," + detail + ");");

                Log.d(C.tag, "insert 됐음.");

                //기존 메인엑티비티 날리고 새롭게 메인엑티비티 생성
                aActivity.finish();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish(); //현재 액티비티(AddMemoActivity) 종료
            }
        });
    }
}
