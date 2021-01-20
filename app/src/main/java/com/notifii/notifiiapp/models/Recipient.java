/////////////////////////////////////////////////////////////////
// Recipient.java
//
// Created by Annapoorna
// Notifii Project
//
//Copyright (c) 2016 Notifii, LLC. All rights reserved
/////////////////////////////////////////////////////////////////
package com.notifii.notifiiapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * This is the model class for Recipient Details.
 */
public class Recipient implements Parcelable {

    private String recipientId;
    private String firstName;
    private String lastName;
    private String fullname;
    private String address1;
    private String recipientTitle="";
    private String email;
    private String cellphone;
    private String wireless_carrier;
    private String pe_alert;
    private String vacation_message;
    private String pt_alert;
    private String recipient_status_value;
    private String recipient_type_value;
    private String special_track_instructions_flag;
    private String special_track_instructions;
    private String vacation_status;
    private String vacation_start_date;
    private String vacation_end_date;
    private String pe_alert_optOut;
    private String stop_track_nonmarketing_email;
    private String stop_connect_nonmarketing_email;
    private String stop_connect_marketing_email;
    private String stop_track_nonmarketing_text;
    private String stop_connect_nonmarketing_text;
    private String stop_connect_marketing_text;
    private String stop_track_nonmarketing_push;
    private String stop_connect_nonmarketing_push;
    private String move_in_date;
    private String move_out_date;
    private String pt_alert_optOut;
    private String send_track_nonmarketing_email;
    private String send_connect_nonmarketing_email;
    private String send_connect_marketing_email;
    private String send_track_nonmarketing_text;
    private String send_connect_nonmarketing_text;
    private String send_connect_marketing_text;
    private String send_track_nonmarketing_push;
    private String send_connect_nonmarketing_push;
    private String send_connect_marketing_push;
    private String lease_start_date;
    private String lease_end_date;
    private String birth_date;
    private String send_pkg_login_notification;
    private String send_pkg_logout_notification;
    private String stop_connect_marketing_push;
    private String email_bounced = "";
    private String email_bounce_alert = "";
    private String email_bounce_reason = "";
    private String phone_bounced = "";
    private String phone_bounce_alert = "";
    private String phone_bounce_reason = "";
    private String phone_type_error = "";
    private String phone_type_reason = "";

    public String getRecipientTitle() {
        return recipientTitle;
    }

    public void setRecipientTitle(String recipientTitle) {
        this.recipientTitle = recipientTitle;
    }

    public String getFullName() {
        return fullname;
    }

    public void setFullName(String fullname) {
        this.fullname = fullname;
    }

    public String getVacation_message() {
        return vacation_message;
    }

    public void setVacation_message(String vacation_message) {
        this.vacation_message = vacation_message;
    }

    public String getSpecial_track_instructions_flag() {
        return special_track_instructions_flag;
    }

    public void setSpecial_track_instructions_flag(String special_track_instructions_flag) {
        this.special_track_instructions_flag = special_track_instructions_flag;
    }

    public String getSpecial_track_instructions() {
        return special_track_instructions;
    }

    public void setSpecial_track_instructions(String special_track_instructions) {
        this.special_track_instructions = special_track_instructions;
    }

    public String getVacation_status() {
        return vacation_status;
    }

    public void setVacation_status(String vacation_status) {
        this.vacation_status = vacation_status;
    }

    public String getVacation_start_date() {
        return vacation_start_date;
    }

    public void setVacation_start_date(String vacation_start_date) {
        this.vacation_start_date = vacation_start_date;
    }

    public String getVacation_end_date() {
        return vacation_end_date;
    }

    public void setVacation_end_date(String vacation_end_date) {
        this.vacation_end_date = vacation_end_date;
    }


    public String getSend_track_nonmarketing_email() {
        return send_track_nonmarketing_email;
    }

    public void setSend_track_nonmarketing_email(String send_track_nonmarketing_email) {
        this.send_track_nonmarketing_email = send_track_nonmarketing_email;
    }

