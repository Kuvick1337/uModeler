package at.jku.ce.umodeler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ULearnObjectId {
    private String type;
    private long created;
    private int sequence;
}