package at.jku.ce.umodeler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ListResponseDto {
    private List<ListData> data;

    @Data
    public static class ListData {
        private ULearnObjectId id;
        private String name;
    }
}
