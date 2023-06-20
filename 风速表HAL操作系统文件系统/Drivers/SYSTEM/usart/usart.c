/**
 ****************************************************************************************************
 * @file        usart.c
 * @author      ����ԭ���Ŷ�(ALIENTEK)
 * @version     V1.0
 * @date        2020-04-20
 * @brief       ���ڳ�ʼ������(һ���Ǵ���1)��֧��printf
 * @license     Copyright (c) 2020-2032, ������������ӿƼ����޹�˾
 ****************************************************************************************************
 * @attention
 *
 * ʵ��ƽ̨:����ԭ�� STM32F103������
 * ������Ƶ:www.yuanzige.com
 * ������̳:www.openedv.com
 * ��˾��ַ:www.alientek.com
 * �����ַ:openedv.taobao.com
 *
 * �޸�˵��
 * V1.0 20211103
 * ��һ�η���
 *
 ****************************************************************************************************
 */

#include "./SYSTEM/sys/sys.h"
#include "./SYSTEM/usart/usart.h"
#include "ui.h"

/* ���ʹ��os,����������ͷ�ļ�����. */
#if SYS_SUPPORT_OS
#include "FreeRTOS.h"   /* FreeRTOS ʹ�� */
#include "task.h"
#endif

/******************************************************************************************/
/* �������´���, ֧��printf����, ������Ҫѡ��use MicroLIB */

#if 1

#if (__ARMCC_VERSION >= 6010050)            /* ʹ��AC6������ʱ */
__asm(".global __use_no_semihosting\n\t");  /* ������ʹ�ð�����ģʽ */
__asm(".global __ARM_use_no_argv \n\t");    /* AC6����Ҫ����main����Ϊ�޲�����ʽ�����򲿷����̿��ܳ��ְ�����ģʽ */

#else
/* ʹ��AC5������ʱ, Ҫ�����ﶨ��__FILE �� ��ʹ�ð�����ģʽ */
#pragma import(__use_no_semihosting)

struct __FILE
{
    int handle;
    /* Whatever you require here. If the only file you are using is */
    /* standard output using printf() for debugging, no file handling */
    /* is required. */
};

#endif

/* ��ʹ�ð�����ģʽ��������Ҫ�ض���_ttywrch\_sys_exit\_sys_command_string����,��ͬʱ����AC6��AC5ģʽ */
int _ttywrch(int ch)
{
    ch = ch;
    return ch;
}

/* ����_sys_exit()�Ա���ʹ�ð�����ģʽ */
void _sys_exit(int x)
{
    x = x;
}

char *_sys_command_string(char *cmd, int len)
{
    return NULL;
}


/* FILE �� stdio.h���涨��. */
FILE __stdout;

/* MDK����Ҫ�ض���fputc����, printf�������ջ�ͨ������fputc����ַ��������� */
int fputc(int ch, FILE *f)
{
    while ((USART_UX->SR & 0X40) == 0);     /* �ȴ���һ���ַ�������� */

    USART_UX->DR = (uint8_t)ch;             /* ��Ҫ���͵��ַ� ch д�뵽DR�Ĵ��� */
    return ch;
}
#endif
/******************************************************************************************/

#if USART_EN_RX /*���ʹ���˽���*/

/* ���ջ���, ���USART_REC_LEN���ֽ�. */
uint8_t g_usart_rx_buf[USART_REC_LEN];

/*  ����״̬
 *  bit15��      ������ɱ�־
 *  bit14��      ���յ�0x0d
 *  bit13~0��    ���յ�����Ч�ֽ���Ŀ
*/
uint16_t g_usart_rx_sta = 0;

uint8_t g_rx_buffer[RXBUFFERSIZE];  /* HAL��ʹ�õĴ��ڽ��ջ��� */

UART_HandleTypeDef g_uart1_handle;  /* UART��� */

