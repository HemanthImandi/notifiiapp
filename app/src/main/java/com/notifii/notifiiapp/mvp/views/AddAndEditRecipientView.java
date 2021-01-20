package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;
import com.notifii.notifiiapp.mvp.models.AddRecipientProfileResponse;
import com.notifii.notifiiapp.mvp.models.EditRecipientProfileResponse;

public interface AddAndEditRecipientView extends BaseMvpView {
    void onEmptyFirstName();

    void onEmpyLastName();

    void onEmptyAddress1();

    void onCheckEmail();

    void onCheckVacationStartDate();

    void onCheckVacationEndDate();

    void onCheckVacationinvalid();

    void onSelectSpinner();

    void onEmptySpecialTrackInstructions();

    void onRecipientDetailsGiven();

    void onAddingSuccess(AddRecipientProfileResponse response);

    void onEditingSuccess(EditRecipientProfileResponse response);

}
