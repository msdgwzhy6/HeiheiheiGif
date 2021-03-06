package com.boredream.hhhgif.entity;

import com.boredream.hhhgif.base.BaseEntity;

public class Comment extends BaseEntity {
    /**
     * 所属动态图id
     */
    private String gifId;

    /**
     * 发送用户, Pointer or User
     */
    private User user;

    /**
     * 评论内容
     */
    private String content;

    public String getGifId() {
        return gifId;
    }

    public void setGifId(String gifId) {
        this.gifId = gifId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
