package com.example.e_library.common;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.e_library.Rent.UseHistory;
import com.example.e_library.book.BookInfoAct;
import com.example.e_library.favor.FavorListAct;
import com.example.e_library.member.JoinAct;
import com.example.e_library.member.LoginAct;
import com.example.e_library.MainActivity;
import com.example.e_library.R;
import com.example.e_library.book.SearchBookAct;
import com.example.e_library.member.MemberAct;

//공통 메뉴
public class BasicActivity extends AppCompatActivity {

    Intent basicIntent = null;

    //옵션메뉴 생성 시
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();//메뉴생성기
        menuInflater.inflate(R.menu.main_menu, menu);//메뉴 생성
        return true;
    }

    //세션 유무에 따른 옵션메뉴 표현범위
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        SharedPreferences sp = getSharedPreferences("sFile",MODE_PRIVATE);
        String session = sp.getString("memberId","empty");
        if (session.equals("empty")) {//세션이 없으면
            menu.findItem(R.id.login).setVisible(true);
            menu.findItem(R.id.logOut).setVisible(false);
            menu.findItem(R.id.join).setVisible(true);
            menu.findItem(R.id.useHistory).setVisible(false);
            menu.findItem(R.id.favorBook).setVisible(false);
            menu.findItem(R.id.member).setVisible(false);
        }
        else {
            menu.findItem(R.id.login).setVisible(false);
            menu.findItem(R.id.logOut).setVisible(true);
            menu.findItem(R.id.join).setVisible(false);
            menu.findItem(R.id.useHistory).setVisible(true);
            menu.findItem(R.id.favorBook).setVisible(true);
            menu.findItem(R.id.member).setVisible(true);
        }
        return true;
    }

    //옵션메뉴 선택 시
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.login:
                basicIntent = new Intent(this, LoginAct.class);
                break;
            case R.id.logOut:
                new AlertDialog.Builder(BasicActivity.this)
                        .setTitle("로그아웃")
                        .setMessage("로그 아웃하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences sp = getSharedPreferences("sFile", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.clear();
                                editor.commit();
                                Toast.makeText(BasicActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .setNegativeButton("아니오", null)
                        .show();
                return false;
            case R.id.join:
                basicIntent = new Intent(this, JoinAct.class);
                break;
            case R.id.search:
                basicIntent = new Intent(this, SearchBookAct.class);
                break;
            case R.id.useHistory:
                basicIntent = new Intent(this, UseHistory.class);
                break;
            case R.id.member:
                basicIntent = new Intent(this, MemberAct.class);
                break;
            case R.id.favorBook:
                basicIntent = new Intent(this, FavorListAct.class);
                break;
        }
        startActivity(basicIntent);
        return false;
    }


    //홈으로 및 현재창 종료
    public void headerOnClick(View v) {
        switch (v.getId()) {
            case R.id.header:
                basicIntent = new Intent(this, MainActivity.class);// 첫번째 액티비티로 인탠트를 설정
                basicIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 스택에 남아있는 중간 액티비티 삭제
                basicIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);// 스택에 남아있는 액티비티라면, 재사용
                startActivity(basicIntent);// 액티비티를 실행한다
                finish();// 현재 액티비티를 종료한다.

            case R.id.imgX:
                finish();// 현재 액티비티를 종료한다.
        }
    }
}
