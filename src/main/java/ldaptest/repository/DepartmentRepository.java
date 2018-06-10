package ldaptest.repository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/10
 */
@Service
public interface DepartmentRepository {
    Map<String, List<String>> getDepartmentMap();
}
