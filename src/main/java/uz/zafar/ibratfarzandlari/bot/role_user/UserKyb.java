package uz.zafar.ibratfarzandlari.bot.role_user;

import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import uz.zafar.ibratfarzandlari.bot.Kyb;
import uz.zafar.ibratfarzandlari.db.domain.Course;
import uz.zafar.ibratfarzandlari.db.domain.Type;

import java.util.ArrayList;
import java.util.List;

import static uz.zafar.ibratfarzandlari.bot.role_user.KybText.back;

@Controller
public class UserKyb extends Kyb {
    public ReplyKeyboardMarkup menu() {
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();
        for (int i = 0; i < KybText.menu.length; i++) {
            row.add(createButton(KybText.menu[i]));
            if ((i+1)%2 == 0){
                rows.add(row );
                row = new KeyboardRow( );
            }
        }
        rows.add(row);
        return markup(rows) ;
    }


    private KeyboardButton createButton(String text) {
        return KeyboardButton.builder().text(text).build();
    }

    private KeyboardButton createButtonWebApp(String text, String webAppUrl) {
        return KeyboardButton.builder().text(text).webApp(new WebAppInfo(webAppUrl)).build();
    }

    public ReplyKeyboardMarkup back = setKeyboards(new String[]{KybText.back}, 1);

    public ReplyKeyboardMarkup getCourses(List<Course> data) {
        String[] res = new String[data.size() + 1];
        res[0] = KybText.back;
        for (int i = 0; i < data.size(); i++) {
            res[i + 1] = data.get(i).getName();
        }
        return setKeyboards(res, 2);
    }

    public ReplyKeyboardMarkup getTypes(List<Type> data) {
        String[] res = new String[data.size() + 1];
        res[0] = KybText.back;
        for (int i = 0; i < data.size(); i++) {
            res[i + 1] = data.get(i).getName();
        }
        return setKeyboards(res, 2);
    }
}
