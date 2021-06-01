/*
 * Copyright 2021 Kato Shinya.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.thinkit.bot.instagram.batch.strategy.report;

import java.util.Date;
import java.util.List;

import org.thinkit.bot.instagram.batch.data.content.mapper.TaskNameMapper;
import org.thinkit.bot.instagram.batch.data.mongo.entity.DailyActionTotal;
import org.thinkit.bot.instagram.batch.data.mongo.repository.DailyActionTotalRepository;
import org.thinkit.bot.instagram.util.DateUtils;
import org.thinkit.bot.instagram.util.IndentUtils;
import org.thinkit.bot.instagram.util.PresentDateSet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "from")
public final class ReportContinueSessionBuildStrategy implements ReportBuildStrategy {

    /**
     * The running user name
     */
    private String runningUserName;

    /**
     * The daily action total repository
     */
    private DailyActionTotalRepository dailyActionTotalRepository;

    @Override
    public String build() {

        final PresentDateSet presentDateSet = PresentDateSet.newInstance();
        final List<DailyActionTotal> dailyActionTotals = this.dailyActionTotalRepository
                .findByYearAndMonthAndDay(presentDateSet.getYear(), presentDateSet.getMonth(), presentDateSet.getDay());

        final StringBuilder report = new StringBuilder();
        report.append(IndentUtils.newline());
        report.append(IndentUtils.newline("Continue Session"));
        report.append(IndentUtils.newline("-------------------"));
        report.append(IndentUtils.newline(String.format("Daily Action Total (%s)", DateUtils.toString(new Date()))));
        report.append(IndentUtils.newline(String.format("Running User: %s", this.runningUserName)));
        report.append(IndentUtils.newline("-------------------"));

        for (final DailyActionTotal dailyActionTotal : dailyActionTotals) {
            report.append(IndentUtils.newline(String.format("%s: %s",
                    this.getTaskName(dailyActionTotal.getTaskTypeCode()), dailyActionTotal.getTotal())));
        }

        report.setLength(report.length() - IndentUtils.newline().length());

        return report.toString();
    }

    private String getTaskName(final int taskTypeCode) {
        return TaskNameMapper.from(taskTypeCode).scan().get(0).getName();
    }
}
