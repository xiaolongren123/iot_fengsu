/**
 ****************************************************************************************************
 * @file        usbd_conf.c
 * @author      ����ԭ���Ŷ�(ALIENTEK)
 * @version     V1.0
 * @date        2020-05-13
 * @brief       usbd_conf ��������
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
 * V1.0 20200513
 * ��һ�η���
 *
 ****************************************************************************************************
 */

#include "usbd_conf.h"
#include "usbd_core.h"
#include "usbd_def.h"
#include "usbd_msc.h"
#include "stm32f1xx_hal_pcd.h"
#include "./SYSTEM/usart/usart.h"
#include "./SYSTEM/delay/delay.h"


/* PCD���� */
PCD_HandleTypeDef hpcd;


/* USB����״̬
 * 0,û������;
 * 1,�Ѿ�����;
 */
volatile uint8_t g_device_state = 0;    /* Ĭ��û������ */

/**
 * @brief       USB�ӿ�����
 *   @note      ʹ��/�ر�USB�ӿ�, �Ա�ÿ����������������
 *              ����USB
 * @param       state   : �ӿ�״̬
 *   @arg       0, �Ͽ�USB����
 *   @arg       1, ʹ��USB����
 * @retval      ��
 */
void usbd_port_config(uint8_t state)
{
    GPIO_InitTypeDef GPIO_Initure;
    __HAL_RCC_GPIOA_CLK_ENABLE();     /* ʹ��PORTAʱ�� */

    if (state)
    {
        USB->CNTR &= ~(1 << 1); /* PWDN = 0, �˳��ϵ�ģʽ */

        /* PA11/PA12,����Ϊ(USB DM/DP)�� */
        GPIO_Initure.Pin = (GPIO_PIN_11 | GPIO_PIN_12);
        GPIO_Initure.Mode = GPIO_MODE_AF_INPUT;
        GPIO_Initure.Pull = GPIO_PULLUP;
        GPIO_Initure.Speed = GPIO_SPEED_FREQ_HIGH;
        HAL_GPIO_Init(GPIOA, &GPIO_Initure);
    }
    else
    {
        USB->CNTR |= 1 << 1;    /* PWDN = 1, ����ϵ�ģʽ */

        /* PA11����ģʽ����,������� */
        GPIO_Initure.Pin = GPIO_PIN_11;
        GPIO_Initure.Mode = GPIO_MODE_OUTPUT_PP;       
        GPIO_Initure.Pull = GPIO_PULLUP;           
        GPIO_Initure.Speed = GPIO_SPEED_FREQ_HIGH; 
        HAL_GPIO_Init(GPIOA, &GPIO_Initure);

        /* PA12����ģʽ����,������� */
        GPIO_Initure.Pin = GPIO_PIN_12;
        HAL_GPIO_Init(GPIOA, &GPIO_Initure);

        HAL_GPIO_WritePin(GPIOA, GPIO_PIN_11, GPIO_PIN_RESET); /* PA11 = 0 */
        HAL_GPIO_WritePin(GPIOA, GPIO_PIN_12, GPIO_PIN_RESET); /* PA12 = 0 */
    }
}

/**
 * @brief       ��ʼ��PCD MSP
 *   @note      ����һ���ص�����, ��stm32f1xx_hal_pcd.c�������
 * @param       hpcd    : PCD�ṹ��ָ��
 * @retval      ��
 */
void HAL_PCD_MspInit(PCD_HandleTypeDef *hpcd)
{
    GPIO_InitTypeDef GPIO_Initure;
    __HAL_RCC_GPIOA_CLK_ENABLE();       /* ʹ��PORTAʱ�� */
    __HAL_RCC_USB_CLK_ENABLE();         /* ʹ��USBʱ�� */

    /* PA11/PA12,����Ϊ(USB DM/DP)�� */
    GPIO_Initure.Pin = (GPIO_PIN_11 | GPIO_PIN_12);
    GPIO_Initure.Mode = GPIO_MODE_AF_INPUT;
    GPIO_Initure.Pull = GPIO_PULLUP;
    GPIO_Initure.Speed = GPIO_SPEED_FREQ_HIGH;
    HAL_GPIO_Init(GPIOA, &GPIO_Initure);

    HAL_NVIC_SetPriority(USB_LP_CAN1_RX0_IRQn, 0, 3); /* ��ռ0�������ȼ�3����2 */
    HAL_NVIC_EnableIRQ(USB_LP_CAN1_RX0_IRQn);         /*����USB�ж�*/
}

