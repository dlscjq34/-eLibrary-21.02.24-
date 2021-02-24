package com.example.e_library;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_library.Rent.UseHistory;
import com.example.e_library.book.SearchBookAct;
import com.example.e_library.common.BasicActivity;
import com.example.e_library.favor.FavorListAct;
import com.example.e_library.member.LoginAct;

import java.util.zip.Inflater;

public class MainActivity extends BasicActivity {

    TextView noticeContent;
    SharedPreferences sp;

    //메인액티비티 생성 시
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        noticeContent = findViewById(R.id.noticeContent);

        //공통화면에 나오는 창닫기 아이콘 표시 해제
        findViewById(R.id.imgX).setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sp = getSharedPreferences("sFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    public void onClick(View v) {
        sp = getSharedPreferences("sFile", MODE_PRIVATE);
        boolean isNotLogin = sp.getString("memberId", "empty").equals("empty");
        Intent intent = null;

        switch (v.getId()) {
            case R.id.search:
                intent = new Intent(this, SearchBookAct.class);
                break;
            case R.id.use://로그인 안했으면 로그인창, 아니면 이용내역 이동
                if (isNotLogin)
                    intent = new Intent(this, LoginAct.class);
                else
                    intent = new Intent(this, UseHistory.class);
                break;
            case R.id.favor://로그인 안했으면 로그인창, 아니면 이용내역 이동
                if (isNotLogin)
                    intent = new Intent(this, LoginAct.class);
                else
                    intent = new Intent(this, FavorListAct.class);
                break;
        }
        startActivity(intent);
    }
}
