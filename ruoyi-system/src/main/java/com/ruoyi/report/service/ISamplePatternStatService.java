package com.ruoyi.report.service;

import com.ruoyi.report.domain.SamplePatternStat;

import java.util.List;

public interface ISamplePatternStatService {
    List<SamplePatternStat> listRecentSixDaysStats();
}
