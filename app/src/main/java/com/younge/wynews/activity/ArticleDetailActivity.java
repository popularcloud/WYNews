package com.younge.wynews.activity;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.younge.wynews.R;
import com.younge.wynews.entity.Detail;
import com.younge.wynews.utils.DateUtils;
import com.younge.wynews.utils.HttpAdress;
import com.younge.wynews.utils.HttpUtils;
import com.younge.wynews.utils.SystemBarTintManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Allen Lake on 2016/2/27 0027.
 * 文章详情页面
 */
public class ArticleDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView comment_web;
    private EditText ed_comment;
    private Button comment_btn;
    private ProgressDialog dialog;
    private Toolbar toolbar;

    private String body;
    private String id;
    private String typeid;
    private String title;//标题
    private String writer;//作者
    private String senddate;//发布时间
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initWindow();
        initView();
        id = getIntent().getStringExtra("id");
        typeid = getIntent().getStringExtra("typeid");
        String url = String.format(HttpAdress.ChapterContent_URL, id, typeid);//文章详情请求地址    //下载网络数据
        //网页加载中，打开进度对话框
        openDialog(0);
        HttpUtils.downLoadData(url, new HttpUtils.OnFetchDataListener() {
            @Override
            public void OnFetch(String url, byte[] data) {
                String json = new String(data);
                //json解析
                Detail detail = new Gson().fromJson(json, Detail.class);
                body = detail.getBody();//文章内容
                title = detail.getTitle();//文章标题
                writer = detail.getWriter();//文章作者
                senddate = detail.getSenddate();//文章发布时间
                initData();
            }
        });

        initListener();
    }

    private void initWindow() {
        SystemBarTintManager tintManager;
        //由于沉浸式状态栏需要在Android4.4.4以上才能使用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(getResources().getColor(R.color.colorBackground));
            tintManager.setStatusBarTintEnabled(true);
        }
    }

    private void initView() {
        comment_web = (WebView) findViewById(R.id.coment_web);
        ed_comment = (EditText) findViewById(R.id.ed_comment);
        comment_btn = (Button) findViewById(R.id.comment_btn);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //2.替代
        setSupportActionBar(toolbar);
        //加载背景颜色
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorBackground)));
        //设置显示返回上一级的图标
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //设置标题
        getSupportActionBar().setTitle("文章详情");
        //设置标题栏字体颜色
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.backup));
    }

    //初始化数据
    private void initData() {
        //启用支持javascript
        WebSettings settings = comment_web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowFileAccess(true);
        settings.setLoadWithOverviewMode(true);
        settings.setTextSize(WebSettings.TextSize.LARGEST);//设置字体大小
        settings.setDefaultTextEncodingName("utf-8");//设置默认编码格式
//        //自适应屏幕
        settings.setUseWideViewPort(true);
//        settings.setLoadWithOverviewMode(true);
        comment_web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    //网页加载完成，关闭对话框
                    closeDialog();
                } else {

                }
            }
        });
        //加载网络资源
        if (body != null) {
            try {
                //由于body的数据进行了URLEncode编码，所以需要我们再进行URLDecoder解码
                //否则只能显示图片
                String decode = URLDecoder.decode(body, "utf-8");
                Log.i("--------->decode", "" + decode);
                String date = DateUtils.dateFromat(senddate);//发布时间
                String html = "<html><body>"
                        + "<h3>"
                        + title
                        + "</h3>"
                        + "<p>"
                        + "作者:" + writer
                        + "&nbsp&nbsp"
                        + "发布时间:" + date
                        + "</p>"
                        + "<style>"
//                                       +"body{text-align:center}"
                        + "img{width:100%;height:auto;}"//自定义样式，设置图片显示大小
                        + "</style>"
                        + decode
                        + "</body></html>";
                //使用这种方法，前面添加网站的地址 http://www.3dmgame.com，可以解决，有些图片前面乜有完整请求地址的问题
                comment_web.loadDataWithBaseURL(HttpAdress.DMGEAME_URL, html, "text/html", "charset=UTF-8", null);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.i("body------>>>>>>>", "" + body);
            //设置在同一个webview中打开新的网页
            comment_web.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                    view.loadUrl(url);
                    return true;
                }
            });
        }

    }

    //设置事件监听
    private void initListener() {
        //toolbard的返回按钮事件监听
        toolbar.setNavigationOnClickListener(this);
        //评论提交按钮
        comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentContent = ed_comment.getText().toString();//获取到输入框中的评论内容
                if (TextUtils.isEmpty(commentContent)) {
                    Toast.makeText(ArticleDetailActivity.this, "评论内容不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                final Map<String, String> params = new HashMap<String, String>();
                params.put("aid", id);
                params.put("msg", commentContent);
                HttpUtils.submitPostData(params, "utf-8", new HttpUtils.OnFetchResponseListener() {
                    @Override
                    public void OnFechResponse(String reponse) {
                        Log.i("responseResult====>", reponse);
                        //json解析，获取到响应状态码
                        try {
                            JSONObject object = new JSONObject(reponse);
                            String code = object.getString("code");//响应状态码
                            if ("1".equals(code)) {
                                ed_comment.setText("");//清空输入框
                                Toast.makeText(ArticleDetailActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ArticleDetailActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });


             /*   Intent intent = new Intent(ArticleDetailActivity.this, CommentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                bundle.putString("typeid", typeid);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);*/
            }
        });
    }

    //关闭进度对话框
    private void closeDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    //显示进度对话框
    private void openDialog(int newProgress) {
        if (dialog == null) {
            dialog = new ProgressDialog(ArticleDetailActivity.this);
            dialog.setProgress(newProgress);
            dialog.setMessage("文章详情加载中。。。");
            dialog.show();
        } else {
            dialog.setProgress(newProgress);
        }
    }

    //toolbar事件监听方法
    @Override
    public void onClick(View v) {
        //返回上一页
        finish();
    }
}
