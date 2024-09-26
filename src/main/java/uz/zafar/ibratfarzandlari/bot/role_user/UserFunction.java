package uz.zafar.ibratfarzandlari.bot.role_user;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;
import uz.zafar.ibratfarzandlari.bot.TelegramBot;
import uz.zafar.ibratfarzandlari.bot.role_admin.AdminFunction;
import uz.zafar.ibratfarzandlari.db.domain.*;
import uz.zafar.ibratfarzandlari.db.repositories.BotInformationRepository;
import uz.zafar.ibratfarzandlari.db.service.CourseService;
import uz.zafar.ibratfarzandlari.db.service.LessonService;
import uz.zafar.ibratfarzandlari.db.service.TypeService;
import uz.zafar.ibratfarzandlari.db.service.UserService;
import uz.zafar.ibratfarzandlari.dto.ResponseDto;

import java.util.List;

import static uz.zafar.ibratfarzandlari.bot.role_user.KybText.back;
import static uz.zafar.ibratfarzandlari.bot.role_user.KybText.menu;

@Controller
@RequiredArgsConstructor
@Log4j2
public class UserFunction {
    private final TelegramBot bot;
    private final UserService userService;
    private final UserKyb kyb;
    private final CourseService courseService;
    private final TypeService typeService;
    private final LessonService lessonService;
    private final BotInformationRepository botInformationRepository;
    private final AdminFunction adminFunction;

