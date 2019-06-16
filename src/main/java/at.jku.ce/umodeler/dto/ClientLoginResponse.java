package at.jku.ce.umodeler.dto;

import at.jku.ce.umodeler.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientLoginResponse {
    private String bearerToken;
    private List<Pair<String, Long>> workspaces;
}
