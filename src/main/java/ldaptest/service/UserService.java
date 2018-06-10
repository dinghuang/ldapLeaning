package ldaptest.service;

import com.google.common.collect.Sets;
import ldaptest.domain.Group;
import ldaptest.domain.User;
import ldaptest.repository.GroupRepository;
import ldaptest.repository.UserRepository;
import ldaptest.utils.DirectoryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.support.BaseLdapNameAware;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;

import javax.naming.Name;
import javax.naming.ldap.LdapName;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/10
 */
@Service
public class UserService implements BaseLdapNameAware {

    private final UserRepository userRepository;
    private LdapName baseLdapPath;
    private final GroupRepository groupRepository;
    private DirectoryType directoryType;

    public final static String USER_GROUP = "ROLE_USER";


    @Autowired
    public UserService(UserRepository userRepository, GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    public Group getUserGroup() {
        return groupRepository.findByName(USER_GROUP);
    }

    public void setDirectoryType(DirectoryType directoryType) {
        this.directoryType = directoryType;
    }

    @Override
    public void setBaseLdapPath(LdapName baseLdapPath) {
        this.baseLdapPath = baseLdapPath;
    }

    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findUser(String userId) {
        return userRepository.findById(LdapUtils.newLdapName(userId));
    }

    public User createUser(User user) {
        User savedUser = userRepository.save(user);
        Group userGroup = getUserGroup();
        //成员属性的DN必须是绝对的
        userGroup.addMember(toAbsoluteDn(savedUser.getId()));
        groupRepository.save(userGroup);
        return savedUser;
    }

    public LdapName toAbsoluteDn(Name relativeName) {
        return LdapNameBuilder.newInstance(baseLdapPath)
                .add(relativeName)
                .build();
    }

    /**
     * 该方法要求组成员的绝对dns。为了找到真正的用户
     * dns需要删除基本的LDAP路径。
     *
     * @param absoluteIds absoluteIds
     * @return UserSet
     */
    public Set<User> findAllMembers(Iterable<Name> absoluteIds) {
        return Sets.newLinkedHashSet(userRepository.findAllById(toRelativeIds(absoluteIds)));
    }

    public Iterable<Name> toRelativeIds(Iterable<Name> absoluteIds) {
        return StreamSupport.stream(absoluteIds.spliterator(), false).
                map(input -> LdapUtils.removeFirst(input, baseLdapPath)).collect(Collectors.toList());
    }

    public User updateUser(String userId, User user) {
        LdapName originalId = LdapUtils.newLdapName(userId);
        Optional<User> query = userRepository.findById(originalId);
        if (query.isPresent()) {
            User existingUser = query.get();
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setFullName(user.getFullName());
            existingUser.setEmail(user.getEmail());
            existingUser.setPhone(user.getPhone());
            existingUser.setTitle(user.getTitle());
            existingUser.setDepartment(user.getDepartment());
            existingUser.setUnit(user.getUnit());
            if (directoryType == DirectoryType.AD) {
                return updateUserAd(originalId, existingUser);
            } else {
                return updateUserStandard(originalId, existingUser);
            }
        } else {
            return null;
        }
    }

    /**
     * 更新用户，如果其id更改，则更新所有组对用户的引用。
     *
     * @param originalId   用户的原始id。
     * @param existingUser 用新数据填充的用户
     * @return 更新的条目
     */
    private User updateUserStandard(LdapName originalId, User existingUser) {
        User savedUser = userRepository.save(existingUser);
        if (!originalId.equals(savedUser.getId())) {
            //用户已经移动——我们需要更新组引用
            LdapName oldMemberDn = toAbsoluteDn(originalId);
            LdapName newMemberDn = toAbsoluteDn(savedUser.getId());
            Collection<Group> groups = groupRepository.findByMember(oldMemberDn);
            updateGroupReferences(groups, oldMemberDn, newMemberDn);
        }
        return savedUser;
    }

    /**
     * Special behaviour in AD forces us to get the group membership before the user is updated,
     * because AD clears group membership for removed entries, which means that once the user is
     * update we've lost track of which groups the user was originally member of, preventing us to
     * update the membership references so that they point to the new DN of the user.
     * <p>
     * This is slightly less efficient, since we need to get the group membership for all updates
     * even though the user may not have been moved. Using our knowledge of which attributes are
     * part of the distinguished name we can do this more efficiently if we are implementing specifically
     * for Active Directory - this approach is just to highlight this quite significant difference.
     *
     * @param originalId   the original id of the user.
     * @param existingUser the user, populated with new data
     * @return the updated entry
     */
    private User updateUserAd(LdapName originalId, User existingUser) {
        LdapName oldMemberDn = toAbsoluteDn(originalId);
        Collection<Group> groups = groupRepository.findByMember(oldMemberDn);
        User savedUser = userRepository.save(existingUser);
        LdapName newMemberDn = toAbsoluteDn(savedUser.getId());
        if (!originalId.equals(savedUser.getId())) {
            //用户已经移动——我们需要更新组引用。
            updateGroupReferences(groups, oldMemberDn, newMemberDn);
        }
        return savedUser;
    }

    private void updateGroupReferences(Collection<Group> groups, Name originalId, Name newId) {
        for (Group group : groups) {
            group.removeMember(originalId);
            group.addMember(newId);
            groupRepository.save(group);
        }
    }

    public List<User> searchByNameName(String lastName) {
        return userRepository.findByFullNameContains(lastName);
    }
}