/**
 * @brief       ����X��ʼ������
 * @param       baudrate: ������, �����Լ���Ҫ���ò�����ֵ
 * @note        ע��: ����������ȷ��ʱ��Դ, ���򴮿ڲ����ʾͻ������쳣.
 *              �����USART��ʱ��Դ��sys_stm32_clock_init()�������Ѿ����ù���.
 * @retval      ��
 */
void usart_init(uint32_t baudrate)
{
    /*UART ��ʼ������*/
    g_uart1_handle.Instance = USART_UX;                                       /* USART_UX */
    g_uart1_handle.Init.BaudRate = baudrate;                                  /* ������ */
    g_uart1_handle.Init.WordLength = UART_WORDLENGTH_8B;                      /* �ֳ�Ϊ8λ���ݸ�ʽ */
    g_uart1_handle.Init.StopBits = UART_STOPBITS_1;                           /* һ��ֹͣλ */
    g_uart1_handle.Init.Parity = UART_PARITY_NONE;                            /* ����żУ��λ */
    g_uart1_handle.Init.HwFlowCtl = UART_HWCONTROL_NONE;                      /* ��Ӳ������ */
    g_uart1_handle.Init.Mode = UART_MODE_TX_RX;                               /* �շ�ģʽ */
    HAL_UART_Init(&g_uart1_handle);                                           /* HAL_UART_Init()��ʹ��UART1 */

    /* �ú����Ὺ�������жϣ���־λUART_IT_RXNE���������ý��ջ����Լ����ջ��������������� */
    HAL_UART_Receive_IT(&g_uart1_handle, (uint8_t *)g_rx_buffer, RXBUFFERSIZE);
}

/**
 * @brief       UART�ײ��ʼ������
 * @param       huart: UART�������ָ��
 * @note        �˺����ᱻHAL_UART_Init()����
 *              ���ʱ��ʹ�ܣ��������ã��ж�����
 * @retval      ��
 */
void HAL_UART_MspInit(UART_HandleTypeDef *huart)
{
    GPIO_InitTypeDef gpio_init_struct;

    if (huart->Instance == USART_UX)                            /* ����Ǵ���1�����д���1 MSP��ʼ�� */
    {
        USART_TX_GPIO_CLK_ENABLE();                             /* ʹ�ܴ���TX��ʱ�� */
        USART_RX_GPIO_CLK_ENABLE();                             /* ʹ�ܴ���RX��ʱ�� */
        USART_UX_CLK_ENABLE();                                  /* ʹ�ܴ���ʱ�� */

        gpio_init_struct.Pin = USART_TX_GPIO_PIN;               /* ���ڷ������ź� */
        gpio_init_struct.Mode = GPIO_MODE_AF_PP;                /* ����������� */
        gpio_init_struct.Pull = GPIO_PULLUP;                    /* ���� */
        gpio_init_struct.Speed = GPIO_SPEED_FREQ_HIGH;          /* IO�ٶ�����Ϊ���� */
        HAL_GPIO_Init(USART_TX_GPIO_PORT, &gpio_init_struct);

        gpio_init_struct.Pin = USART_RX_GPIO_PIN;               /* ����RX�� ģʽ���� */
        gpio_init_struct.Mode = GPIO_MODE_AF_INPUT;
        HAL_GPIO_Init(USART_RX_GPIO_PORT, &gpio_init_struct);   /* ����RX�� �������ó�����ģʽ */

#if USART_EN_RX
        HAL_NVIC_EnableIRQ(USART_UX_IRQn);                      /* ʹ��USART1�ж�ͨ�� */
        HAL_NVIC_SetPriority(USART_UX_IRQn, 3, 3);              /* ��2��������ȼ�:��ռ���ȼ�3�������ȼ�3 */
#endif
    }
}



uint8_t SerialData[36];      //Com���ݽ��ջ���
uint8_t SerialDataLen=0;  //Com���ݽ��ճ���
uint8_t SerialDataSta=0;  //Com���ݽ���״̬

