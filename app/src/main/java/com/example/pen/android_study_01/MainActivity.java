package com.example.pen.android_study_01;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static Activity aActivity;

    RecyclerView recyclerView;
    Adapter adapter;
    ArrayList<Memo> memos = new ArrayList<>();

    FloatingActionButton fab;
    boolean isLongClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 데이터 수정되고 돌아올 때 기존 메인엑티비티 죽이기 위한 장치
        aActivity = MainActivity.this;

        //MySQLite 세팅
        setDatabase();

        //리사이클러뷰 세팅
        recyclerView = findViewById(R.id.recycler);
        // DB에 있는 데이터를 memos Arraylist에 넣는다. (리사이클러뷰 사용하기 위해)
        setData();
        setRecyclerView();

        // 플로팅 액션 버튼 클릭시 AddMemoActivity로 이동
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AddMemoActivity.class);
                startActivity(intent);
            }
        });

        /*
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isLongClicked = true;
                return false;
            }
        });


        fab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN :
                    case MotionEvent.ACTION_MOVE :
                    case MotionEvent.ACTION_UP :
                        if(isLongClicked) {
                            Intent intent = new Intent(getApplicationContext(), AddMemoActivity.class);
                            startActivity(intent);
                            isLongClicked = false;
                            return true;
                        }
                        else {
                            //플로팅 아이콘의 위치를 옮기기
                            fab.setX(event.getX());
                            fab.setY(event.getY());
                            break;
                        }
                }
                return false;
            }
        });
        */
    }

    //MySQLite 세팅
    void setDatabase(){
        C.helper = new MySQLiteOpenHelper(
                this,
                C.dbName,
                null,
                C.dbVersion);

        try {
            C.db = C.helper.getWritableDatabase(); //읽고 쓸 수 있는 DB
        } catch (SQLiteException e) {
            e.printStackTrace();
            Log.e(C.tag, "데이터베이스 얻어올 수 없음");
            finish();
        }
    }

    // DB에 있는 데이터를 memos Arraylist에 넣는다. (리사이클러뷰 사용하기 위해)
    void setData(){
        Cursor c = C.db.rawQuery("SELECT * FROM myMemoTable;", null);
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String title = c.getString(1);
            String content = c.getString(2);
            String detail = c.getString(3);

            memos.add(new Memo(id,title,content,detail));
            Log.d(C.tag,"id : "+ id + " title : " + title + " content : " + content + " detail : " + detail);
        }
        Log.d(C.tag, "Select 됐음.");
    }

    //리사이클러뷰 세팅
    void setRecyclerView(){
        // 레이아웃매니저 생성
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager); // 리사이클러뷰에 레이아웃매니저 장착

        /*
        //구분선 어떻게..?
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(getApplicationContext(),new LinearLayoutManager(this).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        */
        adapter = new Adapter(getApplicationContext(),memos); //어댑터 객체 초기화 (데이터 장착)
        recyclerView.setAdapter(adapter); // 리사이클러뷰에 어댑터 장착
    }
}


class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{
    Context context;
    ArrayList<Memo> memos = new ArrayList<>();


    public Adapter(Context context, ArrayList<Memo> memos){
        this.context = context;
        this.memos = memos;
    }

    //데이터(리스트 아이템) 의 xml layout 을 뷰홀더에 세팅해준다.
    //onCreateViewHolder 에서 뷰클래스를 생성하는데 이때 아이템 레이아웃의 xml코드를 가져와서 세팅해줍니다.
    //그리고 뷰홀더 객체에 만들어준 뷰를 넘겨주고 뷰의 각각의 텍스트뷰를 초기화를 해줍니다
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item,parent,false);

        return new ViewHolder(view);
    }

    //뷰홀더 패턴 방식,
    //즉 뷰를 재활용 할 때 데이터를 알맞게 새로 세팅해주는 역할
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Memo memo = memos.get(position);
        holder.title.setText(memo.getTitle());
        holder.content.setText(memo.getContent());
    }


    @Override
    public int getItemCount() {
        return memos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title,content;
        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            //삭제 버튼
            Button btnDel = itemView.findViewById(R.id.btnDel);
            btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog d = new Dialog(MainActivity.aActivity);
                    d.setTitle("주의");
                    d.setContentView(R.layout.dialog_layout); // 다이얼로그 화면 등록

                    Button b1 = d.findViewById(R.id.button1);
                    Button b2 = d.findViewById(R.id.button2);

                    //확인
                    b1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int id = memos.get(getAdapterPosition()).getId();
                            //SQLite 내에서 id는 1부터 시작하므로
                            C.db.execSQL("DELETE FROM myMemoTable WHERE id = " + id + ";");
                            d.dismiss();

                            //현재 액티비티 죽이고 다시 새로 액티비티 생성
                            Intent intent = new Intent(MainActivity.aActivity,MainActivity.class);
                            MainActivity.aActivity.startActivity(intent);
                            MainActivity.aActivity.finish();
                        }
                    });
                    //취소
                    b2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            d.dismiss();
                        }
                    });

                    d.show(); // 다이얼로그 띄우기
                }
            });

            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.aActivity,DetailMemoActivity.class);
            Memo memo = memos.get(getAdapterPosition());
            intent.putExtra("memo",memo);
            MainActivity.aActivity.startActivity(intent);
        }
    }
}

class Memo implements Serializable {
    private int id;
    private String title;
    private String content;
    private String detail;

    public Memo(int id , String title, String content, String detail) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.detail = detail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}

