package com.example.dps;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.dps.login.SaveSharedPreference;
import com.example.dps.vo.LoginVo;
import com.royrodriguez.transitionbutton.TransitionButton;
import com.royrodriguez.transitionbutton.utils.WindowUtils;

import org.json.JSONObject;

import java.security.cert.CertificateException;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private TransitionButton login_button;
    Button join_button;
    Retrofit retrofit;
    RetrofitAPI retrofitAPI;
    EditText userid,userpwd;
    CheckBox auto_login;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //retrofit 선언
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create()) // converter 선언
                .baseUrl(retrofitAPI.REGIST_URL)
                .client(getUnsafeOkHttpClient().build())
                .build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);
        auto_login = findViewById(R.id.auto_login);
        login_button = findViewById(R.id.login_button);
        userid=findViewById(R.id.userID);
        userpwd=findViewById(R.id.userPwd);

        //로그인 버튼 클릭
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                login_button.startAnimation();
                String user_id = userid.getText().toString();
                String user_pwd = userpwd.getText().toString();
//               CSRF 토큰 생성
                String csrfToken = UUID.randomUUID().toString();
//                System.out.println(csrfToken);
                //파라미터 담기
                LoginVo vo = new LoginVo(user_id,user_pwd);

                Call<ResponseBody> call = retrofitAPI.getLogin(csrfToken ,vo);
//                Handler handler = new Handler();

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try{
                            ResponseBody body = response.body();
//                            string 형채의 json 데이터
                            String jsonstr = body.string();

//                          String to JsonObject
                            JSONObject jsonObj = new JSONObject(jsonstr);

                            String message = (String) jsonObj.get("message");
                            Boolean success = (boolean) jsonObj.get("success");
//                            로그인 성공
                            if ( success == true){
                                // 인텐트 선언 : 현재 액티비티, 넘어갈 액티비티
                                // 사용자 아이디

                                String user_id = (String) jsonObj.get("user_id");
                                System.out.println("로그인 성공");
                                //mqtt_pub user_id
//                                mqtt_pub(user_id);
                                //자동 로그인
                                if (auto_login.isChecked()){
                                    SaveSharedPreference.setUserID(MainActivity.this, user_id);
                                }
                                login_button.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
                                    @Override
                                    public void onAnimationStopEnd() {
                                        Intent intent = new Intent(MainActivity.this, AnalysisActivity.class);
                                        intent.putExtra("user_id", user_id);
                                        // 인텐트 실행
                                        startActivity(intent);
                                        finish();
                                    }
                                });

                            }else{
//                                아이디가 없다
                                if(message.equals("아이디")){
//                                    System.out.println(message+"가 없습니다.");
                                    Toast.makeText(MainActivity.this, message+"가 없습니다.", Toast.LENGTH_SHORT).show();
                                    userid.requestFocus();
                                    login_button.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                                }else {
//                                    비밀번호가 일치하지 않는다.
//                                    System.out.println(message+"가 일치하지 않습니다.");
                                    Toast.makeText(MainActivity.this, message+"가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                                    userpwd.requestFocus();
                                    login_button.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                                }
                            }
                            System.out.println(body.string());
                        }catch (Exception e){

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                        System.out.println("실패");
                        Toast.makeText(MainActivity.this, "서버와 통신이 원할하지 않습니다.", Toast.LENGTH_SHORT).show();

                        System.out.println(t.toString());
                    }
                });

            }
        });

        //  회원가입 버튼 클릭
        join_button = findViewById(R.id.join_button);
        join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // 인텐트 선언 : 현재 액티비티, 넘어갈 액티비티
                Intent intent = new Intent(MainActivity.this, JoinActivity.class);
                // 인텐트 실행
                startActivity(intent);
            }
        });
    }
    //SSL 인증 없이 HTTPS 통과
    public static OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}