package com.damon4u.demo.domain;

/**
 * Description:
 *
 * @author damon4u
 * @version 2017-08-13 17:13
 */
public class User {

    private Long id;

    private String userName;

    private String remark;

    public User() {
    }

    public User(String userName, String remark) {
        this.userName = userName;
        this.remark = remark;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