/**
 * @brief       USB OTG �жϷ�����
 *   @note      ��������USB�ж�
 * @param       ��
 * @retval      ��
 */
void USB_LP_CAN1_RX0_IRQHandler(void)
{
    HAL_PCD_IRQHandler(&hpcd);
}

/******************************************************************************************/
/* ������: USBD LL PCD �����Ļص�����(PCD->USB Device Library) */


/**
 * @brief       USBD ���ý׶λص�����
 * @param       hpcd    : PCD�ṹ��ָ��
 * @retval      ��
 */
void HAL_PCD_SetupStageCallback(PCD_HandleTypeDef *hpcd)
{
    USBD_LL_SetupStage(hpcd->pData, (uint8_t *) hpcd->Setup);
}

/**
 * @brief       USBD OUT �׶λص�����
 * @param       hpcd    : PCD�ṹ��ָ��
 * @param       epnum   : �˵��
 * @retval      ��
 */
void HAL_PCD_DataOutStageCallback(PCD_HandleTypeDef *hpcd, uint8_t epnum)
{
    USBD_LL_DataOutStage(hpcd->pData, epnum, hpcd->OUT_ep[epnum].xfer_buff);
}

/**
 * @brief       USBD IN �׶λص�����
 * @param       hpcd    : PCD�ṹ��ָ��
 * @param       epnum   : �˵��
 * @retval      ��
 */
void HAL_PCD_DataInStageCallback(PCD_HandleTypeDef *hpcd, uint8_t epnum)
{
    USBD_LL_DataInStage(hpcd->pData, epnum, hpcd->IN_ep[epnum].xfer_buff);
}

/**
 * @brief       USBD SOF �ص�����
 * @param       hpcd    : PCD�ṹ��ָ��
 * @retval      ��
 */
void HAL_PCD_SOFCallback(PCD_HandleTypeDef *hpcd)
{
    USBD_LL_SOF(hpcd->pData);
}

/**
 * @brief       USBD ��λ�ص�����
 * @param       hpcd    : PCD�ṹ��ָ��
 * @retval      ��
 */
void HAL_PCD_ResetCallback(PCD_HandleTypeDef *hpcd)
{
    printf("USB Device Library  [FS]\r\n");
    USBD_LL_SetSpeed((USBD_HandleTypeDef *) hpcd->pData, USBD_SPEED_FULL);
    USBD_LL_Reset((USBD_HandleTypeDef *) hpcd->pData);  /* ��λ�豸 */
}

/**
 * @brief       USBD ����ص�����
 * @param       hpcd    : PCD�ṹ��ָ��
 * @retval      ��
 */
void HAL_PCD_SuspendCallback(PCD_HandleTypeDef *hpcd)
{
    g_device_state = 0;
    printf("Device In suspend mode.\r\n");
    USBD_LL_Suspend(hpcd->pData);
}

/**
 * @brief       USBD �ָ��ص�����
 * @param       hpcd    : PCD�ṹ��ָ��
 * @retval      ��
 */
void HAL_PCD_ResumeCallback(PCD_HandleTypeDef *hpcd)
{
    printf("Device Resumed\r\n");
    USBD_LL_Resume(hpcd->pData);
}

/**
 * @brief       USBD ISO OUT ������ɻص�����
 * @param       hpcd    : PCD�ṹ��ָ��
 * @param       epnum   : �˵��
 * @retval      ��
 */
