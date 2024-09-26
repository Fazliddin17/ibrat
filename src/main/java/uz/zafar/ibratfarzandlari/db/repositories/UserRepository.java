package uz.zafar.ibratfarzandlari.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.zafar.ibratfarzandlari.db.domain.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByOrderByIdDesc() ;
    User findByChatId(Long chatId);

    List<User> findAllByRoleOrderByIdDesc(String role);
}
