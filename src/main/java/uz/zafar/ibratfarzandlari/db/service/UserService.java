package uz.zafar.ibratfarzandlari.db.service;

import uz.zafar.ibratfarzandlari.db.domain.User;
import uz.zafar.ibratfarzandlari.dto.ResponseDto;

import java.util.List;

public interface UserService {
    ResponseDto<User> checkUser(Long chatId);

    ResponseDto<List<User>> findAll();

    ResponseDto<List<User>> findAllByRole(String role);

    ResponseDto<User> save(User user);
}
