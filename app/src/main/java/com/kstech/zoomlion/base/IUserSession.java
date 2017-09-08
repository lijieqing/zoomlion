package com.kstech.zoomlion.base;


/**
 * The type Login session.
 *
 * @author lijie 登陆会话接口
 */
public interface IUserSession {

    /**
     * 校验平板是否已在服务器注册
     *
     * @param padId   平板唯一标识ID
     * @param url     对应的请求校验URL
     * @param handler 用来消息发送 android 对应android.os.Handler类
     */
    void checkPadPriority(String padId, String url, Object handler);


    /**
     * 用户登录
     *
     * @param name              用户名
     * @param password          密码
     * @param measureTerminalId 测量终端ID
     * @param url               登陆URL
     * @param handler           用来消息发送 android 对应android.os.Handler类
     */
    void login(String name, String password, String measureTerminalId, String url, Object handler);


    /**
     * 获取可用测量终端
     *
     * @param url     获取可用测量终端URL
     * @param handler 用来消息发送 android 对应android.os.Handler类
     */
    void getAvailableMeasureTerminals(String url, Object handler);


    /**
     * 登陆成功的用户保存用户名到本地
     *
     * @param user 当前已登陆用户
     */
    void userInfoLocalSave(UserBean user);


    /**
     * 检验员身份认证
     *
     * @param name    检验员名称
     * @param pass    检验员密码
     * @param url     检验员验证所需URL
     * @param handler 用来消息发送 android 对应android.os.Handler类
     */
    void checkerAuthenticate(String name, String pass, String url, Object handler);


    /**
     * 用户信息修改
     *
     * @param user    用户信息封装类
     * @param url     用户更新URL
     * @param handler 用来消息发送 android 对应android.os.Handler类
     */
    void updateUserInfo(UserBean user, String url, Object handler);
}
