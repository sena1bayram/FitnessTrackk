package com.example.fitnesstrack.utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {
    private static final String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                    + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                    + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                    + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    private static final String PASSWORD_PATTERN = "^(?=.*?\\p{Lu})(?=.*?\\p{Ll})(?=.*?\\d)" +
            "(?=.*?[`~!@#$%^&*()\\-_=+\\\\|\\[{\\]};:'\",<.>/?]).*$";

    public static boolean validateFields(String name){

        if (TextUtils.isEmpty(name)) {

            return false;

        } else {

            return true;
        }
    }

    public static boolean validateEmail(String mail) {

        if (TextUtils.isEmpty(mail) || !Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {

            return false;

        } else {

            return  true;
        }
    }
    public  static boolean strongValidatePassword(String password){
        boolean inputMatches = true;

        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        System.out.println("state: "+ matcher.matches());
        if (!matcher.matches())
            inputMatches = false;

        return inputMatches;
    }
    public static boolean validateIpAdress(String IpAdress){
        Pattern pattern= Pattern.compile(IPADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(IpAdress);
        return matcher.matches();
    }

    public static boolean validateBirthDay(String birth_day){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateFormat.setLenient(false);
        try {
            // parse the date string and check if it is a valid date
            Date date = dateFormat.parse(birth_day.trim());
            // check if the parsed date is equal to the original date string
            return birth_day.equals(dateFormat.format(date));
        } catch (ParseException e) {
            return false;
        }
    }
    public static boolean validatePhoneNumber(String phoneNumber) {
        // Boşlukları kaldırma
        String trimmedNumber = phoneNumber.replaceAll("\\s+", "");

        // Sadece rakamları sayma
        int digitCount = 0;
        for (int i = 0; i < trimmedNumber.length(); i++) {
            if (Character.isDigit(trimmedNumber.charAt(i))) {
                digitCount++;
            }
        }

        // Rakam sayısı kontrolü
        if (digitCount == 10) {
            return true; // Geçerli
        } else {
            return false; // Geçersiz
        }
    }
    public static String formatStringToDate(String dateTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'XXX yyyy", Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        inputFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            Date date = inputFormat.parse(dateTime);

            // Bugünün tarihini al
            Date today = new Date();

            // Yalnızca tarih bilgisini içeren formatlama
            SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

            // Eğer mesaj bugünden eski ise tarihi bas, aksi halde boş bir tarih bas
            if (date.before(today)) {
                return dateOnlyFormat.format(date);
            } else {
                return "";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
