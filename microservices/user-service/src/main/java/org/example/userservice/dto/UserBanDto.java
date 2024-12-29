package org.example.userservice.dto;
import lombok.Data;

@Data
public class UserBanDto {
    // This functionality will probably be implemented through @RequestParams, but ive left this in just in case i change my mind
    private boolean banned;  // true to ban, false to unban
}