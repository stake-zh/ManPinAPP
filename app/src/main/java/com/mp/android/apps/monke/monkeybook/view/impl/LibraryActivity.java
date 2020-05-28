//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.mp.android.apps.monke.monkeybook.view.impl;

import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mp.android.apps.R;
import com.mp.android.apps.monke.monkeybook.base.MBaseActivity;
import com.mp.android.apps.monke.monkeybook.bean.LibraryBean;
import com.mp.android.apps.monke.monkeybook.bean.LibraryNewBookBean;
import com.mp.android.apps.monke.monkeybook.bean.SearchBookBean;
import com.mp.android.apps.monke.monkeybook.presenter.ILibraryPresenter;
import com.mp.android.apps.monke.monkeybook.presenter.impl.BookDetailPresenterImpl;
import com.mp.android.apps.monke.monkeybook.presenter.impl.LibraryPresenterImpl;
import com.mp.android.apps.monke.monkeybook.utils.DensityUtil;
import com.mp.android.apps.monke.monkeybook.view.ILibraryView;
import com.mp.android.apps.monke.monkeybook.widget.libraryview.LibraryKindBookListView;
import com.mp.android.apps.monke.monkeybook.widget.libraryview.LibraryNewBooksView;
import com.mp.android.apps.monke.monkeybook.widget.refreshview.BaseRefreshListener;
import com.mp.android.apps.monke.monkeybook.widget.refreshview.RefreshProgressBar;
import com.mp.android.apps.monke.monkeybook.widget.refreshview.RefreshScrollView;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LibraryActivity extends MBaseActivity<ILibraryPresenter> implements ILibraryView {
    private RefreshScrollView rscvContent;
    private RefreshProgressBar rpbProgress;

    private LinearLayout llContent;
    private ImageButton ibReturn;
    private FrameLayout flSearch;

    private Animation animIn;
    private Animation animOut;

    private LinearLayout kindLl;

    private LibraryNewBooksView lavHotauthor;
    private LibraryKindBookListView lkbvKindbooklist;

    @Override
    protected ILibraryPresenter initInjector() {
        return new LibraryPresenterImpl();
    }

    @Override
    protected void onCreateActivity() {
        setContentView(R.layout.activity_library);
    }

    @Override
    protected void firstRequest() {
        llContent.startAnimation(animIn);
    }

    @Override
    protected void initData() {
        animIn = AnimationUtils.loadAnimation(this, R.anim.anim_act_importbook_in);
        animOut = AnimationUtils.loadAnimation(this, R.anim.anim_act_importbook_out);
    }

    @Override
    protected void bindView() {
        rscvContent = (RefreshScrollView) findViewById(R.id.rscv_content);
        rpbProgress = (RefreshProgressBar) findViewById(R.id.rpb_progress);
        rscvContent.setRpb(rpbProgress);

        llContent = (LinearLayout) findViewById(R.id.ll_content);
        ibReturn = (ImageButton) findViewById(R.id.ib_return);
        flSearch = (FrameLayout) findViewById(R.id.fl_search);

        kindLl = (LinearLayout) findViewById(R.id.kind_ll);

        mPresenter.getKinds();

        lavHotauthor = (LibraryNewBooksView) findViewById(R.id.lav_hotauthor);
        lkbvKindbooklist = (LibraryKindBookListView) findViewById(R.id.lkbv_kindbooklist);
    }

    private void refreshKind(LinkedHashMap<String, String> linkedHashMap) {
        int columnCout = 4;
        kindLl.removeAllViews();
        Iterator iterator = linkedHashMap.entrySet().iterator();
        int temp = 0;
        LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout = null;
        LinearLayout.LayoutParams tvLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tvLp.weight = 1;
        while (iterator.hasNext()) {
            final Map.Entry<String, String> resultTemp = (Map.Entry<String, String>) iterator.next();
            if (temp % columnCout == 0) {
                linearLayout = new LinearLayout(this);
                linearLayout.setLayoutParams(l);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                kindLl.addView(linearLayout);
            }
            TextView textView = new TextView(this);
            textView.setLayoutParams(tvLp);
            textView.setText(resultTemp.getKey());

            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(14);
            textView.setPadding(0, DensityUtil.dp2px(this, 5), 0, DensityUtil.dp2px(this, 5));
            textView.setLines(1);
            textView.setTextColor(getResources().getColorStateList(R.color.selector_kind_tv_color));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChoiceBookActivity.startChoiceBookActivity(LibraryActivity.this, resultTemp.getKey(), resultTemp.getValue());
                }
            });

            linearLayout.addView(textView);
            temp++;
        }
        int viewCount = linkedHashMap.size() % columnCout == 0 ? 0 : (columnCout - linkedHashMap.size() % columnCout);
        for (int i = 0; i < viewCount; i++) {
            View v = new View(this);
            v.setLayoutParams(tvLp);
            linearLayout.addView(v);
        }
    }

    @Override
    protected void bindEvent() {
        animIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rscvContent.startRefresh();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                LibraryActivity.super.finish();
                overridePendingTransition(0, 0);
                isExiting = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        ibReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        flSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击搜索
                startActivityByAnim(new Intent(LibraryActivity.this, SearchActivity.class), flSearch, "to_search", android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        rscvContent.setBaseRefreshListener(new BaseRefreshListener() {
            @Override
            public void startRefresh() {
                mPresenter.getLibraryData();
            }
        });
    }

    private Boolean isExiting = false;

    @Override
    public void finish() {
        if (!isExiting) {
            isExiting = true;
            llContent.startAnimation(animOut);
        }
    }

    @Override
    public void updateUI(final LibraryBean library) {
        //获取数据后刷新UI
        lavHotauthor.updateData(library.getLibraryNewBooks(), new LibraryNewBooksView.OnClickAuthorListener() {
            @Override
            public void clickNewBook(LibraryNewBookBean libraryNewBookBean) {
                SearchBookBean searchBookBean = new SearchBookBean();
                searchBookBean.setName(libraryNewBookBean.getName());
                searchBookBean.setNoteUrl(libraryNewBookBean.getUrl());
                searchBookBean.setTag(libraryNewBookBean.getTag());
                searchBookBean.setOrigin(libraryNewBookBean.getOrgin());
                Intent intent = new Intent(LibraryActivity.this, BookDetailActivity.class);
                intent.putExtra("from", BookDetailPresenterImpl.FROM_SEARCH);
                intent.putExtra("data", searchBookBean);
                startActivityByAnim(intent, android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        lkbvKindbooklist.updateData(library.getKindBooks(), new LibraryKindBookListView.OnItemListener() {
            @Override
            public void onClickMore(String title, String url) {
                ChoiceBookActivity.startChoiceBookActivity(LibraryActivity.this, title, url);
            }

            @Override
            public void onClickBook(ImageView animView, SearchBookBean searchBookBean) {
                Intent intent = new Intent(LibraryActivity.this, BookDetailActivity.class);
                intent.putExtra("from", BookDetailPresenterImpl.FROM_SEARCH);
                intent.putExtra("data", searchBookBean);
                startActivityByAnim(intent, animView, "img_cover", android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    @Override
    public void finishRefresh() {
        rscvContent.finishRefresh();
    }

    @Override
    public void updateNav(LinkedHashMap<String, String> linkedList) {
        refreshKind(linkedList);
    }
}