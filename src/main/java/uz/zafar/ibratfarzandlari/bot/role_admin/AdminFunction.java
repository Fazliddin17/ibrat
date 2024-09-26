package uz.zafar.ibratfarzandlari.bot.role_admin;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Video;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.zafar.ibratfarzandlari.bot.TelegramBot;
import uz.zafar.ibratfarzandlari.bot.role_user.UserFunction;
import uz.zafar.ibratfarzandlari.bot.role_user.UserKyb;
import uz.zafar.ibratfarzandlari.db.domain.*;
import uz.zafar.ibratfarzandlari.db.repositories.BotInformationRepository;
import uz.zafar.ibratfarzandlari.db.service.CourseService;
import uz.zafar.ibratfarzandlari.db.service.LessonService;
import uz.zafar.ibratfarzandlari.db.service.TypeService;
import uz.zafar.ibratfarzandlari.db.service.UserService;
import uz.zafar.ibratfarzandlari.db.service.impl.TypeServiceImpl;
import uz.zafar.ibratfarzandlari.dto.ResponseDto;

import java.util.List;

import static uz.zafar.ibratfarzandlari.bot.role_admin.KybText.*;
import static uz.zafar.ibratfarzandlari.bot.role_admin.KybText.back;
import static uz.zafar.ibratfarzandlari.bot.role_admin.KybText.menu;
import static uz.zafar.ibratfarzandlari.bot.role_admin.KybText.crudCourseBtn;
import static uz.zafar.ibratfarzandlari.bot.role_admin.KybText.addCourse;
import static uz.zafar.ibratfarzandlari.bot.role_admin.KybText.isAddCourse;

@Controller
@Log4j2
@RequiredArgsConstructor
public class AdminFunction {
    private final TelegramBot bot;
    private final UserService userService;
    private final AdminKyb kyb;
    private final CourseService courseService;
    private final TypeService typeService;
    private final LessonService lessonService;
    private final BotInformationRepository botInformationRepository;
    private final UserKyb userKyb;


    public void start(User user) {
        user.setEventCode("menu");
        bot.sendMessage(user.getChatId(), "Asosiy menyudasiz", kyb.menuBtn);
        userService.save(user);
    }

    protected void menu(User user, String text) {
        Long chatId = user.getChatId();
        boolean success = false;
        if (text.equals(menu[0])) {
            user.setEventCode("choose courses");
            String msg;
            List<Course> courses = courseService.findAll().getData();
            if (courses.isEmpty()) {
                msg = "Hozirda kurslar mavjud emas, Kurs qo'shish tugmasini bosib kurs qo'shishingiz mumkin";
            } else {
                msg = "Barcha kurslarning ro'yxati";
            }
            bot.sendMessage(chatId, msg, kyb.coursesBtn(courses));
            userService.save(user);
        } else if (text.equals(menu[1])) {
            String caption;
            if (botInformationRepository.findAll().isEmpty()) {
                botInformationRepository.save(new BotInformation(true));
            }
            success = botInformationRepository.findAll().get(0).getAboutWe() != null;
            if (!success) caption = "Hozirda biz haqimizda menyusi bo'm bo'sh";
            else {
                caption = botInformationRepository.findAll().get(0).getAboutWe();
            }
            bot.sendMessage(chatId, caption, kyb.editAboutBot);
            user.setEventCode("edit about bot");
            userService.save(user);
        } else if (text.equals(menu[2])) {
            bot.sendMessage(chatId, "Reklamangizni yuboring", kyb.back);
            user.setEventCode("reklama");
            userService.save(user);
        } else if (text.equals(menu[3])) {
            if (botInformationRepository.findAll().isEmpty()) {
                botInformationRepository.save(new BotInformation(true, "me_zafar", "zafar123"));
            }

            boolean requestContact = botInformationRepository.findAll().get(0).getRequestContact();
            bot.sendMessage(chatId, "Foydalanuvchi botga kirgan vaqti kontakt so'ralsinmi\n\nEski holati: " + (requestContact ? "So'raladi" : "So'ralmidi"), kyb.checkContact(requestContact));
            user.setEventCode("edit request contact");
            userService.save(user);
        } else if (text.equals(menu[4])) {
            String msg;
            BotInformation botInformation = botInformationRepository.findAll().get(0);
            msg = """
                    Login: %s
                    Parol: %s""".formatted(botInformation.getLogin(), botInformation.getPassword());
            bot.sendMessage(chatId, msg, kyb.setKeyboards(new String[]{"Loginni o'zgartirish", "Parolni o'zgartirish", back}, 1));
            user.setEventCode("edit bot password");
            userService.save(user);
        } else if (text.equals(menu[5])) {
            user.setRole("user");
            userService.save(user);
            String phone = user.getPhone();
            BotInformation botInformation;
            if (botInformationRepository.findAll().isEmpty()) {
                botInformation = new BotInformation();
                botInformation.setRequestContact(true);
                botInformationRepository.save(botInformation);
            } else botInformation = botInformationRepository.findAll().get(0);
            if (botInformation.getRequestContact() && phone == null) {
                String nickname = user.getFirstname();
                if (user.getLastname() != null) nickname = nickname.concat(" " + user.getLastname());
                bot.sendMessage(
                        user.getChatId(),
                        "Assalomu aleykum, <b>%s</b>. Botdan foydalanishingi uchun ro'yxatdan o'tishingiz kerak".formatted(nickname),
                        kyb.requestContact("\uD83D\uDCDE Ro'yxatdan o'tish")
                );
                user.setEventCode("request contact");
                userService.save(user);
            } else {
                bot.sendMessage(user.getChatId(), "Asosiy menyudasiz, o'zingizga kerakli menyulardan birini tanlang", userKyb.menu());
                user.setEventCode("menu");
                userService.save(user);
            }

        }
    }

