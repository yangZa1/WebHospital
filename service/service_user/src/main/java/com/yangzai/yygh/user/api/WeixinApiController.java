package com.yangzai.yygh.user.api;

import com.alibaba.fastjson.JSONObject;
import com.yangzai.yygh.entity.UserInfo;
import com.yangzai.yygh.helper.JwtHelper;
import com.yangzai.yygh.result.Result;
import com.yangzai.yygh.user.service.IUserInfoService;
import com.yangzai.yygh.user.utils.ConstantWxPropertiesUtils;
import com.yangzai.yygh.user.utils.HttpClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/ucenter/wx")
public class WeixinApiController {

    @Autowired
    private IUserInfoService userInfoService;
    //生成微信扫描的二维码
    //返回生成二维码需要的参数
    @GetMapping("getLoginParam")
    @ResponseBody
    public Result genQrConnect(){

        try {
            HashMap<String, Object> map = new HashMap<>();
            map.put("appid", ConstantWxPropertiesUtils.WX_OPEN_APP_ID);
            String wxOpenRedirectUrl = ConstantWxPropertiesUtils.WX_OPEN_REDIRECT_URL;
            wxOpenRedirectUrl = URLEncoder.encode(wxOpenRedirectUrl, "utf-8");
            map.put("redirect_uri", wxOpenRedirectUrl);
            map.put("scope", "snsapi_login");
            map.put("state", System.currentTimeMillis()+"");//System.currentTimeMillis()+""
            return Result.ok(map);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return  null;
        }
    }

    /**
     * 微信扫码后 的回调的方法，得到扫描人的信息
     * code 微信授权的临时票据
     *
     * 1、使用code值，请求微信提供的地址，得到地址返回的两个值 ： access_token 和 openid
     * 2、使用access_token 和 openid 请求微信提供的地址，请求返回扫码人的相关信息
     * 3、绑定手机号，把手机号和微信扫码人的信息添加到数据库中
     *
     */
    @GetMapping("callback")
    public String callback(String code, String state){
        //1、使用code值，请求微信提供的地址，得到地址返回的两个值 ： access_token 和 openid(用户的微信的唯一标识)
        //使用code和appid以及appscrect换取access_token
        //%s 是一个占位符 表示里面需要传参
        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");

        //向url中设置参数值
        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(),
                ConstantWxPropertiesUtils.WX_OPEN_APP_ID,
                ConstantWxPropertiesUtils.WX_OPEN_APP_SECRET,
                code);

        //使用httpclient请求这个地址
        try {
            String accessTokenInfo = HttpClientUtils.get(accessTokenUrl);
            System.out.println("accessTokenInfo:" + accessTokenInfo);
            //从返回的字符串中获取两个值openid和access_token
            JSONObject jsonObject = JSONObject.parseObject(accessTokenInfo);
            String access_token = jsonObject.getString("access_token");
            String openid = jsonObject.getString("openid");

            //判断数据库中是否存在微信扫码人的信息 （唯一标识openid来判断）
            UserInfo userInfo = userInfoService.selectWxInfoOpenId(openid);
            if(userInfo == null) {

                //2、使用access_token 和 openid 请求微信提供的地址，请求返回扫码人的相关信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                String userInfoUrl = String.format(baseUserInfoUrl, access_token, openid);
                //resultInfo 存储的是扫码人的信息
                String resultInfo = HttpClientUtils.get(userInfoUrl);

                //解析用户信息 把字符串变成json数据
                JSONObject resultUserInfoJson = JSONObject.parseObject(resultInfo);
                //解析用户信息
                String nickname = resultUserInfoJson.getString("nickname");
                String headimgurl = resultUserInfoJson.getString("headimgurl");

                //将扫码人的信息 添加到数据库中
                userInfo = new UserInfo();
                userInfo.setOpenid(openid);
                userInfo.setNickName(nickname);
                userInfo.setStatus(1);
                userInfoService.save(userInfo);
            }

            Map<String, Object> map = new HashMap<>();
            String name = userInfo.getName();
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getNickName();
            }
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getPhone();
            }
            map.put("name", name);
            //判断userInfo是否有手机号，如果有手机号返回openid为空  没手机号 返回openid的值 （为了绑定用户手机号）
            if(StringUtils.isEmpty(userInfo.getPhone())) {
                map.put("openid", userInfo.getOpenid());
            } else {
                map.put("openid", "");
            }
            String token = JwtHelper.createToken(userInfo.getId(), name);
            map.put("token", token);

            //跳转到前端的页面中
            return "redirect:" + ConstantWxPropertiesUtils.YYGH_BASE_URL +
                    "/weixin/callback?token="+map.get("token")+"&openid="+map.get("openid")+"&name="+
                    URLEncoder.encode((String)map.get("name"),"utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
return  null;
    }
}
