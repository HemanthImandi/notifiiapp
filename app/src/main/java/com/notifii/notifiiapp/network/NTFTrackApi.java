package com.notifii.notifiiapp.network;

import com.notifii.notifiiapp.mvp.models.AddRecipientProfileRequest;
import com.notifii.notifiiapp.mvp.models.AddRecipientProfileResponse;
import com.notifii.notifiiapp.mvp.models.ContactUsRequest;
import com.notifii.notifiiapp.mvp.models.ContactUsResponse;
import com.notifii.notifiiapp.mvp.models.EditRecipientProfileRequest;
import com.notifii.notifiiapp.mvp.models.EditRecipientProfileResponse;
import com.notifii.notifiiapp.mvp.models.ForgotPasswordRequest;
import com.notifii.notifiiapp.mvp.models.ForgotPasswordResponse;
import com.notifii.notifiiapp.mvp.models.GetGlobalConstantsRequest;
import com.notifii.notifiiapp.mvp.models.GetPackageDetailsRequest;
import com.notifii.notifiiapp.mvp.models.KioskLoginRequest;
import com.notifii.notifiiapp.mvp.models.KioskLoginResponse;
import com.notifii.notifiiapp.mvp.models.KioskLogoutRequest;
import com.notifii.notifiiapp.mvp.models.LogPackageDataRequest;
import com.notifii.notifiiapp.mvp.models.LogPackageDataResponse;
import com.notifii.notifiiapp.mvp.models.LogPkgImagesRequest;
import com.notifii.notifiiapp.mvp.models.LogPkgImagesResponse;
import com.notifii.notifiiapp.mvp.models.LogPkgInResponse;
import com.notifii.notifiiapp.mvp.models.LoginRequest;
import com.notifii.notifiiapp.mvp.models.LoginResponse;
import com.notifii.notifiiapp.mvp.models.LogoutRequest;
import com.notifii.notifiiapp.mvp.models.LogoutResponse;
import com.notifii.notifiiapp.mvp.models.MFALoginRequest;
import com.notifii.notifiiapp.mvp.models.MultisiteLoginRequest;
import com.notifii.notifiiapp.mvp.models.PackageHistoryRequest;
import com.notifii.notifiiapp.mvp.models.PackageLogoutDataRequest;
import com.notifii.notifiiapp.mvp.models.PackageLogoutDataResponse;
import com.notifii.notifiiapp.mvp.models.PackageLogoutImagesRequest;
import com.notifii.notifiiapp.mvp.models.PackageLogoutImagesResponse;
import com.notifii.notifiiapp.mvp.models.PackageLookUpRequest;
import com.notifii.notifiiapp.mvp.models.PackageLookUpResponse;
import com.notifii.notifiiapp.mvp.models.PackageListforRecipientRequest;
import com.notifii.notifiiapp.mvp.models.PendingPackagesRequest;
import com.notifii.notifiiapp.mvp.models.ProfileRequest;
import com.notifii.notifiiapp.mvp.models.ProfileResponse;
import com.notifii.notifiiapp.mvp.models.RecipientDetailsRequest;
import com.notifii.notifiiapp.mvp.models.RecipientListRequest;
import com.notifii.notifiiapp.mvp.models.RecipientPendingPackagesRequest;
import com.notifii.notifiiapp.mvp.models.RecipientPendingPackagesResponse;
import com.notifii.notifiiapp.mvp.models.ResendMFACodeRequest;
import com.notifii.notifiiapp.mvp.models.ResendMFACodeResponse;
import com.notifii.notifiiapp.mvp.models.SearchPackageRequest;
import com.notifii.notifiiapp.mvp.models.SearchResidentRequest;
import com.notifii.notifiiapp.mvp.models.SsoEmailRequest;
import com.notifii.notifiiapp.mvp.models.SsoEmailResponse;
import com.notifii.notifiiapp.mvp.models.SsoLoginResponseRequest;
import com.notifii.notifiiapp.mvp.models.SwitchAccountRequest;
import com.notifii.notifiiapp.mvp.models.UpdateNamEmailRequest;
import com.notifii.notifiiapp.mvp.models.UpdateNamEmailResponse;
import com.notifii.notifiiapp.mvp.models.UpdatePackageResponse;
import com.notifii.notifiiapp.mvp.models.UpdatePasswordRequest;
import com.notifii.notifiiapp.mvp.models.UpdatePasswordResponse;
import com.notifii.notifiiapp.mvp.models.UpdateUserNameRequest;
import com.notifii.notifiiapp.mvp.models.UpdateUserNameResponse;
import com.notifii.notifiiapp.mvp.models.UpdateUsernamePwdRequest;
import com.notifii.notifiiapp.mvp.models.UpdateUsernamePwdResponse;
import com.notifii.notifiiapp.mvp.models.UsernameCheckAvailabilityRequest;
import com.notifii.notifiiapp.mvp.models.UsernameCheckAvailabilityResponse;
import com.notifii.notifiiapp.utils.NTF_Constants;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface NTFTrackApi {
    @POST(NTF_Constants.Service_URLs.LOGIN_URL)
    Observable<LoginResponse> login(@Body LoginRequest loginRequest);
    @POST(NTF_Constants.Service_URLs.MFA_LOGIN)
    Observable<LoginResponse> doMFAlogin(@Body MFALoginRequest loginRequest);
    @POST(NTF_Constants.Service_URLs.RESEND_MFA)
    Observable<ResendMFACodeResponse> resendVerificationCode(@Body ResendMFACodeRequest request);
    @POST(NTF_Constants.Service_URLs.GLOBAL_CONSTANTS)
    Observable<ResponseBody> getGlobalConstancts(@Body GetGlobalConstantsRequest request);
    @POST(NTF_Constants.Service_URLs.RECIPIENT_LIST_URL)
    Observable<ResponseBody> getRecipientList(@Body RecipientListRequest request);
    @POST(NTF_Constants.Service_URLs.SEARCH_RECIPIENT_URL)
    Observable<ResponseBody> searchRecipientList(@Body SearchResidentRequest request);
    @POST(NTF_Constants.Service_URLs.App_LOGOUT_URL)
    Observable<LogoutResponse> doLogout(@Body LogoutRequest request);
    @POST(NTF_Constants.Service_URLs.SSO_LOGIN_REQUEST)
    Observable<SsoEmailResponse> doSsoEmailLogin(@Body SsoEmailRequest request);
    @POST(NTF_Constants.Service_URLs.KIOSK_SSO_LOGOUT_REQUEST)
    Observable<SsoEmailResponse> doKioskSsoEmailLogin(@Body SsoEmailRequest request);
    @POST(NTF_Constants.Service_URLs.SSO_LOGIN_RESPONSE)
    Observable<LoginResponse> doSsoLogin(@Body SsoLoginResponseRequest request);
    @POST(NTF_Constants.Service_URLs.FORGOT_PASSWORD)
    Observable<ForgotPasswordResponse> forgotpassword(@Body ForgotPasswordRequest request);
    @POST(NTF_Constants.Service_URLs.KIOSK_LOGIN)
    Observable<KioskLoginResponse> kioskLogin(@Body KioskLoginRequest request);
    @POST(NTF_Constants.Service_URLs.KIOSK_LOGOUT_URL)
    Observable<LoginResponse> kioskLogout(@Body KioskLogoutRequest request);
    @POST(NTF_Constants.Service_URLs.RECIPIENT_PENDING_PACKAGES)
    Observable<RecipientPendingPackagesResponse> getRecipientPackages(@Body RecipientPendingPackagesRequest request);
    @POST(NTF_Constants.Service_URLs.LOGOUT_SIGNATURE_KIOSK_DATA)
    Observable<PackageLogoutDataResponse> logoutPkgData(@Body PackageLogoutDataRequest request);
    @POST(NTF_Constants.Service_URLs.LOGOUT_SIGNATURE_KIOSK_IMAGES)
    Observable<PackageLogoutImagesResponse> logoutPkgImages(@Body PackageLogoutImagesRequest request);
    @POST(NTF_Constants.Service_URLs.CONTACT_US_URL)
    Observable<ContactUsResponse> doContactDetailsSubmit(@Body ContactUsRequest request);
    @POST(NTF_Constants.Service_URLs.PROFILE_URL)
    Observable<ProfileResponse> doProfileDetails(@Body ProfileRequest request);
    @POST(NTF_Constants.Service_URLs.UPDATE_NAME_EMAIL)
    Observable<UpdateNamEmailResponse> doUserNameEmailSubmit(@Body UpdateNamEmailRequest request);
    @POST(NTF_Constants.Service_URLs.UPDATE_USERNAME_URL)
    Observable<UpdateUserNameResponse> doUserNameSubmit(@Body UpdateUserNameRequest request);
    @POST(NTF_Constants.Service_URLs.UPDATE_PASSWORD_URL)
    Observable<UpdatePasswordResponse> doUserPasswordSubmit(@Body UpdatePasswordRequest request);
    @POST(NTF_Constants.Service_URLs.ADD_RECIPIENT_URL)
    Observable<AddRecipientProfileResponse> doAddRecipient(@Body AddRecipientProfileRequest request);
    @POST(NTF_Constants.Service_URLs.UPDATE_RECIPIENT_URL)
    Observable<EditRecipientProfileResponse> doUpdateRecipient(@Body EditRecipientProfileRequest request);
    @POST(NTF_Constants.Service_URLs.GET_RECIPIENT_URL)
    Observable<ResponseBody> getRecipient(@Body RecipientDetailsRequest request);
    @POST(NTF_Constants.Service_URLs.PACKAGES_FOR_RECIPIENT_URL)
    Observable<ResponseBody> getPackageListforRecipient(@Body PackageListforRecipientRequest request);
    @POST(NTF_Constants.Service_URLs.PACKAGE_HISTORY_URL)
    Observable<ResponseBody> getPackageHistoryList(@Body PackageHistoryRequest request);
    @POST(NTF_Constants.Service_URLs.SEARCH_PACKAGE_URL)
    Observable<ResponseBody> getSearchPackageList(@Body SearchPackageRequest request);
    @POST(NTF_Constants.Service_URLs.PACKAGE_LOGIN_URL)
    Observable<LogPkgInResponse> doPkgLogin(@Body RequestBody request);
    @POST(NTF_Constants.Service_URLs.GET_PACKAGE_DETAILS_URL)
    Observable<ResponseBody> doGetPackageDetails(@Body GetPackageDetailsRequest request);
    @POST(NTF_Constants.Service_URLs.USERNAME_PASSWORD_UPDATE)
    Observable<UpdateUsernamePwdResponse> doUpdate(@Body UpdateUsernamePwdRequest request);
    @POST(NTF_Constants.Service_URLs.USERNAME_AVAILABILITY)
    Observable<UsernameCheckAvailabilityResponse> doCheckAvailability(@Body UsernameCheckAvailabilityRequest request);
    @POST(NTF_Constants.Service_URLs.PACKAGE_LOOKUP_URL)
    Observable<PackageLookUpResponse> doLookUp(@Body PackageLookUpRequest request);
    @POST(NTF_Constants.Service_URLs.LOGOUT_SIGNATURE_KIOSK_DATA)
    Observable<LogPackageDataResponse> doPkgLogout(@Body LogPackageDataRequest request);
    @POST(NTF_Constants.Service_URLs.LOGOUT_SIGNATURE_KIOSK_IMAGES)
    Observable<LogPkgImagesResponse> doPkgImagesLogout(@Body LogPkgImagesRequest request);
    @POST(NTF_Constants.Service_URLs.PACKAGES_PENDING_WITH_IMAGES_URL)
    Observable<ResponseBody> getPendingPackages(@Body PendingPackagesRequest request);
    @POST(NTF_Constants.Service_URLs.MULTIPLE_UPDATE_PACKAGE_URL)
    Observable<UpdatePackageResponse> doUpdatePackage(@Body RequestBody request);
    @POST(NTF_Constants.Service_URLs.UPDATE_PACKAGE_URL)
    Observable<ResponseBody> doUpdateSinglePkg(@Body RequestBody requestBody);
    @POST(NTF_Constants.Service_URLs.MULTISITE_LOGIN)
    Observable<LoginResponse> doMultisiteLogin(@Body MultisiteLoginRequest request);
    @POST(NTF_Constants.Service_URLs.SWITCH_ACCOUNTS)
    Observable<LoginResponse> doSwitchAccounts(@Body SwitchAccountRequest request);

}
