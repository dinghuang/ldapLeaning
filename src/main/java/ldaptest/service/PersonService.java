package ldaptest.service;

import ldaptest.domain.Person;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/10
 */
public interface PersonService {

    void create(Person person);

    void update(Person person);

    void delete(Person person);

    List<String> getAllPersonNames();

    List<Person> findAll();

    Person findByPrimaryKey(String country, String company, String fullname);
}
