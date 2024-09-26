package uz.zafar.ibratfarzandlari.db.service;

import org.springframework.data.domain.Page;
import uz.zafar.ibratfarzandlari.db.domain.Lesson;
import uz.zafar.ibratfarzandlari.dto.ResponseDto;

public interface LessonService {
    ResponseDto<Void> save(Lesson lesson);
    ResponseDto<Lesson> findByStatus(String status);
    ResponseDto<Page<Lesson>> findAllByTypeId(Long typeId, int page, int size);
    ResponseDto<Lesson>findById(Long id);
    ResponseDto<Lesson>nextLesson(Long id,Long typeId);
    ResponseDto<Lesson>oldLesson(Long id,Long typeId);
}