    public void editBotPassword(User user, String text) {
        if (text.equals("Parolni o'zgartirish")) {
            bot.sendMessage(user.getChatId(), "Yangi parolni kiriting", true);
            user.setEventCode("edit new password");
        } else if (text.equals("Loginni o'zgartirish")) {
            bot.sendMessage(user.getChatId(), "Yangi loginni kiriting", true);
            user.setEventCode("edit new login get");
        } else if (text.equals(back)) {
            start(user);
        }
        userService.save(user);
    }

    protected void chooseCourses(User user, String text) {
        Long chatId = user.getChatId();
        if (text.equals(addCourse)) {
            user.setEventCode("get course name for add course");
            bot.sendMessage(chatId, "Kurs qo'shish uchun kursning nomini kiriting aks holda orqaga tugmasini bosing", kyb.setKeyboards(new String[]{back}, 1));
            userService.save(user);
        } else if (text.equals(back)) {
            start(user);
        } else {
            ResponseDto<Course> checkCourse = courseService.findByName(text);
            if (checkCourse.isSuccess()) {
                Course course = checkCourse.getData();
                user.setCourseId(course.getId());
                user.setEventCode("crud course");
                bot.sendMessage(chatId, """
                        Kurs nomi: %s
                                                
                        Quyidagilardan birini tanlang""".formatted(course.getName()), kyb.crudCourse);
                userService.save(user);
            }
        }
    }

    protected void getCourseNameForAddCourse(User user, String text) {
        if (text.equals(back)) {
            menu(user, menu[0]);
        } else {
            Course course;
            ResponseDto<Course> checkCourse = courseService.findByStatus("draft");
            if (checkCourse.isSuccess()) {
                course = checkCourse.getData();
            } else {
                course = new Course();
                course.setActive(false);
                course.setStatus("draft");
            }
            course.setName(text);
            ResponseDto<Void> save = courseService.save(course);
            if (save.isSuccess()) {
                user.setEventCode("is add course");
                bot.sendMessage(user.getChatId(), "Muvaffaqiyatli saqlandi");
                bot.sendMessage(user.getChatId(), """
                        Ushbu kursni rostanam qo'shmoqchimisiz
                                                
                        Kurs nomi: %s""".formatted(course.getName()), kyb.isAddCourse);
                userService.save(user);
            } else {
                bot.sendMessage(user.getChatId(), "Bu nom band iltimos boshqa nom kiriting", kyb.setKeyboards(new String[]{back}, 1));
            }
        }
    }

