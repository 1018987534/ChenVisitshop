package com.bdqn.visitshop.bean;

import java.util.List;

public class FeedBackResult {
    private int code;
    private String msg;
    private List<BodyBean> body;

  public static class BodyBean {
      private Integer feedbackType;
      private String replyContent;
      private String img;
      private String insertDt;

      public String getInsertDt() {
          return insertDt;
      }

      public void setInsertDt(String insertDt) {
          this.insertDt = insertDt;
      }

      public String getImg() {
          return img;
      }

      public void setImg(String img) {
          this.img = img;
      }

      public Integer getFeedbackType() {
          return feedbackType;
      }

      public void setFeedbackType(Integer feedbackType) {
          this.feedbackType = feedbackType;
      }

      public String getReplyContent() {
          return replyContent;
      }

      public void setReplyContent(String replyContent) {
          this.replyContent = replyContent;
      }
  }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<BodyBean> getBody() {
        return body;
    }

    public void setBody(List<BodyBean> body) {
        this.body = body;
    }
}
