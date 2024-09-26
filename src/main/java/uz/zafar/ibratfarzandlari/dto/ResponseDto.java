package uz.zafar.ibratfarzandlari.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDto<T> {
    private boolean success ;
    private String message ;
    private T data ;
    public ResponseDto(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
