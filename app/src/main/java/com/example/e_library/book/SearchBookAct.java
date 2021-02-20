package com.example.e_library.book;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_library.R;
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

//도서 검색
public class SearchBookAct extends BasicActivity {

    final String[] items = {"도서명","저자","출판사"};//스피너
    List<BookVO> bookList = new ArrayList<>();//검색 결과 리스트
    BookAdapter bookAdapter;
    Spinner spinner;
    EditText etSearchBook;
    Button btnSearch;
    String column;
    ListView lvBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_book);
        spinner = findViewById(R.id.field_spinner);
        etSearchBook = findViewById(R.id.etSearchBook);
        btnSearch = findViewById(R.id.btnSearch);
        lvBook = findViewById(R.id.lvBook);

        //스피너 세팅
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(spinnerAdapter);

        //검색버튼
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //스피너 선택값으로 DB 검색용 필드 변환
                String spinnerStr = spinner.getSelectedItem().toString();
                switch (spinnerStr) {
                    case "도서명":    column = "bookName";     break;
                    case "저자":      column = "writer";       break;
                    case "출판사":    column = "publisher";    break;
                    default:         column = "";             break;
                }

                //도서검색 백그라운드작업
                new JoinThread().start();

            }//onClick
        });//btnSearch
    }//onCreate





    //도서검색 백그라운드작업
    class JoinThread extends Thread {
        StringBuilder sb = new StringBuilder();
        Handler handler = new Handler();

        @Override
        public void run() {
            try {

                //서버 접속 및 데이터 전송
                String server = "http://192.168.200.173:8080/androidSearchBook";
                URL url = new URL(server);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("POST");
                conn.setUseCaches(false);
                String param = "column="+column+
                              "&searchBook="+etSearchBook.getText().toString();//서버로 갈 정보
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
                    JSONArray jArray = jsonObj.getJSONArray("result");//제이슨객체에서 제이슨배열 꺼내기

                    bookList.clear();//검색버튼 누를 때마다 리스트를 초기화(검색 결과 화면 누적 방지)

                    for (int i = 0; i < jArray.length(); i++) {//제이슨배열에서 리스트로 데이터 이동
                        JSONObject jsonRow = jArray.getJSONObject(i);
                        BookVO book = new BookVO();
                        book.setBookId(jsonRow.getString("bookId"));
                        book.setBookName(jsonRow.getString("bookName"));
                        book.setWriter(jsonRow.getString("writer"));
                        book.setPublisher(jsonRow.getString("publisher"));
                        book.setPubliDate(jsonRow.getString("publiDate"));
                        book.setStatus(jsonRow.getString("status"));
                        bookList.add(book);
                    }

                    //결과 화면
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //검색 결과가 있으면 어댑터로 리스트 나열
                            if (bookList.size() > 0) {
                                BookAdapter bookAdapter = new BookAdapter(SearchBookAct.this, R.layout.book_info_row, bookList);
                                lvBook.setAdapter(bookAdapter);
                                
                                //아이템 클릭 시 도서정보로 이동
                                lvBook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Intent intent = new Intent(getApplicationContext(), BookInfoAct.class);
                                        intent.putExtra("book", bookList.get(position));
                                        startActivity(intent);
                                    }
                                });
                            }
                            else {
                                Toast.makeText(SearchBookAct.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                                lvBook.setAdapter(bookAdapter);
                            }
                        }
                    });
                }//if (conn.getResponseCode() == 200)
            }
            catch (Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SearchBookAct.this, "ERROR : "+e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }//run
    }//JoinThread




    //커스텀 어댑터
    class BookAdapter extends ArrayAdapter<BookVO> {

        public BookAdapter(@NonNull Context context, int resource, @NonNull List<BookVO> objects) {
            super(context, resource, objects);
        }

        //자식뷰 생산
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View v = convertView;

            if (v == null) {//처음 한번만 실행
                LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);//뷰생성기
                v = li.inflate(R.layout.book_info_row, null);//자식뷰 생성
            }

            //자식뷰에 데이터 투입
            BookVO bookVO = bookList.get(position);
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