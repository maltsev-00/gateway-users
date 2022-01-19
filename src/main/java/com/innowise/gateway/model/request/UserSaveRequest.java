package com.innowise.gateway.model.request;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Value
@Builder
public class UserSaveRequest {
    @NotNull
    @NotEmpty
    @Size(max = 50)
    String username;
    @Email
    @NotNull
    @NotEmpty
    String email;
    @NotNull
    Set<RoleRequest> roles;
    @NotNull
    CardRequest card;
    @NotNull
    List<CourseRequest> courses;
}
