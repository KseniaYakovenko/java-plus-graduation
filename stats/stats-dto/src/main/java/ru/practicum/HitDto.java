package ru.practicum;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HitDto {
    private Long id;
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}