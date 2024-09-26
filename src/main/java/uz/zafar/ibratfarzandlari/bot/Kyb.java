package uz.zafar.ibratfarzandlari.bot;

import org.springframework.data.domain.Page;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.zafar.ibratfarzandlari.db.domain.Lesson;

import java.util.ArrayList;
import java.util.List;

public class Kyb {
    public ReplyKeyboardMarkup markup(List<KeyboardRow> r) {
        return ReplyKeyboardMarkup.builder().selective(true).resizeKeyboard(true).keyboard(r).build();
    }
    public ReplyKeyboardMarkup setKeyboards(String[] words, int size) {
        KeyboardButton button;
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            button = new KeyboardButton();
            button.setText(words[i]);
            row.add(button);
            if ((i + 1) % size == 0) {
                rows.add(row);
                row = new KeyboardRow();
            }
        }
        rows.add(row);
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setSelective(true);
        markup.setKeyboard(rows);
        return markup;
    }
    public ReplyKeyboardMarkup requestContact(String word) {
        KeyboardButton button;
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();
        button = new KeyboardButton();
        button.setText(word);
        button.setRequestContact(true);
        row.add(button);
        rows.add(row);
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setSelective(true);
        markup.setKeyboard(rows);
        return markup;
    }
    public InlineKeyboardMarkup getLessons(Page<Lesson> lessons, boolean video, Long lessonId) {
        String nextPage = "next page";
        String oldPage = "old page";
        String nextLesson = "next lesson";
        String oldLesson = "old lesson";
        InlineKeyboardButton button;
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        if (video) {
            row.add(createButton("⬅️ Avvali video", oldLesson));
            row.add(createButton("➡️ Keyingi video", nextLesson));
            rows.add(row);
            row = new ArrayList<>();


        }
        List<Lesson> list = lessons.getContent();
        for (int i = 0; i < list.size(); i++) {
            Lesson lesson = list.get(i);
            if (lessonId == null) {
                row.add(createButton(lesson.getName(), lesson.getId()));
            } else if (lesson.getId().equals(lessonId)) {
                row.add(createButton("\uD83D\uDFE2 " + lesson.getName(), lesson.getId()));
            } else {
                row.add(createButton(lesson.getName(), lesson.getId()));
            }
            if ((i + 1) % 2 == 0) {
                rows.add(row);
                row = new ArrayList<>();
            }
        }
        rows.add(row);
        row = new ArrayList<>();
        row.add(createButton("⬅️ Oldingi", oldPage));
        row.add(createButton("➡️ Keyingi", nextPage));
        rows.add(row);
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    public InlineKeyboardButton createButton(String text, String data) {
        return InlineKeyboardButton.builder().callbackData(data).text(text).build();
    }

    public InlineKeyboardButton createButton(String text, Long data) {
        return InlineKeyboardButton.builder().callbackData(String.valueOf(data)).text(text).build();
    }

}
