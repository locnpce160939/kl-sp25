package com.ftcs.common.service;

import com.ftcs.common.exception.InternalServerException;
import lombok.AllArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@AllArgsConstructor
public class SendMailService {
    private final JavaMailSender javaMailSender;

    public void sendOtp(String email, String body, Integer randomNumber) {
        String[] parts = Integer.toString(randomNumber).split("(?<=.)");
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy", Locale.ENGLISH);
        String formattedDate = currentDate.format(formatter);
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject(body);
            String htmlContent = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\" />\n" +
                    "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                    "    <title>MUO - Technology, Simplified</title>\n" +
                    "</head>\n" +
                    "\n" +
                    "<body style=\"font-family: Arial; margin: 0\">\n" +
                    "    <table style=\"\n" +
                    "        background-color: #f3f3f5;\n" +
                    "        padding: 16px 12px;\n" +
                    "        min-height: 100vh;\n" +
                    "        width: 80%;\n" +
                    "        margin: 0 auto;\n" +
                    "      \">\n" +
                    "        <tbody>\n" +
                    "            <tr>\n" +
                    "                <td style=\"vertical-align: top\">\n" +
                    "                    <table border=\"0\" width=\"600\" cellpadding=\"0\" cellspacing=\"0\" align=\"center\" style=\"\n" +
                    "                width: 600px !important;\n" +
                    "                min-width: 600px !important;\n" +
                    "                max-width: 600px !important;\n" +
                    "                margin: auto;\n" +
                    "                border-spacing: 0;\n" +
                    "                border-collapse: collapse;\n" +
                    "                background: white;\n" +
                    "                border-radius: 0px 0px 10px 10px;\n" +
                    "                padding-left: 30px;\n" +
                    "                padding-right: 30px;\n" +
                    "                padding-top: 30px;\n" +
                    "                padding-bottom: 30px;\n" +
                    "                display: block;\n" +
                    "              \">\n" +
                    "                        <tbody>\n" +
                    "                            <tr>\n" +
                    "                                <td style=\"\n" +
                    "                      text-align: center;\n" +
                    "                      vertical-align: top;\n" +
                    "                      font-size: 0;\n" +
                    "                      border-collapse: collapse;\n" +
                    "                    \">\n" +
                    "                                    <table border=\"0\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#F8F8F8\"\n" +
                    "                                        style=\"border-spacing: 0; border-collapse: collapse\">\n" +
                    "                                        <tbody>\n" +
                    "                                            <tr style=\"background-size: cover\">\n" +
                    "                                                <td style=\"\n" +
                    "                              width: 60%;\n" +
                    "                              text-align: left;\n" +
                    "                              border-collapse: collapse;\n" +
                    "                              background: #fff;\n" +
                    "                              border-radius: 10px 10px 0px 0px;\n" +
                    "                              color: white;\n" +
                    "                              height: 50px;\n" +
                    "                            \">\n" +
                    "                                                    <img src=\"https://img.upanh.tv/2024/04/02/istockphoto-1371844292-170667a.jpg\"\n" +
                    "                                                        width=\"120px\" class=\"CToWUd\" />\n" +
                    "                                                </td>\n" +
                    "                                                <td style=\"\n" +
                    "                              width: 40%;\n" +
                    "                              text-align: right;\n" +
                    "                              border-collapse: collapse;\n" +
                    "                              background: #fff;\n" +
                    "                              border-radius: 10px 10px 0px 0px;\n" +
                    "                              color: white;\n" +
                    "                              height: 50px;\n" +
                    "                            \">\n" +
                    "                                                    <div style=\"color: #828282; font-size: 14px\">\n" +
                    "                                                        " + formattedDate + "\n" +
                    "                                                    </div>\n" +
                    "                                                </td>\n" +
                    "                                            </tr>\n" +
                    "                                        </tbody>\n" +
                    "                                    </table>\n" +
                    "                                </td>\n" +
                    "                            </tr>\n" +
                    "\n" +
                    "                            <tr>\n" +
                    "                                <td style=\"\n" +
                    "                      vertical-align: top;\n" +
                    "                      font-size: 0;\n" +
                    "                      border-collapse: collapse;\n" +
                    "                    \">\n" +
                    "                                    <table border=\"0\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#F8F8F8\"\n" +
                    "                                        style=\"border-spacing: 0; border-collapse: collapse\">\n" +
                    "                                        <tbody>\n" +
                    "                                            <tr>\n" +
                    "                                                <td style=\"\n" +
                    "                              padding-top: 30px;\n" +
                    "                              padding-bottom: 5px;\n" +
                    "                              background-color: white;\n" +
                    "                            \">\n" +
                    "                                                    <span style=\"font-size: 20px; color: #363636\">Hi " +
                    "                                                </td>\n" +
                    "                                            </tr>\n" +
                    "                                            <tr>\n" +
                    "                                                <td style=\"\n" +
                    "                              padding-top: 5px;\n" +
                    "                              padding-bottom: 9px;\n" +
                    "                              background-color: white;\n" +
                    "                            \">\n" +
                    "                                                    <span style=\"\n" +
                    "                                font-size: 24px;\n" +
                    "                                color: #363636;\n" +
                    "                                font-weight: bold;\n" +
                    "                              \">Thank you for visiting our site.</span>\n" +
                    "                                                </td>\n" +
                    "                                            </tr>\n" +
                    "\n" +
                    "                                            <tr>\n" +
                    "                                                <td style=\"\n" +
                    "                              padding: 10px 0px;\n" +
                    "                              background-color: white;\n" +
                    "                              border-collapse: collapse;\n" +
                    "                            \">\n" +
                    "                                                    <div style=\"\n" +
                    "                                font-size: 18px;\n" +
                    "                                color: #828282;\n" +
                    "                                font-weight: normal;\n" +
                    "                              \">\n" +
                    "                                                        We hope you learnt something new today.\n" +
                    "                                                    </div>\n" +
                    "                                                </td>\n" +
                    "                                            </tr>\n" +
                    "\n" +
                    "                                            <tr style=\"background-color: #ffd4e3\">\n" +
                    "                                                <td style=\"\n" +
                    "                              padding: 16px;\n" +
                    "                              border-collapse: collapse;\n" +
                    "                              border-radius: 8px;\n" +
                    "                            \">\n" +
                    "                                                    <div style=\"\n" +
                    "                                font-size: 22px;\n" +
                    "                                color: #363636;\n" +
                    "                                font-weight: bold;\n" +
                    "                              \">\n" +
                    "                                                        Your verification code is!\n" +
                    "                                                    </div>\n" +
                    "                                                    <div style=\"\n" +
                    "                                font-size: 18px;\n" +
                    "                                margin-top: 8px;\n" +
                    "                                color: #444;\n" +
                    "                                margin-bottom: 20px;\n" +
                    "                              \">\n" +
                    "                                                        Use this code to identify.\n" +
                    "                                                    </div>\n" +
                    "\n" +
                    "                                                    <div style=\"width: 100%; display: flex\">\n" +
                    "                                                        <a style=\"\n" +
                    "                                  text-decoration: none;\n" +
                    "                                  width: 9%;\n" +
                    "                                  color: #1c1c1c;\n" +
                    "                                \" target=\"_blank\">\n" +
                    "                                                            <span style=\"\n" +
                    "                                    width: 70%;\n" +
                    "                                    padding-top: 20%;\n" +
                    "                                    padding-bottom: 20%;\n" +
                    "                                    padding-left: 5%;\n" +
                    "                                    padding-right: 5%;\n" +
                    "                                    border-radius: 20%;\n" +
                    "                                    border: 1px solid #f4a1bd;\n" +
                    "                                    display: block;\n" +
                    "                                    margin-bottom: 5px;\n" +
                    "                                    font-size: 20px;\n" +
                    "                                    font-weight: bold;\n" +
                    "                                    text-align: center;\n" +
                    "\n" +
                    "                                    background: #ffffff;\n" +
                    "                                  \">" + parts[0] + "</span>\n" +
                    "                                                        </a>\n" +
                    "                                                        <a style=\"\n" +
                    "                                  text-decoration: none;\n" +
                    "                                  width: 9%;\n" +
                    "                                  color: #1c1c1c;\n" +
                    "                                \" target=\"_blank\">\n" +
                    "                                                            <span style=\"\n" +
                    "                                    width: 70%;\n" +
                    "                                    padding-top: 20%;\n" +
                    "                                    padding-bottom: 20%;\n" +
                    "                                    padding-left: 5%;\n" +
                    "                                    padding-right: 5%;\n" +
                    "                                    border-radius: 20%;\n" +
                    "                                    border: 1px solid #f4a1bd;\n" +
                    "                                    display: block;\n" +
                    "                                    margin-bottom: 5px;\n" +
                    "                                    font-size: 20px;\n" +
                    "                                    font-weight: bold;\n" +
                    "                                    text-align: center;\n" +
                    "\n" +
                    "                                    background: #ffffff;\n" +
                    "                                  \">" + parts[1] + "</span>\n" +
                    "                                                        </a>\n" +
                    "                                                        <a style=\"\n" +
                    "                                  text-decoration: none;\n" +
                    "                                  width: 9%;\n" +
                    "                                  color: #1c1c1c;\n" +
                    "                                \" target=\"_blank\">\n" +
                    "                                                            <span style=\"\n" +
                    "                                    width: 70%;\n" +
                    "                                    padding-top: 20%;\n" +
                    "                                    padding-bottom: 20%;\n" +
                    "                                    padding-left: 5%;\n" +
                    "                                    padding-right: 5%;\n" +
                    "                                    border-radius: 20%;\n" +
                    "                                    border: 1px solid #f4a1bd;\n" +
                    "                                    display: block;\n" +
                    "                                    margin-bottom: 5px;\n" +
                    "                                    font-size: 20px;\n" +
                    "                                    font-weight: bold;\n" +
                    "                                    text-align: center;\n" +
                    "\n" +
                    "                                    background: #ffffff;\n" +
                    "                                  \">" + parts[2] + "</span>\n" +
                    "                                                        </a>\n" +
                    "                                                        <a style=\"\n" +
                    "                                  text-decoration: none;\n" +
                    "                                  width: 9%;\n" +
                    "                                  color: #1c1c1c;\n" +
                    "                                \" target=\"_blank\">\n" +
                    "                                                            <span style=\"\n" +
                    "                                    width: 70%;\n" +
                    "                                    padding-top: 20%;\n" +
                    "                                    padding-bottom: 20%;\n" +
                    "                                    padding-left: 5%;\n" +
                    "                                    padding-right: 5%;\n" +
                    "                                    border-radius: 20%;\n" +
                    "                                    border: 1px solid #f4a1bd;\n" +
                    "                                    display: block;\n" +
                    "                                    margin-bottom: 5px;\n" +
                    "                                    font-size: 20px;\n" +
                    "                                    font-weight: bold;\n" +
                    "                                    text-align: center;\n" +
                    "\n" +
                    "                                    background: #ffffff;\n" +
                    "                                  \">" + parts[3] + "</span>\n" +
                    "                                                        </a>\n" +
                    "                                                        <a style=\"\n" +
                    "                                  text-decoration: none;\n" +
                    "                                  width: 9%;\n" +
                    "                                  color: #1c1c1c;\n" +
                    "                                \" target=\"_blank\">\n" +
                    "                                                            <span style=\"\n" +
                    "                                    width: 70%;\n" +
                    "                                    padding-top: 20%;\n" +
                    "                                    padding-bottom: 20%;\n" +
                    "                                    padding-left: 5%;\n" +
                    "                                    padding-right: 5%;\n" +
                    "                                    border-radius: 20%;\n" +
                    "                                    border: 1px solid #f4a1bd;\n" +
                    "                                    display: block;\n" +
                    "                                    margin-bottom: 5px;\n" +
                    "                                    font-size: 20px;\n" +
                    "                                    font-weight: bold;\n" +
                    "                                    text-align: center;\n" +
                    "\n" +
                    "                                    background: #ffffff;\n" +
                    "                                  \">" + parts[4] + "</span>\n" +
                    "                                                        </a>\n" +
                    "                                                        <a style=\"\n" +
                    "                                  text-decoration: none;\n" +
                    "                                  width: 9%;\n" +
                    "                                  color: #1c1c1c;\n" +
                    "                                \" target=\"_blank\">\n" +
                    "                                                            <span style=\"\n" +
                    "                                    width: 70%;\n" +
                    "                                    padding-top: 20%;\n" +
                    "                                    padding-bottom: 20%;\n" +
                    "                                    padding-left: 5%;\n" +
                    "                                    padding-right: 5%;\n" +
                    "                                    border-radius: 20%;\n" +
                    "                                    border: 1px solid #f4a1bd;\n" +
                    "                                    display: block;\n" +
                    "                                    margin-bottom: 5px;\n" +
                    "                                    font-size: 20px;\n" +
                    "                                    font-weight: bold;\n" +
                    "                                    text-align: center;\n" +
                    "\n" +
                    "                                    background: #ffffff;\n" +
                    "                                  \">" + parts[5] + "</span>\n" +
                    "                                                        </a>\n" +
                    "                                                    </div>\n" +
                    "                                                </td>\n" +
                    "                                            </tr>\n" +
                    "                                            <tr>\n" +
                    "                                                <td style=\"background: #ffffff; height: 20px\"></td>\n" +
                    "                                            </tr>\n" +
                    "\n" +
                    "\n" +
                    "                                        </tbody>\n" +
                    "                                    </table>\n" +
                    "                                </td>\n" +
                    "                            </tr>\n" +
                    "\n" +
                    "\n" +
                    "                            <tr>\n" +
                    "                                <td>\n" +
                    "                                    <h1 style=\"text-align: left\">About Nursing home\n" +
                    "                                    </h1>\n" +
                    "                                    <p style=\"line-height: 1.4; letter-spacing: 0.5px\">\n" +
                    "                                        Welcome to SWP391 Project!\n" +
                    "                                        <br>\n" +
                    "                                        We are proud to be a dedicated senior care facility\n" +
                    "                                        committed to providing a safe, warm and supportive living environment for the\n" +
                    "                                        senior community.\n" +
                    "                                    </p>\n" +
                    "                                </td>\n" +
                    "                            </tr>\n" +
                    "                            <tr>\n" +
                    "                                <td><br /></td>\n" +
                    "                            </tr>\n" +
                    "                            <tr>\n" +
                    "                                <td style=\"\n" +
                    "                      background-color: #e23744;\n" +
                    "                      padding: 16px 0px;\n" +
                    "                      border-radius: 8px;\n" +
                    "                    \">\n" +
                    "                                    <h2 style=\"\n" +
                    "                        font-size: 35px;\n" +
                    "                        color: #ffffff;\n" +
                    "                        margin: 0;\n" +
                    "                        text-align: center;\n" +
                    "                      \">\n" +
                    "                                        Thanks You\n" +
                    "                                    </h2>\n" +
                    "                                </td>\n" +
                    "                            </tr>\n" +
                    "                            <tr>\n" +
                    "                                <td style=\"text-align: center\">\n" +
                    "                                    <div style=\"\n" +
                    "                        width: 100%;\n" +
                    "                        margin-top: 30px;\n" +
                    "                        display: inline-block;\n" +
                    "                        border-top: 1px solid #e8e8e8;\n" +
                    "                      \"></div>\n" +
                    "                                </td>\n" +
                    "                            </tr>\n" +
                    "                            <tr></tr>\n" +
                    "\n" +
                    "                            <tr>\n" +
                    "                                <td>\n" +
                    "                                    <p style=\"\n" +
                    "                        line-height: 1.4;\n" +
                    "                        letter-spacing: 0.5px;\n" +
                    "                        text-align: center;\n" +
                    "                        color: #444;\n" +
                    "                        margin-bottom: 8px;\n" +
                    "                      \">\n" +
                    "                                        Copyright © 2024\n" +
                    "                                        <a href=\"SWP391/\"\n" +
                    "                                            style=\"text-decoration: none; color: #444\">SWP391</a>\n" +
                    "                                    </p>\n" +
                    "                                </td>\n" +
                    "                            </tr>\n" +
                    "                        </tbody>\n" +
                    "                    </table>\n" +
                    "                </td>\n" +
                    "            </tr>\n" +
                    "        </tbody>\n" +
                    "    </table>\n" +
                    "</body>\n" +
                    "\n" +
                    "</html>";
            helper.setText(htmlContent, true);
            javaMailSender.send(message);
        } catch (MessagingException | MailException e) {
            throw new InternalServerException("Error");
        }
    }
}