    protected void isAddCourse(User user, String text) {
        ResponseDto<Course> checkCourse = courseService.findByStatus("draft");
        Course course = checkCourse.getData();
        if (text.equals(isAddCourse[0])) {
            course.setActive(true);
            course.setStatus("open");
            courseService.save(course);
            bot.sendMessage(user.getChatId(), "Muvaffaqiyatli qo'shildi");
        } else if (text.equals(isAddCourse[1])) {
            bot.sendMessage(user.getChatId(), "Ushbu kurs qo'shilmadi");
        } else return;
        menu(user, menu[0]);
    }

    public void crudCourse(User user, String text) {//qaytib kelman
        Long chatId = user.getChatId();
        if (text.equals(crudCourseBtn[0])) {
            user.setEventCode("edit course name");
            bot.sendMessage(chatId, """
                    Kurs nomini o'zgartirish uchun yangi kursning nomini kiriting

                    Avvalgi nomi: <code>%s</code>""".formatted(course(user).getName()), kyb.back);
            userService.save(user);
        } else if (text.equals(crudCourseBtn[1])) {
            user.setEventCode("is delete course");
            bot.sendMessage(chatId, """
                    Kursni haqiqatdan ham o'chirmoqchimisiz ?

                    Kurs nomi: <code>%s</code>""".formatted(course(user).getName()), kyb.isAddCourse);
            userService.save(user);
        } else if (text.equals(crudCourseBtn[2])) {
            menu(user, menu[0]);
        } else if (text.equals(crudCourseBtn[3])) {
            List<Type> types = typeService.findAllByCourseId(user.getCourseId()).getData();
            String msg;
            if (types.isEmpty()) {
                msg = "Hozirda kurslarga hech qanaqa dars qo'shilmagan, qo'shish tugmasini bosib qo'shishingiz mumkin";
            } else {
                msg = "O'zingizga kerakli menyuni tanalang";
            }
            bot.sendMessage(chatId, msg, kyb.getAllTypes(types));
            user.setEventCode("get types from course");
            userService.save(user);
        }
    }

    private Course course(User user) {
        return courseService.findById(user.getCourseId()).getData();
    }


    public void editCourseName(User user, String text) {
        Course course = course(user);
        if (text.equals(back)) {
            chooseCourses(user, course.getName());
            return;
        }
        course.setName(text);
        ResponseDto<Void> save = courseService.save(course);
        if (save.isSuccess()) {
            bot.sendMessage(user.getChatId(), "Muvaffaqiyatli o'zgartirildi");
            chooseCourses(user, course.getName());
        } else {
            bot.sendMessage(user.getChatId(), "Bu nom band, iltimos boshqa nom kiriting", kyb.back);
        }
    }

    public void isDeleteCourse(User user, String text) {
        Course course = course(user);
        if (text.equals(isAddCourse[0])) {
            course.setActive(false);
            courseService.save(course);
            bot.sendMessage(user.getChatId(), "Muvaffaqiyatli o'chirildi");
            menu(user, menu[0]);
        } else if (text.equals(isAddCourse[1])) {
            bot.sendMessage(user.getChatId(), "Ushbu kurs o'chirilmadi");
            chooseCourses(user, course.getName());
        } else return;

    }

    public void getTypesFromCourse(User user, String text) {
        Course course = course(user);
        if (text.equals(back)) {
            chooseCourses(user, course.getName());
        } else if (text.equals(add)) {
            bot.sendMessage(user.getChatId(), "Yangi kurs turini kiriting", kyb.back);
            user.setEventCode("add new course type");
            userService.save(user);
        } else {
            ResponseDto<Type> checkType = typeService.findByName(text);
            Type type;
            if (checkType.isSuccess()) {
                type = checkType.getData();
                user.setTypeId(type.getId());
                bot.sendMessage(user.getChatId(), """
                        Kurs turi: %s
                        Qaysi kursga tegishli: %s

                        O'zingizga kerakli menyulardan birini tanlang""".formatted(type.getName(), type.getCourse().getName()), kyb.crudType);
                user.setEventCode("type crud from course");
                userService.save(user);
            }
        }
    }

