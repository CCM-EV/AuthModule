package com.xdpmhdt.authmodule.entity;

/**
 * Role enum for Carbon Credit Marketplace
 * - EV_OWNER: EV (Electric Vehicle) Owner who generates and sells carbon credits
 * - CC_BUYER: Carbon Credit Buyer who purchases carbon credits
 * - CVA: Carbon Verification & Audit organization that verifies and approves carbon credits
 * - ADMIN: System administrator who manages the platform
 */
public enum Role {
    EV_OWNER,     // Chủ sở hữu xe điện
    CC_BUYER,     // Người mua tín chỉ carbon
    CVA,          // Tổ chức kiểm toán và xác minh carbon
    ADMIN         // Quản trị viên hệ thống
}

