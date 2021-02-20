package com.example.e_library.member;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.e_library.MainActivity;
import com.example.e_library.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//회원 가입
public class JoinAct extends AppCompatActivity {

    EditText etId, etPswd, etMemberName, etAddr, etTel;
    Button btnJoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join);

        etId = findViewById(R.id.etId);
        etPswd = findViewById(R.id.etPswd);
        etMemberName = findViewById(R.id.etMemberName);
        etAddr = findViewById(R.id.etAddr);
        etTel = findViewById(R.id.etTel);
        btnJoin = findViewById(R.id.btnJoin);

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //회원정보 유효성 검사
                if (etId.getText().length() < 5) {
                    Toast.makeText(JoinAct.this, "아이디를 5자리 이상 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (etPswd.getText().length() < 5) {
                    Toast.makeText(JoinAct.this, "비밀번호를 5자리 이상 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (etMemberName.getText().toString().equals("") || etAddr.getText().toString().equals("") || etTel.getText().toString().equals("")) {
                    Toast.makeText(JoinAct.this, "빈 칸에 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }


                //회원가입 백그라운드작업
                new JoinThread().start();

            }//onClick
        });//btnLogin
    }//onCreate






    //회원가입 백그라운드작업
    class JoinThread extends Thread {
        StringBuilder sb = new StringBuilder();
        Handler handler = new Handler();

        @Override
        public void run() {
            try {
                String server = "http://192.168.200.173:8080/androidJoin";
                URL url = new URL(server);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("POST");
                conn.setUseCaches(false);
                String param = "memberId="+etId.getText().toString()+
                        "&pswd="+etPswd.getText().toString()+
                        "&memberName="+etMemberName.getText().toString()+
                        "&addr="+etAddr.getText().toString()+
                        "&tel="+etTel.getText().toString();//서버로 갈 정보
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
                    String result = jsonObj.getString("result");

                    //결과 메시지
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (result.equals("회원가입 되었습니다.")) {
                                Toast.makeText(JoinAct.this, ""+result, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else {
                                Toast.makeText(JoinAct.this, ""+result, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
            catch (Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(JoinAct.this, "ERROR : "+e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }//run
    }//JoinThread
}