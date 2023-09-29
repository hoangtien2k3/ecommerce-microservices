package com.hoangtien2k3.orderservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.ws.rs.GET;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginResponse {
    private Long id;
    private String token;
    private String type;
    private String name;
    private List<Role> roles;

    // Các getter và setter

    public static class Role {
        private String authority;

        public String getAuthority() {
            return authority;
        }

        public void setAuthority(String authority) {
            this.authority = authority;
        }
    }

}



