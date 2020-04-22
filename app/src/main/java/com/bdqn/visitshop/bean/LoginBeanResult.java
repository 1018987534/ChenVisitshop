package com.bdqn.visitshop.bean;


public class LoginBeanResult {


    private int code;
    private String msg;

    private BodyBean body;

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

    public BodyBean getBody() {
        return body;
    }

    public void setBody(BodyBean body) {
        this.body = body;
    }

    public static class BodyBean {
        private String userid;
        private String job;
        private String nickname;
        private String phonenum;
        private int sex;
        private String img;
        private String registdate;
        private String area;

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getJob() {
            return job;
        }

        public void setJob(String job) {
            this.job = job;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getPhonenum() {
            return phonenum;
        }

        public void setPhonenum(String phonenum) {
            this.phonenum = phonenum;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getRegistdate() {
            return registdate;
        }

        public void setRegistdate(String registdate) {
            this.registdate = registdate;
        }

		public String getArea() {
			return area;
		}

		public void setArea(String area) {
			this.area = area;
		}
        
    }
}
