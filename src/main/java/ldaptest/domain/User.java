package ldaptest.domain;

import org.springframework.ldap.odm.annotations.*;
import org.springframework.ldap.support.LdapUtils;

import javax.naming.Name;

/**
 * LDAP的信息是以树型结构存储的，在树根一般定义国家(c=CN)或域名(dc=com)，
 * 在其下则往往定义一个或多个组织 (organization)(o=Acme)或组织单元(organizational units) (ou=People)
 * LDAP支持对条目能够和必须支持哪些属性进行控制，这是有一个特殊的称为对象类别(objectClass)的属性来实现的。
 * 该属性的值决定了该条目必须遵循的一些规则，其规定了该条目能够及至少应该包含哪些属性
 *
 * @author dinghuang123@gmail.com
 * @since 2018/6/10
 */
@Entry(objectClasses = {"inetOrgPerson", "organizationalPerson", "person", "top"}, base = "ou=Departments")
public final class User {
    @Id
    private Name id;

    @Attribute(name = "cn")
    @DnAttribute(value = "cn", index = 3)
    private String fullName;

    @Attribute(name = "employeeNumber")
    private int employeeNumber;

    @Attribute(name = "givenName")
    private String firstName;

    @Attribute(name = "sn")
    private String lastName;

    @Attribute(name = "title")
    private String title;

    @Attribute(name = "mail")
    private String email;

    @Attribute(name = "telephoneNumber")
    private String phone;

    @DnAttribute(value = "ou", index = 2)
    @Transient
    private String unit;

    @DnAttribute(value = "ou", index = 1)
    @Transient
    private String department;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Name getId() {
        return id;
    }

    public void setId(Name id) {
        this.id = id;
    }

    public void setId(String id) {
        this.id = LdapUtils.newLdapName(id);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(int employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return getEmployeeNumber() == user.getEmployeeNumber() &&
                com.google.common.base.Objects.equal(getId(), user.getId()) &&
                com.google.common.base.Objects.equal(getFullName(), user.getFullName()) &&
                com.google.common.base.Objects.equal(getFirstName(), user.getFirstName()) &&
                com.google.common.base.Objects.equal(getLastName(), user.getLastName()) &&
                com.google.common.base.Objects.equal(getTitle(), user.getTitle()) &&
                com.google.common.base.Objects.equal(getEmail(), user.getEmail()) &&
                com.google.common.base.Objects.equal(getPhone(), user.getPhone()) &&
                com.google.common.base.Objects.equal(getUnit(), user.getUnit()) &&
                com.google.common.base.Objects.equal(getDepartment(), user.getDepartment());
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(getId(), getFullName(), getEmployeeNumber(), getFirstName(), getLastName(), getTitle(), getEmail(), getPhone(), getUnit(), getDepartment());
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", employeeNumber=" + employeeNumber +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", title='" + title + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", unit='" + unit + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