    public String getSend_connect_marketing_email() {
        return send_connect_marketing_email;
    }

    public void setSend_connect_marketing_email(String send_connect_marketing_email) {
        this.send_connect_marketing_email = send_connect_marketing_email;
    }

    public String getSend_connect_nonmarketing_email() {
        return send_connect_nonmarketing_email;
    }

    public void setSend_connect_nonmarketing_email(String send_connect_nonmarketing_email) {
        this.send_connect_nonmarketing_email = send_connect_nonmarketing_email;
    }

    public String getSend_track_nonmarketing_text() {
        return send_track_nonmarketing_text;
    }

    public void setSend_track_nonmarketing_text(String send_track_nonmarketing_text) {
        this.send_track_nonmarketing_text = send_track_nonmarketing_text;
    }

    public String getSend_connect_marketing_text() {
        return send_connect_marketing_text;
    }

    public void setSend_connect_marketing_text(String send_connect_marketing_text) {
        this.send_connect_marketing_text = send_connect_marketing_text;
    }

    public String getSend_connect_nonmarketing_text() {
        return send_connect_nonmarketing_text;
    }

    public void setSend_connect_nonmarketing_text(String send_connect_nonmarketing_text) {
        this.send_connect_nonmarketing_text = send_connect_nonmarketing_text;
    }

    public String getSend_track_nonmarketing_push() {
        return send_track_nonmarketing_push;
    }

    public void setSend_track_nonmarketing_push(String send_track_nonmarketing_push) {
        this.send_track_nonmarketing_push = send_track_nonmarketing_push;
    }

    public String getSend_connect_nonmarketing_push() {
        return send_connect_nonmarketing_push;
    }

    public void setSend_connect_nonmarketing_push(String send_connect_nonmarketing_push) {
        this.send_connect_nonmarketing_push = send_connect_nonmarketing_push;
    }

    public String getSend_connect_marketing_push() {
        return send_connect_marketing_push;
    }

    public void setSend_connect_marketing_push(String send_connect_marketing_push) {
        this.send_connect_marketing_push = send_connect_marketing_push;
    }


    public String getStop_track_nonmarketing_email() {
        return stop_track_nonmarketing_email;
    }

    public void setStop_track_nonmarketing_email(String stop_track_nonmarketing_email) {
        this.stop_track_nonmarketing_email = stop_track_nonmarketing_email;
    }

    public String getStop_connect_nonmarketing_email() {
        return stop_connect_nonmarketing_email;
    }

    public void setStop_connect_nonmarketing_email(String stop_connect_nonmarketing_email) {
        this.stop_connect_nonmarketing_email = stop_connect_nonmarketing_email;
    }

    public String getStop_connect_marketing_email() {
        return stop_connect_marketing_email;
    }

    public void setStop_connect_marketing_email(String stop_connect_marketing_email) {
        this.stop_connect_marketing_email = stop_connect_marketing_email;
    }

    public String getStop_track_nonmarketing_text() {
        return stop_track_nonmarketing_text;
    }

    public void setStop_track_nonmarketing_text(String stop_track_nonmarketing_text) {
        this.stop_track_nonmarketing_text = stop_track_nonmarketing_text;
    }

    public String getStop_connect_nonmarketing_text() {
        return stop_connect_nonmarketing_text;
    }

    public void setStop_connect_nonmarketing_text(String stop_connect_nonmarketing_text) {
        this.stop_connect_nonmarketing_text = stop_connect_nonmarketing_text;
    }

    public String getStop_connect_marketing_text() {
        return stop_connect_marketing_text;
    }

    public void setStop_connect_marketing_text(String stop_connect_marketing_text) {
        this.stop_connect_marketing_text = stop_connect_marketing_text;
    }

    public String getStop_track_nonmarketing_push() {
        return stop_track_nonmarketing_push;
    }

    public void setStop_track_nonmarketing_push(String stop_track_nonmarketing_push) {
        this.stop_track_nonmarketing_push = stop_track_nonmarketing_push;
    }

