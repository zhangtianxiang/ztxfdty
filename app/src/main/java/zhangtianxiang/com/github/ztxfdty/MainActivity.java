package zhangtianxiang.com.github.ztxfdty;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.FloatingActionButton;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton refresh_btn;
    private EditText edit_username;
    private EditText edit_password;
    private Button update_btn;
    private String str_username;
    private String str_password;
    private SharedHelper sh;
    private Context mContext;
    private OkHttpClient okHttpClient;
    private TextView infoText;
    private TextView recText;
    private TextView nicknameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();
        sh = new SharedHelper(mContext);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC); // set logger

        okHttpClient = new OkHttpClient.Builder()
                .followRedirects(false) // un_allow 302 redirect
                .followSslRedirects(false)
                .cookieJar(new CookieJar() { // save cookie
                    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookieStore.put(url.host(), cookies);
                        cookieStore.put(HttpUrl.parse("https://tac.fudan.edu.cn").host(), cookies);
                    }
                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();

        bindViews();

        show_data();

        try {
            update_data();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"获取数据失败 catch err"+e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    private void bindViews() {
        edit_username = (EditText)findViewById(R.id.edit_username);
        edit_password = (EditText)findViewById(R.id.edit_password);
        update_btn = (Button)findViewById(R.id.update_btn);
        refresh_btn = (FloatingActionButton) findViewById(R.id.floatingActionButton2);
        infoText = (TextView) findViewById(R.id.infoText);
        recText = (TextView) findViewById(R.id.recText);
        nicknameText = (TextView) findViewById(R.id.nicknameText);

        recText.setMovementMethod(ScrollingMovementMethod.getInstance());

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_username = edit_username.getText().toString();
                str_password = edit_password.getText().toString();
                sh.save(str_username,str_password);
                try {
                    update_data();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(),"获取数据发生错误:"+e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    update_data();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(),"获取数据发生错误:"+e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String,String> data = sh.read();
        edit_username.setText(data.get("username"));
        edit_password.setText(data.get("password"));
    }

    private void show_data() {
        String nickname = sh.read_nickname();
        ArrayList<String> info = sh.read_info();
        ArrayList<String> rec = sh.read_rec();
        String info_show = "";
        int sz = info.size();
        for (int i = 0; i < sz; i ++ ) {
            info_show += info.get(i);
            if ((i & 1) == 1) info_show += "\n";
            else info_show += "　";
        }
        String rec_show = "";
        for (String item : rec) {
            rec_show = item+"\n"+rec_show;
        }
        nicknameText.setText(nickname);
        infoText.setText(info_show);
        recText.setText(rec_show);
    }

    private void update_data() throws IOException {
        if (isNetworkAvailable(getApplicationContext())) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //放在UI线程弹Toast
                    Toast.makeText(MainActivity.this,"正在获取数据",Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //放在UI线程弹Toast
                    Toast.makeText(MainActivity.this,"网络连接已断开",Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        Map<String,String> data = sh.read();

        RequestBody req_uis_body = new FormBody.Builder()
                .add("IDToken1", data.get("username"))
                .add("IDToken2", data.get("password"))
                .add("IDButton", "Submit")
                .add("goto", "aHR0cHM6Ly90YWMuZnVkYW4uZWR1LmNuL3RoaXJkcy90amIuYWN0P3JlZGlyPXNwb3J0U2NvcmU=")
                .add("encode", "true")
                .add("gx_charset", "UTF-8")
                .build();

        Request request_uis = new Request.Builder()
                .url("https://uis2.fudan.edu.cn/amserver/UI/Login?goto=https://tac.fudan.edu.cn/thirds/tjb.act%3Fredir=sportScore")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; NMTE; rv:11.0) like Gecko")
                .addHeader("Connection","keep-alive")
                .post(req_uis_body)
                .build();

        final Call call = okHttpClient.newCall(request_uis);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                final String errdata = e.toString();
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //放在UI线程弹Toast
                        Toast.makeText(MainActivity.this,"获取数据失败:请求失败，网络问题"+errdata,Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
//                System.out.println(response.headers().toString());
                if (response.code() == 200) { // 登录失败，提示重新输入账号密码
//                    System.out.println(response.body().string());

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //放在UI线程弹Toast
                            Toast.makeText(MainActivity.this,"账号或密码错误",Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (response.code() == 302) { // 登陆成功会跳转三次
                    Request redirect_1 = new Request.Builder().url("https://tac.fudan.edu.cn/thirds/tjb.act?redir=sportScore")
                            .build();
                    Response response_1 = okHttpClient.newCall(redirect_1).execute();
                    Request redirect_2 = new Request.Builder().url(response_1.header("Location"))
                            .build();
                    okHttpClient.newCall(redirect_2).execute();
                    Request redirect_3 = new Request.Builder().url("http://www.fdty.fudan.edu.cn/sportScore/stScore.aspx?item=1")
                            .build();
                    Response response_3 = okHttpClient.newCall(redirect_3).execute();
                    final String fdtydata = response_3.body().string(); // string方法只能使用一次
//                    System.out.println(fdtydata);
//                    System.out.println(response_3.headers().toString());
                    // 此时得到了最终数据需要处理
                    parse_data(fdtydata);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //放在UI线程弹Toast
                            Toast.makeText(MainActivity.this,"获取数据成功",Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //放在UI线程弹Toast
                            Toast.makeText(MainActivity.this, String.format("未处理情况 response code: %d", response.code()),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
        } else {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null&&networkInfo.length>0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private void parse_data(String html) {
        ArrayList<String> info = new ArrayList<String>();
        ArrayList<String> rec = new ArrayList<String>();

        Document doc = Jsoup.parse(html);

        // phase nickname
        String nickname = doc.getElementById("lblname").text();
        sh.save_nickname(nickname);

        // phase info
        Element table = doc.getElementById("lblitem").nextElementSibling().child(0);
        Element row0 = table.child(0);
        Element row1 = table.child(1);
        Element row2 = table.child(2);

        info.add("　　早操："+row0.child(1).text());
        info.add("课外活动："+row0.child(3).text());
        info.add("　晚锻炼："+row0.child(5).text());
        info.add("　　违规："+row0.child(7).text());

        info.add("仰卧起坐："+row1.child(1).text());
        info.add("　中长跑："+row1.child(3).text());
        info.add("立定跳远："+row1.child(5).text());
        info.add("引体向上："+row1.child(7).text());

        info.add("周末上午："+row2.child(1).text());
        sh.save_info(info);

        // phase rec
        Elements records = doc.getElementById("lbldet").nextElementSibling().child(0).children();
        for (Element record : records) {
            String nowrec = String.format("[%10s %s]%s %s",record.child(2).text(),record.child(3).text(),record.child(1).text(),record.child(4).text());
            rec.add(nowrec);
        }
        sh.save_rec(rec);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                //放在UI线程弹Toast
                show_data();
            }
        });
    }
}
