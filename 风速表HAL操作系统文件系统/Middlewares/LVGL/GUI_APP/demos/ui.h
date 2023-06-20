// SquareLine LVGL GENERATED FILE
// EDITOR VERSION: SquareLine Studio 1.1.1
// LVGL VERSION: 8.2.0
// PROJECT: fengsubiao

#ifndef _FENGSUBIAO_UI_H
#define _FENGSUBIAO_UI_H

#ifdef __cplusplus
extern "C" {
#endif

#if defined __has_include
#if __has_include("lvgl.h")
#include "lvgl.h"
#elif __has_include("lvgl/lvgl.h")
#include "lvgl/lvgl.h"
#else
#include "lvgl.h"
#endif
#else
#include "lvgl.h"
#endif

extern lv_obj_t * ui_Screen_menu1;
extern lv_obj_t * ui_background_menu1;
extern lv_obj_t * ui_menu1_bg1;
extern lv_obj_t * ui_menu1_name1;
void ui_event_menu2_bg1(lv_event_t * e);
extern lv_obj_t * ui_menu2_bg1;
void ui_event_menu2_name1(lv_event_t * e);
extern lv_obj_t * ui_menu2_name1;
void ui_event_menu3_bg1(lv_event_t * e);
extern lv_obj_t * ui_menu3_bg1;
void ui_event_menu3_name1(lv_event_t * e);
extern lv_obj_t * ui_menu3_name1;
extern lv_obj_t * ui_backgroud_top1;
extern lv_obj_t * ui_battery1;
extern lv_obj_t * ui_time2;
extern lv_obj_t * ui_backgroud_1;
extern lv_obj_t * ui_Screen1_Label3;
extern lv_obj_t * ui_countdown;
extern lv_obj_t * ui_Screen1_Label5;
extern lv_obj_t * ui_wind_speed;
extern lv_obj_t * ui_record_start;
extern lv_obj_t * ui_record_end;
extern lv_obj_t * ui_cedian_bai_bg;
extern lv_obj_t * ui_cedian_shi_bg;
extern lv_obj_t * ui_cedian_ge_bg;
extern lv_obj_t * ui_cedian_bai;
extern lv_obj_t * ui_cedian_shi;
extern lv_obj_t * ui_cedian_ge;
extern lv_obj_t * ui_Screen_meun2;
extern lv_obj_t * ui_background_menu2;
void ui_event_menu1_bg2(lv_event_t * e);
extern lv_obj_t * ui_menu1_bg2;
extern lv_obj_t * ui_menu1_name2;
extern lv_obj_t * ui_menu2_bg2;
void ui_event_menu2_name2(lv_event_t * e);
extern lv_obj_t * ui_menu2_name2;
void ui_event_menu3_bg2(lv_event_t * e);
extern lv_obj_t * ui_menu3_bg2;
void ui_event_menu3_name2(lv_event_t * e);
extern lv_obj_t * ui_menu3_name2;
extern lv_obj_t * ui_backgroud_top2;
extern lv_obj_t * ui_battery2;
extern lv_obj_t * ui_time1;
extern lv_obj_t * ui_backgroud_2;
extern lv_obj_t * ui_Screen1_Roller2;
extern lv_obj_t * ui_Screen_meun3;
extern lv_obj_t * ui_background_menu3;
void ui_event_menu1_bg3(lv_event_t * e);
extern lv_obj_t * ui_menu1_bg3;
extern lv_obj_t * ui_menu1_name3;
void ui_event_menu2_bg3(lv_event_t * e);
extern lv_obj_t * ui_menu2_bg3;
void ui_event_menu2_name3(lv_event_t * e);
extern lv_obj_t * ui_menu2_name3;
extern lv_obj_t * ui_menu3_bg3;
void ui_event_menu3_name3(lv_event_t * e);
extern lv_obj_t * ui_menu3_name3;
extern lv_obj_t * ui_backgroud_top3;
extern lv_obj_t * ui_battery3;
extern lv_obj_t * ui_time3;
extern lv_obj_t * ui_backgroud_3;
extern lv_obj_t * ui_setting3;
extern lv_obj_t * ui_setting1;
extern lv_obj_t * ui_setting2;
extern lv_obj_t * ui_dialog_box;
extern lv_obj_t * ui_tishi;

void my_timer_time(lv_timer_t * timer);
extern lv_timer_t * timer_time;
void my_timer_show_fengsu(lv_timer_t * timer);
extern lv_timer_t * timer_show_fengsu;
void my_timer_dialog(lv_timer_t *timer);
extern lv_timer_t *timer_show_dialog;

LV_FONT_DECLARE(ui_font_Langmanyayuan24);
LV_FONT_DECLARE(ui_font_Langmanyayuan16);

extern int year;
extern int month;
extern int day;
extern int hour;
extern int minute;
extern int second;
extern int num_uart;

void ui_init(void);

#ifdef __cplusplus
} /*extern "C"*/
#endif

#endif
