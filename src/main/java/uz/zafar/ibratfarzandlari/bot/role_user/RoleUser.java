package uz.zafar.ibratfarzandlari.bot.role_user;

import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.zafar.ibratfarzandlari.db.domain.User;

import static uz.zafar.ibratfarzandlari.bot.role_user.KybText.back;

@Controller
public class RoleUser {
    private final UserFunction function;

    public RoleUser(UserFunction function) {
        this.function = function;
    }

    public void menu(User user, Update update, int size, String username) {
        String eventCode = user.getEventCode();
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (eventCode.equals("get announcement")) {
                if (message.hasText()) {
                    String text = message.getText();
                    if (text.equals("/start") || text.equals(back))
                        function.start(user);
                    else {
                        function.getAnnouncement(user, user.getChatId(), message.getMessageId());
                    }
                } else function.getAnnouncement(user, user.getChatId(), message.getMessageId());
                return;
            }
            if (message.hasText()) {
                String text = message.getText();
                if (text.equals("/start")) {
                    function.start(user);
                } else if (text.equals("admin role")) {
                    function.adminRole(user);
                } else {
                    switch (eventCode) {
                        case "get login for admin panel" -> function.getLoginForAdminPanel(user, text);
                        case "get password for admin" -> function.getPasswordForAdmin(user , text );
                        case "request contact" -> function.requestContact(user);
                        case "menu" -> function.menu(user, text, username);
                        case "get courses" -> function.getCourses(user, text);
                        case "get types" -> function.getTypes(user, text, size, username);
                        case "get lessons" -> function.getLessons(user, text);

                    }
                }
            } else if (message.hasContact()) {
                if (eventCode.equals("request contact")) {
                    function.requestContact(user, message.getContact());
                }
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            int messageId = callbackQuery.getMessage().getMessageId();
            if (eventCode.equals("get lessons")) {
                function.getLessons(user, messageId, data, callbackQuery, size);
            }
        }
    }
}
