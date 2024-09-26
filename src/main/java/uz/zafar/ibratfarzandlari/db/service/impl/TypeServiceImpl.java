package uz.zafar.ibratfarzandlari.db.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.zafar.ibratfarzandlari.db.domain.Type;
import uz.zafar.ibratfarzandlari.db.repositories.TypeRepository;
import uz.zafar.ibratfarzandlari.db.service.TypeService;
import uz.zafar.ibratfarzandlari.dto.ResponseDto;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class TypeServiceImpl implements TypeService {
    @Autowired
    private TypeRepository typeRepository;

    @Override
    public ResponseDto<List<Type>> findAllByCourseId(Long courseId) {
        try {
            return new ResponseDto<>(true, "Ok", typeRepository.findAllByCourseIdAndActiveAndStatus(courseId, true, "open"));
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Type> findByName(String name) {
        try {
            Type type = typeRepository.findByName(name);
            if (type == null) {
                log.error(name + " not found");
                return new ResponseDto<>(false, name + " not found");
            }
            return new ResponseDto<>(true, "Ok", type);
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Void> save(Type type) {
        try {
            typeRepository.save(type);
            return new ResponseDto<>(true, "Ok");
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Type> findById(Long id) {
        try {
            Optional<Type> tOp = typeRepository.findById(id);
            if (tOp.isEmpty()) {
                log.error(id + " not found");
                return new ResponseDto<>(false, id + " not found");
            }
            return new ResponseDto<>(true, "Ok", tOp.get());
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Type> findByStatus(String status) {
        try {
            Type type = typeRepository.findByStatus(status);
            if (type == null) {
                return new ResponseDto<>(false, status + " not found");
            }
            return new ResponseDto<>(true, "Ok", type);

        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }
}
