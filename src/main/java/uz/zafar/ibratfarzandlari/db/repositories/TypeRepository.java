package uz.zafar.ibratfarzandlari.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.zafar.ibratfarzandlari.db.domain.Type;

import java.util.List;

public interface TypeRepository extends JpaRepository<Type, Long> {
    List<Type> findAllByCourseIdAndActiveAndStatus(Long courseId, Boolean active, String status);
    Type findByName(String name);
    Type findByStatus(String status);
}