void HAL_PCD_ISOOUTIncompleteCallback(PCD_HandleTypeDef *hpcd, uint8_t epnum)
{
    USBD_LL_IsoOUTIncomplete(hpcd->pData, epnum);
}

/**
 * @brief       USBD ISO IN ������ɻص�����
 * @param       hpcd    : PCD�ṹ��ָ��
 * @param       epnum   : �˵��
 * @retval      ��
 */
void HAL_PCD_ISOINIncompleteCallback(PCD_HandleTypeDef *hpcd, uint8_t epnum)
{
    USBD_LL_IsoINIncomplete(hpcd->pData, epnum);
}

/**
 * @brief       USBD ���ӳɹ��ص�����
 * @param       hpcd    : PCD�ṹ��ָ��
 * @retval      ��
 */
void HAL_PCD_ConnectCallback(PCD_HandleTypeDef *hpcd)
{
    g_device_state = 1;
    USBD_LL_DevConnected(hpcd->pData);
}

/**
 * @brief       USBD �Ͽ����ӻص�����
 * @param       hpcd    : PCD�ṹ��ָ��
 * @retval      ��
 */
void HAL_PCD_DisconnectCallback(PCD_HandleTypeDef *hpcd)
{
    g_device_state = 0;
    printf("USB Device Disconnected.\r\n");
    USBD_LL_DevDisconnected(hpcd->pData);
}

/******************************************************************************************/
/* ������: USBD LL �����ӿں���(PCD->USB Device Library) */


/**
 * @brief       USBD �ײ��ʼ������
 * @param       pdev    : USBD���ָ��
 * @retval      USB״̬
 *   @arg       USBD_OK(0)   , ����;
 *   @arg       USBD_BUSY(1) , æ;
 *   @arg       USBD_FAIL(2) , ʧ��;
 */
USBD_StatusTypeDef USBD_LL_Init(USBD_HandleTypeDef *pdev)
{
    /* ����LL������ز��� */
    hpcd.Instance = USB;                    /* ʹ��USB */
    hpcd.Init.dev_endpoints = 8;            /* �˵���Ϊ8 */
    hpcd.Init.phy_itface = PCD_PHY_EMBEDDED;/* ʹ���ڲ�PHY */
    hpcd.Init.speed = PCD_SPEED_FULL;       /* USBȫ��(12Mbps) */
    hpcd.Init.low_power_enable = 0;         /* ��ʹ�ܵ͹���ģʽ */

    hpcd.pData = pdev;                      /* hpcd��pDataָ��pdev */
    pdev->pData = &hpcd;                    /* pdev��pDataָ��hpcd */

    HAL_PCD_Init((PCD_HandleTypeDef *) pdev->pData);    /* ��ʼ��LL���� */

    HAL_PCDEx_PMAConfig(pdev->pData, 0x00, PCD_SNG_BUF, 0x18);
    HAL_PCDEx_PMAConfig(pdev->pData, 0x80, PCD_SNG_BUF, 0x58);
    HAL_PCDEx_PMAConfig(pdev->pData, MSC_EPIN_ADDR, PCD_SNG_BUF, 0x98);
    HAL_PCDEx_PMAConfig(pdev->pData, MSC_EPOUT_ADDR, PCD_SNG_BUF, 0xD8);

    return USBD_OK;
}

/**
 * @brief       USBD �ײ�ȡ����ʼ��(�ظ�Ĭ�ϸ�λ״̬)����
 * @param       pdev    : USBD���ָ��
 * @retval      USB״̬
 *   @arg       USBD_OK(0)   , ����;
 *   @arg       USBD_BUSY(1) , æ;
 *   @arg       USBD_FAIL(2) , ʧ��;
 */
USBD_StatusTypeDef USBD_LL_DeInit(USBD_HandleTypeDef *pdev)
{
    HAL_PCD_DeInit(pdev->pData);
    return USBD_OK;
}

/**
 * @brief       USBD �ײ�������ʼ����
 * @param       pdev    : USBD���ָ��
 * @retval      USB״̬
 *   @arg       USBD_OK(0)   , ����;
 *   @arg       USBD_BUSY(1) , æ;
 *   @arg       USBD_FAIL(2) , ʧ��;
 */
