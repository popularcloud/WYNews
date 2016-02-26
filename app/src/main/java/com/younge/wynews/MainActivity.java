package com.younge.wynews;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.younge.wynews.adpter.MainFragmentPageAdapter;
import com.younge.wynews.fragment.ArticleFragment;
import com.younge.wynews.fragment.ForumFragment;
import com.younge.wynews.fragment.GameFragment;
import com.younge.wynews.fragment.VideoFragment;
import com.younge.wynews.utils.SystemBarTintManager;
import com.younge.wynews.view.TipsToast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private ViewPager main_viewPager;
    private RadioGroup rgp;
    private List<Fragment> fragemnts = new ArrayList<>();
    private MainFragmentPageAdapter adapter;
    //退出时间
    private long exitTime = 0;
    private TipsToast tipsToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWindow();
        initView();
        initData();
        setAdapter();
        setListener();
    }

    /**
     * 初始化窗体布局
     */
    private void initWindow() {
        SystemBarTintManager tintManager;
        //沉浸式状态栏只有4.4以上的版本才能使用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(getResources().getColor(R.color.colorBackground));
            tintManager.setStatusBarTintEnabled(true);
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        main_viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        //设置viewpage预加载页数 默认只会加载前后两边的页的数据
        main_viewPager.setOffscreenPageLimit(4);
        rgp = (RadioGroup) findViewById(R.id.main_rgp);
        //设置默认第一个为选中状态
        RadioButton rb = (RadioButton) rgp.getChildAt(0);
        rb.setChecked(true);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        ArticleFragment airticle_fragemnt = new ArticleFragment();
        ForumFragment forum_Fragment = new ForumFragment();
        GameFragment game_Fragment = new GameFragment();
        VideoFragment video_Fragment = new VideoFragment();
        fragemnts.add(airticle_fragemnt);//文章
        fragemnts.add(video_Fragment);//视频
        fragemnts.add(forum_Fragment);//论坛
        fragemnts.add(game_Fragment);//游戏
    }

    /**
     * 设置适配器
     */
    private void setAdapter() {
        //实例化适配器
        adapter = new MainFragmentPageAdapter(getSupportFragmentManager(), fragemnts);
        //设置适配器
        main_viewPager.setAdapter(adapter);
    }

    private void setListener() {
        //viewPager的滑动监听
        main_viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //获取当前位置的RadioButton
                RadioButton rb = (RadioButton) rgp.getChildAt(position);
                //设置为true
                rb.setChecked(true);
            }
        });
        //RadioGroup的事件监听
        rgp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int index = 0; index < rgp.getChildCount(); index++) {
                    RadioButton rb = (RadioButton) rgp.getChildAt(index);
                    if (rb.isChecked()) {
                        main_viewPager.setCurrentItem(index, false);
                        break;
                    }
                }
            }
        });
    }

    /**
     * 按两次退出应用
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                /*Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();*/
                showTips(R.mipmap.tips_smile, "再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 自定义toast
     *
     * @param iconResId 图片
     * @param tips      提示文字
     */
    private void showTips(int iconResId, String tips) {
        if (tipsToast != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                tipsToast.cancel();
            }
        } else {
            tipsToast = TipsToast.makeText(MainActivity.this.getApplication()
                    .getBaseContext(), tips, TipsToast.LENGTH_SHORT);
        }
        tipsToast.show();
        tipsToast.setIcon(iconResId);
        tipsToast.setText(tips);
    }
}
