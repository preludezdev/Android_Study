package com.example.pen.android_study_01;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pen.android_study_01.VO.BackupRequest;
import com.example.pen.android_study_01.VO.BaseResult;
import com.example.pen.android_study_01.VO.GetBackupResult;
import com.example.pen.android_study_01.VO.TokenResult;
import com.example.pen.android_study_01.VO.ValidResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private final static int ADD_MEMO_REQUEST = 3000;

    public String mToken;

    Adapter adapter;
    List<Memo> memos;
    String sfTokenFile = "myToken";
    SharedPreferences sf;

    RecyclerView recyclerView;
    Button b1;
    Button b2;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init(); // SharedPreferences 값 초기화, findViewById 초기화

        initiateToken(); // 처음 앱 실행시 토큰 값 가져오기

        // DB에 있는 데이터를 memos List에 넣는다. (리사이클러뷰 사용하기 위해)
        reloadDataFromDB();
        setRecyclerView();

        // 플로팅 액션 버튼 클릭시 AddMemoActivity로 이동
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AddMemoActivity.class);
                //startActivity(intent);
                startActivityForResult(intent,ADD_MEMO_REQUEST);
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rfBackUp();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rfRestore();
            }
        });
    }

    void init() {
        sf = getSharedPreferences(sfTokenFile,0);

        fab = findViewById(R.id.fab);
        //리사이클러뷰 세팅
        recyclerView = findViewById(R.id.recycler);
        //백업버튼
        b1  = findViewById(R.id.button1);
        //복원버튼
        b2 = findViewById(R.id.button2);
    }


    private void notifyDataSetChanged(){
        reloadDataFromDB();
        adapter.notifyDataSetChanged();
    }

    // 다른 액티비티에서 처리된 결과를 받는 메소드
    // 처리된 결과 코드 (resultCode) 가 RESULT_OK 이면 requestCode 를 판별해 결과 처리를 진행한다.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        switch (resultCode){
            // MainActivity 에서 요청할 때 보낸 요청 코드 (3000)
            case ADD_MEMO_REQUEST:
                notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    void reloadDataFromDB(){
        // DB에 있는 데이터를 memos Arraylist에 넣는다. (리사이클러뷰 사용하기 위해)
        memos = DBHelper.getInstance(this).getMemosFromDB();
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

        adapter = new Adapter(); //어댑터 객체 초기화 (데이터 장착)
        recyclerView.setAdapter(adapter); // 리사이클러뷰에 어댑터 장착

    }

    // 처음 앱 실행시 서버에서 토큰값 가져오기
    private void initiateToken(){
        //key 가 token 인 String 데이터를 가져온다.
        //만약 해당 키의 데이터가 없으면 "-1"을 리턴.
        mToken = sf.getString("token",null);

        if(mToken == null){
            getNewToken();
        }
        else{   // mToken 이 null 이 아니면 이 토큰이 현재도 유효한지 서버에 체크한다.
            NetworkHelper.getInstance().memoService.checkToken(mToken).enqueue(new Callback<ValidResult>() {
                @Override
                public void onResponse(Call<ValidResult> call, Response<ValidResult> response) {
                    if(response.isSuccessful()){
                        if(response.body().isValid()){
                            //유효한 토큰이므로 패스
                        }
                        else{
                            //유효하지 않은 경우
                            //새로운 토큰을 발급받아야 할 듯..??
                            Toast.makeText(MainActivity.this,"유효하지 않은 토큰\n토큰 재발급 시도...",Toast.LENGTH_SHORT).show();
                            getNewToken();
                        }
                    }
                    else{
                        Toast.makeText(MainActivity.this,"요청에 실패했습니다.",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ValidResult> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(MainActivity.this,"네트워크 환경을 확인해주세요.",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getNewToken(){
        //Post Method
        NetworkHelper.getInstance().memoService.createToken().enqueue(new Callback<TokenResult>() {
            @Override
            public void onResponse(Call<TokenResult> call, Response<TokenResult> response) {
                if(response.isSuccessful()) {
                    TokenResult result = response.body();
                    mToken = result.getToken();
                    //발급받은 토큰을 SharedPreferences 데이터 안에 저장
                    SharedPreferences.Editor editor = sf.edit();
                    editor.putString("token",mToken);
                    editor.apply();
                }
                else{
                    Toast.makeText(MainActivity.this,"새 토큰 요청에 실패했습니다.",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TokenResult> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(MainActivity.this,"네트워크 환경을 확인해주세요.",Toast.LENGTH_SHORT).show();
            }
        });
    }


    // 서버에 데이터 백업하기
    // POST backup
    void rfBackUp(){
        final BackupRequest request = new BackupRequest(mToken,memos);
        NetworkHelper.getInstance().memoService.backup(request).enqueue(new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, response.body().getMessage()+"\n"+mToken, Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MainActivity.this, "백업 요청에 실패했습니다.\n"+response.code()+"\n"+mToken, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {
                Toast.makeText(MainActivity.this,"네트워크 환경을 확인해주세요.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 서버에서 데이터 복원하기
    // GET backup
    void rfRestore(){
        NetworkHelper.getInstance().memoService.getBackup(mToken).enqueue(new Callback<GetBackupResult>() {
            @Override
            public void onResponse(Call<GetBackupResult> call, Response<GetBackupResult> response) {
                if(response.isSuccessful()){
                    memos = response.body().getMemos();
                    // 로컬DB 해당 테이블 안의 내용 전부 지우기
                    DBHelper.getInstance(MainActivity.this).clearMemos();
                    // 받아온 메모 넣어주기
                    DBHelper.getInstance(MainActivity.this).addAllMemos(memos);

                    notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "복원 성공!!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MainActivity.this, "존재하지 않는 토큰입니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetBackupResult> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(MainActivity.this,"네트워크 환경을 확인해주세요.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRemoveDialog(final int position){
        final Dialog d = new Dialog(this);
        d.setTitle("주의");
        d.setContentView(R.layout.dialog_layout); // 다이얼로그 화면 등록

        Button b1 = d.findViewById(R.id.button1);
        Button b2 = d.findViewById(R.id.button2);

        //확인
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = memos.get(position).getId();
                //SQLite 내에서 id는 1부터 시작하므로
                DBHelper.getInstance(MainActivity.this).removeMemoFromDB(id);
                d.dismiss();

                //현재 액티비티 죽이고 다시 새로 액티비티 생성
                Intent intent = new Intent(v.getContext(),MainActivity.class);
                v.getContext().startActivity(intent);
                finish();
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



    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{

        public Adapter(){
        }

        //데이터(리스트 아이템) 의 xml layout 을 뷰홀더에 세팅해준다.
        //onCreateViewHolder 에서 뷰클래스를 생성하는데 이때 아이템 레이아웃의 xml코드를 가져와서 세팅해줍니다.
        //그리고 뷰홀더 객체에 만들어준 뷰를 넘겨주고 뷰의 각각의 텍스트뷰를 초기화를 해줍니다
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item,parent,false);

            return new ViewHolder(view);
        }

        //뷰홀더 패턴 방식,
        //즉 뷰를 재활용 할 때 데이터를 알맞게 새로 세팅해주는 역할
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Memo memo = memos.get(position);
            holder.name.setText(memo.getName());
            holder.content.setText(memo.getContent());
        }


        @Override
        public int getItemCount() {
            return memos.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            TextView name,content;
            public ViewHolder(View itemView) {
                super(itemView);

                itemView.setOnClickListener(this);

                //삭제 버튼
                Button btnDel = itemView.findViewById(R.id.btnDel);
                btnDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showRemoveDialog(getAdapterPosition());
                    }
                });

                name = itemView.findViewById(R.id.title);
                content = itemView.findViewById(R.id.content);
            }

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),DetailMemoActivity.class);
                Memo memo = memos.get(getAdapterPosition());
                intent.putExtra("memo",memo);
                startActivity(intent);
            }
        }
    }
}




