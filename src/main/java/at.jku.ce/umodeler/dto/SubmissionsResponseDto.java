package at.jku.ce.umodeler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SubmissionsResponseDto {
    List<SubmissionData> data;

    @Data
    public static class SubmissionData {
        ResponseId id;
        List<SubmissionMember> members;

        @Data
        public static class SubmissionMember {
            private ResponseId id;
        }
    }
}