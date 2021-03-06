package model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StormDto {
    private String id;
    private Double lng;
    private Double lat;
    private Integer max;
    private Integer current = 0;
}