    public void addNewCourseType(User user, String text) {
        if (text.equals(back)) {
            crudCourse(user, crudCourseBtn[3]);
            return;
        }
        ResponseDto<Type> checkType = typeService.findByStatus("draft");
        Type type;
        if (checkType.isSuccess()) type = checkType.getData();
        else {
            type = new Type();
            type.setActive(false);
            type.setStatus("draft");
        }
        type.setName(text);
        type.setCourse(course(user));
        ResponseDto<Void> save = typeService.save(type);
        if (save.isSuccess()) {
            bot.sendMessage(user.getChatId(), "Muvaffaqiyatli saqlandi, ushbu kurs turini haqiqatdanam qo'shmoqchimisiz", kyb.isAddCourse);
            user.setEventCode("is add type to course");
            userService.save(user);
        } else {
            bot.sendMessage(user.getChatId(), "Bu nom band, iltimos boshqa nom kiriting", kyb.back);
        }
    }

    public void isAddTypeToCourse(User user, String text) {
        Type type = typeService.findByStatus("draft").getData();
        if (text.equals(isAddCourse[0])) {
            type.setActive(true);
            type.setStatus("open");
            typeService.save(type);
            bot.sendMessage(user.getChatId(), "Muvaffaqiyatli qo'shildi", kyb.menuBtn);
            crudCourse(user, crudCourseBtn[3]);
        } else if (text.equals(isAddCourse[1])) {
            bot.sendMessage(user.getChatId(), "Ushbu kurs turi qo'shilmadi", kyb.menuBtn);
            crudCourse(user, crudCourseBtn[3]);
        }
    }

    private Type type(User user) {
        return typeService.findById(user.getTypeId()).getData();
    }

    public void typeCrudFromCourse(User user, String text, int size) {
        Type type = type(user);
        Long chatId = user.getChatId();
        if (text.equals(crudType[0])) {
            bot.sendMessage(chatId, "Ushbu kurs turining yangi nomini kiriting: \n\n\nEski nomi: %s".formatted(type.getName()), kyb.back);
            user.setEventCode("get edit type name from course");
            userService.save(user);
        } else if (text.equals(crudType[1])) {
            bot.sendMessage(chatId, "Ushbu kurs turini haqiqatdanam o'chirmoqchimisiz ?\n\n\nNomi: %s".formatted(type.getName()), kyb.isAddCourse);
            user.setEventCode("delete type name from course");
            userService.save(user);
        } else if (text.equals(crudType[2])) {
            crudCourse(user, crudCourseBtn[3]);
        } else if (text.equals(crudType[3])) {
            bot.sendMessage(chatId, "Darslar", kyb.setKeyboards(new String[]{back, addLesson}, 2));
            user.setPage(0);
            user.setLessonId(null);
            user.setVideo(false);
            bot.sendMessage(chatId, "O'zingizga kerakli darsni tanlang", kyb.getLessons(lessonService.findAllByTypeId(type.getId(), user.getPage(), size).getData(), user.getVideo(), user.getLessonId()));
            user.setEventCode("get lessons from type");
            userService.save(user);
        }
    }

    public void getLessonsFromType(User user, String text) {
        if (text.equals(back)) {
            getTypesFromCourse(user, typeService.findById(user.getTypeId()).getData().getName());
        } else if (text.equals(addLesson)) {
            user.setEventCode("get add lesson to type");
            bot.sendMessage(user.getChatId(), "Yangi dars nomini kiriting", kyb.back);
            userService.save(user);
        }
    }

    private String lessonCaption(Lesson lesson) {
        return """
                %s

                %s
                """.formatted(lesson.getName(), lesson.getDescription());
    }

    private Lesson lesson(User user) {
        return lessonService.findById(user.getLessonId()).getData();
    }

