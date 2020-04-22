package com.bdqn.visitshop.bean;

import java.io.Serializable;

public class TrainDetailResult implements Serializable{
    private int code;//请求返回码，0为成功
    private String msg;//请求放回信息
    private Body body;//请求培训详情信息

    public class Body implements Serializable{
        private int score;
        private String feedback;
        private String imgPath;
        private String imgName;

        @Override
        public String toString() {
            return "Body{" +
                    "score=" + score +
                    ", feedback='" + feedback + '\'' +
                    ", imgPath='" + imgPath + '\'' +
                    ", imgName='" + imgName + '\'' +
                    '}';
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public String getFeedback() {
            return feedback;
        }

        public void setFeedback(String feedback) {
            this.feedback = feedback;
        }

        public String getImgPath() {
            return imgPath;
        }

        public void setImgPath(String imgPath) {
            this.imgPath = imgPath;
        }

        public String getImgName() {
            return imgName;
        }

        public void setImgName(String imgName) {
            this.imgName = imgName;
        }
    }

    @Override
    public String toString() {
        return "TrainDetailResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", body=" + body +
                '}';
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

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}
