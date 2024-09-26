package uz.zafar.ibratfarzandlari.db.service.impl;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.stereotype.Service;
import uz.zafar.ibratfarzandlari.db.domain.Course;
import uz.zafar.ibratfarzandlari.db.repositories.CourseRepository;
import uz.zafar.ibratfarzandlari.db.service.CourseService;
import uz.zafar.ibratfarzandlari.dto.ResponseDto;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Override
    public ResponseDto<Course> findById(Long id) {
        try {
            Optional<Course> cOp = courseRepository.findById(id);
            return cOp.map(course -> new ResponseDto<>(true, "OK", course)).orElseGet(() -> new ResponseDto<>(false, "Not found"));
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Course> findByName(String name) {
        try {
            Course checkCourse = courseRepository.findByNameAndStatusAndActive(name, "open", true);
            if (checkCourse == null) return new ResponseDto<>(false, "Not found");
            return new ResponseDto<>(true, "Ok", checkCourse);
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Course> findByStatus(String status) {
        try {
            Course checkCourse = courseRepository.findByStatus(status);
            if (checkCourse == null) return new ResponseDto<>(false, "Not found Course");
            return new ResponseDto<>(true, "Ok", checkCourse);
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Void> save(Course course) {
        try {
            courseRepository.save(course);
            return new ResponseDto<>(true, "Ok");
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<List<Course>> findAll() {
        try {
            return new ResponseDto<>(true, "Ok", courseRepository.findAllByActiveAndStatusOrderByIdAsc(true, "open"));
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }
}