    @SneakyThrows
    public void getLessonsFromType(User user, CallbackQuery callbackQuery, int size) {

        int messageId = callbackQuery.getMessage().getMessageId();
        String data = callbackQuery.getData();
        Boolean video = user.getVideo();
        Long chatId = user.getChatId();
        Lesson lesson;
        Page<Lesson> lessonPage = lessonService.findAllByTypeId(user.getTypeId(), user.getPage(), size).getData();
        switch (data) {
            case "next page" -> {
                Page<Lesson> newPage = lessonService.findAllByTypeId(user.getTypeId(), user.getPage() + 1, size).getData();
                if (newPage.isEmpty()) {
                    bot.alertMessage(callbackQuery, "Malumotlar yo'q");
                    return;
                }
                user.setPage(user.getPage() + 1);
                userService.save(user);
                if (user.getVideo()) {
                    EditMessageCaption editMessageCaption = new EditMessageCaption();
                    editMessageCaption.setMessageId(messageId);
                    editMessageCaption.setChatId(chatId);
                    lesson = lessonService.findById(user.getLessonId()).getData();
                    editMessageCaption.setCaption(lessonCaption(lesson));
                    editMessageCaption.setReplyMarkup(kyb.getLessons(newPage, user.getVideo(), user.getLessonId()));
                    editMessageCaption.setParseMode(ParseMode.HTML);
                    bot.execute(editMessageCaption);
                } else {
                    EditMessageText editMessageCaption = new EditMessageText();
                    editMessageCaption.setMessageId(messageId);
                    editMessageCaption.setChatId(chatId);
                    editMessageCaption.setText(callbackQuery.getMessage().getText() + ".");
                    editMessageCaption.setReplyMarkup(kyb.getLessons(newPage, user.getVideo(), user.getLessonId()));
                    editMessageCaption.setParseMode(ParseMode.HTML);
                    bot.execute(editMessageCaption);
                }
            }
            case "old page" -> {
                if (user.getPage() == 0) {
                    bot.alertMessage(callbackQuery, "Siz allaqachon 1-sahifadasiz");
                    return;
                }
                Page<Lesson> newPage = lessonService.findAllByTypeId(user.getTypeId(), user.getPage() - 1, size).getData();
                if (newPage.isEmpty()) {
                    bot.alertMessage(callbackQuery, "Malumotlar yo'q");
                    return;
                }
                user.setPage(user.getPage() - 1);
                userService.save(user);
                if (user.getVideo()) {
                    lesson = lessonService.findById(user.getLessonId()).getData();
                    EditMessageCaption editMessageCaption = new EditMessageCaption();
                    editMessageCaption.setMessageId(messageId);
                    editMessageCaption.setChatId(chatId);
                    editMessageCaption.setCaption(lessonCaption(lesson));
                    editMessageCaption.setReplyMarkup(kyb.getLessons(newPage, user.getVideo(), user.getLessonId()));
                    editMessageCaption.setParseMode(ParseMode.HTML);
                    bot.execute(editMessageCaption);
                } else {
                    EditMessageText editMessageCaption = new EditMessageText();
                    editMessageCaption.setMessageId(messageId);
                    editMessageCaption.setChatId(chatId);
                    editMessageCaption.setText(callbackQuery.getMessage().getText());
                    editMessageCaption.setReplyMarkup(kyb.getLessons(newPage, user.getVideo(), user.getLessonId()));
                    editMessageCaption.setParseMode(ParseMode.HTML);
                    bot.execute(editMessageCaption);
                }
            }
            case "next lesson" -> {
                ResponseDto<Lesson> dto = lessonService.nextLesson(user.getLessonId(), user.getTypeId());
                if (!dto.isSuccess()) {
                    bot.alertMessage(callbackQuery, "Ma'lumotlar yo'q");
                    return;
                }
                lesson = dto.getData();
                user.setLessonId(lesson.getId());
                userService.save(user);
                EditMessageMedia editMessageMedia = new EditMessageMedia();
                InputMedia videoMedia = new InputMediaVideo();
                videoMedia.setCaption(lessonCaption(lesson));
                videoMedia.setMedia(lesson.getVideo());
                videoMedia.setParseMode("HTML");
                editMessageMedia.setMessageId(messageId);
                editMessageMedia.setReplyMarkup(kyb.getLessons(lessonPage, true, lesson.getId()));
                editMessageMedia.setChatId(chatId);
                editMessageMedia.setMedia(videoMedia);
                bot.execute(editMessageMedia);
            }
            case "old lesson" -> {
                ResponseDto<Lesson> dto = lessonService.oldLesson(user.getLessonId(), user.getTypeId());
                if (!dto.isSuccess()) {
                    bot.alertMessage(callbackQuery, "Allaqachon 1-sahifadasiz");
                    return;
                }
                lesson = dto.getData();
                user.setLessonId(lesson.getId());
                userService.save(user);
                EditMessageMedia editMessageMedia = new EditMessageMedia();
                InputMedia videoMedia = new InputMediaVideo();
                videoMedia.setCaption(lessonCaption(lesson));
                videoMedia.setMedia(lesson.getVideo());
                videoMedia.setParseMode("HTML");
                editMessageMedia.setMessageId(messageId);
                editMessageMedia.setReplyMarkup(kyb.getLessons(lessonPage, true, lesson.getId()));
                editMessageMedia.setChatId(chatId);
                editMessageMedia.setMedia(videoMedia);
                bot.execute(editMessageMedia);
            }
            case "back to lessons" -> {
                EditMessageCaption editMessageCaption = new EditMessageCaption();
                editMessageCaption.setChatId(chatId);
                editMessageCaption.setMessageId(messageId);
                editMessageCaption.setParseMode("HTML");
                editMessageCaption.setCaption(lessonCaption(lesson(user)));
                editMessageCaption.setReplyMarkup(kyb.getLessons(lessonService.findAllByTypeId(type(user).getId(), user.getPage(), size).getData(), user.getVideo(), user.getLessonId()));
                bot.execute(editMessageCaption);
            }
            case "edit name" -> {
                bot.deleteMessage(chatId, messageId);
                bot.sendMessage(chatId, "Darsning yangi nomini kiriting\n\nAvvalgi nomi: <code>" + lesson(user).getName() + "</code>", kyb.back);
                user.setEventCode("get edit lesson name form type");
                userService.save(user);
            }
            case "edit desc" -> {
                bot.deleteMessage(chatId, messageId);
                bot.sendMessage(chatId, "Darsning yangi tavsifini kiriting\n\nAvvalgi nomi: <code>" + lesson(user).getDescription() + "</code>", kyb.back);
                user.setEventCode("get edit lesson desc form type");
                userService.save(user);
            }
            case "edit video" -> {
                bot.deleteMessage(chatId, messageId);
                bot.sendVideo(user.getChatId(), lesson(user).getVideo(), null, null, true);
                bot.sendMessage(chatId, "Darsning yangi vidyosini yuboring eski vidyo tepadagi vidyoda", kyb.back);
                user.setEventCode("get edit lesson video form type");
                userService.save(user);
            }
            case "delete" -> {
                EditMessageCaption editMessageCaption = new EditMessageCaption();
                editMessageCaption.setChatId(user.getChatId());
                editMessageCaption.setMessageId(messageId);
                editMessageCaption.setCaption(lessonCaption(lesson(user)) + "\n\nUshbu darsnni haqiqatdanam o'chirmoqchimisiz ?");
                editMessageCaption.setReplyMarkup(kyb.isDelete());
                bot.execute(editMessageCaption);
            }
            case "yes delete" -> {
                Lesson lesson1 = lesson(user);
                lesson1.setActive(false);
                lessonService.save(lesson1);
                bot.alertMessage(callbackQuery, "Muvaffaqiyatli o'chirildi");
                bot.deleteMessage(chatId, messageId);
                bot.sendMessage(chatId, "Darslar", kyb.setKeyboards(new String[]{back, addLesson}, 2));
                user.setPage(0);
                user.setLessonId(null);
                user.setVideo(false);
                bot.sendMessage(chatId, "O'zingizga kerakli darsni tanlang", kyb.getLessons(lessonService.findAllByTypeId(type(user).getId(), user.getPage(), size).getData(), user.getVideo(), user.getLessonId()));
                user.setEventCode("get lessons from type");
                userService.save(user);
            }
            case "no delete" -> {
                lesson = lesson(user);
                EditMessageMedia editMessageMedia = new EditMessageMedia();
                InputMedia videoMedia = new InputMediaVideo();
                videoMedia.setCaption(lessonCaption(lesson));
                videoMedia.setMedia(lesson.getVideo());
                videoMedia.setParseMode("HTML");
                editMessageMedia.setMessageId(messageId);
                editMessageMedia.setReplyMarkup(kyb.crudLesson());
                editMessageMedia.setChatId(chatId);
                editMessageMedia.setMedia(videoMedia);
                bot.execute(editMessageMedia);
            }
            default -> {
                Long lessonId = Long.valueOf(data);
                lesson = lessonService.findById(lessonId).getData();
                user.setLessonId(lessonId);
                userService.save(user);
                if (user.getVideo()) {
                    EditMessageMedia editMessageMedia = new EditMessageMedia();
                    InputMedia videoMedia = new InputMediaVideo();
                    videoMedia.setCaption(lessonCaption(lesson));
                    videoMedia.setMedia(lesson.getVideo());
                    videoMedia.setParseMode("HTML");
                    editMessageMedia.setMessageId(messageId);
                    editMessageMedia.setReplyMarkup(kyb.crudLesson());
/*
                editMessageMedia.setReplyMarkup(kyb.getLessons(
                        lessonPage, true, lessonId
                ));
*/
                    editMessageMedia.setChatId(chatId);
                    editMessageMedia.setMedia(videoMedia);
                    bot.execute(editMessageMedia);
                } else {
                    user.setVideo(true);
                    userService.save(user);
                    bot.deleteMessage(chatId, messageId);
                    bot.sendVideo(chatId, lesson.getVideo(), lessonCaption(lesson), kyb.crudLesson(), true);
/*
                bot.sendVideo(chatId, lesson.getVideo(), lessonCaption(lesson), kyb.getLessons(
                        lessonPage, user.getVideo(), lessonId
                ), true);
*/
                }
            }
        }
    }

