package com.example.pen.android_study_01;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DetailMemoActivity extends AppCompatActivity {

    TextView tv1,tv2,tv3,tv4,tv5,tv6;
    Button bt1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_memo);

        tv1 = findViewById(R.id.textView1);
        tv2 = findViewById(R.id.textView2);
        tv3 = findViewById(R.id.textView3);
        tv4 = findViewById(R.id.textView4);
        tv5 = findViewById(R.id.textView5);
        tv6 = findViewById(R.id.textView6);

        Intent intent = getIntent();
        Memo memo = (Memo)intent.getSerializableExtra("memo");
        tv4.setText(memo.getTitle());
        tv5.setText(memo.getContent());
        tv6.setText(memo.getDetail());

        bt1 = findViewById(R.id.button1);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}

