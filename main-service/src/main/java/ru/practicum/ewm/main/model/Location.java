package ru.practicum.ewm.main.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.main.controller.Create;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder(toBuilder = true)
public class Location {
    @NotNull(groups = {Create.class})
    @Max(90)
    @Min(-90)
    private Double lat;
    @NotNull(groups = {Create.class})
    @Max(180)
    @Min(-180)
    private Double lon;
}
