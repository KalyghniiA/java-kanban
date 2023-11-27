package com.yandex.kanban.util;

import java.util.regex.Pattern;

public class DateRegEx {
    public static Pattern DATE_TIME_REGEX = Pattern.compile("^([0-1]\\d|2[0-3]):[0-5]\\d ([0-2]\\d|3[0,1])\\.(0[1-9]|1[0-2])\\.20\\d\\d$");
    public static Pattern TIME_HOURS_AND_MINUTES_REGEX = Pattern.compile("^\\d*:\\d\\d$");
    public static Pattern TIME_MINUTES_REGEX = Pattern.compile("^[0-5]?\\d$");//не знаю как лучше, ограничить минуты только 2 символами и заставлять передавать продолжительность в минутах, если меньше часа и часы минуты, если больше часа или дать возможность передавать минуты в любом количестве
}
