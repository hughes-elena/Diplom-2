package ru.praktikums.models.pojo;

//импортирую плагин lombok для уменьшения кода
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCreateAndEditRequest {
    private String email;
    private String password;
    private String name;

}
