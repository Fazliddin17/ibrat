package uz.zafar.ibratfarzandlari.db.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uz.zafar.ibratfarzandlari.db.domain.Lesson;
import uz.zafar.ibratfarzandlari.db.repositories.LessonRepository;
import uz.zafar.ibratfarzandlari.db.service.LessonService;
import uz.zafar.ibratfarzandlari.dto.ResponseDto;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class LessonServiceImpl implements LessonService {
    @Autowired
    private LessonRepository lessonRepository;

    @Override
    public ResponseDto<Void> save(Lesson lesson) {
        try {
            lessonRepository.save(lesson);
            return new ResponseDto<>(true, "OK");
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Page<Lesson>> findAllByTypeId(Long typeId, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Lesson> res = lessonRepository.findByStatusAndActiveAndTypeIdOrderByIdAsc(pageable, "open", true, typeId);
            return new ResponseDto<>(true, "Ok", res);
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Lesson> findByStatus(String status) {
        try {
            Lesson checkStatus = lessonRepository.findByStatus(status);
            if (checkStatus == null)
                return new ResponseDto<>(false, "Not found");
            return new ResponseDto<>(true, "Ok", checkStatus);
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Lesson> nextLesson(Long id, Long typeId) {
        try {
            List<Lesson> list = lessonRepository.findByStatusAndActiveAndTypeIdOrderByIdAsc(
                    "open", true, typeId
            );
            for (int i = 0; i < list.size(); i++) {

                if (id.equals(list.get(i).getId())) {
                    if (i + 1 == list.size()) {
                        return new ResponseDto<>(false, "Eng oxirgisi");
                    }
                    return new ResponseDto<>(true, "Ok", list.get(i + 1));
                }
            }
            return new ResponseDto<>(false, "Not found");
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Lesson> oldLesson(Long id, Long typeId) {
        try {
            List<Lesson> list = lessonRepository.findByStatusAndActiveAndTypeIdOrderByIdAsc(
                    "open", true, typeId
            );

            for (int i = 0; i < list.size(); i++) {
                if (id.equals(list.get(i).getId())) {
                    if (i == 0) {
                        return new ResponseDto<>(false, "Not found");
                    }
                    return new ResponseDto<>(true, "Ok", list.get(i - 1));
                }
            }
            return new ResponseDto<>(false, "Not found");
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Lesson> findById(Long id) {
        try {
            Optional<Lesson> lOp = lessonRepository.findById(id);
            if (lOp.isEmpty())
                return new ResponseDto<>(false, "Not found lesson");
            else {
                Lesson lesson = lOp.get();
                if (!lesson.getActive() || !lesson.getStatus().equals("open"))
                    return new ResponseDto<>(false, "Not found lesson");
                else return new ResponseDto<>(true, "Ok", lesson);
            }
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }
}
