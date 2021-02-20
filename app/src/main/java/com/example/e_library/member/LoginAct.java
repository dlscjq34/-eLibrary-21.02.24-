package com.example.e_library.member;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.e_library.MainActivity;
import com.example.e_library.R;
import com.example.e_library.common.BasicActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;

//로그인
public class LoginAct extends BasicActivity {
    EditText etId, etPswd;
    Button btnLogin;
    boolean loginOK = false;
    SharedPreferences sp;
    MemberVO member = new MemberVO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        etId = findViewById(R.id.etId);
        etPswd = findViewById(R.id.etPswd);
        btnLogin = findViewById(R.id.btnLogin);
        sp = getSharedPreferences("sFile", MODE_PRIVATE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //로그인 백그라운드 작업
                new LoginThread().start();

            }//onClick
        });//btnLogin
    }//onCreate







    //로그인 백그라운드 작업
    class LoginThread extends Thread {
        StringBuilder sb = new StringBuilder();
        Handler handler = new Handler();

        @Override
        public void run() {
            try {
                String server = "http://192.168.200.173:8080/androidLogin";
                URL url = new URL(server);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("POST");
                conn.setUseCaches(false);
                String param = "memberId="+etId.getText().toString()+"&pswd="+etPswd.getText().toString();//서버로 갈 로그인 정보
                byte[] bytes = param.getBytes("UTF-8");//로그인 정보를 바이트배열에 담고
                OutputStream os = conn.getOutputStream();//접속객체에서 출력스트림 만들어서
                os.write(bytes);//출력스트림에 바이트배열을 담자 (전송 완료)


                //서버로부터 정상 응답 받으면
                if (conn.getResponseCode() == 200) {

                    InputStream is = conn.getInputStream();//서버에서 자료 받을 입력스트림
                    InputStreamReader isr = new InputStreamReader(is, "utf-8");//한글처리
                    BufferedReader br = new BufferedReader(isr);//성능

                    //리더에서 데이터 꺼내오고 리더 및 접속 닫기
                    while (true) {
                        String line = br.readLine();
                        if (line == null) break;
                        sb.append(line+"\n");
                    }
                    br.close();
                    conn.disconnect();

                    //꺼내온 데이터를 json 변환
                    JSONObject jsonObj = new JSONObject(sb.toString());

                    if (!jsonObj.getString("memberId").equals("empty")) {
                        setMember(jsonObj);
                        getMember();
                        loginOK = true;
                    }

                    //결과 메시지
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (loginOK) {
                                Toast.makeText(LoginAct.this, "로그인 되었습니다." , Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else {
                                Toast.makeText(LoginAct.this, "아이디와 비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
            catch (Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginAct.this, "ERROR : "+e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }//run
    }//LoginThread





    //커스텀 메서드///////////////////////////////////////////////////////////////////////////////////////////////////

    //멤버정보 변경
    public void setMember(JSONObject jsonObj) {
        SharedPreferences.Editor editor = sp.edit();
        try {
            editor.putString("memberId", jsonObj.getString("memberId"));
            editor.putString("pswd", jsonObj.getString("pswd"));
            editor.putString("memberName", jsonObj.getString("memberName"));
            editor.putString("addr", jsonObj.getString("addr"));
            editor.putString("tel", jsonObj.getString("tel"));
            editor.putString("regDate", jsonObj.getString("regDate"));
            editor.commit();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //현재 멤버정보
    public void getMember() {
        member.setMemberId(sp.getString("memberId", "empty"));
        member.setPswd(sp.getString("pswd", "empty"));
        member.setMemberName(sp.getString("memberName", "empty"));
        member.setAddr(sp.getString("addr", "empty"));
        member.setTel(sp.getString("tel", "empty"));
        member.setRegDate(sp.getString("regDate", "empty"));
    }
}
