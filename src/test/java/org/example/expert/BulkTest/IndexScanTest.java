package org.example.expert.BulkTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StopWatch;

import java.util.List;

@SpringBootTest
public class IndexScanTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private StopWatch stopWatch;

    @BeforeEach
    public void setUp() {
        stopWatch = new StopWatch("test");

        // ✅ 인덱스 추가 (이미 존재하는 경우 에러 방지를 위해 IF NOT EXISTS 사용)
        String createIndexSql = "CREATE INDEX idx_users_nickname ON users (nickname)";
        jdbcTemplate.execute(createIndexSql);
        System.out.println("✅ 'nickname' 컬럼에 인덱스 추가 완료!");
    }

    @Test
    void indexSearchFindByUserName() {
        String nicknameToFind = "user_777778_4084";
        String findUserSql = "SELECT * FROM users WHERE nickname = ?";

        stopWatch.start("indexed search");
        List<String> userInfo = jdbcTemplate.query(findUserSql,
                new Object[]{nicknameToFind},
                (rs, rowNum) -> rs.getString("email"));
        stopWatch.stop();

        if (!userInfo.isEmpty()) {
            System.out.println("✅ 닉네임 " + nicknameToFind + " 조회 성공: " + userInfo.get(0));
        } else {
            System.out.println("❌ 닉네임 " + nicknameToFind + " 조회 실패");
        }

        System.out.println(stopWatch.prettyPrint());
    }

}
