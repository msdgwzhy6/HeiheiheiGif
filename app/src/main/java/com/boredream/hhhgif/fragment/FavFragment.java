package com.boredream.hhhgif.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.boredream.bdcodehelper.adapter.LoadMoreAdapter;
import com.boredream.bdcodehelper.utils.DialogUtils;
import com.boredream.bdcodehelper.utils.DisplayUtils;
import com.boredream.bdcodehelper.utils.TitleBuilder;
import com.boredream.bdcodehelper.view.GridSpacingDecorator;
import com.boredream.hhhgif.R;
import com.boredream.hhhgif.adapter.FavGifInfoAdapter;
import com.boredream.hhhgif.adapter.GifLoadMoreAdapter;
import com.boredream.hhhgif.base.BaseEntity;
import com.boredream.hhhgif.base.BaseFragment;
import com.boredream.hhhgif.constants.CommonConstants;
import com.boredream.hhhgif.entity.Gif;
import com.boredream.hhhgif.entity.ListResponse;
import com.boredream.hhhgif.entity.User;
import com.boredream.hhhgif.net.HttpRequest;
import com.boredream.hhhgif.net.ObservableDecorator;
import com.boredream.hhhgif.net.SimpleSubscriber;
import com.boredream.hhhgif.utils.UserInfoKeeper;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class FavFragment extends BaseFragment implements FavGifInfoAdapter.OnRemoveGifFavListener {
    private View view;
    private SwipeRefreshLayout srl_fav;
    private RecyclerView rv_fav;
    private View ll_no_login;
    private Button btn_login;

    private GifLoadMoreAdapter adapter;
    private List<Gif> infos = new ArrayList<>();

    private int currentPage = 1;
    private User currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = View.inflate(activity, R.layout.frag_fav, null);
        initView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        currentUser = UserInfoKeeper.getCurrentUser();
        srl_fav.setVisibility(currentUser == null ? View.GONE : View.VISIBLE);

        if (currentUser != null && infos.size() == 0 && !srl_fav.isRefreshing()) {
            srl_fav.post(new Runnable() {
                @Override
                public void run() {
                    srl_fav.setRefreshing(true);
                }
            });
            loadData(1);
        }
    }

    private void initView() {
        new TitleBuilder(view).setTitleText("首页");

        srl_fav = (SwipeRefreshLayout) view.findViewById(R.id.srl_fav);
        rv_fav = (RecyclerView) view.findViewById(R.id.rv_fav);
        ll_no_login = view.findViewById(R.id.ll_no_login);
        btn_login = (Button) view.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoKeeper.checkLogin(activity);
            }
        });

        initRecyclerView();
    }

    private void initRecyclerView() {
        FavGifInfoAdapter gifInfoAdapter = new FavGifInfoAdapter(activity, infos);
        gifInfoAdapter.setOnRemoveGifFavListener(this);
        adapter = new GifLoadMoreAdapter(rv_fav, gifInfoAdapter, new LoadMoreAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadData(currentPage + 1);
            }
        });
        rv_fav.setAdapter(adapter);
        srl_fav.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(1);
            }
        });

        final StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rv_fav.setLayoutManager(staggeredGridLayoutManager);
        rv_fav.addItemDecoration(new GridSpacingDecorator(DisplayUtils.dp2px(activity, 8)));
    }

    @Override
    public void onRemoveGifFav(final int position) {
        DialogUtils.showCommonDialog(activity, "确认删除该收藏动态图？",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeGifFav(position);
                    }
                });
    }

    private void removeGifFav(int position) {
        final Gif gifInfo = infos.get(position);

        showProgressDialog();
        Observable<BaseEntity> observable = HttpRequest.removeFavGif(gifInfo.getObjectId());
        ObservableDecorator.decorate(activity, observable)
                .subscribe(new SimpleSubscriber<BaseEntity>(activity) {
                    @Override
                    public void onNext(BaseEntity entity) {
                        dismissProgressDialog();

                        infos.remove(gifInfo);
                        adapter.notifyDataSetChanged();

                        showToast("取消收藏成功");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);

                        dismissProgressDialog();

                        showToast("取消收藏失败");
                    }
                });
    }

    private void loadData(final int page) {
        Observable<ListResponse<Gif>> observable = HttpRequest.getFavGifs(currentUser.getObjectId(), currentPage);
        ObservableDecorator.decorate(activity, observable)
                .subscribe(new SimpleSubscriber<ListResponse<Gif>>(activity) {
                    @Override
                    public void onNext(ListResponse<Gif> gifInfos) {
                        srl_fav.setRefreshing(false);

                        if (page == 1) {
                            infos.clear();
                        }

                        if (gifInfos.getResults().size() > 0) {
                            currentPage = page;
                            infos.addAll(gifInfos.getResults());
                        }

                        adapter.setStatus(gifInfos.getResults().size() == CommonConstants.COUNT_OF_PAGE
                                ? LoadMoreAdapter.STATUS_HAVE_MORE : LoadMoreAdapter.STATUS_LOADED_ALL);

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        srl_fav.setRefreshing(false);
                    }
                });
    }

}