    public String getStop_connect_nonmarketing_push() {
        return stop_connect_nonmarketing_push;
    }

    public void setStop_connect_nonmarketing_push(String stop_connect_nonmarketing_push) {
        this.stop_connect_nonmarketing_push = stop_connect_nonmarketing_push;
    }

    public String getStop_connect_marketing_push() {
        return stop_connect_marketing_push;
    }

    public void setStop_connect_marketing_push(String stop_connect_marketing_push) {
        this.stop_connect_marketing_push = stop_connect_marketing_push;
    }


    public String getMove_in_date() {
        return move_in_date;
    }

    public void setMove_in_date(String move_in_date) {
        this.move_in_date = move_in_date;
    }

    public String getMove_out_date() {
        return move_out_date;
    }

    public void setMove_out_date(String move_out_date) {
        this.move_out_date = move_out_date;
    }

    public String getLease_start_date() {
        return lease_start_date;
    }

    public void setLease_start_date(String lease_start_date) {
        this.lease_start_date = lease_start_date;
    }

    public String getLease_end_date() {
        return lease_end_date;
    }

    public void setLease_end_date(String lease_end_date) {
        this.lease_end_date = lease_end_date;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public String getSend_pkg_login_notification() {
        return send_pkg_login_notification;
    }

    public void setSend_pkg_login_notification(String send_pkg_login_notification) {
        this.send_pkg_login_notification = send_pkg_login_notification;
    }

    public String getSend_pkg_logout_notification() {
        return send_pkg_logout_notification;
    }

    public void setSend_pkg_logout_notification(String send_pkg_logout_notification) {
        this.send_pkg_logout_notification = send_pkg_logout_notification;
    }


    public String getEmail_bounced() {
        return email_bounced;
    }

    public void setEmail_bounced(String email_bounced) {
        this.email_bounced = email_bounced;
    }

    public String getEmail_bounce_alert() {
        return email_bounce_alert;
    }

    public void setEmail_bounce_alert(String email_bounce_alert) {
        this.email_bounce_alert = email_bounce_alert;
    }

    public String getEmail_bounce_reason() {
        return email_bounce_reason;
    }

    public void setEmail_bounce_reason(String email_bounce_reason) {
        this.email_bounce_reason = email_bounce_reason;
    }

    public String getPhone_bounced() {
        return phone_bounced;
    }

    public void setPhone_bounced(String phone_bounced) {
        this.phone_bounced = phone_bounced;
    }

    public String getPhone_bounce_alert() {
        return phone_bounce_alert;
    }

    public void setPhone_bounce_alert(String phone_bounce_alert) {
        this.phone_bounce_alert = phone_bounce_alert;
    }

    public String getPhone_bounce_reason() {
        return phone_bounce_reason;
    }

    public void setPhone_bounce_reason(String phone_bounce_reason) {
        this.phone_bounce_reason = phone_bounce_reason;
    }

    public String getPhone_type_error() {
        return phone_type_error;
    }

    public void setPhone_type_error(String phone_type_error) {
        this.phone_type_error = phone_type_error;
    }

    public String getPhone_type_reason() {
        return phone_type_reason;
    }

    public void setPhone_type_reason(String phone_type_reason) {
        this.phone_type_reason = phone_type_reason;
    }

    public String getPe_alert_optOut() {
        return pe_alert_optOut;
    }

    public void setPe_alert_optOut(String pe_alert_optOut) {
        this.pe_alert_optOut = pe_alert_optOut;
    }

    public String getPt_alert_optOut() {
        return pt_alert_optOut;
    }

    public void setPt_alert_optOut(String pt_alert_optOut) {
        this.pt_alert_optOut = pt_alert_optOut;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getWireless_carrier() {
        return wireless_carrier;
    }

    public void setWireless_carrier(String wireless_carrier) {
        this.wireless_carrier = wireless_carrier;
    }

    public String getPe_alert() {
        return pe_alert;
    }

    public void setPe_alert(String pe_alert) {
        this.pe_alert = pe_alert;
    }

    public String getPt_alert() {
        return pt_alert;
    }

    public void setPt_alert(String pt_alert) {
        this.pt_alert = pt_alert;
    }

    public String getRecipient_status_value() {
        return recipient_status_value;
    }

    public void setRecipient_status_value(String recipient_status_value) {
        this.recipient_status_value = recipient_status_value;
    }

    public String getRecipient_type_value() {
        return recipient_type_value;
    }

    public void setRecipient_type_value(String recipient_type_value) {
        this.recipient_type_value = recipient_type_value;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    @Override
    public String toString() {
        return getAddress1() + " - " + getFirstName();
    }

    // This Method deals with the Response parsing of RecipientList Service.
    public static ArrayList<Recipient> getRecipientsList(JSONArray jsonArray) {
        ArrayList<Recipient> list = new ArrayList<>();
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0, len = jsonArray.length(); i < len; i++) {
                    JSONObject recipientJsonObj = jsonArray.getJSONObject(i);
                    Recipient recipient = new Recipient();
                    recipient.setRecipientId(recipientJsonObj.optString("recipient_id"));
                    recipient.setFirstName(recipientJsonObj.optString("first_name"));
                    recipient.setFullName(recipientJsonObj.optString("full_name"));
                    recipient.setLastName(recipientJsonObj.optString("last_name"));
                    recipient.setAddress1(recipientJsonObj.optString("address1"));
                    recipient.setPt_alert(recipientJsonObj.optString("pt_alert"));
                    recipient.setPe_alert(recipientJsonObj.optString("pe_alert"));
                    recipient.setPe_alert_optOut(recipientJsonObj.optString("pe_alert_override_optout"));
                    recipient.setPt_alert_optOut(recipientJsonObj.optString("pt_alert_override_optout"));
                    recipient.setEmail(recipientJsonObj.optString("email"));
                    recipient.setCellphone(recipientJsonObj.optString("cellphone"));
                    recipient.setRecipient_status_value(recipientJsonObj.optString("recipient_status"));
                    list.add(recipient);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return list;
        }
    }


    public static Recipient getRecipient(JSONObject jsonObject) {
        Recipient mRecipient = new Recipient();

        try {
            if (jsonObject != null) {
                mRecipient.setFirstName(jsonObject.optString("first_name"));
                mRecipient.setLastName(jsonObject.optString("last_name"));
                mRecipient.setFullName(jsonObject.optString("full_name"));
                // mRecipient.setRecipientId(mRecipientId);
                mRecipient.setEmail(jsonObject.optString("email"));
                mRecipient.setCellphone(jsonObject.optString("cellphone"));
                mRecipient.setAddress1(jsonObject.optString("address1"));
                mRecipient.setPe_alert(jsonObject.optString("pe_alert"));
                mRecipient.setPt_alert(jsonObject.optString("pt_alert"));
                mRecipient.setPe_alert_optOut(jsonObject.optString("pe_alert_override_optout"));
                mRecipient.setPt_alert_optOut(jsonObject.optString("pt_alert_override_optout"));
                mRecipient.setRecipient_status_value(jsonObject.optString("recipient_status"));
                mRecipient.setRecipient_type_value(jsonObject.optString("recipient_type"));
                mRecipient.setWireless_carrier(jsonObject.optString("wireless_carrier"));
                mRecipient.setSend_track_nonmarketing_email(jsonObject.optString("send_track_nonmarketing_email"));
                mRecipient.setSend_track_nonmarketing_text(jsonObject.optString("send_track_nonmarketing_text"));
                mRecipient.setSend_track_nonmarketing_push( jsonObject.optString("send_track_nonmarketing_push"));
                mRecipient.setSend_connect_marketing_email(jsonObject.optString("send_connect_marketing_email"));
                mRecipient.setSend_connect_marketing_text(jsonObject.optString("send_connect_marketing_text"));
                mRecipient.setSend_connect_marketing_push(jsonObject.optString("send_connect_marketing_push"));
                mRecipient.setSend_connect_nonmarketing_email( jsonObject.optString("send_connect_nonmarketing_email"));
                mRecipient.setSend_connect_nonmarketing_text( jsonObject.optString("send_connect_nonmarketing_text"));
                mRecipient.setSend_connect_nonmarketing_push( jsonObject.optString("send_connect_nonmarketing_push"));
                mRecipient.setStop_track_nonmarketing_email(jsonObject.optString("stop_track_nonmarketing_email"));
                mRecipient.setStop_track_nonmarketing_text( jsonObject.optString("stop_track_nonmarketing_text"));
                mRecipient.setStop_track_nonmarketing_push(jsonObject.optString("stop_track_nonmarketing_push"));
                mRecipient.setStop_connect_marketing_email(jsonObject.optString("stop_connect_marketing_email"));
                mRecipient.setStop_connect_marketing_text(jsonObject.optString("stop_connect_marketing_text"));
                mRecipient.setStop_connect_marketing_push(jsonObject.optString("stop_connect_marketing_push"));
                mRecipient.setStop_connect_nonmarketing_email(jsonObject.optString("stop_connect_nonmarketing_email"));
                mRecipient.setStop_connect_nonmarketing_text(jsonObject.optString("stop_connect_nonmarketing_text"));
                mRecipient.setStop_connect_nonmarketing_push(jsonObject.optString("stop_connect_nonmarketing_push"));
                mRecipient.setEmail_bounced(jsonObject.optString("email_bounced"));
                mRecipient.setEmail_bounce_alert(jsonObject.optString("email_bounced_alert"));
                mRecipient.setEmail_bounce_reason(jsonObject.optString("email_bounce_reason"));
                mRecipient.setPhone_bounced(jsonObject.optString("cellphone_bounced"));
                mRecipient.setPhone_bounce_alert(jsonObject.optString("cellphone_bounced_alert"));
                mRecipient.setPhone_bounce_reason( jsonObject.optString("cellphone_bounce_reason"));
                mRecipient.setPhone_type_error(jsonObject.optString("phone_type_error"));
                mRecipient.setPhone_type_reason(jsonObject.optString("phone_type_reason"));
                mRecipient.setSpecial_track_instructions(jsonObject.optString("special_track_instructions"));
                mRecipient.setSpecial_track_instructions_flag(jsonObject.optString("special_track_instructions_flag"));
                mRecipient.setVacation_status( jsonObject.optString("vacation_status"));
                mRecipient.setVacation_start_date(jsonObject.optString("vacation_start_date"));
                mRecipient.setVacation_end_date(jsonObject.optString("vacation_end_date"));
                mRecipient.setMove_in_date(jsonObject.optString("move_in_date"));
                mRecipient.setMove_out_date(jsonObject.optString("move_out_date"));
                mRecipient.setVacation_message(jsonObject.optString("vacation_message"));
                mRecipient.setLease_start_date(jsonObject.optString("lease_start_date"));
                mRecipient.setLease_end_date(jsonObject.optString("lease_end_date"));
                mRecipient.setBirth_date(jsonObject.optString("birth_date"));
                mRecipient.setSend_pkg_login_notification(jsonObject.optString("send_pkg_login_notification"));
                mRecipient.setSend_pkg_logout_notification(jsonObject.optString("send_pkg_logout_notification"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return mRecipient;
        }
    }

    public Recipient() {
    }

    protected Recipient(Parcel in) {
        recipientId = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        address1 = in.readString();
        email = in.readString();
        cellphone = in.readString();
        wireless_carrier = in.readString();
        pe_alert = in.readString();
        pt_alert = in.readString();
        recipient_status_value = in.readString();
        recipient_type_value = in.readString();

        email_bounced = in.readString();
        email_bounce_alert = in.readString();
        email_bounce_reason = in.readString();
        phone_bounced = in.readString();
        phone_bounce_alert = in.readString();
        phone_bounce_reason = in.readString();
        phone_type_error = in.readString();
        phone_type_reason = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(recipientId);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(address1);
        dest.writeString(email);
        dest.writeString(cellphone);
        dest.writeString(wireless_carrier);
        dest.writeString(pe_alert);
        dest.writeString(pt_alert);
        dest.writeString(recipient_status_value);
        dest.writeString(recipient_type_value);

        dest.writeString(email_bounced);
        dest.writeString(email_bounce_alert);
        dest.writeString(email_bounce_reason);
        dest.writeString(phone_bounced);
        dest.writeString(phone_bounce_alert);
        dest.writeString(phone_bounce_reason);
        dest.writeString(phone_type_error);
        dest.writeString(phone_type_reason);

    }

    @SuppressWarnings("unused")
    public static final Creator<Recipient> CREATOR = new Creator<Recipient>() {
        @Override
        public Recipient createFromParcel(Parcel in) {
            return new Recipient(in);
        }

        @Override
        public Recipient[] newArray(int size) {
            return new Recipient[size];
        }
    };

    private String scannedName;

    public String getScannedName() {
        return scannedName;
    }

    public void setScannedName(String scannedName) {
        this.scannedName = scannedName;
    }

    private static Pattern specialCharactersPattern = Pattern.compile("[{}\\[\\]()*&^%$#@!<>?:;'./\"|\\\\]", Pattern.CASE_INSENSITIVE);

    public static boolean isMatchedOnlyAddress(String searchableString, String address) {
        try {
            if (specialCharactersPattern.matcher(String.valueOf(searchableString.charAt(0))).find()) {
                searchableString = " " + searchableString.substring(1);
            } else {
                searchableString = " " + searchableString;
            }
            String concatedString = "";
            String[] addressBlocks = (address != null ? address : "").split(" ");
            concatedString = concatedString + getConcatedString(addressBlocks);
            return concatedString.toLowerCase().contains(searchableString.toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isMatchedExeptAddress(String searchableString, Recipient borrower) {
        try {
            if (specialCharactersPattern.matcher(String.valueOf(searchableString.charAt(0))).find()) {
                searchableString = " " + searchableString.substring(1);
            } else {
                searchableString = " " + searchableString;
            }
            String concatedString = "";
            String[] firstnameblocks = (borrower.getFirstName() != null ? borrower.getFirstName() : "").split(" ");
            String[] lastNameBlocks = (borrower.getLastName() != null ? borrower.getLastName() : "").split(" ");
            concatedString = concatedString + getConcatedString(firstnameblocks);
            concatedString = concatedString + getConcatedString(lastNameBlocks);
            return concatedString.toLowerCase().contains(searchableString.toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isMatched(String searchableString, Recipient borrower) {
        try {
            if (specialCharactersPattern.matcher(String.valueOf(searchableString.charAt(0))).find()) {
                searchableString = " " + searchableString.substring(1);
            } else {
                searchableString = " " + searchableString;
            }
            String concatedString = "";
            String[] firstnameblocks = (borrower.getFirstName() != null ? borrower.getFirstName() : "").split(" ");
            String[] lastNameBlocks = (borrower.getLastName() != null ? borrower.getLastName() : "").split(" ");
            String[] addressBlocks = (borrower.getAddress1() != null ? borrower.getAddress1() : "").split(" ");
            concatedString = concatedString + getConcatedString(firstnameblocks);
            concatedString = concatedString + getConcatedString(lastNameBlocks);
            concatedString = concatedString + getConcatedString(addressBlocks);
            return concatedString.toLowerCase().contains(searchableString.toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String getConcatedString(String[] array) {
        String concatedString = "";
        try {
            for (String s : array) {
                if (s.length() > 0 && specialCharactersPattern.matcher(String.valueOf(s.charAt(0))).find()) {
                    concatedString = concatedString + " " + s.substring(1);
                } else {
                    concatedString = concatedString + " " + s;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return concatedString;
    }


}