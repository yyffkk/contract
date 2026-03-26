package com.ruoyi.contract.util;

import java.text.DecimalFormat;

public class ContractNumberGenerator {
    /** 合同编号前缀 */
    private static final String CONTRACT_PREFIX = "HT";

    /** 流水号长度 */
    private static final int SERIAL_NUMBER_LENGTH = 6;

    /**
     * 生成合同编号（格式：HT-年月日 - 流水号）
     * 例如：HT-20251104-000001
     *
     * @param maxSerialNumber 当前最大流水号
     * @param dateStr 日期字符串（格式：yyyyMMdd）
     * @return 合同编号
     */
    public static String generateContractNumber(Integer maxSerialNumber, String dateStr)
    {
        // 流水号 +1
        int newSerialNumber = (maxSerialNumber == null || maxSerialNumber == 0) ? 1 : maxSerialNumber + 1;

        // 格式化流水号，不足 6 位左补零
        DecimalFormat df = new DecimalFormat("000000");
        String serialNumber = df.format(newSerialNumber);

        // 组装合同编号：HT-年月日 - 流水号
        return CONTRACT_PREFIX + "-" + dateStr + "-" + serialNumber;
    }
}