    public void getAddLessonToType(User user, String text, int size) {
        if (text.equals(back)) {
            typeCrudFromCourse(user, crudType[3], size);
            return;
        }
        ResponseDto<Lesson> checkLesson = lessonService.findByStatus("draft");
        Lesson lesson;
        if (checkLesson.isSuccess()) lesson = checkLesson.getData();
        else {
            Type type = type(user);
            lesson = new Lesson();
            lesson.setActive(false);
            lesson.setType(type);
            lesson.setStatus("draft");
            lessonService.save(lesson);
            checkLesson = lessonService.findByStatus("draft");
            lesson = checkLesson.getData();
        }
        lesson.setName(text);
        lessonService.save(lesson);
        bot.sendMessage(user.getChatId(), "Ushbu dars uchun tavsif kiriting", true);
        user.setEventCode("get desc from lesson");
        userService.save(user);
    }

    public void getDescFromLesson(User user, String text) {
        Lesson lesson = lessonService.findByStatus("draft").getData();
        lesson.setDescription(text);
        lessonService.save(lesson);
        bot.sendMessage(user.getChatId(), "Ushbu darsning vidyosini yuboring", true);
        user.setEventCode("get video of lesson");
        userService.save(user);
    }

    public void getVideoOfLesson(User user, String text) {
        bot.sendMessage(user.getChatId(), "Xabar yuborish mumkin emas, iltismo video ni yuboring", true);
    }

