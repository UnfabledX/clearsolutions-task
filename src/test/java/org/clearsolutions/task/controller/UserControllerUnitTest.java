package org.clearsolutions.task.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.SneakyThrows;
import org.clearsolutions.task.dto.UserCreateRequest;
import org.clearsolutions.task.dto.UserResponse;
import org.clearsolutions.task.dto.UserUpdateRequest;
import org.clearsolutions.task.exception.YoungAgeException;
import org.clearsolutions.task.service.UserService;
import org.clearsolutions.task.utils.DataUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
class UserControllerUnitTest {

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Test
    @SneakyThrows
    void createUser_WhenOk() {
        UserCreateRequest request = DataUtils.createUserCreateRequest();
        String jsonContent = objectMapper.writeValueAsString(request);
        UserResponse userResponse = DataUtils.toUserResponse(request);
        when(userService.createUser(request)).thenReturn(userResponse);
        mockMvc.perform(post("/api/v1/users")
                        .content(jsonContent)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName", Matchers.is("Oleksii")))
                .andExpect(jsonPath("$.email").value("oleksii.ivanchenko@gmail.com"));

        verify(userService, Mockito.times(1)).createUser(request);
    }

    @Test
    @SneakyThrows
    void createUser_WhenEmailIsWrong_Test() {
        UserCreateRequest request = DataUtils.createUserCreateRequest();
        request.setEmail("wrong.email#gmail.com");
        String jsonContent = objectMapper.writeValueAsString(DataUtils.toUserResponse(request));
        String expectedJsonResult = """
                {
                "type":"about:blank",
                "title":"Bad Request",
                "status":400,
                "detail":"Failed validation",
                "instance":"/api/v1/users",
                "problemDetails":
                    [
                        {  "message":"Wrong email format.",
                            "field":"email",
                            "wrongValue":"wrong.email#gmail.com"
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/v1/users")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedJsonResult));

        verify(userService, Mockito.times(0)).createUser(request);
    }

    @Test
    @SneakyThrows
    void createUser_WhenBirthDayIsInFuture_Test() {
        UserCreateRequest request = DataUtils.createUserCreateRequest();
        LocalDate birthDateInFuture = LocalDate.now().plusDays(5);
        request.setBirthDate(birthDateInFuture);
        String jsonContent = objectMapper.writeValueAsString(DataUtils.toUserResponse(request));
        String expectedJsonResult = """ 
                {
                "type":"about:blank",
                "title":"Bad Request",
                "status":400,
                "detail":"Failed validation",
                "instance":"/api/v1/users",
                "problemDetails":
                    [
                        {  "message":   "The date must be in the past.",
                            "field":    "birthDate",
                            "wrongValue":"%s"
                        }
                    ]
                }
                """.formatted(birthDateInFuture);

        mockMvc.perform(post("/api/v1/users")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedJsonResult));

        verify(userService, Mockito.times(0)).createUser(request);
    }

    @Test
    @SneakyThrows
    void createUser_WhenNecessaryFieldsAbsent_Test() {
        //no email and birthdate
        UserCreateRequest request = UserCreateRequest.builder()
                .firstName("Oleksii")
                .lastName("Ivanchenko")
                .phoneNumber("+380 93 123 4567")
                .build();

        String jsonContent = objectMapper.writeValueAsString(DataUtils.toUserResponse(request));
        String expectedJsonResult = """ 
                {
                "type":"about:blank",
                "title":"Bad Request",
                "status":400,
                "detail":"Failed validation",
                "instance":"/api/v1/users",
                "problemDetails":
                    [
                        {   "message":  "must not be null",
                            "field":    "birthDate"
                        },
                        {   "message":  "must not be null",
                            "field":    "email"
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/v1/users")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedJsonResult));

        verify(userService, Mockito.times(0)).createUser(request);
    }

    @Test
    @SneakyThrows
    void createUser_WhenShortName_Test() {
        UserCreateRequest request = DataUtils.createUserCreateRequest();
        request.setFirstName("A");
        String jsonContent = objectMapper.writeValueAsString(DataUtils.toUserResponse(request));
        String expectedJsonResult = """
                {
                "type":"about:blank",
                "title":"Bad Request",
                "status":400,
                "detail":"Failed validation",
                "instance":"/api/v1/users",
                "problemDetails":
                    [
                        {  "message":   "The first name must be more than 2 letters.",
                            "field":    "firstName",
                            "wrongValue":"A"
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/v1/users")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedJsonResult));

        verify(userService, Mockito.times(0)).createUser(request);
    }

    @Test
    @SneakyThrows
    void createUser_WhenAgeIsLess18_Test() {
        UserCreateRequest request = DataUtils.createUserCreateRequest();
        request.setBirthDate(LocalDate.now().minusYears(10));
        String jsonContent = objectMapper.writeValueAsString(DataUtils.toUserResponse(request));
        LocalDate birthDate = request.getBirthDate();
        String expectedJsonResult = """
                {
                     "type": "about:blank",
                     "title": "Bad Request",
                     "status": 400,
                     "detail": "Young Age",
                     "instance": "/api/v1/users",
                     "problemDetails": [
                         {
                             "message": "You are too young to register. Your birthday is at '%s'",
                             "field": "birthDate",
                             "wrongValue": "%s"
                         }
                     ]
                 }
                """.formatted(birthDate, birthDate);
        doThrow(new YoungAgeException(
                "You are too young to register. Your birthday is at '%s'" .formatted(birthDate),
                HttpStatus.BAD_REQUEST)).when(userService).createUser(request);

        mockMvc.perform(post("/api/v1/users")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedJsonResult));

        verify(userService, Mockito.times(1)).createUser(request);
    }

    @Test
    @SneakyThrows
    void deleteUser_WhenOK_Test() {
        Long id = 1L;
        doNothing().when(userService).deleteUser(id);
        mockMvc.perform(delete("/api/v1/users/{id}", id))
                .andExpect(status().isOk());
        verify(userService, Mockito.times(1)).deleteUser(id);
    }

    @Test
    @SneakyThrows
    void deleteUser_WhenIdDoesNotExist_Test() {
        Long id = 100L;
        String expectedJsonResult = """
                {
                     "type": "about:blank",
                     "title": "Not Found",
                     "status": 404,
                     "detail": "User is not found",
                     "instance": "/api/v1/users/100",
                     "problemDetails": [
                         {
                             "message":     "User with id='100' can not be found",
                             "field":       "User id",
                             "wrongValue":  "100"
                         }
                     ]
                 }
                """;
        doThrow(new EntityNotFoundException("User with id='%d' can not be found" .formatted(id)))
                .when(userService).deleteUser(id);
        mockMvc.perform(delete("/api/v1/users/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json(expectedJsonResult));
        verify(userService, times(1)).deleteUser(id);
    }

    @Test
    @SneakyThrows
    void updateUser_WhenOK_Test() {
        UserUpdateRequest requestForUpdate = UserUpdateRequest.builder()
                .firstName("Oleksii")
                .build();
        Long id = 1L;
        UserResponse userResponse = DataUtils.toUserResponse(requestForUpdate);
        when(userService.updateUser(id, requestForUpdate))
                .thenReturn(userResponse);
        String jsonContent = objectMapper.writeValueAsString(requestForUpdate);

        mockMvc.perform(put("/api/v1/users/{id}", id)
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName", Matchers.is(userResponse.getFirstName())));

        verify(userService, times(1)).updateUser(id, requestForUpdate);
    }

    @Test
    @SneakyThrows
    void updateUser_WhenIdNegative_Test() {
        UserUpdateRequest requestForUpdate = UserUpdateRequest.builder()
                .firstName("Oleksii")
                .build();
        Long id = -5L;
        String jsonContent = objectMapper.writeValueAsString(requestForUpdate);

        mockMvc.perform(put("/api/v1/users/{id}", id)
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

        verify(userService, times(0)).updateUser(id, requestForUpdate);
    }

    @Test
    @SneakyThrows
    void getAllUsers_WhenOk_Test() {
        Pageable pageRequest = PageRequest.of(0, 10, Sort.unsorted());
        List<UserResponse> userList = DataUtils.createUserResponseList();
        Page<UserResponse> users = new PageImpl<>(userList, pageRequest, userList.size());
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(users);

        mockMvc.perform(get("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        jsonPath("$.content.size()").value(userList.size()),
                        jsonPath("$.content[*].lastName",
                                containsInAnyOrder(
                                        userList.get(0).getLastName(),
                                        userList.get(1).getLastName(),
                                        userList.get(2).getLastName(),
                                        userList.get(3).getLastName()
                                )
                        )
                );

        verify(userService, times(1)).getAllUsers(any(Pageable.class));
    }

    @Test
    @SneakyThrows
    void searchUsersByBirthDate_WhenToIsBeforeFrom() {
        String dateFrom = "1997-03-10";
        LocalDate from = LocalDate.parse(dateFrom);
        String dateTo = "1990-03-10";
        LocalDate to = LocalDate.parse(dateTo);
        String expectedJsonResult = """
                {
                      "type": "about:blank",
                      "title": "Bad Request",
                      "status": 400,
                      "detail": "Illegal arguments",
                      "instance": "/api/v1/users/birthdays",
                      "problemDetails": [
                          {
                              "message": "Date `to`-'1990-03-10' is before date `from`-'1997-03-10'."
                          }
                      ]
                  }
                """;
        doThrow(new IllegalArgumentException("Date `to`-'%s' is before date `from`-'%s'." .formatted(to, from)))
                .when(userService).searchUsersByBirthDate(eq(from), eq(to), any(Pageable.class));

        mockMvc.perform(get("/api/v1/users/birthdays")
                        .param("from", dateFrom)
                        .param("to", dateTo))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedJsonResult));
        verify(userService, times(1))
                .searchUsersByBirthDate(eq(from), eq(to), any(Pageable.class));
    }

    @Test
    @SneakyThrows
    void searchUsersByBirthDate_WhenOk() {
        String dateFrom = "1990-03-10";
        LocalDate from = LocalDate.parse(dateFrom);
        String dateTo = "2003-03-10";
        LocalDate to = LocalDate.parse(dateTo);

        Pageable pageRequest = PageRequest.of(0, 10, Sort.unsorted());
        List<UserResponse> userList = DataUtils.createUserResponseList();
        List<UserResponse> searchList = userList.subList(1,3);
        Page<UserResponse> users = new PageImpl<>(searchList, pageRequest, searchList.size());
        when(userService.searchUsersByBirthDate(eq(from), eq(to), any(Pageable.class))).thenReturn(users);

        mockMvc.perform(get("/api/v1/users/birthdays")
                        .param("from", dateFrom)
                        .param("to", dateTo))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.content.size()").value(searchList.size()),
                        jsonPath("$.content[*].lastName",
                                containsInAnyOrder(
                                        searchList.get(0).getLastName(),
                                        searchList.get(1).getLastName())),
                        jsonPath("$.content[0].birthDate").value(searchList.get(0).getBirthDate().toString()),
                        jsonPath("$.content[1].birthDate").value(searchList.get(1).getBirthDate().toString())
                );

        verify(userService, times(1))
                .searchUsersByBirthDate(eq(from), eq(to), any(Pageable.class));
    }

}
