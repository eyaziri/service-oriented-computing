package com.smart_tourism.smart_tourism;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DatabaseTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testDatabaseConnection() throws SQLException {
        assertThat(dataSource).isNotNull();

        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection.isValid(2)).isTrue();
            System.out.println("✅ Connexion MySQL réussie !");
        }
    }

    @Test
    void testTablesExist() {
        String sql = "SHOW TABLES";
        jdbcTemplate.query(sql, (ResultSet rs) -> {
            while (rs.next()) {
                System.out.println("📋 Table: " + rs.getString(1));
            }
        });

        // Vérifier que la table monument existe
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'smart_tourism' AND table_name = 'monument'",
                Integer.class
        );

        assertThat(count).isEqualTo(1);
        System.out.println("✅ Table 'monument' existe !");
    }
}