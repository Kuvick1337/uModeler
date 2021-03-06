package at.jku.ce.umodeler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ULearnLoginResponseDto {
    private LoginData data;

    @Data
    public static class LoginData {
        private ULearnObjectId id;
    }
}