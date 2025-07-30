package com.recargapay.wallet.model.mapper;

import com.recargapay.wallet.model.User;
import com.recargapay.wallet.model.dto.UserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toUserDto(User user);

}
