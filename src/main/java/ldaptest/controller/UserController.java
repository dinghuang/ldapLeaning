package ldaptest.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ldaptest.domain.User;
import ldaptest.repository.DepartmentRepository;
import ldaptest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/10
 */
@Controller
@RequestMapping(value = "/users")
public class UserController {
    private final AtomicInteger nextEmployeeNumber = new AtomicInteger(10);

    @Autowired
    private UserService userService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping(value = "/")
    public JSONObject index(@RequestParam(required = false) String name) {
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.hasText(name)) {
            jsonObject.put("users", userService.searchByNameName(name));
        } else {
            jsonObject.put("users", userService.findAll());
        }
        return jsonObject;
    }

    @GetMapping(value = "/{userId}")
    public JSONObject getUser(@PathVariable String userId) throws JsonProcessingException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user", userService.findUser(userId));
        populateDepartments(jsonObject);
        return jsonObject;
    }

    @GetMapping(value = "/new_user")
    public JSONObject initNewUser() throws JsonProcessingException {
        JSONObject jsonObject = new JSONObject();
        User user = new User();
        user.setEmployeeNumber(nextEmployeeNumber.getAndIncrement());
        jsonObject.put("new", true);
        jsonObject.put("user", user);
        populateDepartments(jsonObject);
        return jsonObject;
    }

    @PostMapping(value = "/new_user")
    public JSONObject createUser(User user) throws JsonProcessingException {
        User savedUser = userService.createUser(user);
        return getUser(savedUser.getId().toString());
    }

    @PostMapping(value = "/users/{userId}")
    public JSONObject updateUser(@PathVariable String userId, User user) throws JsonProcessingException {
        User savedUser = userService.updateUser(userId, user);
        return getUser(savedUser.getId().toString());
    }

    private void populateDepartments(JSONObject jsonObject) throws JsonProcessingException {
        Map<String, List<String>> departmentMap = departmentRepository.getDepartmentMap();
        ObjectMapper objectMapper = new ObjectMapper();
        String departmentsAsJson = objectMapper.writeValueAsString(departmentMap);
        jsonObject.put("departments", departmentsAsJson);
    }
}
