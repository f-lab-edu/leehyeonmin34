package com.leehyeonmin34.weather_reminder.domain.notification.service.weather;

import com.leehyeonmin34.weather_reminder.domain.notification.model.NotiContentType;
import com.leehyeonmin34.weather_reminder.domain.user.domain.User;
import com.leehyeonmin34.weather_reminder.domain.weather_info.domain.WeatherInfo;
import com.leehyeonmin34.weather_reminder.domain.weather_info.service.WeatherApiTimeConverter;
import com.leehyeonmin34.weather_reminder.domain.weather_info.model.WeatherInfoList;
import com.leehyeonmin34.weather_reminder.domain.weather_info.service.WeatherTempConverter;
import com.leehyeonmin34.weather_reminder.global.common.service.TimeStringifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ColdMessageGenerator implements WeatherMessageGenerator{

    public static final NotiContentType notiContentType = NotiContentType.COLD;

    @Override
    public NotiContentType getNotiContentType(){ return notiContentType; }

    @Override
    public String generate(final User user, final WeatherInfoList weatherInfoList) {

        // 알림이 꺼져있다면 빈 문자열 반환
        if(!user.getColdNotiSetting().isOn())
            return "";

        // 조건을 만족하는 시간대 구하기
        final Byte condition = user.getColdNotiSetting().getConditionCelcius();
        final List<WeatherInfo> satisfying = weatherInfoList.getTempWeatherInfoList().stream()
                .filter(item -> item.getValue() < condition ).collect(Collectors.toList());

        // 조건을 만족하는 시간대가 없다면 빈 문자열 반환
        if(satisfying.size() == 0)
            return "";

        // 조건을 만족하는 시간을 추출
        // ex) 오전 9시, 오후 4시, 5시
        final List<LocalDateTime> forcastTimeList = satisfying.stream().map(WeatherInfo::getFcstTime).collect(Collectors.toList());
        final String satisfyingCondigionString = TimeStringifier.stringifyByNoon(forcastTimeList);

        // 알림 메시지 생성하기
        return "🥶 오늘 " + satisfyingCondigionString + "의 기온이 " + WeatherTempConverter.stringify(condition) + "보다 낮아요. 옷을 특히 따뜻하게 입고 손난로를 챙겨가세요!\n" +
                "\n" +
                "두꺼운 옷 한 벌보다 얇은 옷을 여러 겹 입는 게 도움되고, 장갑이나 목도리 등도 도움이 될 수 있어요";
    }

}
