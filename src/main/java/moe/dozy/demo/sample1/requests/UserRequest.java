package moe.dozy.demo.sample1.requests;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import moe.dozy.demo.sample1.models.User;
import moe.dozy.demo.sample1.services.AuthRoleService;

@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRequest implements Serializable {

    @Autowired
    private AuthRoleService authRoleService;

    @Min(1)
    @NotNull @NonNull
    private Long id;
    @Size(max = 150)
    @NotEmpty @NonNull
    private String name;
    @Email @Size(max = 250)
    @NotEmpty @NonNull
    private String email;
    @Min(1)
    @NotNull @NonNull
    private Long role_id;

    public Long getRoleId() { return role_id; }
    public void setRoleId(Long id) { this.role_id = id; }

    public static UserRequest of(User from) {
        var role = from.getRoles().stream().findFirst().get();
        return new UserRequest(from.getId(), from.getName(), from.getEmail(),
                role.getId());
    }

    public void mergeTo(User target) {
        target.setName(name);
        target.setEmail(email);
    }

    public void mergeRelationsTo(User target) {
        target.setRoles(authRoleService.findById(role_id));
    }
}
