package com.chatapp.ui;

import java.awt.Color;

/**
 * Lớp tiện ích chứa tất cả màu sắc được sử dụng trong ứng dụng chat.
 * Việc tập trung các màu sắc vào một nơi giúp dễ dàng quản lý và thay đổi giao diện.
 * Lớp này được thiết kế theo mẫu Singleton để đảm bảo chỉ có một phiên bản duy nhất.
 */
public class AppColors {
    // Phiên bản duy nhất của AppColors
    private static AppColors instance;
    
    // Màu chính theo bảng màu Coolors
    public static final Color PRIMARY_COLOR = new Color(0x26, 0x46, 0x53);     // #264653 - Prussian Blue
    public static final Color SECONDARY_COLOR = new Color(0x2a, 0x9d, 0x8f);   // #2a9d8f - Persian Green
    public static final Color ACCENT_COLOR = new Color(0xe9, 0xc4, 0x6a);      // #e9c46a - Sandy Brown
    public static final Color HIGHLIGHT_COLOR = new Color(0xf4, 0xa2, 0x61);   // #f4a261 - Sandy Brown
    public static final Color ATTENTION_COLOR = new Color(0xe7, 0x6f, 0x51);   // #e76f51 - Burnt Sienna
    
    // Màu nền và panel
    public static final Color BACKGROUND_COLOR = new Color(245, 245, 250);     // #f5f5fa - Màu nền chính
    public static final Color PANEL_COLOR = new Color(255, 255, 255);          // #ffffff - Màu panel
    
    // Màu văn bản
    public static final Color TEXT_LIGHT = new Color(250, 250, 250);           // #fafafa - Văn bản trên nền tối
    public static final Color TEXT_DARK = new Color(60, 60, 60);               // #3c3c3c - Văn bản trên nền sáng
    public static final Color TEXT_MUTED = new Color(150, 150, 150);           // #969696 - Văn bản mờ
    
    // Màu trạng thái
    public static final Color SUCCESS_COLOR = new Color(0x2a, 0xb0, 0x90);     // #2ab090 - Màu thành công
    public static final Color ERROR_COLOR = ATTENTION_COLOR;                   //  #e76f51 - Màu lỗi 
    public static final Color WARNING_COLOR = ACCENT_COLOR;                    // #e9c46a - Màu cảnh báo
    
    // Màu viền 
    public static final Color BORDER_COLOR = new Color(220, 220, 220);         // #dcdcdc - Viền thông thường
    public static final Color PRIMARY_BORDER = new Color(0x21, 0x3d, 0x49);    // #213d49 - Viền màu chính đậm hơn
    public static final Color SECONDARY_BORDER = new Color(0x21, 0x89, 0x7e);  // #21897e - Viền màu phụ đậm hơn
    public static final Color ATTENTION_BORDER = new Color(0xd5, 0x5e, 0x41);  // #d55e41 - Viền màu cảnh báo
    
    // Màu hover (khi di chuột qua)
    public static final Color PRIMARY_HOVER = new Color(0x30, 0x52, 0x61);     // #305261 - Hover cho primary
    public static final Color SECONDARY_HOVER = new Color(0x32, 0xb3, 0xa3);   // #32b3a3 - Hover cho secondary
    public static final Color ACCENT_HOVER = new Color(0xf7, 0xf3, 0xe8);      // #f7f3e8 - Hover nhẹ (nền sáng)
    
    /**
     * Constructor riêng tư để ngăn việc tạo đối tượng trực tiếp từ bên ngoài
     */
    private AppColors() {
        // Constructor riêng tư
    }
    
    /**
     * Lấy phiên bản duy nhất của lớp AppColors
     * @return Phiên bản duy nhất của AppColors
     */
    public static synchronized AppColors getInstance() {
        if (instance == null) {
            instance = new AppColors();
        }
        return instance;
    }
    
    /**
     * Lấy màu với độ sáng được điều chỉnh
     * @param color Màu cần điều chỉnh
     * @param factor Hệ số điều chỉnh (1.0 = không đổi, >1.0 = sáng hơn, <1.0 = tối hơn)
     * @return Màu mới sau khi điều chỉnh
     */
    public static Color adjustBrightness(Color color, float factor) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        hsb[2] = Math.min(1.0f, hsb[2] * factor); // Điều chỉnh độ sáng
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }
    
    /**
     * Lấy màu với độ trong suốt được điều chỉnh
     * @param color Màu cần điều chỉnh
     * @param alpha Giá trị alpha (0-255)
     * @return Màu mới với độ trong suốt được điều chỉnh
     */
    public static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
}