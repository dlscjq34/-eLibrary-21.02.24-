package com.example.e_library.book;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_library.R;
import com.example.e_library.common.BasicActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//도서 정보(대출, 관심도서등록 포함)
public class BookInfoAct extends BasicActivity {

    Button btnRent, btnFavor;
    TextView bookName, writer, publisher, publiDate, status, bookId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_info);
        btnRent = findViewById(R.id.btnRent);
        btnFavor = findViewById(R.id.btnFavor);
        bookName = findViewById(R.id.bookName);
        writer = findViewById(R.id.writer);
        publisher = findViewById(R.id.publisher);
        publiDate = findViewById(R.id.publiDate);
        status = findViewById(R.id.status);
        bookId = findViewById(R.id.bookId);

        //도서 정보 표시
        BookVO book = (BookVO) getIntent().getSerializableExtra("book");
        bookName.setText(book.getBookName());
        writer.setText("저자 : "+book.getWriter());
        publisher.setText("출판사 : "+book.getPublisher());
        publiDate.setText("출간일 : "+book.getPubliDate());
        status.setText("상태 : "+book.getStatus());
        bookId.setText("도서번호 : "+book.getBookId());



        //대출신청 버튼
        btnRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //세션 확인
                SharedPreferences sp = getSharedPreferences("sFile",MODE_PRIVATE);
                String session = sp.getString("memberId","empty");
                if (session.equals("empty"))
                    Toast.makeText(BookInfoAct.this, "로그인 후 사용하세요.", Toast.LENGTH_SHORT).show();
                else
                    new RentThread().start();
            }
        });


        //관심도서등록 버튼
        btnFavor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //세션 확인
                SharedPreferences sp = getSharedPreferences("sFile",MODE_PRIVATE);
                String session = sp.getString("memberId","empty");
                if (session.equals("empty"))
                    Toast.makeText(BookInfoAct.this, "로그인 후 사용하세요.", Toast.LENGTH_SHORT).show();
                else
                    new FavorBookThread().start();
            }
        });
    }






    //대출신청 백그라운드작업
    class RentThread extends Thread {
        StringBuilder sb = new StringBuilder();
        Handler handler = new Handler();
        SharedPreferences sp = getSharedPreferences("sFile",MODE_PRIVATE);
        String memberId = sp.getString("memberId","empty");
        BookVO book = (BookVO) getIntent().getSerializableExtra("book");

        @Override
        public void run() {
            try {
                //서버 접속 및 데이터 전송
                String server = "http://192.168.200.173:8080/androidRent";
                URL url = new URL(server);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("POST");
                conn.setUseCaches(false);
                String param = "memberId="+memberId+
                              "&bookId="+book.getBookId();//서버로 갈 정보
                byte[] bytes = param.getBytes("UTF-8");//정보를 바이트배열에 담고
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
                    JSONObject jsonObj = new JSONObject(sb.toString());//제이슨객체 생성
                    String result = jsonObj.getString("result");

                    //결과 화면
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BookInfoAct.this, ""+result, Toast.LENGTH_SHORT).show();
                            status.setText("관 외 대출 중");
                        }
                    });
                }//if (conn.getResponseCode() == 200)
            }
            catch (Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BookInfoAct.this, "ERROR : "+e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }//run
    }//RentThread







    //관심도서등록 백그라운드작업
    class FavorBookThread extends Thread {
        StringBuilder sb = new StringBuilder();
        Handler handler = new Handler();
        SharedPreferences sp = getSharedPreferences("sFile",MODE_PRIVATE);
        String session = sp.getString("memberId","empty");
        BookVO book = (BookVO) getIntent().getSerializableExtra("book");

        @Override
        public void run() {
            try {
                //서버 접속 및 데이터 전송
                String server = "http://192.168.200.173:8080/androidFavorBook";
                URL url = new URL(server);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("POST");
                conn.setUseCaches(false);
                String param = "memberId="+session+
                              "&bookId="+book.getBookId();//서버로 갈 정보
                byte[] bytes = param.getBytes("UTF-8");//정보를 바이트배열에 담고
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
                    JSONObject jsonObj = new JSONObject(sb.toString());//제이슨객체 생성
                    String result = jsonObj.getString("result");

                    //결과 화면
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BookInfoAct.this, ""+result, Toast.LENGTH_SHORT).show();
                        }
                    });
                }//if (conn.getResponseCode() == 200)
            }
            catch (Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BookInfoAct.this, "ERROR : "+e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }//run
    }//FavorBookThread
}