package ldaptest.controller;

import ldaptest.domain.Person;
import ldaptest.service.PersonService;
import ldaptest.utils.HtmlRowLdapTreeVisitor;
import ldaptest.utils.LdapTree;
import ldaptest.utils.LdapTreeBuilder;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.naming.Name;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/10
 */
@Controller
public class PersonController {

    @Autowired
    private LdapTreeBuilder ldapTreeBuilder;

    @Autowired
    private PersonService personService;

    @RequestMapping("/welcome.do")
    public void welcomeHandler() {
    }

    @RequestMapping("/showTree.do")
    public ModelAndView showTree() {
        LdapTree ldapTree = ldapTreeBuilder.getLdapTree(LdapUtils.emptyLdapName());
        HtmlRowLdapTreeVisitor visitor = new PersonLinkHtmlRowLdapTreeVisitor();
        ldapTree.traverse(visitor);
        return new ModelAndView("showTree", "rows", visitor.getRows());
    }

    @RequestMapping("/addPerson.do")
    public String addPerson() {
        Person person = getPerson();
        personService.create(person);
        return "redirect:/showTree.do";
    }

    @RequestMapping("/updatePhoneNumber.do")
    public String updatePhoneNumber() {
        Person person = personService.findByPrimaryKey("Sweden", "company1", "John Doe");
        person.setPhone(StringUtils.join(new String[]{person.getPhone(), "0"}));
        personService.update(person);
        return "redirect:/showTree.do";
    }

    @RequestMapping("/removePerson.do")
    public String removePerson() {
        Person person = getPerson();

        personService.delete(person);
        return "redirect:/showTree.do";
    }

    @RequestMapping("/showPerson.do")
    public ModelMap showPerson(String country, String company, String fullName) {
        Person person = personService.findByPrimaryKey(country, company, fullName);
        return new ModelMap("person", person);
    }

    private Person getPerson() {
        Person person = new Person();
        person.setFullName("John Doe");
        person.setLastName("Doe");
        person.setCompany("company1");
        person.setCountry("Sweden");
        person.setDescription("Test user");
        return person;
    }

    /**
     * Generates appropriate links for person leaves in the tree.
     *
     * @author Mattias Hellborg Arthursson
     */
    private static final class PersonLinkHtmlRowLdapTreeVisitor extends HtmlRowLdapTreeVisitor {
        @Override
        protected String getLinkForNode(DirContextOperations node) {
            String[] objectClassValues = node.getStringAttributes("objectClass");
            if (containsValue(objectClassValues, "person")) {
                Name dn = node.getDn();
                String country = encodeValue(LdapUtils.getStringValue(dn, "c"));
                String company = encodeValue(LdapUtils.getStringValue(dn, "ou"));
                String fullName = encodeValue(LdapUtils.getStringValue(dn, "cn"));

                return "showPerson.do?country=" + country + "&company=" + company + "&fullName=" + fullName;
            } else {
                return super.getLinkForNode(node);
            }
        }

        private String encodeValue(String value) {
            try {
                return URLEncoder.encode(value, "UTF8");
            } catch (UnsupportedEncodingException e) {
                // Not supposed to happen
                throw new RuntimeException("Unexpected encoding exception", e);
            }
        }

        private boolean containsValue(String[] values, String value) {
            for (String oneValue : values) {
                if (StringUtils.equals(oneValue, value)) {
                    return true;
                }
            }
            return false;
        }
    }
}
