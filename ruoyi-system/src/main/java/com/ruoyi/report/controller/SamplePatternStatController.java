package com.ruoyi.report.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.report.service.ISamplePatternStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/report/samplePatternStat")
public class SamplePatternStatController extends BaseController {

    @Autowired
    private ISamplePatternStatService samplePatternStatService;

    @GetMapping("/list")
    public AjaxResult list() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(5);
        return AjaxResult.success()
                .put("title", "本周样衣样板数统计表")
                .put("dateRange", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 至 " + end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .put("days", 6)
                .put("rows", samplePatternStatService.listRecentSixDaysStats());
    }
}
