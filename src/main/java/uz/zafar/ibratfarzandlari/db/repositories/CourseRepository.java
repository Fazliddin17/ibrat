package uz.zafar.ibratfarzandlari.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.zafar.ibratfarzandlari.db.domain.Course;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course , Long> {
    List<Course>findAllByActiveAndStatusOrderByIdAsc(boolean active, String status);
    Course findByNameAndStatusAndActive(String name , String status , Boolean active);
    Course findByStatus(String status);
}
