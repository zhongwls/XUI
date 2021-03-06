/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xuexiang.xuidemo.fragment.components.refresh;

import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.adapter.SmartRecyclerAdapter;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.statelayout.StatefulLayout;
import com.xuexiang.xuidemo.R;
import com.xuexiang.xuidemo.base.BaseFragment;
import com.xuexiang.xutil.tip.ToastUtils;

import java.util.Arrays;
import java.util.Collection;

import butterknife.BindView;

import static android.R.layout.simple_list_item_2;
import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

/**
 * @author xuexiang
 * @since 2018/12/7 下午3:30
 */
@Page(name = "刷新状态布局")
public class RefreshStatusLayoutFragment extends BaseFragment {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.ll_stateful)
    StatefulLayout mLlStateful;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    private SmartRecyclerAdapter<String> mAdapter;

    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_refresh_status_layout;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter = new SmartRecyclerAdapter<String>(simple_list_item_2) {
            @Override
            protected void onBindViewHolder(SmartViewHolder holder, String v, int position) {
                holder.text(android.R.id.text1, getString(R.string.item_example_number_title, position));
                holder.text(android.R.id.text2, getString(R.string.item_example_number_abstract, position));
                holder.textColorId(android.R.id.text2, R.color.xui_config_color_light_blue_gray);
            }
        });
        //下拉刷新
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                mRefreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Status status = getRefreshStatus();
                        switch(status) {
                            case SUCCESS:
                                mAdapter.refresh(initData());
                                mRefreshLayout.resetNoMoreData();//setNoMoreData(false);
                                mLlStateful.showContent();
                                mRefreshLayout.setEnableLoadMore(true);
                                break;
                            case EMPTY:
                                mLlStateful.showEmpty();
                                mRefreshLayout.setEnableLoadMore(false);
                                break;
                            case ERROR:
                                showError();
                                break;
                            case NO_NET:
                                showOffline();
                                break;
                            default:
                                break;
                        }
                        refreshLayout.finishRefresh();

                    }
                }, 2000);
            }
        });
        //上拉加载
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mAdapter.getItemCount() > 30) {
                            ToastUtils.toast("数据全部加载完毕");
                            refreshLayout.finishLoadMoreWithNoMoreData();//将不会再次触发加载更多事件
                        } else {
                            mAdapter.loadMore(initData());
                            refreshLayout.finishLoadMore();
                        }
                    }
                }, 2000);
            }
        });
        mRefreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
    }

    private void showOffline() {
        mLlStateful.showOffline(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRefreshLayout.autoRefresh();
            }
        });
        mRefreshLayout.setEnableLoadMore(false);
    }

    private void showError() {
        mLlStateful.showError(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRefreshLayout.autoRefresh();
            }
        });
        mRefreshLayout.setEnableLoadMore(false);
    }

    private Collection<String> initData() {
        return Arrays.asList("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
    }


    private enum Status {
        SUCCESS,
        EMPTY,
        ERROR,
        NO_NET,
    }

    private Status getRefreshStatus() {
        int status = (int) (Math.random() *  10);
        if (status % 2 == 0) {
            return Status.SUCCESS;
        } else if (status % 3 == 0) {
            return Status.EMPTY;
        } else if (status % 5 == 0) {
            return Status.ERROR;
        } else {
            return Status.NO_NET;
        }
    }
}
