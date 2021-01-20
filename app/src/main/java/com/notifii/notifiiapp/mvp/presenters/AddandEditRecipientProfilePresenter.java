package com.notifii.notifiiapp.mvp.presenters;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.achercasky.initialclasses.mvp.presenter.BasePresenter;
import com.notifii.notifiiapp.mvp.models.AddRecipientProfileRequest;
import com.notifii.notifiiapp.mvp.models.AddRecipientProfileResponse;
import com.notifii.notifiiapp.mvp.models.EditRecipientProfileRequest;
import com.notifii.notifiiapp.mvp.models.EditRecipientProfileResponse;
import com.notifii.notifiiapp.mvp.views.AddAndEditRecipientView;
import com.notifii.notifiiapp.network.CustomDisposableObserver;
import com.notifii.notifiiapp.network.NTFTrackService;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddandEditRecipientProfilePresenter extends BasePresenter<AddAndEditRecipientView> {

    public void doCheckRecipientDetails(EditText firstName, EditText lastName, EditText address1, EditText email, LinearLayout linear, Spinner spinner, EditText specialTrackInstructionsET, TextView vacationStartDate, TextView vacationEndDate, String specialTrackInstructions, boolean isAddr1Required) {

        if (TextUtils.isEmpty(firstName.getText().toString().trim())) {
            getMvpView().onEmptyFirstName();
            return;
        }
        if (TextUtils.isEmpty(lastName.getText().toString().trim())) {
            getMvpView().onEmpyLastName();
            return;
        }
        if (isAddr1Required && TextUtils.isEmpty(address1.getText().toString().trim())) {
            getMvpView().onEmptyAddress1();
            return;
        }
        if (!TextUtils.isEmpty(email.getText().toString().trim()) && !NTF_Utils.isValidEmail(email.getText().toString().trim())) {
            getMvpView().onCheckEmail();
            return;
        }
        if (linear.getVisibility() == View.VISIBLE && spinner.getSelectedItem() == null) {
            getMvpView().onSelectSpinner();
            return;
        }
        if (specialTrackInstructions != null && !specialTrackInstructions.equalsIgnoreCase("none") && specialTrackInstructionsET.getText().toString().trim().isEmpty()) {
            getMvpView().onEmptySpecialTrackInstructions();
            return;
        }

        if (!vacationStartDate.getText().toString().isEmpty() || !vacationEndDate.getText().toString().isEmpty()) {
            if (vacationStartDate.getText().toString().isEmpty()) {
                getMvpView().onCheckVacationStartDate();
                return;
            } else if (vacationEndDate.getText().toString().isEmpty()) {
                getMvpView().onCheckVacationEndDate();
                return;
            } else {
                if (!isValidVacationEndDate(vacationStartDate, vacationEndDate)) {
                    getMvpView().onCheckVacationinvalid();
                    return;
                }
            }

        }
        getMvpView().onRecipientDetailsGiven();
    }

    public final static boolean isValidVacationEndDate(TextView vacationStartDateTV, TextView vacationEndDateTV) {
        String[] startVacationDate = vacationStartDateTV.getText().toString().split("-");
        int startDay = Integer.parseInt(startVacationDate[2]);
        int startmonth = Integer.parseInt(startVacationDate[1]);
        int startYear = Integer.parseInt(startVacationDate[0]);
        String[] endVacationDate = vacationEndDateTV.getText().toString().split("-");
        int endDay = Integer.parseInt(endVacationDate[2]);
        int endmonth = Integer.parseInt(endVacationDate[1]);
        int endYear = Integer.parseInt(endVacationDate[0]);
        if (startYear == endYear) {
            if (startmonth == endmonth && startDay >= endDay) {
                return false;
            } else if (startmonth > endmonth) {
                return false;
            }
        } else if (startYear > endYear) {
            return false;
        }
        return true;
    }


    public void addingAllRecipientDetails(String header, AddRecipientProfileRequest request, boolean isEdit) {
        Disposable disposable = NTFTrackService.getInstance(header).doAddRecipient(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CustomDisposableObserver<AddRecipientProfileResponse>() {
                    @Override
                    public void onNext(AddRecipientProfileResponse response) {
                        if (getMvpView() != null) {
                            if (response.getApiStatus().equals(NTF_Constants.ResponseKeys.SESSION_EXPIRED)) {
                                getMvpView().onSessionExpired(response.getApiMessage());
                                return;
                            }
                            if (response.getApiStatus().toLowerCase().equalsIgnoreCase(NTF_Constants.ResponseKeys.SUCCESS)) {
                                getMvpView().onAddingSuccess(response);
                            } else {
                                getMvpView().onErrorCode(response.getApiMessage());
                            }
                        }
                    }

                    @Override
                    public void onConnectionLost() {
                        if (getMvpView() != null) {
                            getMvpView().onNoInternetConnection();
                        }
                    }

                    @Override
                    public void onServerError() {
                        if (getMvpView() != null) {
                            getMvpView().onServerError();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (getMvpView() != null) {
                            getMvpView().onError(t);
                        }
                    }
                });
        compositeSubscription.add(disposable);
    }

    public void editingAllRecipientDetails(String header, EditRecipientProfileRequest request) {
        Disposable disposable = NTFTrackService.getInstance(header).doUpdateRecipient(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CustomDisposableObserver<EditRecipientProfileResponse>() {
                    @Override
                    public void onNext(EditRecipientProfileResponse response) {
                        if (getMvpView() != null) {
                            if (response.getApiStatus().equals(NTF_Constants.ResponseKeys.SESSION_EXPIRED)) {
                                getMvpView().onSessionExpired(response.getApiMessage());
                                return;
                            }
                            if (response.getApiStatus().toLowerCase().equalsIgnoreCase(NTF_Constants.ResponseKeys.SUCCESS)) {
                                getMvpView().onEditingSuccess(response);
                            } else {
                                getMvpView().onErrorCode(response.getApiMessage());
                            }
                        }

                    }

                    @Override
                    public void onConnectionLost() {
                        if (getMvpView() != null) {
                            getMvpView().onNoInternetConnection();
                        }
                    }

                    @Override
                    public void onServerError() {
                        if (getMvpView() != null) {
                            getMvpView().onServerError();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (getMvpView() != null) {
                            getMvpView().onError(t);
                        }
                    }
                });
        compositeSubscription.add(disposable);

    }

    public AddRecipientProfileRequest getAddRecipientRequest(String session_id, String authentication_token, String account_id,
                                                             String first_name, String last_name, String address1, String email, String cellphone,
                                                             String track_nonMark_EmailCB, String track_nonMark_TextCB, String recipient_status, String recipient_type,
                                                             String specialTrackInsFlag, String sendPkgLoginNotifii, String sendPkgLogoutNotifii, String vacationStart,
                                                             String vacationEnd, String specialTrackIns,String userId) {
        AddRecipientProfileRequest addRecipientProfileRequest = new AddRecipientProfileRequest();
        addRecipientProfileRequest.setSessionId(session_id);
        addRecipientProfileRequest.setAuthenticationToken(authentication_token);
        addRecipientProfileRequest.setAccountId(account_id);
        addRecipientProfileRequest.setUserId(userId);
        addRecipientProfileRequest.setFirstName(first_name);
        addRecipientProfileRequest.setLastName(last_name);
        addRecipientProfileRequest.setAddress1(address1);
        addRecipientProfileRequest.setEmail(email);
        addRecipientProfileRequest.setCellphone(cellphone);
        addRecipientProfileRequest.setSendTrackNonmarketingEmail(track_nonMark_EmailCB);
        addRecipientProfileRequest.setSendTrackNonmarketingText(track_nonMark_TextCB);
        addRecipientProfileRequest.setRecipientStatus("1");
        addRecipientProfileRequest.setRecipientType(recipient_type);
        addRecipientProfileRequest.setSpecialTrackInstructionsFlag(specialTrackInsFlag);
        addRecipientProfileRequest.setSendPkgLoginNotification(sendPkgLoginNotifii);
        addRecipientProfileRequest.setSendPkgLogoutNotification(sendPkgLogoutNotifii);
        addRecipientProfileRequest.setVacationStartDate(vacationStart);
        addRecipientProfileRequest.setVacationEndDate(vacationEnd);
        addRecipientProfileRequest.setSpecialTrackInstructions(specialTrackIns);
        return addRecipientProfileRequest;

    }

    public EditRecipientProfileRequest getUpdateRecipientRequest(String session_id, String authentication_token, String account_id, String recipient_id,
                                                                 String first_name, String last_name, String address1, String email, String cellphone,
                                                                 String track_nonMark_EmailCB, String track_nonMark_TextCB, String track_nonMark_PushCB,
                                                                 String connect_nonMark_EmailCB, String connect_nonMark_TextCB, String connect_nonMark_PushCB,
                                                                 String connect_Mark_EmailCB, String connect_Mark_TextCB, String connect_Mark_PushCB,
                                                                 String track_nonMark_Email_optOut, String track_nonMark_Text_optOut, String track_nonMark_push_optOut,
                                                                 String connect_nonMark_Email_optOut, String connect_nonMark_Text_optOut, String connect_nonMark_Push_optOut,
                                                                 String connect_Mark_Email_optOut, String connect_Mark_Text_optOut, String connect_Mark_Push_optOut,
                                                                 String recipient_status, String recipient_type, String specialTrackIns, String specialTrackInsFlag, String vacationStartDate,
                                                                 String vacationEndDate, String vacationStatus, String lease_start_date, String lease_end_date, String birth_date, String move_in_date,
                                                                 String move_out_date, String send_pkg_login_notification, String send_pkg_logout_notification,
                                                                 String userId,String recipientTitle) {

        EditRecipientProfileRequest editRecipientProfileRequest = new EditRecipientProfileRequest();
        editRecipientProfileRequest.setSessionId(session_id);
        editRecipientProfileRequest.setAuthenticationToken(authentication_token);
        editRecipientProfileRequest.setAccountId(account_id);
        editRecipientProfileRequest.setRecipientId(recipient_id);
        editRecipientProfileRequest.setFirstName(first_name);
        editRecipientProfileRequest.setLastName(last_name);
        editRecipientProfileRequest.setAddress1(address1);
        editRecipientProfileRequest.setEmail(email);
        editRecipientProfileRequest.setCellphone(cellphone);
        editRecipientProfileRequest.setSendTrackNonmarketingEmail(track_nonMark_EmailCB);
        editRecipientProfileRequest.setSendTrackNonmarketingText(track_nonMark_TextCB);
        editRecipientProfileRequest.setSendTrackNonmarketingPush(track_nonMark_PushCB);
        editRecipientProfileRequest.setSendConnectNonmarketingEmail(connect_nonMark_EmailCB);
        editRecipientProfileRequest.setSendConnectNonmarketingText(connect_nonMark_TextCB);
        editRecipientProfileRequest.setSendConnectNonmarketingPush(connect_nonMark_PushCB);
        editRecipientProfileRequest.setSendConnectMarketingEmail(connect_Mark_EmailCB);
        editRecipientProfileRequest.setSendConnectMarketingText(connect_Mark_TextCB);
        editRecipientProfileRequest.setSendConnectMarketingPush(connect_Mark_PushCB);
        editRecipientProfileRequest.setStopTrackNonmarketingEmail(track_nonMark_Email_optOut);
        editRecipientProfileRequest.setStopTrackNonmarketingText(track_nonMark_Text_optOut);
        editRecipientProfileRequest.setStopTrackNonmarketingPush(track_nonMark_push_optOut);
        editRecipientProfileRequest.setStopConnectNonmarketingEmail(connect_nonMark_Email_optOut);
        editRecipientProfileRequest.setStopConnectNonmarketingText(connect_nonMark_Text_optOut);
        editRecipientProfileRequest.setStopConnectNonmarketingPush(connect_nonMark_Push_optOut);
        editRecipientProfileRequest.setStopConnectMarketingEmail(connect_Mark_Email_optOut);
        editRecipientProfileRequest.setStopConnectMarketingText(connect_Mark_Text_optOut);
        editRecipientProfileRequest.setStopConnectMarketingPush(connect_Mark_Push_optOut);
        editRecipientProfileRequest.setRecipientStatus(recipient_status);
        editRecipientProfileRequest.setRecipientType(recipient_type);
        editRecipientProfileRequest.setSpecialTrackInstructions(specialTrackIns);
        editRecipientProfileRequest.setSpecialTrackInstructionsFlag(specialTrackInsFlag);
        editRecipientProfileRequest.setVacationStartDate(vacationStartDate);
        editRecipientProfileRequest.setVacationEndDate(vacationEndDate);
        editRecipientProfileRequest.setVacationStatus(vacationStatus);
        editRecipientProfileRequest.setLeaseStartDate(lease_start_date);
        editRecipientProfileRequest.setLeaseEndDate(lease_end_date);
        editRecipientProfileRequest.setBirthDate(birth_date);
        editRecipientProfileRequest.setMoveInDate(move_in_date);
        editRecipientProfileRequest.setMoveOutDate(move_out_date);
        editRecipientProfileRequest.setSendPkgLoginNotification(send_pkg_login_notification);
        editRecipientProfileRequest.setSendPkgLogoutNotification(send_pkg_logout_notification);
        editRecipientProfileRequest.setUserId(userId);
        editRecipientProfileRequest.setRecipientTitle(recipientTitle);
        return editRecipientProfileRequest;

    }
}
