package uz.zafar.ibratfarzandlari.db.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BotInformation {
    public BotInformation(Boolean requestContact) {
        this.requestContact = requestContact;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "TEXT")
    private String results;
    private String fileType;
    private Boolean requestContact;
    @Column(columnDefinition = "TEXT")
    private String aboutWe;
    private String login ;
    private String password ;

    public BotInformation(Boolean requestContact, String login, String password) {
        this.requestContact = requestContact;
        this.login = login;
        this.password=password;
    }
}