    public void start(User user) {
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
            bot.sendMessage(user.getChatId(), "Asosiy menyudasiz, o'zingizga kerakli menyulardan birini tanlang", kyb.menu());
            user.setEventCode("menu");
            userService.save(user);
        }
    }

    public void requestContact(User user) {
        bot.sendMessage(user.getChatId(), "Iltimos, tugmalardan foydalaning", kyb.requestContact("\uD83D\uDCDE Ro'yxatdan o'tish"));
    }

    public void requestContact(User user, Contact contact) {
        if (user.getChatId().equals(contact.getUserId())) {
            String phone = contact.getPhoneNumber();
            if (phone.charAt(0) != '+') phone = ("+" + phone);
            user.setPhone(phone);
            user.setEventCode("menu");
            bot.sendMessage(user.getChatId(), "Asosiy menyudasiz, o'zingizga kerakli menyulardan birini tanlang", kyb.menu());
            userService.save(user);
        } else {
            bot.sendMessage(user.getChatId(), "Iltimos, tugmalardan foydalaning", kyb.requestContact("\uD83D\uDCDE Ro'yxatdan o'tish"));
        }
    }

    public void menu(User user, String text, String username) {
        if (text.equals(back)) {
            start(user);
        } else if (text.equals(menu[0])) {
            bot.sendMessage(user.getChatId(), "Quyidagilardan birini tanlang", kyb.getCourses(courseService.findAll().getData()));
            user.setEventCode("get courses");
            userService.save(user);
        } else if (text.equals(menu[1])) {
            bot.sendMessage(user.getChatId(), "Izoh va taklifingizni qoldiring", kyb.back);
            user.setEventCode("get announcement");
            userService.save(user);
        } else if (text.equals(menu[2])) {
            List<BotInformation> list = botInformationRepository.findAll();
            bot.sendMessage(user.getChatId(), list.get(0).getAboutWe() == null ? "Mavjud emas" : list.get(0).getAboutWe(), kyb.back);
        } else if (text.equals(menu[3])) {
            String count = String.format("%,d", userService.findAllByRole("user").getData().size());
            String caption = """
                    👨🏻‍💻 Aktiv obunachilar soni — %s ta
                                        
                                        
                    📊  %s statistikasi
                    """.formatted(
                    count, username
            );
            bot.sendMessage(user.getChatId(), caption, kyb.back);
        }
    }

    public void getCourses(User user, String text) {
        if (text.equals(back)) {
            start(user);
        } else {
            ResponseDto<Course> checkCourse = courseService.findByName(text);
            if (checkCourse.isSuccess()) {
                Course course = checkCourse.getData();
                List<Type> types = typeService.findAllByCourseId(course.getId()).getData();
                if (types.isEmpty()) {
                    bot.sendMessage(user.getChatId(), "Tez orada %s darslari joylanadi, boshqa darslardan foydalanib turing".formatted(text), kyb.getCourses(courseService.findAll().getData()));
                } else {
                    bot.sendMessage(user.getChatId(), "Quyidagilardan birini tanlang", kyb.getTypes(types));
                    user.setCourseId(course.getId());
                    user.setEventCode("get types");
                    userService.save(user);
                }
            }
        }
    }

    public void getTypes(User user, String text, int size, String username) {
        if (text.equals(back)) {
            menu(user, menu[0], username);
        } else {
            ResponseDto<Type> checkType = typeService.findByName(text);
            if (checkType.isSuccess()) {
                Type type = checkType.getData();
                user.setPage(0);
                user.setVideo(false);
                user.setLessonId(null);
                user.setTypeId(type.getId());
                Page<Lesson> lessonPage = lessonService.findAllByTypeId(type.getId(), user.getPage(), size).getData();
                if (lessonPage.getContent().isEmpty()) {
                    bot.sendMessage(user.getChatId(), "Tez orada %s darslari qo'shiladi, boshqa darslarimizdan foydalanib turing".formatted(text), kyb.getTypes(typeService.findAllByCourseId(user.getCourseId()).getData()));
                    return;
                }
                bot.sendMessage(user.getChatId(), "Darslar:", kyb.back);
                bot.sendMessage(user.getChatId(), "O'zingizga kerakli darslardan birini tanlang\n\n" + ("""
                        %s - %s darslari""".formatted(course(user).getName(), type(user).getName())), kyb.getLessons(
                        lessonPage, user.getVideo(), user.getLessonId()
                ));
                user.setEventCode("get lessons");
                userService.save(user);
            }
        }
    }

    private String lessonCaption(Lesson lesson) {
        return """
                %s - %s

                %s - %s darslari""".formatted(
                lesson.getName(), lesson.getDescription(),
                lesson.getType().getCourse().getName(),
                lesson.getType().getName()
        );
    }

    @SneakyThrows
    public void getLessons(User user, int messageId, String data, CallbackQuery callbackQuery, int size) {
        Integer page = user.getPage();
        if (data.equals("next page")) {
            page++;
        } else if (data.equals("old page")) {
            if (user.getPage() == 0) {
                bot.alertMessage(callbackQuery, "Allaqachon 1-sahifadasiz");
                return;
            }
            page--;
        } else if (data.equals("next lesson")) {
            ResponseDto<Lesson> dto = lessonService.nextLesson(user.getLessonId(), user.getTypeId());
            if (!dto.isSuccess()) {
                bot.alertMessage(callbackQuery, "Ma'lumotlar yo'q");
                return;
            }
            Lesson lesson = dto.getData();
            user.setLessonId(lesson.getId());
            userService.save(user);
            EditMessageMedia editMessageMedia = new EditMessageMedia();
            InputMedia videoMedia = new InputMediaVideo();
            videoMedia.setCaption(lessonCaption(lesson));
            videoMedia.setMedia(lesson.getVideo());
            Page<Lesson> lessonPage = lessonService.findAllByTypeId(user.getTypeId(), user.getPage(), size).getData();
            videoMedia.setParseMode("HTML");
            editMessageMedia.setMessageId(messageId);
            editMessageMedia.setReplyMarkup(kyb.getLessons(
                    lessonPage, true, lesson.getId()
            ));
            editMessageMedia.setChatId(user.getChatId());
            editMessageMedia.setMedia(videoMedia);
            bot.execute(editMessageMedia);
        } else if (data.equals("old lesson")) {
            ResponseDto<Lesson> dto = lessonService.oldLesson(user.getLessonId(), user.getTypeId());
            if (!dto.isSuccess()) {
                bot.alertMessage(callbackQuery, "Eng birinchi video darsdasiz");
                return;
            }
            Lesson lesson = dto.getData();
            user.setLessonId(lesson.getId());
            userService.save(user);
            EditMessageMedia editMessageMedia = new EditMessageMedia();
            InputMedia videoMedia = new InputMediaVideo();
            videoMedia.setCaption(lessonCaption(lesson));
            videoMedia.setMedia(lesson.getVideo());
            Page<Lesson> lessonPage = lessonService.findAllByTypeId(user.getTypeId(), user.getPage(), size).getData();
            videoMedia.setParseMode("HTML");
            editMessageMedia.setMessageId(messageId);
            editMessageMedia.setReplyMarkup(kyb.getLessons(
                    lessonPage, true, lesson.getId()
            ));
            editMessageMedia.setChatId(user.getChatId());
            editMessageMedia.setMedia(videoMedia);
            bot.execute(editMessageMedia);
        } else {
            Long lessonId = Long.valueOf(data);
            Lesson lesson = lessonService.findById(lessonId).getData();
            if (lessonId.equals(user.getLessonId())) {
                bot.alertMessage(callbackQuery, lesson.getName());
                return;
            }
            user.setLessonId(lessonId);
            Page<Lesson> lessons = lessonService.findAllByTypeId(user.getTypeId(), page, size).getData();
            if (user.getVideo()) {
                EditMessageMedia editMessageMedia = new EditMessageMedia();
                InputMedia inputMedia = new InputMediaVideo();
                inputMedia.setParseMode("HTML");
                inputMedia.setMedia(lesson.getVideo());
                inputMedia.setCaption(lessonCaption(lesson(user)));
                editMessageMedia.setMedia(inputMedia);
                editMessageMedia.setMessageId(messageId);
                editMessageMedia.setChatId(user.getChatId());
                editMessageMedia.setReplyMarkup(kyb.getLessons(
                        lessons, user.getVideo(), user.getLessonId()
                ));
                bot.execute(editMessageMedia);
            } else {

                user.setVideo(true);

                bot.deleteMessage(user.getChatId(), messageId);
                bot.sendVideo(
                        user.getChatId(), lesson.getVideo(), lessonCaption(lesson(user)), kyb.getLessons(
                                lessons, user.getVideo(), user.getLessonId()
                        ), false
                );
                userService.save(user);
            }
            return;
        }
        Page<Lesson> lessons = lessonService.findAllByTypeId(user.getTypeId(), page, size).getData();
        if (lessons.isEmpty()) {
            bot.alertMessage(callbackQuery, "Malumotlar mavjud emas");
            return;
        }
        user.setPage(page);
        userService.save(user);
        if (user.getVideo()) {
            EditMessageCaption editMessageCaption = new EditMessageCaption();
            editMessageCaption.setMessageId(messageId);
            editMessageCaption.setCaption(lessonCaption(lesson(user)));
            editMessageCaption.setReplyMarkup(kyb.getLessons(
                    lessons, user.getVideo(), user.getLessonId()
            ));
            editMessageCaption.setChatId(user.getChatId());
            bot.execute(editMessageCaption);
        } else {
            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setMessageId(messageId);
            editMessageText.setText("""
                    %s - %s darslari""".formatted(course(user).getName(), type(user).getName()));
            editMessageText.setReplyMarkup(kyb.getLessons(
                    lessons, user.getVideo(), user.getLessonId()
            ));
            editMessageText.setChatId(user.getChatId());
            bot.execute(editMessageText);
        }
    }

    private Lesson lesson(User user) {
        return lessonService.findById(user.getLessonId()).getData();
    }

    private Type type(User user) {
        return typeService.findById(user.getTypeId()).getData();
    }

    private Course course(User user) {
        return courseService.findById(user.getCourseId()).getData();
    }

    public void getLessons(User user, String text) {
        if (text.equals(back)) {
            getCourses(user, course(user).getName());
        }
    }

    @SneakyThrows
    public void getAnnouncement(User user, Long chatId, Integer messageId) {
        for (User admin : userService.findAllByRole("admin").getData()) {
            String nickname = user.getFirstname();
            if (user.getLastname() != null) nickname = nickname.concat(" " + user.getLastname());
            bot.sendMessage(admin.getChatId(), """
                    Foydalanuvchining niki: %s
                    Telefon raqam: %s
                    %s
                    """.formatted(
                    nickname, user.getPhone() == null ? "Mavjud emas" : ("@" + user.getPhone()), user.getUsername() != null ? ("Username: " + user.getUsername()) : ""
            ));
            bot.execute(
                    ForwardMessage
                            .builder()
                            .chatId(admin.getChatId())
                            .messageId(messageId)
                            .fromChatId(chatId)
                            .build()
            );
        }
        bot.sendMessage(chatId, "✅ Izohingiz qabul qilindi");
        start(user);
    }

    public void adminRole(User user) {
        bot.sendMessage(user.getChatId(), "Admin panelga o'tish uchun loginni kiriting");
        user.setEventCode("get login for admin panel");
        userService.save(user);
    }

    public void getLoginForAdminPanel(User user, String text) {
        if (botInformationRepository.findAll().isEmpty()) {
            botInformationRepository.save(new BotInformation(true, "me_zafar", "zafar123"));
        }
        BotInformation botInformation = botInformationRepository.findAll().get(0);
        if (text.equals(botInformation.getLogin())) {
            bot.sendMessage(user.getChatId(), "Login to'g'ri kiritildi, endi parolni kiriting");
            user.setEventCode("get password for admin");
            userService.save(user);
        } else {
            bot.sendMessage(user.getChatId(), "Login noto'g'ri kiritildi");
            start(user);
        }
    }

    public void getPasswordForAdmin(User user, String text) {
        BotInformation botInformation = botInformationRepository.findAll().get(0);
        if (text.equals(botInformation.getPassword())) {
            user.setRole("admin");
            bot.sendMessage(user.getChatId(), "Tabriklaymiz, siz admin paneldasiz");
            userService.save(user );
            adminFunction.start(user);
        } else {
            bot.sendMessage(user.getChatId(), "Parol noto'g'ri kiritildi");
            start(user);
        }
    }
}
