package com.example.e_library.favor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_library.R;
import com.example.e_library.book.BookVO;
import com.example.e_library.book.SearchBookAct;
import com.example.e_library.common.BasicActivity;

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


//관심도서 목록
public class FavorListAct extends BasicActivity {

    ListView lvFavorList;
    TextView bookName, writer, publisher, publiDate, status, bookId;
    List<BookVO> favorList = new ArrayList<>();//검색 결과 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favor_list);
        lvFavorList = findViewById(R.id.lvFavorList);

        new FavorListThread().start();//관심도서 백그라운드작업
    }




    //관심도서 백그라운드작업
    class FavorListThread extends Thread {
        StringBuilder sb = new StringBuilder();
        Handler handler = new Handler();
        SharedPreferences sp = getSharedPreferences("sFile",MODE_PRIVATE);
        String memberId = sp.getString("memberId","empty");

        @Override
        public void run() {
            try {
                //서버 접속 및 데이터 전송
                String server = "http://192.168.200.173:8080/androidFavorList";
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
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jsonRow = jArray.getJSONObject(i);
                        BookVO book = new BookVO();
                        book.setBookId(jsonRow.getString("bookId"));
                        book.setBookName(jsonRow.getString("bookName"));
                        book.setWriter(jsonRow.getString("writer"));
                        book.setPublisher(jsonRow.getString("publisher"));
                        book.setPubliDate(jsonRow.getString("publiDate"));
                        book.setStatus(jsonRow.getString("status"));
                        favorList.add(book);
                    }

                    //결과 화면
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            FavorListAdapter adapter = new FavorListAdapter(FavorListAct.this, R.layout.favor_book_row, favorList);
                            lvFavorList.setAdapter(adapter);
                        }
                    });
                }//if (conn.getResponseCode() == 200)
            }
            catch (Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FavorListAct.this, "ERROR : "+e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }//run
    }//FavorListThread








    //커스텀 어댑터
    class FavorListAdapter extends ArrayAdapter<BookVO> {

        public FavorListAdapter(@NonNull Context context, int resource, @NonNull List<BookVO> objects) {
            super(context, resource, objects);
        }

        //자식뷰 생산
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View v = convertView;

            if (v == null) {//처음 한번만 실행
                LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);//뷰생성기
                v = li.inflate(R.layout.favor_book_row, null);//자식뷰 생성
            }

            //자식뷰에 데이터 투입
            BookVO bookVO = favorList.get(position);
            if (bookVO != null) {
                TextView bookName = v.findViewById(R.id.bookName);
                TextView writer = v.findViewById(R.id.writer);
                TextView publisher = v.findViewById(R.id.publisher);
                TextView publiDate = v.findViewById(R.id.publiDate);
                TextView status = v.findViewById(R.id.status);
                TextView bookId = v.findViewById(R.id.bookId);

                bookName.setText(bookVO.getBookName());
                writer.setText(bookVO.getWriter());
                publisher.setText(bookVO.getPublisher());
                publiDate.setText(bookVO.getPubliDate());
                status.setText(bookVO.getStatus());
                bookId.setText(bookVO.getBookId());
            }

            return  v;
        }
    }
}