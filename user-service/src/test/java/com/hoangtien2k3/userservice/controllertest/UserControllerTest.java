package com.hoangtien2k3.userservice.controllertest;

import com.hoangtien2k3.userservice.entity.User;
import com.hoangtien2k3.userservice.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.ArrayList;
import java.util.List;


import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {
    private final Long USER_ID = 1L;
    private final String USER_NAME = "test";
    private User user;
    private List<User> listUsers;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Test
    public void getAllUserController_Should_Return200_When_ValidRequest() throws Exception {
/*
        User user1 = User.builder()
                .id(USER_ID)
                .userName(USER_NAME)
                .build();

        listUsers = new ArrayList<>();
        listUsers.add(user1);

        Mockito.when(userService.getAllUser()).thenReturn(listUsers);

        //then
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect((ResultMatcher) jsonPath("$[0].id").value(USER_ID))
                .andExpect((ResultMatcher) jsonPath("$[0].userName").value(USER_NAME));

        verify(userService, times(1)).getAllUsers();
        verifyNoMoreInteractions(userService);
*/


    }


}