USBD_StatusTypeDef USBD_LL_Start(USBD_HandleTypeDef *pdev)
{
    HAL_PCD_Start(pdev->pData);
    return USBD_OK;
}

/**
 * @brief       USBD �ײ�����ֹͣ����
 * @param       pdev    : USBD���ָ��
 * @retval      USB״̬
 *   @arg       USBD_OK(0)   , ����;
 *   @arg       USBD_BUSY(1) , æ;
 *   @arg       USBD_FAIL(2) , ʧ��;
 */
USBD_StatusTypeDef USBD_LL_Stop(USBD_HandleTypeDef *pdev)
{
    HAL_PCD_Stop(pdev->pData);
    return USBD_OK;
}

/**
 * @brief       USBD ��ʼ��(��)ĳ���˵�
 * @param       pdev    : USBD���ָ��
 * @param       ep_addr : �˵��
 * @param       ep_type : �˵�����
 * @param       ep_mps  : �˵���������(�ֽ�)
 * @retval      USB״̬
 *   @arg       USBD_OK(0)   , ����;
 *   @arg       USBD_BUSY(1) , æ;
 *   @arg       USBD_FAIL(2) , ʧ��;
 */
USBD_StatusTypeDef USBD_LL_OpenEP(USBD_HandleTypeDef *pdev, uint8_t ep_addr, uint8_t ep_type, uint16_t ep_mps)
{
    HAL_PCD_EP_Open(pdev->pData, ep_addr, ep_mps, ep_type);
    return USBD_OK;
}

/**
 * @brief       USBD ȡ����ʼ��(�ر�)ĳ���˵�
 * @param       pdev    : USBD���ָ��
 * @param       ep_addr : �˵��
 * @retval      USB״̬
 *   @arg       USBD_OK(0)   , ����;
 *   @arg       USBD_BUSY(1) , æ;
 *   @arg       USBD_FAIL(2) , ʧ��;
 */
USBD_StatusTypeDef USBD_LL_CloseEP(USBD_HandleTypeDef *pdev, uint8_t ep_addr)
{
    HAL_PCD_EP_Close(pdev->pData, ep_addr);
    return USBD_OK;
}

/**
 * @brief       USBD ���ĳ���˵������
 * @param       pdev    : USBD���ָ��
 * @param       ep_addr : �˵��
 * @retval      USB״̬
 *   @arg       USBD_OK(0)   , ����;
 *   @arg       USBD_BUSY(1) , æ;
 *   @arg       USBD_FAIL(2) , ʧ��;
 */
USBD_StatusTypeDef USBD_LL_FlushEP(USBD_HandleTypeDef *pdev, uint8_t ep_addr)
{
    HAL_PCD_EP_Flush(pdev->pData, ep_addr);
    return USBD_OK;
}

/**
 * @brief       USBD ��ĳ���˵�����һ����ͣ״̬
 * @param       pdev    : USBD���ָ��
 * @param       ep_addr : �˵��
 * @retval      USB״̬
 *   @arg       USBD_OK(0)   , ����;
 *   @arg       USBD_BUSY(1) , æ;
 *   @arg       USBD_FAIL(2) , ʧ��;
 */
USBD_StatusTypeDef USBD_LL_StallEP(USBD_HandleTypeDef *pdev, uint8_t ep_addr)
{
    HAL_PCD_EP_SetStall(pdev->pData, ep_addr);
    return USBD_OK;
}

/**
 * @brief       USBD ȡ��ĳ���˵����ͣ״̬
 * @param       pdev    : USBD���ָ��
 * @param       ep_addr : �˵��
 * @retval      USB״̬
 *   @arg       USBD_OK(0)   , ����;
 *   @arg       USBD_BUSY(1) , æ;
 *   @arg       USBD_FAIL(2) , ʧ��;
 */
