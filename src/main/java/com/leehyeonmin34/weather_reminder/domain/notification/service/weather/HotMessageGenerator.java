package com.leehyeonmin34.weather_reminder.domain.notification.service.weather;

import com.leehyeonmin34.weather_reminder.domain.notification.model.NotiContentType;
import com.leehyeonmin34.weather_reminder.domain.user.domain.User;
import com.leehyeonmin34.weather_reminder.domain.weather_info.domain.WeatherInfo;
import com.leehyeonmin34.weather_reminder.domain.weather_info.model.WeatherInfoList;
import com.leehyeonmin34.weather_reminder.domain.weather_info.service.WeatherTempConverter;
import com.leehyeonmin34.weather_reminder.global.common.service.TimeStringifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class HotMessageGenerator implements WeatherMessageGenerator{


    public static NotiContentType notiContentType = NotiContentType.HOT;

    @Override
    public NotiContentType getNotiContentType(){ return notiContentType; }

    @Override
    public String generate(final User user, final WeatherInfoList weatherInfoList) {

        // 알림이 꺼져있다면 빈 문자열 반환
        if(!user.getHotNotiSetting().isOn())
            return "";

        // 조건을 만족하는 시간대 구하기
        final Byte condition = user.getHotNotiSetting().getConditionCelcius();
        final List<WeatherInfo> satisfying = weatherInfoList.getTempWeatherInfoList().stream()
                .filter(item -> item.getValue() > condition ).collect(Collectors.toList());

        // 조건을 만족하는 시간대가 없다면 빈 문자열 반환
        if(satisfying.size() == 0)
            return "";

        // 조건을 만족하는 시간을 추출
        // ex) 오전 9시, 오후 4시, 5시
        final List<LocalDateTime> forcastTimeList = satisfying.stream().map(WeatherInfo::getFcstTime).collect(Collectors.toList());
        final String satisfyingCondigionString = TimeStringifier.stringifyByNoon(forcastTimeList);

        // 알림 메시지 생성하기
        return "🥵 오늘 " + satisfyingCondigionString + "의 기온이 " + WeatherTempConverter.stringify(condition) + "보다 높아요.\n" +
                "\n" +
                "옷을 시원하게 입고 썬크림, 손풍기 등에 신경써요 !";
    }
}
