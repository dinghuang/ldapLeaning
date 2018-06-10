package ldaptest.repository;

import ldaptest.domain.User;
import org.springframework.data.ldap.repository.LdapRepository;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/10
 */
public interface UserRepository extends LdapRepository<User> {
    
    User findByEmployeeNumber(int employeeNumber);

    List<User> findByFullNameContains(String name);
}