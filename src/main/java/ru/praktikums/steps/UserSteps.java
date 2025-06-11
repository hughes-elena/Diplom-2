package ru.praktikums.steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import ru.praktikums.constants.ApiEndPoints;
import ru.praktikums.models.pojo.UserCreateAndEditRequest;
import ru.praktikums.models.pojo.UserLoginRequest;
import static ru.praktikums.steps.BaseSteps.requestSpecification;
import static io.restassured.RestAssured.given;

//Создаю класс, который содержит шаги Steps для работы с API пользователя.
//Нужен для создания и логина пользователя

public class UserSteps {

    @Step("Создать нового пользователя") //Аннотация для Allure-отчета
    public ValidatableResponse createUser(UserCreateAndEditRequest userCreateAndEditRequest) {
        return requestSpecification()
                .body(userCreateAndEditRequest) //передается тело запроса(данные) из pojo и переводится в JSON
                .post(ApiEndPoints.USER_CREATE_POST) //отправляется post-запрос на api
                .then(); //возвращаем для валидации
    }


    @Step("Авторизация пользователя") //Аннотация для Allure-отчета
    public ValidatableResponse loginUser(UserLoginRequest userLoginRequest) {
        return requestSpecification()
                .body(userLoginRequest)
                .when()
                .post(ApiEndPoints.USER_LOGIN_POST)
                .then();
    }

    @Step("Получение accessToken пользователя")
    public String getAccessToken(UserLoginRequest userLoginRequest) {
        return loginUser(userLoginRequest)
                .extract()
                .path("accessToken");
    }
    @Step("Изменение данных пользователя без авторизации")
    public ValidatableResponse userEdit(UserCreateAndEditRequest userCreateAndEditRequest) {
        return requestSpecification()
                .body(userCreateAndEditRequest)
                .patch(ApiEndPoints.USER_UPDATE_PATCH)
                .then();
    }

    @Step("Изменение данных пользователя после авторизации")
    public ValidatableResponse userEditAfterLogin(UserLoginRequest userLoginRequest, UserCreateAndEditRequest userCreateAndEditRequest) {
        String accessToken = getAccessToken(userLoginRequest); // получаем токен через отдельный step

        return requestSpecification()
                .header("Authorization", accessToken)
                .body(userCreateAndEditRequest)
                .patch(ApiEndPoints.USER_UPDATE_PATCH)
                .then();
    }

    @Step("Удаление пользователя без авторизации")
    public ValidatableResponse userDelete(String accessToken) {
        return requestSpecification()
                .header("Authorization", accessToken)
                .delete(ApiEndPoints.USER_UPDATE_PATCH)
                .then();
    }

    @Step ("Удаление пользователя после авторизации")
    public ValidatableResponse userDeleteAfterLogin(UserLoginRequest userLoginRequest) {

        String accessToken = getAccessToken(userLoginRequest); // Достаём accessToken из JSON-ответа
        return userDelete(accessToken);

    }
}
