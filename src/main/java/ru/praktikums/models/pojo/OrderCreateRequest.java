package ru.praktikums.models.pojo;

import java.util.List;
//импортирую плагин lombok для уменьшения кода
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderCreateRequest {
    private List<String> ingredients;
}