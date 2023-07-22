package com.yangzai.yygh.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yangzai.yygh.entity.base.BaseEntity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author yangzai
 * @since 2022-12-01
 */
@TableName("user_info")
public class UserInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

//    /**
//     * 编号
//     */
//    @TableId(value = "id", type = IdType.AUTO)
//    private Long id;

    /**
     * 微信openid
     */
    private String openid;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 证件类型
     */
    private String certificatesType;

    /**
     * 证件编号
     */
    private String certificatesNo;

    /**
     * 证件路径
     */
    private String certificatesUrl;

    /**
     * 认证状态（0：未认证 1：认证中 2：认证成功 -1：认证失败）
     */
    private Integer authStatus;

    /**
     * 状态（0：锁定 1：正常）
     */
    private Integer status;

//    /**
//     * 创建时间
//     */
//    private LocalDateTime createTime;
//
//    /**
//     * 更新时间
//     */
//    private LocalDateTime updateTime;

//    /**
//     * 逻辑删除(1:已删除，0:未删除)
//     */
//    private Integer isDeleted;
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }
    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getCertificatesType() {
        return certificatesType;
    }

    public void setCertificatesType(String certificatesType) {
        this.certificatesType = certificatesType;
    }
    public String getCertificatesNo() {
        return certificatesNo;
    }

    public void setCertificatesNo(String certificatesNo) {
        this.certificatesNo = certificatesNo;
    }
    public String getCertificatesUrl() {
        return certificatesUrl;
    }

    public void setCertificatesUrl(String certificatesUrl) {
        this.certificatesUrl = certificatesUrl;
    }
    public Integer getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(Integer authStatus) {
        this.authStatus = authStatus;
    }
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
//    public LocalDateTime getCreateTime() {
//        return createTime;
//    }
//
//    public void setCreateTime(LocalDateTime createTime) {
//        this.createTime = createTime;
//    }
//    public LocalDateTime getUpdateTime() {
//        return updateTime;
//    }
//
//    public void setUpdateTime(LocalDateTime updateTime) {
//        this.updateTime = updateTime;
//    }
//    public Integer getIsDeleted() {
//        return isDeleted;
//    }
//
//    public void setIsDeleted(Integer isDeleted) {
//        this.isDeleted = isDeleted;
//    }

    @Override
    public String toString() {
        return "UserInfo{" +
            //"id=" + id +
            "openid=" + openid +
            ", nickName=" + nickName +
            ", phone=" + phone +
            ", name=" + name +
            ", certificatesType=" + certificatesType +
            ", certificatesNo=" + certificatesNo +
            ", certificatesUrl=" + certificatesUrl +
            ", authStatus=" + authStatus +
            ", status=" + status +
//            ", createTime=" + createTime +
//            ", updateTime=" + updateTime +
//            ", isDeleted=" + isDeleted +
        "}";
    }
}
