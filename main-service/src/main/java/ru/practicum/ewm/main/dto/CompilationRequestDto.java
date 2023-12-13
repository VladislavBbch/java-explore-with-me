package ru.practicum.ewm.main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.main.controller.Create;
import ru.practicum.ewm.main.controller.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Builder(toBuilder = true)
public class CompilationRequestDto {
    private Set<Long> events;
    @JsonProperty("pinned")
    private Boolean isPinned;
    @NotBlank(groups = {Create.class})
    @Size(min = 1, max = 50, groups = {Create.class, Update.class})
    private String title;
}
