package ldaptest.domain;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;
import java.util.HashSet;
import java.util.Set;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/10
 */
@Entry(objectClasses = {"groupOfNames", "top"}, base = "ou=Groups")
public final class Group {
    @Id
    private Name id;

    @Attribute(name = "cn")
    @DnAttribute(value = "cn", index=1)
    private String name;

    @Attribute(name = "description")
    private String description;

    @Attribute(name = "member")
    private Set<Name> members = new HashSet<Name>();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Name> getMembers() {
        return  members;
    }

    public void addMember(Name newMember) {
        members.add(newMember);
    }

    public void removeMember(Name member) {
        members.remove(member);
    }

    public Name getId() {
        return id;
    }

    public void setId(Name id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}