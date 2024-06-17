package com.myproject.authserver.utils;

import com.myproject.authserver.dto.UserDto;
import com.myproject.authserver.model.Role;
import com.myproject.authserver.model.User;
import lombok.experimental.UtilityClass;
import org.springframework.beans.BeanUtils;

import java.util.stream.Collectors;

@UtilityClass
public class DtoMapperUtil {

    public UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        userDto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        return userDto;
    }


}
