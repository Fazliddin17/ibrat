package uz.zafar.ibratfarzandlari.db.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.zafar.ibratfarzandlari.db.domain.Lesson;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson,Long> {
    Page<Lesson>findByStatusAndActiveAndTypeIdOrderByIdAsc(Pageable pageable,String status,Boolean active,Long typeId) ;
    List<Lesson> findByStatusAndActiveAndTypeIdOrderByIdAsc(String status, Boolean active, Long typeId) ;
    Lesson findByStatus(String status) ;
}
