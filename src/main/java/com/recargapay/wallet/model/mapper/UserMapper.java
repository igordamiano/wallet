package com.recargapay.wallet.model.mapper;

import com.recargapay.wallet.model.User;
import com.recargapay.wallet.model.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User user);

}
