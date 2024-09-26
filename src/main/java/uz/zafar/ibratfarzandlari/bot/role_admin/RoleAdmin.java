package uz.zafar.ibratfarzandlari.bot.role_admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Video;
import uz.zafar.ibratfarzandlari.bot.TelegramBot;
import uz.zafar.ibratfarzandlari.db.domain.User;
import uz.zafar.ibratfarzandlari.db.service.UserService;

import static uz.zafar.ibratfarzandlari.bot.role_admin.KybText.back;

@Controller
@Log4j2
@RequiredArgsConstructor
public class RoleAdmin {
    private final AdminFunction function;

    public void mainMenu(User user, Update update, int size) {
        String eventCode = user.getEventCode();
        if (update.hasMessage()) {

            Message message = update.getMessage();
            if (eventCode.equals("reklama")){
                if (message.hasText()){
                    if (message.getText().equals("/start") || message.getText().equals(back))function.start(user);
                    else {
                        function.reklama(user  , message.getMessageId());
                    }
                }else {
                    function.reklama(user  , message.getMessageId());
                }
                return;
            }
            if (message.hasText()) {
                String text = message.getText();
                if (text.equals("/start")) {
                    function.start(user);
                } else {
                    switch (eventCode) {
                        case "menu" -> function.menu(user, text);
                        case "choose courses" -> function.chooseCourses(user, text);
                        case "get course name for add course" -> function.getCourseNameForAddCourse(user, text);
                        case "is add course" -> function.isAddCourse(user, text);
                        case "crud course" -> function.crudCourse(user, text);
                        case "edit course name" -> function.editCourseName(user, text);
                        case "edit request contact" -> function.editRequestContact(user  , text );
                        case "edit bot password" -> function.editBotPassword(user , text );
                        case "edit new password" ,"edit new login get" -> function.editProfile(user, text , eventCode);
                        case "is delete course" -> function.isDeleteCourse(user, text);
                        case "get types from course" -> function.getTypesFromCourse(user, text);
                        case "add new course type" -> function.addNewCourseType(user, text);
                        case "is add type to course" -> function.isAddTypeToCourse(user, text);
                        case "type crud from course" -> function.typeCrudFromCourse(user, text, size);
                        case "get lessons from type" -> function.getLessonsFromType(user, text);
                        case "get add lesson to type" -> function.getAddLessonToType(user, text, size);
                        case "get desc from lesson" -> function.getDescFromLesson(user, text);
                        case "get video of lesson" -> function.getVideoOfLesson(user, text);
                        case "get edit lesson name form type" -> function.getEditLessonNameFormType(user,text);
                        case "get edit lesson desc form type" -> function.getEditLessonDescFormType(user , text );
                        case "get edit lesson video form type" -> function.getEditLessonVideoFormType(user , text );
                        case "delete type name from course" -> function.deleteTypeNameFromCourse(user , text );
                        case "edit about bot" -> function.editAboutBot(user , text);
                    }
                }
            } else if (message.hasVideo()) {
                Video video = message.getVideo();
                if (eventCode.equals("get video of lesson")) function.getVideoOfLesson(user, video,size);
                else if (eventCode.equals("get edit lesson video form type")) function.getEditLessonVideoFormType(user , video );
            }
        } else if (update.hasCallbackQuery()) {

            if (eventCode.equals("get lessons from type")) {

                String caption = update.getCallbackQuery().getMessage().getCaption();

                function.getLessonsFromType(user, update.getCallbackQuery(), size);
            }
        }
    }
}
