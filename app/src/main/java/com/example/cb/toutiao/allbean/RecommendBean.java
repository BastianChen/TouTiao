package com.example.cb.toutiao.allbean;

public class RecommendBean {
    private String title;
    private String img;
    private String img2;
    private String img3;
    private String source;//文章出处
    private boolean hasimg;//记录是否有图片，若为true则有图片
    private String commentCouont;//文章评论数
    private String shareUrl;//
    private String groupId;//文章url

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String group_id) {
        this.groupId = group_id;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String share_url) {
        this.shareUrl = share_url;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getImg3() {
        return img3;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
    }

    public boolean isHasimg() {
        return hasimg;
    }

    public void setHasimg(boolean hasimg) {
        this.hasimg = hasimg;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCommentCouont() {
        return commentCouont;
    }

    public void setCommentCouont(String comment_couont) {
        this.commentCouont = comment_couont;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
