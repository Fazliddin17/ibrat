package uz.zafar.ibratfarzandlari.db.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.zafar.ibratfarzandlari.db.domain.User;
import uz.zafar.ibratfarzandlari.db.repositories.UserRepository;
import uz.zafar.ibratfarzandlari.db.service.UserService;
import uz.zafar.ibratfarzandlari.dto.ResponseDto;

import java.util.List;

@Service
@Log4j2
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseDto<User> checkUser(Long chatId) {
        try {
            User checkUser = userRepository.findByChatId(chatId);
            if (checkUser == null)
                return new ResponseDto<>(false, "Not found user");
            return new ResponseDto<>(true, "Ok", checkUser);
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<List<User>> findAll() {
        try {
            return new ResponseDto<>(true, "Ok", userRepository.findAllByOrderByIdDesc());
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<List<User>> findAllByRole(String role) {

        try {
            List<User> users = userRepository.findAllByRoleOrderByIdDesc(role);
            if (users.isEmpty())
                return new ResponseDto<>(false, "Users is empty");
            return new ResponseDto<>(true, "Ok", users);
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false , e.getMessage());
        }
    }

    @Override
    public ResponseDto<User> save(User user) {
        try {
            return new ResponseDto<>(true , "Ok" , userRepository.save(user));
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false , e.getMessage());
        }
    }
}
