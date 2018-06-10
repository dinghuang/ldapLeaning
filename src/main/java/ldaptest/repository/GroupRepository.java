package ldaptest.repository;

import ldaptest.domain.Group;
import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.data.ldap.repository.Query;

import javax.naming.Name;
import java.util.Collection;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/10
 */
public interface GroupRepository extends LdapRepository<Group> {

    List<String> getAllGroupNames();

    void create(Group group);

    Group findByName(String groupName);

    @Query("(member={0})")
    Collection<Group> findByMember(Name member);
}
