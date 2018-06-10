package ldaptest.utils;

import org.springframework.ldap.core.DirContextOperations;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/10
 */
public interface LdapTreeVisitor {

	public void visit(DirContextOperations node, int currentDepth);
}
