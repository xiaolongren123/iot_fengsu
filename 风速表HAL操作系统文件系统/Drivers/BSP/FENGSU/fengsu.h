#ifndef __FENGSU_H
#define __FENGSU_H	 
#include "./SYSTEM/sys/sys.h"

extern unsigned int Total_times;

#define KEY0_INT_GPIO_PORT              GPIOB
#define KEY0_INT_GPIO_PIN               GPIO_PIN_15
#define KEY0_INT_GPIO_CLK_ENABLE()      do{ __HAL_RCC_GPIOB_CLK_ENABLE(); }while(0)   /* PB口时钟使能 */
#define KEY0_INT_IRQn                   EXTI15_10_IRQn
#define KEY0_INT_IRQHandler             EXTI4_IRQHandler
	
void extix_init(void);  /* 外部中断初始化 */
void extix_disinit(void);				    /* 外部中断反初始化 */


#define FENGSU_GPIO_PORT                  GPIOB
#define FENGSU_GPIO_PIN                   GPIO_PIN_15
#define FENGSU_GPIO_CLK_ENABLE()          do{ __HAL_RCC_GPIOB_CLK_ENABLE(); }while(0)   /* PB口时钟使能 */

#define fengsu_state        HAL_GPIO_ReadPin(FENGSU_GPIO_PORT, FENGSU_GPIO_PIN)     /* 读取KEY0引脚 */

void fengsu_init(void);                /* 按键初始化函数 */
#endif
