package ldaptest.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import ldaptest.domain.Group;
import ldaptest.domain.User;
import ldaptest.repository.GroupRepository;
import ldaptest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/10
 */
@Controller
@RequestMapping(value = "/groups")
public class GroupController {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserService userService;

    @GetMapping
    public JSONObject listGroups() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("groups", groupRepository.getAllGroupNames());
        return jsonObject;
    }

    @PostMapping
    public JSONObject newGroup(Group group) {
        groupRepository.create(group);
        return editGroup(group.getName());
    }

    @GetMapping(value = "/{name}")
    public JSONObject editGroup(@PathVariable String name) {
        JSONObject jsonObject = new JSONObject();
        Group foundGroup = groupRepository.findByName(name);
        jsonObject.put("group", foundGroup);
        final Set<User> groupMembers = userService.findAllMembers(foundGroup.getMembers());
        jsonObject.put("members", groupMembers);
        Iterable<User> otherUsers = StreamSupport.stream(userService.findAll().spliterator(), false)
                .filter(user -> !groupMembers.contains(user)).collect(Collectors.toList());
        jsonObject.put("nonMembers", Lists.newLinkedList(otherUsers));
        return jsonObject;
    }

    @PostMapping(value = "/{name}/members")
    public JSONObject addUserToGroup(@PathVariable String name, @RequestParam String userId) {
        Group group = groupRepository.findByName(name);
        group.addMember(userService.toAbsoluteDn(LdapUtils.newLdapName(userId)));
        groupRepository.save(group);
        return editGroup(name);
    }

    @DeleteMapping(value = "/{name}/members")
    public JSONObject removeUserFromGroup(@PathVariable String name, @RequestParam String userId) {
        Group group = groupRepository.findByName(name);
        group.removeMember(userService.toAbsoluteDn(LdapUtils.newLdapName(userId)));
        groupRepository.save(group);
        return editGroup(name);
    }
}
