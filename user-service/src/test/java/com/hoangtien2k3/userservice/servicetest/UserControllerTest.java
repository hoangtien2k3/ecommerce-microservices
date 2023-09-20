package com.hoangtien2k3.userservice.servicetest;

import com.hoangtien2k3.userservice.entity.User;
import com.hoangtien2k3.userservice.repository.UserRepository;
import com.hoangtien2k3.userservice.service.impl.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;


@SpringBootTest
@RunWith(SpringRunner.class)
public class UserControllerTest {

    private final Long USER_ID = 2L;
    private final String USER_NAME = "test";
    private List<User> userList;
    private User user;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Before
    public void setUp() {
        user = User.builder()
                .id(USER_ID)
                .userName(USER_NAME)
                .build();

        userList = new ArrayList<>();
        userList.add(user);
    }

    @Test
    public void get_AllUser_Test() {
        // give
        Mockito.when(userRepository.findAll()).thenReturn(userList);

        // when
        List<User> foundUsers = userService.getAllUser();

        // then
        assertEquals(foundUsers.get(0).getUserName(), USER_NAME);
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void get_UserById_Test(){
        // given
        Mockito.when(userRepository.getOne(anyLong())).thenReturn(user);

        // when
        User foundUser = userService.getUserById(USER_ID);

        // then
        assertEquals(foundUser.getUserName(), USER_NAME);
        Mockito.verify(userRepository, Mockito.times(1)).getOne(anyLong());
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void get_UserByName_Test(){
        // given
        Mockito.when(userRepository.findByUserName(anyString())).thenReturn(user);

        // when
        User foundUser = userService.getUserByName(USER_NAME);

        // then
        assertEquals(foundUser.getId(), USER_ID);
        Mockito.verify(userRepository, Mockito.times(1)).findByUserName(USER_NAME);
        Mockito.verifyNoMoreInteractions(userRepository);
    }



}
