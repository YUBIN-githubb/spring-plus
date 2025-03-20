package org.example.expert.BulkTest;

import org.aspectj.lang.annotation.Before;
import org.example.expert.config.JwtUtil;
import org.example.expert.config.SecurityConfig;
import org.example.expert.config.WithMockAuthUser;
import org.example.expert.domain.common.entity.Timestamped;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StopWatch;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@Import({SecurityConfig.class, JwtUtil.class})
public class BulkInsertTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final int BATCH_SIZE = 5000;
    private static final int TOTAL_DATA = 1_000_000;
    private static final SecureRandom random = new SecureRandom();
    private static final String DEFAULT_PROFILE_URL = "java/org/example/expert/images/oiia_cat.jpg";

    @Test
    //@WithMockAuthUser(userId = 1L, email = "admin@test.com", role = UserRole.ROLE_USER)
    void bulkInsert() {
        String sql = "INSERT INTO users (email, password, user_role, nickname, profile_url, created_at, modified_at) VALUES (?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> batchArgs = new ArrayList<>(BATCH_SIZE);

        for (int i = 1; i <= TOTAL_DATA; i++) {
            String nickname = generateUniqueNickname(i);
            String email = "user" + i + "@test.com";
            String password = generateRandomPassword();
            Timestamp now = new Timestamp(System.currentTimeMillis());

            batchArgs.add(new Object[]{
                    email,
                    password,
                    UserRole.ROLE_USER.name(),
                    nickname,
                    DEFAULT_PROFILE_URL,
                    now,  // created_at
                    now   // modified_at
            });

            if (i % BATCH_SIZE == 0) {
                jdbcTemplate.batchUpdate(sql, batchArgs);
                batchArgs.clear();
            }
        }

        if (!batchArgs.isEmpty()) {
            jdbcTemplate.batchUpdate(sql, batchArgs);
        }
    }

    private String generateUniqueNickname(int index) {
        return "user_" + index + "_" + random.nextInt(10000);
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 10);
    }


}


