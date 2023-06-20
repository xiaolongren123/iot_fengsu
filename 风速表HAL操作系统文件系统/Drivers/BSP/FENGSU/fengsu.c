#include "./SYSTEM/sys/sys.h"
#include "fengsu.h"

unsigned int Total_times=0;//总的光电次数




void KEY0_INT_IRQHandler(void)
{
    HAL_GPIO_EXTI_IRQHandler(KEY0_INT_GPIO_PIN);         /* 调用中断处理公用函数 清除KEY0所在中断线 的中断标志位 */
    __HAL_GPIO_EXTI_CLEAR_IT(KEY0_INT_GPIO_PIN);         /* HAL库默认先清中断再处理回调，退出时再清一次中断，避免按键抖动误触发 */
}

/**
 * @brief       中断服务程序中需要做的事情
                在HAL库中所有的外部中断服务函数都会调用此函数
 * @param       GPIO_Pin:中断引脚号
 * @retval      无
 */
void HAL_GPIO_EXTI_Callback(uint16_t GPIO_Pin)
{
    Total_times++;
}

/**
 * @brief       外部中断初始化程序
 * @param       无
 * @retval      无
 */
void extix_init(void)
{

    GPIO_InitTypeDef gpio_init_struct;
    KEY0_INT_GPIO_CLK_ENABLE();                                  /* KEY0时钟使能 */

    gpio_init_struct.Pin = KEY0_INT_GPIO_PIN;
    gpio_init_struct.Mode = GPIO_MODE_IT_RISING;            /* 上升沿触发 */
    gpio_init_struct.Pull = GPIO_PULLUP;
    HAL_GPIO_Init(KEY0_INT_GPIO_PORT, &gpio_init_struct);    /* KEY0配置为上升沿触发中断 */


    HAL_NVIC_SetPriority(KEY0_INT_IRQn, 4, 4);               /* 抢占0，子优先级2 */
    HAL_NVIC_EnableIRQ(KEY0_INT_IRQn);                       /* 使能中断线4 */

    Total_times=0;

}

void extix_disinit(void){
	
	  GPIO_InitTypeDef gpio_init_struct;
    KEY0_INT_GPIO_CLK_ENABLE();                                  /* KEY0时钟使能 */

    gpio_init_struct.Pin = KEY0_INT_GPIO_PIN;
    gpio_init_struct.Mode = GPIO_MODE_IT_RISING;            /* 上升沿触发 */
    gpio_init_struct.Pull = GPIO_PULLUP;
    HAL_GPIO_Init(KEY0_INT_GPIO_PORT, &gpio_init_struct);    /* KEY0配置为上升沿触发中断 */


    HAL_NVIC_SetPriority(KEY0_INT_IRQn, 4, 4);   
	HAL_NVIC_DisableIRQ(KEY0_INT_IRQn);
}




void fengsu_init(void){
	GPIO_InitTypeDef gpio_init_struct;
    FENGSU_GPIO_CLK_ENABLE();                                     /* KEY0时钟使能 */
	gpio_init_struct.Pin = FENGSU_GPIO_PIN;                       /* KEY0引脚 */
    gpio_init_struct.Mode = GPIO_MODE_INPUT;                    /* 输入 */
    gpio_init_struct.Pull = GPIO_PULLUP;                        /* 上拉 */
    gpio_init_struct.Speed = GPIO_SPEED_FREQ_HIGH;              /* 高速 */
    HAL_GPIO_Init(FENGSU_GPIO_PORT, &gpio_init_struct);           /* KEY0引脚模式设置,上拉输入 */
}