    public void getVideoOfLesson(User user, Video video, int size) {
        Lesson lesson = lessonService.findByStatus("draft").getData();
        lesson.setVideo(video.getFileId());
        lesson.setActive(true);
        lesson.setStatus("open");
        lessonService.save(lesson);
        bot.sendMessage(user.getChatId(), "Muvaffaqiiyatli qo'shildi");
        typeCrudFromCourse(user, crudType[3], size);
    }

    public void getEditLessonNameFormType(User user, String text) {
        Lesson lesson = lesson(user);
        if (!text.equals(back)) {
            lesson.setName(text);
            lessonService.save(lesson);
        }
        user.setVideo(true);
        bot.sendVideo(user.getChatId(), lesson.getVideo(), lessonCaption(lesson), kyb.crudLesson(), true);
        user.setEventCode("get lessons from type");
        userService.save(user);
    }

    public void getEditLessonDescFormType(User user, String text) {
        Lesson lesson = lesson(user);
        if (!text.equals(back)) {
            lesson.setDescription(text);
            lessonService.save(lesson);
        }
        user.setVideo(true);
        bot.sendVideo(user.getChatId(), lesson.getVideo(), lessonCaption(lesson), kyb.crudLesson(), true);
        user.setEventCode("get lessons from type");
        userService.save(user);
    }

