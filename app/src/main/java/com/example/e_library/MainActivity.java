package com.example.e_library;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.e_library.Rent.UseHistory;
import com.example.e_library.book.SearchBookAct;
import com.example.e_library.common.BasicActivity;

import java.util.zip.Inflater;

public class MainActivity extends BasicActivity {

    TextView noticeContent;

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

        SharedPreferences sp = getSharedPreferences("sFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.search:
                intent = new Intent(this, SearchBookAct.class);
                break;
            case R.id.use:
                intent = new Intent(this, UseHistory.class);
                break;
        }

        startActivity(intent);
    }

}
