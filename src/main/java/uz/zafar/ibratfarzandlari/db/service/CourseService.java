package uz.zafar.ibratfarzandlari.db.service;

import uz.zafar.ibratfarzandlari.db.domain.Course;
import uz.zafar.ibratfarzandlari.dto.ResponseDto;

import java.util.List;

public interface CourseService {
    ResponseDto<Course> findByName(String name);
    ResponseDto<Course> findById(Long id);

    ResponseDto<Course> findByStatus(String status);

    ResponseDto<List<Course>> findAll();

    ResponseDto<Void> save(Course course);
}
