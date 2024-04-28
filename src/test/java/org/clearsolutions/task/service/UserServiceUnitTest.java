package org.clearsolutions.task.service;

import jakarta.persistence.EntityNotFoundException;
import org.clearsolutions.task.dto.UserCreateRequest;
import org.clearsolutions.task.dto.UserResponse;
import org.clearsolutions.task.dto.UserUpdateRequest;
import org.clearsolutions.task.entity.User;
import org.clearsolutions.task.exception.YoungAgeException;
import org.clearsolutions.task.mapper.UserMapper;
import org.clearsolutions.task.repository.UserRepository;
import org.clearsolutions.task.service.impl.UserServiceImpl;
import org.clearsolutions.task.utils.DataUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private UserServiceImpl userService;

  @BeforeEach
  void init() {
    userService.setAgeCondition(18);
  }

  @Test
  void createUser_WhenOk_Test() {
    UserCreateRequest request = DataUtils.createUserCreateRequest();
    User user = DataUtils.toUser(request);
    when(userMapper.toEntity(request)).thenReturn(user);
    user.setId(1L);
    when(userRepository.save(user)).thenReturn(user);
    UserResponse expected = DataUtils.toUserResponse(user);
    when(userMapper.toResponse(user)).thenReturn(expected);

    UserResponse actual = userService.createUser(request);

    assertEquals(expected.getFirstName(), actual.getFirstName());
    assertEquals(expected.getBirthDate(), actual.getBirthDate());

  }

  @Test
  void createUser_WhenAgeIsLess18_Test() {
    UserCreateRequest request = DataUtils.createUserCreateRequest();
    request.setBirthDate(LocalDate.now().minusYears(10));

    assertThrows(YoungAgeException.class, () -> userService.createUser(request));
  }

  @Test
  void deleteUser_WhenOk_Test() {
    Long userId = 1L;
    when(userRepository.existsById(userId)).thenReturn(true);
    Mockito.doNothing().when(userRepository).deleteById(userId);

    assertDoesNotThrow(() -> userService.deleteUser(userId));
  }

  @Test
  void deleteUser_WhenUserNotFound_Test() {
    Long userId = 1L;
    when(userRepository.existsById(userId)).thenReturn(false);
    assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(userId));
  }

  @Test
  void updateUser_WhenOK_Test() {
    UserUpdateRequest requestForUpdate = UserUpdateRequest.builder()
            .firstName("Oleksii")
            .build();
    Long id = 1L;
    UserResponse expected = DataUtils.toUserResponse(requestForUpdate);
    Optional<User> optionalUser = Optional.ofNullable(DataUtils.toUser(requestForUpdate));
    when(userRepository.findById(id)).thenReturn(optionalUser);
    User existingUser = optionalUser.get();
    doNothing().when(userMapper).updateUser(existingUser, requestForUpdate);
    when(userMapper.toResponse(existingUser)).thenReturn(expected);

    UserResponse actual = userService.updateUser(id, requestForUpdate);

    assertEquals(expected.getFirstName(), actual.getFirstName());

  }

  @Test
  void updateUser_WhenUserIdNotFound_Test() {
    UserUpdateRequest requestForUpdate = UserUpdateRequest.builder()
            .firstName("Oleksii")
            .build();
    Long id = 1L;
    when(userRepository.findById(id)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> userService.updateUser(id, requestForUpdate));
  }

  @Test
  void searchUsersByBirthDate_WhenToIsBeforeFrom_Test(){
    String dateFrom = "2003-03-10";
    LocalDate from = LocalDate.parse(dateFrom);
    String dateTo = "1990-03-10";
    LocalDate to = LocalDate.parse(dateTo);
    Pageable pageRequest = PageRequest.of(0, 10, Sort.unsorted());

    assertThrows(IllegalArgumentException.class, () -> userService.searchUsersByBirthDate(from, to, pageRequest));
  }

  @Test
  void searchUsersByBirthDate_Ok_Test(){
    String dateFrom = "1990-03-10";
    LocalDate from = LocalDate.parse(dateFrom);
    String dateTo = "2003-03-10";
    LocalDate to = LocalDate.parse(dateTo);
    Pageable pageRequest = PageRequest.of(0, 10, Sort.unsorted());
    List<User> userList = DataUtils.createUserList();
    List<User> searchedList = userList.subList(1, 3);
    PageImpl<User> users = new PageImpl<>(searchedList, pageRequest, userList.size());
    when(userRepository.getUsersByBirthDateBetween(from, to, pageRequest)).thenReturn(users);
    when(userMapper.toResponse(searchedList.get(0))).thenReturn(DataUtils.toUserResponse(searchedList.get(0)));
    when(userMapper.toResponse(searchedList.get(1))).thenReturn(DataUtils.toUserResponse(searchedList.get(1)));

    Page<UserResponse> actual = userService.searchUsersByBirthDate(from, to, pageRequest);

    assertEquals(searchedList.size(), actual.getContent().size());
    assertEquals(searchedList.get(0).getEmail(), actual.getContent().get(0).getEmail());
    assertEquals(searchedList.get(1).getEmail(), actual.getContent().get(1).getEmail());
    assertEquals(searchedList.get(0).getBirthDate(), actual.getContent().get(0).getBirthDate());
    assertEquals(searchedList.get(1).getBirthDate(), actual.getContent().get(1).getBirthDate());
  }

}
