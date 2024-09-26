package uz.zafar.ibratfarzandlari.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.zafar.ibratfarzandlari.db.domain.BotInformation;

public interface BotInformationRepository extends JpaRepository<BotInformation , Integer> {
}
