package moe.dozy.demo.sample1.models;

import java.io.Serializable;
import java.time.ZonedDateTime;

import org.apache.ibatis.session.SqlSession;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Getter
@Setter
public class AuthRole implements Serializable {

    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;
    @ToString.Include
    private String name;
    @ToString.Include
    private String guard_name;
    private ZonedDateTime created_at;
    private ZonedDateTime updated_at;

    private SqlSession sqlSession;

    public String getGuardName() { return guard_name; }
    public void setGuardName(String name) { this.guard_name = name; }
    public ZonedDateTime getCreatedAt() { return created_at; }
    public void setCreatedAt(ZonedDateTime date) { this.created_at = date; }
    public ZonedDateTime getUpdatedAt() { return updated_at; }
    public void setUpdatedAt(ZonedDateTime date) { this.updated_at = date; }
}
