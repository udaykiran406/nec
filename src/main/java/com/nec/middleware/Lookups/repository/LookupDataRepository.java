package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.dto.LookupValueResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LookupDataRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<LookupValueResponseDto> getLookupValuesByTableName(String tableName) {

        String sql = String.format("""
                SELECT id, code, value, description, is_active, display_order
                FROM %s
                WHERE is_deleted = 0
                ORDER BY display_order ASC, value ASC
                """, tableName);

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                LookupValueResponseDto.builder()
                        .id(rs.getLong("id"))
                        .code(rs.getString("code"))
                        .value(rs.getString("value"))
                        .description(rs.getString("description"))
                        .isActive(rs.getInt("is_active"))
                        .displayOrder(rs.getInt("display_order"))
                        .build()
        );
    }

    public LookupValueResponseDto getLookupValueById(String tableName, Long id) {

        String sql = String.format("""
                SELECT id, code, value, description, is_active, display_order
                FROM %s
                WHERE id = ?
                  AND is_deleted = 0
                """, tableName);

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                LookupValueResponseDto.builder()
                        .id(rs.getLong("id"))
                        .code(rs.getString("code"))
                        .value(rs.getString("value"))
                        .description(rs.getString("description"))
                        .isActive(rs.getInt("is_active"))
                        .displayOrder(rs.getInt("display_order"))
                        .build(), id
        );
    }

    public boolean existsById(String tableName, Long id) {

        String sql = String.format("""
                SELECT COUNT(1)
                FROM %s
                WHERE id = ?
                  AND is_deleted = 0
                """, tableName);

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);

        return count != null && count > 0;
    }

    public boolean existsByValue(String tableName, String value) {

        String sql = String.format("""
                SELECT COUNT(1)
                FROM %s
                WHERE LOWER(value) = LOWER(?)
                  AND is_deleted = 0
                """, tableName);

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, value);

        return count != null && count > 0;
    }

    public boolean existsByValueExcludingId(String tableName, String value, Long id) {

        String sql = String.format("""
                SELECT COUNT(1)
                FROM %s
                WHERE LOWER(value) = LOWER(?)
                  AND id <> ?
                  AND is_deleted = 0
                """, tableName);

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, value, id);

        return count != null && count > 0;
    }

    public Integer getNextDisplayOrder(String tableName) {

        String sql = String.format("""
                SELECT COALESCE(MAX(display_order), 0) + 1
                FROM %s
                WHERE is_deleted = 0
                """, tableName);

        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public Long getNextGlobalCodeNumber() {

        String sql = "SELECT nextval('nec_lkp_global_code_seq')";

        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public Long insertLookupValue(
            String tableName,
            String code,
            String value,
            String description,
            Integer isActive,
            Integer displayOrder) {

        String sql = String.format("""
                INSERT INTO %s
                (code, value, description, is_active, display_order, is_deleted, created_by, updated_by)
                VALUES (?, ?, ?, ?, ?, 0, 'system', 'system')
                RETURNING id
                """, tableName);

        return jdbcTemplate.queryForObject(
                sql,
                Long.class,
                code,
                value,
                description,
                isActive,
                displayOrder
        );
    }

    public int updateLookupValue(
            String tableName,
            Long id,
            String value,
            String description,
            Integer isActive,
            Integer displayOrder) {

        String sql = String.format("""
                UPDATE %s
                SET value = ?,
                    description = ?,
                    is_active = ?,
                    display_order = ?,
                    updated_by = 'system',
                    updated_dt = CURRENT_TIMESTAMP
                WHERE id = ?
                  AND is_deleted = 0
                """, tableName);

        return jdbcTemplate.update(
                sql,
                value,
                description,
                isActive,
                displayOrder,
                id
        );
    }
}