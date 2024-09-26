package uz.zafar.ibratfarzandlari.db.service;

import uz.zafar.ibratfarzandlari.db.domain.Type;
import uz.zafar.ibratfarzandlari.dto.ResponseDto;

import java.util.List;

public interface TypeService {
    ResponseDto<List<Type>> findAllByCourseId(Long courseId);

    ResponseDto<Type> findByName(String name);

    ResponseDto<Void> save(Type type);

    ResponseDto<Type> findById(Long id);

    ResponseDto<Type> findByStatus(String status);
}
