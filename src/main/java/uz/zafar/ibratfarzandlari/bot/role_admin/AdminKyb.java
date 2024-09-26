package uz.zafar.ibratfarzandlari.bot.role_admin;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.zafar.ibratfarzandlari.bot.Kyb;
import uz.zafar.ibratfarzandlari.db.domain.Course;
import uz.zafar.ibratfarzandlari.db.domain.Lesson;
import uz.zafar.ibratfarzandlari.db.domain.Type;

import java.util.ArrayList;
import java.util.List;

import static uz.zafar.ibratfarzandlari.bot.role_admin.KybText.*;

@Controller
@Log4j2
public class AdminKyb extends Kyb {
    public ReplyKeyboardMarkup isAddCourse = setKeyboards(KybText.isAddCourse, 2);
    public ReplyKeyboardMarkup back = setKeyboards(new String[]{KybText.back}, 1);
    public ReplyKeyboardMarkup editAboutBot = setKeyboards(KybText.editAboutBot, 1);
    protected ReplyKeyboardMarkup menuBtn = setKeyboards(menu, 2);
    protected ReplyKeyboardMarkup crudCourse = setKeyboards(crudCourseBtn, 2);

    protected ReplyKeyboardMarkup getAllTypes(List<Type> list) {
        KeyboardButton button;
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            button = new KeyboardButton();
            button.setText(list.get(i).getName());
            row.add(button);
            if ((i + 1) % 2 == 0) {
                rows.add(row);
                row = new KeyboardRow();
            }
        }
        rows.add(row);
        button = new KeyboardButton();
        row = new KeyboardRow();
        button.setText(KybText.back);
        row.add(button);
        button = new KeyboardButton();
        button.setText(add);
        row.add(button);
        rows.add(row);
        return markup(rows);
    }

    protected ReplyKeyboardMarkup coursesBtn(List<Course> list) {
        KeyboardButton button;
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        for (int i = 0; i < list.size(); i++) {
            button = new KeyboardButton();
            button.setText(list.get(i).getName());
            row.add(button);
            if ((i + 1) % 2 == 0) {
                rows.add(row);
                row = new KeyboardRow();
            }
        }
        rows.add(row);
        row = new KeyboardRow();
        button = new KeyboardButton();
        button.setText(KybText.back);
        row.add(button);
        button = new KeyboardButton();
        button.setText(addCourse);
        row.add(button);
        rows.add(row);
        return markup(rows);
    }

    protected ReplyKeyboardMarkup crudType = setKeyboards(KybText.crudType, 2);


    public InlineKeyboardMarkup crudLesson() {
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        row.add(createButton("Nomini o'zgartirish", "edit name"));
        row.add(createButton("Tavsifini o'zgartirish", "edit desc"));
        rows.add(row);
        row = new ArrayList<>();
        row.add(createButton("Videoni o'zgartirish", "edit video"));
        row.add(createButton("O'chirish", "delete"));
        rows.add(row);
        row = new ArrayList<>();
        row.add(createButton(KybText.back, "back to lessons"));
        rows.add(row);
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    public InlineKeyboardMarkup isDelete() {
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        row.add(createButton("✅ Ha o'chirilsin", "yes delete"));
        rows.add(row);
        row = new ArrayList<>();
        row.add(createButton("❌ Yo'q o'chirilmasin", "no delete"));
        rows.add(row);
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    public ReplyKeyboardMarkup checkContact(boolean requestContact) {
        return setKeyboards(new String[]{
                requestContact ? "Kontakt so'rovini olib tashlash" : "Kontakt so'rash", KybText.back
        }, 1);
    }
}