    public void getEditLessonVideoFormType(User user, String text) {
        if (text.equals(back)) {
            Lesson lesson = lesson(user);
            user.setVideo(true);
            bot.sendVideo(user.getChatId(), lesson.getVideo(), lessonCaption(lesson), kyb.crudLesson(), true);
            user.setEventCode("get lessons from type");
            userService.save(user);
        }
    }

    public void getEditLessonVideoFormType(User user, Video video) {
        Lesson lesson = lesson(user);
        lesson.setVideo(video.getFileId());
        lessonService.save(lesson);
        bot.sendVideo(user.getChatId(), lesson.getVideo(), lessonCaption(lesson), kyb.crudLesson(), true);
        user.setEventCode("get lessons from type");
        userService.save(user);
    }

    public void deleteTypeNameFromCourse(User user, String text) {
        Type type = type(user);
        if (text.equals(isAddCourse[0])) {
            type.setActive(false);
            typeService.save(type);
            bot.sendMessage(user.getChatId(), "Muvaffaqiyatli o'chirildi");
            crudCourse(user, crudCourseBtn[3]);
        } else if (text.equals(isAddCourse[1])) {
            bot.sendMessage(user.getChatId(), "Ushbu kurs o'chirlmadi");
            getTypesFromCourse(user, type.getName());
        }
    }

    public void editAboutBot(User user, String text) {
        if (text.equals(back)) start(user);
        else if (editAboutBot[0].equals(text)) {
            bot.sendMessage(user.getChatId(), "Biz haqimizda menyusi uchun matn kiriting", kyb.back);
        } else {
            BotInformation botInformation = botInformationRepository.findAll().get(0);
            botInformation.setAboutWe(text);
            botInformationRepository.save(botInformation);
            bot.sendMessage(user.getChatId(), "Muvaffaqiyatli o'zgartirildi");
            menu(user, menu[1]);
        }
    }

    public void reklama(User user, Integer messageId) {
        long count = 0;
        for (User datum : userService.findAllByRole("user").getData()) {
            try {
                bot.execute(CopyMessage.builder().fromChatId(user.getChatId()).chatId(datum.getChatId()).messageId(messageId).build());
                count++;
            } catch (TelegramApiException ignore) {

            }
        }
        bot.sendMessage(user.getChatId(), """
                Sizning xabaringiz %d kishidan %d kishiga muvaffaqiyatli yuborildi""".formatted(userService.findAllByRole("user").getData().size(), count));
        start(user);
    }

    public void editRequestContact(User user, String text) {
        BotInformation botInformation = botInformationRepository.findAll().get(0);
        if (text.equals("Kontakt so'rovini olib tashlash") || text.equals("Kontakt so'rash")) {
            botInformation.setRequestContact(!botInformation.getRequestContact());
            botInformationRepository.save(botInformation);
            bot.sendMessage(user.getChatId(), "Muvaffaqiyatli o'zgartirildi");
        } else {
            if (text.equals(back)) {
                start(user);
            }
            return;
        }
        menu(user, menu[3]);
    }

    public void editProfile(User user, String text, String eventCode) {
        BotInformation botInformation = botInformationRepository.findAll().get(0);
        if (eventCode.equals("edit new login get")) {
            botInformation.setLogin(text);
        } else if (eventCode.equals("edit new password")) {
            botInformation.setPassword(text);
        }
        botInformationRepository.save(botInformation);
        bot.sendMessage(user.getChatId(), "Muvaffaqiyatli o'zgartirildi");
        menu(user, menu[4]);
    }
}
