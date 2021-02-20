package com.example.e_library.Rent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_library.R;
import com.example.e_library.book.BookInfoAct;
import com.example.e_library.book.BookVO;
import com.example.e_library.common.DTO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UseHistory extends AppCompatActivity {

    ListView lvUseHistory;
    List<DTO> dtoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.use_history);
        lvUseHistory = findViewById(R.id.lvUseHistory);

        new UseHistoryThread().start();
    }





    //도서대출 이용내역 백그라운드작업
    class UseHistoryThread extends Thread {
        StringBuilder sb = new StringBuilder();
        Handler handler = new Handler();
        SharedPreferences sp = getSharedPreferences("sFile",MODE_PRIVATE);
        String memberId = sp.getString("memberId","empty");

        @Override
        public void run() {
            try {
                //서버 접속 및 데이터 전송
                String server = "http://192.168.200.173:8080/androidUseHistory";
                URL url = new URL(server);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("POST");
                conn.setUseCaches(false);
                String param = "memberId="+memberId;//서버로 갈 정보
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
                    JSONArray jArray = jsonObj.getJSONArray("result");

                    //json 에서 dto 로 이동
                    for (int i = 0; i < jArray.length(); i++) {
                        DTO dto = new DTO();
                        JSONObject jsonRow = jArray.getJSONObject(i);
                        dto.setRentId(jsonRow.getString("rentId"));
                        dto.setBookId(jsonRow.getString("bookId"));
                        dto.setBookName(jsonRow.getString("bookName"));
                        dto.setRentDate(jsonRow.getString("rentDate"));
                        dto.setDueDate(jsonRow.getString("dueDate"));
                        dto.setReturnDate(jsonRow.getString("returnDate"));
                        dto.setLateDate(jsonRow.getInt("lateDate"));
                        dtoList.add(dto);
                    }


                    //결과 화면
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                           // Toast.makeText(UseHistory.this, ""+dtoList, Toast.LENGTH_SHORT).show();
                            UseHistoryAdapter adapter = new UseHistoryAdapter(UseHistory.this, R.layout.use_history_row, dtoList);
                            lvUseHistory.setAdapter(adapter);
                        }
                    });
                }//if (conn.getResponseCode() == 200)
            }
            catch (Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UseHistory.this, "ERROR : "+e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }//run
    }//useHistoryThread





    //커스텀 어댑터
    class UseHistoryAdapter extends ArrayAdapter<DTO> {

        public UseHistoryAdapter(@NonNull Context context, int resource, @NonNull List<DTO> objects) {
            super(context, resource, objects);
        }

        //자식뷰 생산
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View v = convertView;

            if (v == null) {//처음 한번만 실행
                LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);//뷰생성기
                v = li.inflate(R.layout.use_history_row, null);//자식뷰 생성
            }

            //자식뷰에 데이터 투입
            DTO dto = dtoList.get(position);
            if (dto != null) {
                TextView tvBookName = v.findViewById(R.id.tvBookName);
                TextView tvRentDate = v.findViewById(R.id.tvRentDate);
                TextView tvBackDate = v.findViewById(R.id.tvBackDate);
                TextView tvLateDate = v.findViewById(R.id.tvLateDate);
                Button btnBackBook = v.findViewById(R.id.btnBackBook);

                tvBookName.setText(dto.getBookName());
                tvRentDate.setText("대출일 : "+dto.getRentDate());
                tvBackDate.setText("반납일 : "+dto.getReturnDate());
                if (dto.getLateDate() > 0)
                    tvLateDate.setText("연체기간 : "+dto.getLateDate());
                else
                    tvLateDate.setText("연체없음");

                //도서반납이 안된 자식뷰만 반납버튼 생성
                if (dto.getReturnDate().equals("")) {
                    btnBackBook.setVisibility(View.VISIBLE);
                    btnBackBook.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(UseHistory.this, ""+dto.getReturnDate(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    btnBackBook.setVisibility(View.INVISIBLE);
                }
            }
            return  v;
        }
    }



//////////////////////////////////////////////////////////////////반납작업할 것


    //도서반납 백그라운드작업
    class backRentThread extends Thread {
        StringBuilder sb = new StringBuilder();
        Handler handler = new Handler();

        @Override
        public void run() {
            try {
                //서버 접속 및 데이터 전송
                String server = "http://192.168.200.173:8080/androidUseHistory";
                URL url = new URL(server);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("POST");
                conn.setUseCaches(false);
                String param = "rentId=";//서버로 갈 정보
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
                    JSONArray jArray = jsonObj.getJSONArray("result");

                    //json 에서 dto 로 이동
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jsonRow = jArray.getJSONObject(i);
                        DTO dto = new DTO();
                        dto.setRentId(jsonRow.getString("rentId"));
                        dto.setBookId(jsonRow.getString("bookId"));
                        dto.setBookName(jsonRow.getString("bookName"));
                        dto.setRentDate(jsonRow.getString("rentDate"));
                        dto.setDueDate(jsonRow.getString("dueDate"));
                        dto.setReturnDate(jsonRow.getString("returnDate"));
                        dto.setLateDate(jsonRow.getInt("lateDate"));
                        dtoList.add(dto);
                    }


                    //결과 화면
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // Toast.makeText(UseHistory.this, ""+dtoList, Toast.LENGTH_SHORT).show();
                            UseHistoryAdapter adapter = new UseHistoryAdapter(UseHistory.this, R.layout.use_history_row, dtoList);
                            lvUseHistory.setAdapter(adapter);
                        }
                    });
                }//if (conn.getResponseCode() == 200)
            }
            catch (Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UseHistory.this, "ERROR : "+e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }//run
    }//useHistoryThread*




}