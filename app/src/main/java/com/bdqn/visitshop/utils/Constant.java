package com.bdqn.visitshop.utils;

public class Constant {

    public static final int HttpOk = 7000;//成功
    public static final int HttpFail = 7001;//失败
    public static final int Scroll = 2;//通知图片轮播
    public static final int ForResultShopBack = 101;//新建巡店返回码
    public static final int ForResultVisitBack = 102;//新建拜访返回码
    public static final int ForResultTrainBack = 99;//培训查看返回码
    public static final int ForResultFeedBack = 103;//意见反馈
    public static final int ForResultImageBack = 104;//现场拍照
    public static final int SelectShop = 105;//店面选择
    public static final int SelectShopFailed = 109;//店面选择
    public static final int PhotoUp = 501;//培训拍照上传图片查看
    public static final int PhotoLook = 502;//培训图片查看
    public static final int ShopImgLook = 503;//巡店图片查看
    public static final int ShopImgUp = 504;//巡店图片上传查看

    public static final int HttpGetTrainInfo = 108;//获取培训信息

    public static final int TASK_ING = 0;//任务状态，进行中
    public static final int TASK_DONE = 1;// 任务状态，已完成

  // public static final String BaseUrl = "http://10.0.2.2:8080";//模拟器根接口
   // public static final String BaseUrl = "http://60.181.138.185:8080";//
    //public static final String BaseUrl = "http://192.168.0.2:8080";//私网IP
    public static final String BaseUrl = "http://49.233.137.47:8080";//公网IP
    /*public static final String Login = BaseUrl + "/user/login";//登录get
    public static final String regist = BaseUrl + "/user/register";//用户注册post
    public static final String Task = BaseUrl + "/task";//任务获取get
    public static final String Info = BaseUrl + "/info";//咨询获取get
    public static final String Announcement = BaseUrl + "/announcement";//公告获取get
    public static final String UpdateHead = BaseUrl + "/user/uploadHead";//更新用户资料头像get
    public static final String FeedBack = BaseUrl + "/feedback/add";//意见反馈post
    public static final String HistoryFeedBack = BaseUrl + "/feedback";//获取聊天记录
    public static final String UpdateUser = BaseUrl + "/user/update";//更新用户资料get
    public static final String HistroyShop = BaseUrl + "/visit/history";//历史巡店get
    public static final String ShopSelect = BaseUrl + "/visit/shop";//店面选择get
    public static final String VisitShopSubmit = BaseUrl + "/visit/add";//巡店数据提交post
    public static final String AppUpdate = BaseUrl + "/appInfo";//app更新get
    public static final String Train = BaseUrl + "/train";//培训列表接口get
    public static final String TrainDetail = BaseUrl + "/train/detail";//培训详情get
    public static final String TrainSubmit = BaseUrl + "/train/add";//培训数据提交post
    public static final String InterviewSubmit = BaseUrl + "/interview/add";//拜访提交post
    public static final String HistoryInterview = BaseUrl + "/interview";//历史拜访post*/
    //Springboot服务端接口
    public static final String Login = BaseUrl + "/chen/user/login";//登录get
    public static final String regist = BaseUrl + "/chen/user/register";//用户注册post
    public static final String Task = BaseUrl + "/chen/task";//任务获取get
    public static final String Info = BaseUrl + "/chen/info";//咨询获取get
    public static final String Announcement = BaseUrl + "/chen/announcement";//公告获取get
    public static final String UpdateHead = BaseUrl + "/chen/user/uploadHead";//更新用户资料头像get
    public static final String FeedBack = BaseUrl + "/chen/feedback/add";//意见反馈post
    public static final String HistoryFeedBack = BaseUrl + "/chen/feedback";//获取聊天记录
    public static final String UpdateUser = BaseUrl + "/chen/user/update";//更新用户资料get
    public static final String HistroyShop = BaseUrl + "/chen/visit/history";//历史巡店get
    public static final String ShopSelect = BaseUrl + "/chen/visit/shop";//店面选择get
    public static final String VisitShopSubmit = BaseUrl + "/chen/visit/add";//巡店数据提交post
    public static final String AppUpdate = BaseUrl + "/chen/appInfo";//app更新get
    public static final String Train = BaseUrl + "/chen/train";//培训列表接口get
    public static final String TrainDetail = BaseUrl + "/chen/train/detail";//培训详情get
    public static final String TrainSubmit = BaseUrl + "/chen/train/add";//培训数据提交post
    public static final String InterviewSubmit = BaseUrl + "/chen/interview/add";//拜访提交post
    public static final String HistoryInterview = BaseUrl + "/chen/interview";//历史拜访post
    //heibernate服务端接口
   // public static final String regist = BaseUrl + "/shopgo/regist";//用户注册get
   // public static final String Login = BaseUrl + "/shopgo/login";//登录get
    //public static final String FeedBack = BaseUrl + "/shopgo/feedback";//意见反馈post
   // public static final String Announcement = BaseUrl + "/shopgo/announcement";//公告获取get
   // public static final String Task = BaseUrl + "/shopgo/task";//任务获取get
   // public static final String Info = BaseUrl + "/shopgo/info";//咨询获取get
   // public static final String AppUpdate = BaseUrl + "/shopgo/appinfo";//app更新get
    //public static final String HistroyShop = BaseUrl + "/shopgo/history";//历史巡店get
    //public static final String ShopSelect = BaseUrl + "/shopgo/shop";//店面选择get
   // public static final String VisitShopSubmit = BaseUrl + "/shopgo/visitupload";//巡店数据提交post
    //public static final String Train = BaseUrl + "/shopgo/historytrain";//培训列表接口get
   // public static final String TrainDetail = BaseUrl + "/shopgo/triandetail";//培训详情get
   // public static final String TrainSubmit = BaseUrl + "/shopgo/trainupload";//培训数据提交post
  //  public static final String InterviewSubmit = BaseUrl + "/shopgo/interviewsubmit";//拜访提交post
   // public static final String HistoryInterview = BaseUrl + "/shopgo/historyinterview";//历史拜访post
    //public static final String UpdateUser = BaseUrl + "/shopgo/updateuser";//更新用户资料get
   // public static final String UpdateHead = BaseUrl + "/shopgo/uploadhead";//更新用户资料头像get

}
