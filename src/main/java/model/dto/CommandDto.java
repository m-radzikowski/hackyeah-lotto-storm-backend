package model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommandDto {
    private Long id;
    private Type commandType;
    public enum Type {
        CREATE,
        REMOVE
    }
}
