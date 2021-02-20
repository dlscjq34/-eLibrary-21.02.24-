package com.example.e_library.member;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class MemberAct extends BasicActivity {

    TextView tvId, tvPswd, tvMemberName, tvAddr, tvTel, tvRegDate;
    EditText etId, etPswd ,etMemberName, etAddr, etTel, etRegDate;
    Button modifyStart, showStart, btnModify;
    LinearLayout showMember, modifyMember;
    SharedPreferences sp;
    MemberVO member = new MemberVO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member);
        tvId = findViewById(R.id.tvId);
        tvPswd = findViewById(R.id.tvPswd);
        tvMemberName = findViewById(R.id.tvMemberName);
        tvAddr = findViewById(R.id.tvAddr);
        tvTel = findViewById(R.id.tvTel);
        tvRegDate = findViewById(R.id.tvRegDate);
        etId = findViewById(R.id.etId);
        etPswd = findViewById(R.id.etPswd);
        etMemberName = findViewById(R.id.etMemberName);
        etAddr = findViewById(R.id.etAddr);
        etTel = findViewById(R.id.etTel);
        etRegDate = findViewById(R.id.etRegDate);
        modifyStart = findViewById(R.id.modifyStart);
        showMember = findViewById(R.id.showMember);
        modifyMember = findViewById(R.id.modifyMember);
        showStart = findViewById(R.id.showStart);
        btnModify = findViewById(R.id.btnModify);
        sp = getSharedPreferences("sFile", MODE_PRIVATE);


        //기본세팅
        getMember();//현재 멤버정보
        initModify();//수정화면 데이터 초기화



        //수정하기 버튼
        modifyStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMember.setVisibility(View.GONE);
                modifyMember.setVisibility(View.VISIBLE);
            }
        });

        //취소버튼
        showStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMember.setVisibility(View.VISIBLE);
                modifyMember.setVisibility(View.GONE);
                initModify();//수정하던 데이터 초기화
            }
        });

        //수정완료 버튼
        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //회원정보 유효성 검사
                if (etId.getText().length() < 5) {
                    Toast.makeText(MemberAct.this, "아이디를 5자리 이상 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (etPswd.getText().length() < 5) {
                    Toast.makeText(MemberAct.this, "비밀번호를 5자리 이상 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (etMemberName.getText().toString().equals("") || etAddr.getText().toString().equals("") || etTel.getText().toString().equals("")) {
                    Toast.makeText(MemberAct.this, "빈 칸에 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }

                //회원정보 수정 백그라운드작업
                new MemberThread().start();
            }
        });
    }



    //아이디 수정 못하게/////////////////////////////////////////////////////////////////////////////////////////////////////////



    //회원정보 수정 백그라운드작업
    class MemberThread extends Thread {
        StringBuilder sb = new StringBuilder();
        Handler handler = new Handler();

        @Override
        public void run() {
            try {
                String server = "http://192.168.200.173:8080/androidMemberUpdate";
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
                byte[] bytes = param.getBytes("UTF-8");//정보를 바이트배열에 담고
                OutputStream os = conn.getOutputStream();//접속객체에서 출력스트림 만들어서
                os.write(bytes);//출력스트림에 바이트배열을 담자 (전송 완료)
                conn.getResponseCode();


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
                    setMember(jsonObj);////멤버정보 변경(sp에 적용)

                    //화면 변경
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            getMember();//현재 멤버정보(sp 에서 VO로 적용, setText)
                            //회원정보화면으로 전환
                            showMember.setVisibility(View.VISIBLE);
                            modifyMember.setVisibility(View.GONE);
                            Toast.makeText(MemberAct.this, result, Toast.LENGTH_SHORT).show();
                            
                        }
                    });
                }//if (conn.getResponseCode() == 200)
            }
            catch (Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MemberAct.this, "ERROR : "+e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }//run
    }//MemberThread







    //커스텀 메서드///////////////////////////////////////////////////////////////////////////////////////////////////

    //현재 멤버정보(sp 에서 VO로 적용, setText)
    public void getMember() {
        member.setMemberId(sp.getString("memberId", "empty"));
        member.setPswd(sp.getString("pswd", "empty"));
        member.setMemberName(sp.getString("memberName", "empty"));
        member.setAddr(sp.getString("addr", "empty"));
        member.setTel(sp.getString("tel", "empty"));
        member.setRegDate(sp.getString("regDate", "empty"));

        tvId.setText(member.getMemberId());
        tvPswd.setText(member.getPswd());
        tvMemberName.setText(member.getMemberName());
        tvAddr.setText(member.getAddr());
        tvTel.setText(member.getTel());
        tvRegDate.setText(member.getRegDate());
    }

    //멤버정보 변경(sp에 적용)
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
        } catch
        (JSONException e) {
            e.printStackTrace();
        }
    }

    //수정화면 초기화
    public void initModify() {
        etId.setText(member.getMemberId());
        etPswd.setText(member.getPswd());
        etMemberName.setText(member.getMemberName());
        etAddr.setText(member.getAddr());
        etTel.setText(member.getTel());
        etRegDate.setText(member.getRegDate());
    }
}