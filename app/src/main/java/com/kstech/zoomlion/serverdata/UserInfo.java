package com.kstech.zoomlion.serverdata;

/**
 * Created by lijie on 2017/11/21.
 */

public class UserInfo {
    /**
     * 用户名
     */
    private String username;
    /**
     * 真实姓名
     */
    private String name;
    /**
     * 生日
     */
    private String birthday;
    /**
     * 性别
     */
    private String sex;
    /**
     * 性别描述
     */
    private String sexDescription;
    /**
     * 民族
     */
    private String nation;
    /**
     * 婚否
     */
    private Boolean married;
    /**
     * 婚否描述
     */
    private String marriedDescription;
    /**
     * 座机号码
     */
    private String phoneNumber;
    /**
     * 手机号码
     */
    private String cellPhoneNumber;
    /**
     * 联系地址
     */
    private String address;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 照片
     */
    private String photo;
    /**
     * 学历
     */
    private String education;
    /**
     * 毕业学校
     */
    private String graduatedFrom;
    /**
     * 最后登录时间
     */
    private String lastLoginTime;
    /**
     * 用户状态
     */
    private Integer status;
    /**
     * 用户状态描述
     */
    private String statusName;
    /**
     * 所属部门ID
     */
    private Long departmentId;
    /**
     * 所属部门名称
     */
    private String departmentName;
    /**
     * 用户角色Id集合
     */
    private String roleIds;
    /**
     * 角色名称集合
     */
    private String rolesName;
    /**
     * 用户机型授权的ID集合
     */
    private String categoryIds;
    /**
     * 用户机型授权的名称集合
     */
    private String categoriesName;

    public UserInfo() {
    }

    @Override
    public String toString() {
        return "当前用户信息：" +
                "用户名：'" + username + '\'' +
                ", 姓名：'" + name + '\'' +
                ", 生日'" + birthday + '\'' +
                ", 性别" + sex +
                ", 国家：'" + nation + '\'' +
                ", 婚否：" + married +
                ", 座机号：'" + phoneNumber + '\'' +
                ", 手机号：'" + cellPhoneNumber + '\'' +
                ", 地址：'" + address + '\'' +
                ", email：'" + email + '\'' +
                ", 头像：'" + photo + '\'' +
                ", 学历='" + education + '\'' +
                ", 毕业院校：'" + graduatedFrom + '\'';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public Boolean getMarried() {
        return married;
    }

    public void setMarried(Boolean married) {
        this.married = married;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    public void setCellPhoneNumber(String cellPhoneNumber) {
        this.cellPhoneNumber = cellPhoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getGraduatedFrom() {
        return graduatedFrom;
    }

    public void setGraduatedFrom(String graduatedFrom) {
        this.graduatedFrom = graduatedFrom;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSexDescription() {
        return sexDescription;
    }

    public void setSexDescription(String sexDescription) {
        this.sexDescription = sexDescription;
    }

    public String getMarriedDescription() {
        return marriedDescription;
    }

    public void setMarriedDescription(String marriedDescription) {
        this.marriedDescription = marriedDescription;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(String roleIds) {
        this.roleIds = roleIds;
    }

    public String getRolesName() {
        return rolesName;
    }

    public void setRolesName(String rolesName) {
        this.rolesName = rolesName;
    }

    public String getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(String categoryIds) {
        this.categoryIds = categoryIds;
    }

    public String getCategoriesName() {
        return categoriesName;
    }

    public void setCategoriesName(String categoriesName) {
        this.categoriesName = categoriesName;
    }
}
