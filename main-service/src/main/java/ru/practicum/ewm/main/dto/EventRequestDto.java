package ru.practicum.ewm.main.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.main.controller.Create;
import ru.practicum.ewm.main.controller.Update;
import ru.practicum.ewm.main.model.Location;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.ewm.main.Constant.PATTERN;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestDto {
    @NotBlank(groups = {Create.class})
    @Size(min = 20, max = 2000, groups = {Create.class, Update.class})
    private String annotation;
    @NotNull(groups = {Create.class})
    @JsonProperty("category")
    private Long categoryId;
    @NotBlank(groups = {Create.class})
    @Size(min = 20, max = 7000, groups = {Create.class, Update.class})
    private String description;
    @NotNull(groups = {Create.class})
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN)
    private LocalDateTime eventDate;
    @NotNull(groups = {Create.class})
    @Valid
    private Location location;
    @JsonProperty("paid")
    private Boolean isPaid;
    @Min(0)
    private Integer participantLimit;
    @JsonProperty("requestModeration")
    private Boolean isModerationRequested;
    @NotBlank(groups = {Create.class})
    @Size(min = 3, max = 120, groups = {Create.class, Update.class})
    private String title;
}