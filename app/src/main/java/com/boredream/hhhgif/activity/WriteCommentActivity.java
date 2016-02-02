package com.boredream.hhhgif.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.boredream.hhhgif.R;
import com.boredream.hhhgif.base.BaseActivity;
import com.boredream.hhhgif.base.BaseEntity;
import com.boredream.hhhgif.entity.Comment;
import com.boredream.hhhgif.entity.GifInfo;
import com.boredream.hhhgif.entity.User;
import com.boredream.hhhgif.net.HttpRequest;
import com.boredream.hhhgif.net.ObservableDecorator;
import com.boredream.hhhgif.utils.UserInfoKeeper;

import rx.Observable;
import rx.functions.Action1;

public class WriteCommentActivity extends BaseActivity {

    private GifInfo gif;
    private EditText et_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_comment);
        initExtras();
        initView();
    }

    private void initExtras() {
        gif = (GifInfo) getIntent().getSerializableExtra("gif");
    }

    private void initView() {
        initBackTitle("写评论")
                .setRightText("完成")
                .setRightOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        submit();
                    }
                });

        et_content = (EditText) findViewById(R.id.et_content);
    }

    private void submit() {
        // validate
        String content = et_content.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "评论内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        User currentUser = UserInfoKeeper.getCurrentUser();
        Comment comment = new Comment();
        comment.setGifId(gif.getObjectId() + "1111");
        comment.setUser(currentUser);
        comment.setContent(content);

        showProgressDialog();
        Observable<BaseEntity> observable = HttpRequest.addGifComment(this, comment);
        ObservableDecorator.decorate(this, observable)
                .subscribe(new Action1<BaseEntity>() {
                    @Override
                    public void call(BaseEntity entity) {
                        dismissProgressDialog();
                        showToast("评论成功");

                        commentSuccess();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        dismissProgressDialog();
                    }
                });
    }

    private void commentSuccess() {
        Intent intent = new Intent();
        setResult(RESULT_OK);
    }
}