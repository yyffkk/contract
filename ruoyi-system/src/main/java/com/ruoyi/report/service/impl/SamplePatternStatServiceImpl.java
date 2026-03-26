package com.ruoyi.report.service.impl;

import com.ruoyi.report.domain.SamplePatternStat;
import com.ruoyi.report.service.ISamplePatternStatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class SamplePatternStatServiceImpl implements ISamplePatternStatService {

    @Value("${sampleReport.sqlserver.url:jdbc:sqlserver://192.168.0.2:1433;databaseName=UFDATA_328_2020;encrypt=false;trustServerCertificate=true}")
    private String sqlServerUrl;

    @Value("${sampleReport.sqlserver.username:Sa}")
    private String sqlServerUsername;

    @Value("${sampleReport.sqlserver.password:Sa123}")
    private String sqlServerPassword;

    private static final BigDecimal SIX = new BigDecimal("6");

    @Override
    public List<SamplePatternStat> listRecentSixDaysStats() {
        List<SamplePatternStat> rows = queryDesignerStats();
        rows.sort(Comparator.comparing(SamplePatternStat::getPublishCount, Comparator.nullsFirst(BigDecimal::compareTo)).reversed());

        List<SamplePatternStat> result = new ArrayList<>();
        result.add(buildTotalRow(rows));
        result.addAll(rows);
        result.add(buildAverageRow(rows));
        return result;
    }

    private List<SamplePatternStat> queryDesignerStats() {
        List<SamplePatternStat> list = new ArrayList<>();
        String sql = "SELECT " +
                "ISNULL(NULLIF(LTRIM(RTRIM(i.cDBPersonName)), ''), N'未分配') AS designer, " +
                "CAST(SUM(ISNULL(d.iQuantity, 0)) AS DECIMAL(18,2)) AS publishCount, " +
                "CAST(SUM(CASE WHEN ISNULL(i.cSampleStatus, '') LIKE N'%存版%' THEN ISNULL(d.iQuantity, 0) ELSE 0 END) AS DECIMAL(18,2)) AS storedCount, " +
                "CAST(AVG(CASE WHEN i.dDate IS NOT NULL AND d.dDeliveryDate IS NOT NULL THEN DATEDIFF(DAY, CAST(i.dDate AS DATE), CAST(d.dDeliveryDate AS DATE)) * 1.0 END) AS DECIMAL(18,1)) AS avgCycleDays " +
                "FROM v_HY_FZ_Indication i " +
                "LEFT JOIN v_HY_FZ_IndicationDetail d ON i.ID = d.ID " +
                "WHERE i.dDate >= DATEADD(DAY, -5, CAST(GETDATE() AS DATE)) " +
                "AND i.dDate < DATEADD(DAY, 1, CAST(GETDATE() AS DATE)) " +
                "GROUP BY ISNULL(NULLIF(LTRIM(RTRIM(i.cDBPersonName)), ''), N'未分配')";

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            try (Connection connection = DriverManager.getConnection(sqlServerUrl, sqlServerUsername, sqlServerPassword);
                 PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    SamplePatternStat item = new SamplePatternStat();
                    item.setDesigner(rs.getString("designer"));
                    item.setPublishCount(scale(rs.getBigDecimal("publishCount"), 2));
                    item.setStoredCount(scale(rs.getBigDecimal("storedCount"), 2));
                    item.setAvgCycleDays(scale(rs.getBigDecimal("avgCycleDays"), 1));
                    item.setDailyAvgPublishCount(calcDailyAvg(item.getPublishCount()));
                    list.add(item);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("读取样衣样板统计失败：" + e.getMessage(), e);
        }
        return list;
    }

    private SamplePatternStat buildTotalRow(List<SamplePatternStat> rows) {
        SamplePatternStat total = new SamplePatternStat();
        total.setDesigner("技术科");
        BigDecimal publish = BigDecimal.ZERO;
        BigDecimal stored = BigDecimal.ZERO;
        BigDecimal cycle = BigDecimal.ZERO;
        int cycleCount = 0;
        for (SamplePatternStat row : rows) {
            publish = publish.add(defaultValue(row.getPublishCount()));
            stored = stored.add(defaultValue(row.getStoredCount()));
            if (row.getAvgCycleDays() != null) {
                cycle = cycle.add(row.getAvgCycleDays());
                cycleCount++;
            }
        }
        total.setPublishCount(scale(publish, 2));
        total.setStoredCount(scale(stored, 2));
        total.setDailyAvgPublishCount(calcDailyAvg(publish));
        total.setAvgCycleDays(cycleCount > 0 ? cycle.divide(new BigDecimal(cycleCount), 1, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP));
        return total;
    }

    private SamplePatternStat buildAverageRow(List<SamplePatternStat> rows) {
        SamplePatternStat avg = new SamplePatternStat();
        avg.setDesigner("平均值");
        if (rows == null || rows.isEmpty()) {
            avg.setPublishCount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            avg.setDailyAvgPublishCount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            avg.setStoredCount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            avg.setAvgCycleDays(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP));
            return avg;
        }

        BigDecimal publish = BigDecimal.ZERO;
        BigDecimal daily = BigDecimal.ZERO;
        BigDecimal stored = BigDecimal.ZERO;
        BigDecimal cycle = BigDecimal.ZERO;
        int cycleCount = 0;
        for (SamplePatternStat row : rows) {
            publish = publish.add(defaultValue(row.getPublishCount()));
            daily = daily.add(defaultValue(row.getDailyAvgPublishCount()));
            stored = stored.add(defaultValue(row.getStoredCount()));
            if (row.getAvgCycleDays() != null) {
                cycle = cycle.add(row.getAvgCycleDays());
                cycleCount++;
            }
        }
        BigDecimal size = new BigDecimal(rows.size());
        avg.setPublishCount(publish.divide(size, 2, RoundingMode.HALF_UP));
        avg.setDailyAvgPublishCount(daily.divide(size, 2, RoundingMode.HALF_UP));
        avg.setStoredCount(stored.divide(size, 2, RoundingMode.HALF_UP));
        avg.setAvgCycleDays(cycleCount > 0 ? cycle.divide(new BigDecimal(cycleCount), 1, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP));
        return avg;
    }

    private BigDecimal calcDailyAvg(BigDecimal publishCount) {
        return defaultValue(publishCount).divide(SIX, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal defaultValue(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private BigDecimal scale(BigDecimal value, int scale) {
        return defaultValue(value).setScale(scale, RoundingMode.HALF_UP);
    }
}