/**
 * @brief       �������ݽ��ջص�����
                ���ݴ������������
 * @param       huart:���ھ��
 * @retval      ��
 */
void HAL_UART_RxCpltCallback(UART_HandleTypeDef *huart)
{

//    if (huart->Instance == USART_UX)                    /* ����Ǵ���1 */
//    {
//        if (g_usart_rx_sta==0)             /* ����δ��� */
//        {
//            if (g_rx_buffer[0] != '"')             /* ���յ��Ĳ���0x0a�������ǻ��м��� */
//            {
//                g_usart_rx_sta = 0;                 /* ���մ���,���¿�ʼ */
//            }
//            else                                    /* ���յ�����0x0a�������м��� */
//            {
//                g_usart_rx_sta=1;           /* ��������� */
//            }
//            g_usart_rx_buf[num_uart] = g_rx_buffer[0];
//            num_uart++;
//						if(g_usart_rx_sta==1)
//						{
//							  year=(int)(g_usart_rx_buf[16]-'0')*1000+(int)(g_usart_rx_buf[17]-'0')*100+(int)(g_usart_rx_buf[18]-'0')*10+(int)(g_usart_rx_buf[19]-'0');
//                month=(int)(g_usart_rx_buf[21]-'0')*10+(int)(g_usart_rx_buf[22]-'0');
//                day=(int)(g_usart_rx_buf[24]-'0')*10+(int)(g_usart_rx_buf[25]-'0');
//                hour=(int)(g_usart_rx_buf[27]-'0')*10+(int)(g_usart_rx_buf[28]-'0');
//                minute=(int)(g_usart_rx_buf[30]-'0')*10+(int)(g_usart_rx_buf[31]-'0');
//                second=(int)(g_usart_rx_buf[33]-'0')*10+(int)(g_usart_rx_buf[34]-'0');
//								num_uart=0;
//						}
//        }
//    }
		
		
		if (huart->Instance == USART_UX)                    /* ����Ǵ���1 */
    {

					if (g_rx_buffer[0] == ':'||num_uart>0)             /* ���յ��Ĳ���0x0a�������ǻ��м��� */
            {
							  g_usart_rx_buf[num_uart] = g_rx_buffer[0];
								num_uart++;
            }
            else                                    /* ���յ�����0x0a�������м��� */
            {
                num_uart=0;
            }

						if(num_uart==21)
						{
							num_uart=0;
							  year=(int)(g_usart_rx_buf[2]-'0')*1000+(int)(g_usart_rx_buf[3]-'0')*100+(int)(g_usart_rx_buf[4]-'0')*10+(int)(g_usart_rx_buf[5]-'0');
                month=(int)(g_usart_rx_buf[7]-'0')*10+(int)(g_usart_rx_buf[8]-'0');
                day=(int)(g_usart_rx_buf[10]-'0')*10+(int)(g_usart_rx_buf[11]-'0');
                hour=(int)(g_usart_rx_buf[13]-'0')*10+(int)(g_usart_rx_buf[14]-'0');
                minute=(int)(g_usart_rx_buf[16]-'0')*10+(int)(g_usart_rx_buf[17]-'0');
                second=(int)(g_usart_rx_buf[19]-'0')*10+(int)(g_usart_rx_buf[20]-'0');
						}

    }
		
		
}

/**
 * @brief       ����X�жϷ�����
                ע��,��ȡUSARTx->SR�ܱ���Ī������Ĵ���
 * @param       ��
 * @retval      ��
 */
void USART_UX_IRQHandler(void)
{
    HAL_UART_IRQHandler(&g_uart1_handle);                               /* ����HAL���жϴ����ú��� */

    while (HAL_UART_Receive_IT(&g_uart1_handle, (uint8_t *)g_rx_buffer, RXBUFFERSIZE) != HAL_OK)     /* ���¿����жϲ��������� */
    {
        /* �������Ῠ�������� */
    }
}
#endif