USBD_StatusTypeDef USBD_LL_ClearStallEP(USBD_HandleTypeDef *pdev,
                                        uint8_t ep_addr)
{
    HAL_PCD_EP_ClrStall(pdev->pData, ep_addr);
    return USBD_OK;
}

/**
 * @brief       USBD �����Ƿ�����ͣ״̬
 * @param       pdev    : USBD���ָ��
 * @param       ep_addr : �˵��
 * @retval      0, ����ͣ; 1, ��ͣ;
 */
uint8_t USBD_LL_IsStallEP(USBD_HandleTypeDef *pdev, uint8_t ep_addr)
{
    PCD_HandleTypeDef *hpcd = pdev->pData;

    if ((ep_addr & 0x80) == 0x80)
    {
        return hpcd->IN_ep[ep_addr & 0x7F].is_stall;
    }
    else
    {
        return hpcd->OUT_ep[ep_addr & 0x7F].is_stall;
    }
}

/**
 * @brief       USBD Ϊ�豸ָ���µ�USB��ַ
 * @param       pdev    : USBD���ָ��
 * @param       dev_addr: �µ��豸��ַ,USB1_OTG_HS/USB2_OTG_HS
 * @retval      USB״̬
 *   @arg       USBD_OK(0)   , ����;
 *   @arg       USBD_BUSY(1) , æ;
 *   @arg       USBD_FAIL(2) , ʧ��;
 */
USBD_StatusTypeDef USBD_LL_SetUSBAddress(USBD_HandleTypeDef *pdev, uint8_t dev_addr)
{
    g_device_state = 1; /* ��ִ�е��ú���,˵��USB���ӳɹ��� */
    HAL_PCD_SetAddress(pdev->pData, dev_addr);
    return USBD_OK;
}

/**
 * @brief       USBD ͨ���˵㷢������
 * @param       pdev    : USBD���ָ��
 * @param       ep_addr : �˵��
 * @param       pbuf    : ���ݻ������׵�ַ
 * @param       size    : Ҫ���͵����ݴ�С
 * @retval      USB״̬
 *   @arg       USBD_OK(0)   , ����;
 *   @arg       USBD_BUSY(1) , æ;
 *   @arg       USBD_FAIL(2) , ʧ��;
 */
USBD_StatusTypeDef USBD_LL_Transmit(USBD_HandleTypeDef *pdev, uint8_t ep_addr, uint8_t *pbuf, uint16_t size)
{
    HAL_PCD_EP_Transmit(pdev->pData, ep_addr, pbuf, size);
    return USBD_OK;
}

/**
 * @brief       USBD ׼��һ���˵��������
 * @param       pdev    : USBD���ָ��
 * @param       ep_addr : �˵��
 * @param       pbuf    : ���ݻ������׵�ַ
 * @param       size    : Ҫ���յ����ݴ�С
 * @retval      USB״̬
 *   @arg       USBD_OK(0)   , ����;
 *   @arg       USBD_BUSY(1) , æ;
 *   @arg       USBD_FAIL(2) , ʧ��;
 */
USBD_StatusTypeDef USBD_LL_PrepareReceive(USBD_HandleTypeDef *pdev, uint8_t ep_addr, uint8_t *pbuf, uint16_t size)
{
    HAL_PCD_EP_Receive(pdev->pData, ep_addr, pbuf, size);
    return USBD_OK;
}

/**
 * @brief       USBD ��ȡ���һ��������Ĵ�С
 * @param       pdev    : USBD���ָ��
 * @param       ep_addr : �˵��
 * @retval      ����С
 */
uint32_t USBD_LL_GetRxDataSize(USBD_HandleTypeDef *pdev, uint8_t ep_addr)
{
    return HAL_PCD_EP_GetRxCount(pdev->pData, ep_addr);
}

/**
 * @brief       USBD ��ʱ����(��msΪ��λ)
 * @param       Delay   : ��ʱ��ms��
 * @retval      ��
 */
void USBD_LL_Delay(uint32_t Delay)
{
    delay_ms(Delay);
}



























