package com.ovvi.remotelocation.base;

public final class Common {

    /** 用户类型: 1-老人，2-子女 */
    public static final int type = 1;
    /** 问题列表 */
    public static final String[] question_data = new String[] {"生日", "大学校名", "父亲姓名" };
    /** 当前所选问题id */
    public static int question_Id;
    /** 添加成员状态 */
    public static final String[] member_state = new String[] { "待确认", "同意", "已拒绝" };

    public static final class code {
        /** 请求成功 */
        public static final int SUCCESS = 1;
        /** 未知错误 */
        public static final int UNKNOW_ERROR = -1;
        /** VT头验证失败 */
        public static final int VT_FAILED = 1001;
        /** token 验证失败 */
        public static final int TOKEN_FAILED = 1002;
        /** 记录已存在 */
        public static final int RECORD_ALREADY_EXIST = 1003;
        /** 记录不存在或密码错误 */
        public static final int LOGIN_FAILED = 1004;
        /** 必须结合数字和字母，并且长度必须8和12之间 */
        public static final int PASSWORD_ERROR = 1005;
        /** 参数不能为空 */
        public static final int PARAM_EMPTY = 1006;
        /** 格式不正确 */
        public static final int ILLEGAL_FORMAT = 1007;
        /** 昵称过长 */
        public static final int NICKNAME_LENGTH = 1008;
        /** 未知用户类型 */
        public static final int UNKNOW_USER_TYPE = 1010;
        /** 用户不存在 */
        public static final int USER_NOT_EXIST = 1011;
        /** 手机号没有注册 */
        public static final int PHONE_NO_REGISTER = 1012;
        /** 数据为空 */
        public static final int DATA_EMPTY = 1018;
        /** 用户没有上报位置 */
        public static final int NEVER_REPORT_LOCATION = 1019;
        /** 成员已经存在 */
        public static final int ALREADY_MEMBER = 1020;
        /** 答案不匹配 */
        public static final int ANSWER_UNMATCH = 1027;
    }

    public static final class api {
        // 建议换成你的内网IP的地址
//        public static final String base = "http://192.168.9.164:9001";
        public static final String base = "http://older.legalaxy.cn";
        /** 注册接口 */
        public static final String register = base + "/api/account/register";
        /** 登录接口 */
        public static final String login = base + "/api/account/login";
        // public static final String smsCode = "/api/user/smcode";
        /** 上报定位接口 */
        public static final String location_report = base + "/api/location/report";
        /** 主页接口 */
        public static final String home = base + "/api/user/home";
        /** 设置页接口 */
        public static final String settings = base + "/api/user/setting";
        /** 添加家庭成员接口 */
        public static final String family_add = base + "/api/user/family/add";
        /** 家庭成员列表接口 */
        public static final String family_list = base + "/api/user/family/list";
        /** 删除家成员接口 */
        public static final String family_del = base + "/api/user/family/del";
        /** 修改家成员备注名接口 */
        public static final String elabel = base + "/api/user/elabel";
        /** 上传头像接口 */
        public static final String portrait = base + "/api/upload/portrait";
        /** 获取通知消息接口 */
        public static final String notice_ask = base + "/api/user/notice/ask";
        /** 上报消息接口 */
        public static final String notice_report = base + "/api/user/notice/report";
        /** 发起远程定位接口 */
        public static final String remote_ask = base + "/api/location/remote/ask";
        /** 刷新远程定位接口 */
        public static final String remote_receive = base + "/api/location/remote/receive";
        /** 历史轨迹接口 */
        public static final String history = base + "/api/location/history";
        /** 电子栅栏接口 */
        public static final String fence = base + "/api/location/fence";
        /** 个人信息更改接口 */
        public static final String userinfo = base + "/api/user/einfo";
        /** 密保验证问题接口 */
        public static final String question = base + "/api/account/question";
        /** 找回密码接口 */
        public static final String seek_pwd = base + "/api/account/pwd";

    }

    public static final class task {
        /** 注册 */
        public static final int register = 1001;
        /** 登录 */
        public static final int login = 1002;
        // public static final int smsCode = 1003;
        /** 上报定位 */
        public static final int location_report = 1004;
        /** 主界面 */
        public static final int home = 1005;
        /** 设置界面 */
        public static final int settings = 1006;
        /** 添加家成员 */
        public static final int family_add = 1007;
        /** 家成员列表 */
        public static final int family_list = 1008;
        /** 删除家成员 */
        public static final int family_del = 1009;
        /** 修改家成员备注名 */
        public static final int elabel = 1010;
        /** 上传头像 */
        public static final int portrait = 1011;
        /** 消息请求 */
        public static final int notice_ask = 1012;
        /** 上报消息 */
        public static final int notice_report = 1013;
        /** 远程定位请求 */
        public static final int remote_ask = 1014;
        /** 刷新远程定位 */
        public static final int remote_receive = 1015;
        /** 历史轨迹 */
        public static final int history = 1016;
        /** 电子栅栏 */
        public static final int fence = 1017;
        /** 个人信息 */
        public static final int userinfo = 1018;
        /** 密保验证问题 */
        public static final int question = 1019;
        /** 找回密码 */
        public static final int seek_pwd = 1020;

    }

}
