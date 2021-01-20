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

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class NTFTrackService {

    private static NTFTrackService instance;
    private static String headerValue;

    private Retrofit retrofit;

    private NTFTrackApi trackApi;

    private static final int TIMEOUT = 20;
//    private static final int TIMEOUT = 600;
    private Request request;

    public static NTFTrackService getInstance(String header) {
        if (instance == null) {
            instance = new NTFTrackService();
        }
        headerValue = header;
        return instance;
    }

    private NTFTrackService() {
        if (retrofit == null || trackApi == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(NTF_Constants.Base_API.BASE_URL)
                    .client(createOkHttpClientInterceptor())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build();

            trackApi = retrofit.create(NTFTrackApi.class);
        }
    }

    private OkHttpClient createOkHttpClientInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Interceptor headerInterceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder builder = original.newBuilder();

                if (headerValue != null && !headerValue.isEmpty()) {
                    builder.header(NTF_Constants.APP_HEADER, headerValue);
                }
                builder.method(original.method(), original.body());
                request = builder.build();
                return chain.proceed(request);
            }
        };

        return new OkHttpClient.Builder().connectTimeout(TIMEOUT, TimeUnit.SECONDS).readTimeout(TIMEOUT,
                TimeUnit.SECONDS).addInterceptor(interceptor)
                .addNetworkInterceptor(headerInterceptor).build();
    }

    public Observable<LoginResponse> login(LoginRequest loginParams) {
        return trackApi.login(loginParams);
    }

    public Observable<LoginResponse> doMFAlogin(MFALoginRequest loginParams) {
        return trackApi.doMFAlogin(loginParams);
    }

    public Observable<ResendMFACodeResponse> resendVerificationCode(ResendMFACodeRequest request) {
        return trackApi.resendVerificationCode(request);
    }

    public Observable<ResponseBody> getGlobalConstancts(GetGlobalConstantsRequest request) {
        return trackApi.getGlobalConstancts(request);
    }

    public Observable<ResponseBody> getRecipientList(RecipientListRequest request) {
        return trackApi.getRecipientList(request);
    }

    public Observable<ResponseBody> searchRecipientList(SearchResidentRequest request) {
        return trackApi.searchRecipientList(request);
    }

    public Observable<LogoutResponse> doLogout(LogoutRequest request) {
        return trackApi.doLogout(request);
    }

    public Observable<SsoEmailResponse> doSsoEmailLogin(SsoEmailRequest request) {
        return trackApi.doSsoEmailLogin(request);
    }

    public Observable<SsoEmailResponse> doKioskSsoEmailLogin(SsoEmailRequest request) {
        return trackApi.doKioskSsoEmailLogin(request);
    }

    public Observable<LoginResponse> doSsoLogin(SsoLoginResponseRequest request) {
        return trackApi.doSsoLogin(request);
    }

    public Observable<ForgotPasswordResponse> forgotpassword(ForgotPasswordRequest request) {
        return trackApi.forgotpassword(request);
    }

    public Observable<KioskLoginResponse> kioskLogin(KioskLoginRequest request) {
        return trackApi.kioskLogin(request);
    }

    public Observable<LoginResponse> kioskLogout(KioskLogoutRequest request) {
        return trackApi.kioskLogout(request);
    }

    public Observable<RecipientPendingPackagesResponse> getRecipientPackages(RecipientPendingPackagesRequest request) {
        return trackApi.getRecipientPackages(request);
    }

    public Observable<PackageLogoutDataResponse> logoutPkgData(PackageLogoutDataRequest request) {
        return trackApi.logoutPkgData(request);
    }

    public Observable<PackageLogoutImagesResponse> logoutPkgImages(PackageLogoutImagesRequest request) {
        return trackApi.logoutPkgImages(request);
    }

    public Observable<ContactUsResponse> doContactDetailsSubmit(ContactUsRequest request) {
        return trackApi.doContactDetailsSubmit(request);
    }

    public Observable<ProfileResponse> doProfileDetails(ProfileRequest request) {
        return trackApi.doProfileDetails(request);
    }

    public Observable<UpdateNamEmailResponse> doUserNameEmailSubmit(UpdateNamEmailRequest request) {
        return trackApi.doUserNameEmailSubmit(request);
    }
    public Observable<UpdateUserNameResponse> doUserNameSubmit(UpdateUserNameRequest request) {
        return trackApi.doUserNameSubmit(request);
    }

    public Observable<AddRecipientProfileResponse> doAddRecipient(AddRecipientProfileRequest request) {
        return trackApi.doAddRecipient(request);
    }

    public Observable<EditRecipientProfileResponse> doUpdateRecipient(EditRecipientProfileRequest request) {
        return trackApi.doUpdateRecipient(request);
    }

    public Observable<UpdatePasswordResponse> doUserPasswordSubmit(UpdatePasswordRequest request) {
        return trackApi.doUserPasswordSubmit(request);
    }

    public Observable<ResponseBody> getRecipient(RecipientDetailsRequest request) {
        return trackApi.getRecipient(request);
    }
    public Observable<ResponseBody> getPackageListforRecipient(PackageListforRecipientRequest request) {
        return trackApi.getPackageListforRecipient(request);
    }
    public Observable<ResponseBody> getPackageHistoryList(PackageHistoryRequest request) {
        return trackApi.getPackageHistoryList(request);
    }
    public Observable<ResponseBody> getSearchPackageList(SearchPackageRequest request) {
        return trackApi.getSearchPackageList(request);
    }
    public Observable<LogPkgInResponse> doPkgLogin(RequestBody request) {
        return trackApi.doPkgLogin(request);
    }
    public Observable<ResponseBody> doGetPackageDetails(GetPackageDetailsRequest request) {
        return trackApi.doGetPackageDetails(request);
    }
    public Observable<UpdateUsernamePwdResponse> doUpdate(UpdateUsernamePwdRequest request) {
        return trackApi.doUpdate(request);
    }
    public Observable<UsernameCheckAvailabilityResponse> doCheckAvailability(UsernameCheckAvailabilityRequest request) {
        return trackApi.doCheckAvailability(request);
    }

    public Observable<PackageLookUpResponse> doLookUp(PackageLookUpRequest request) {
        return trackApi.doLookUp(request);
    }

    public Observable<LogPackageDataResponse> doPkgLogout(LogPackageDataRequest request) {
        return trackApi.doPkgLogout(request);
    }

    public Observable<LogPkgImagesResponse> doPkgImagesLogout(LogPkgImagesRequest request) {
        return trackApi.doPkgImagesLogout(request);
    }

    public Observable<ResponseBody> getPendingPackages(PendingPackagesRequest request) {
        return trackApi.getPendingPackages(request);
    }

    public Observable<UpdatePackageResponse> doUpdateMultiplePackages(RequestBody request) {
        return trackApi.doUpdatePackage(request);
    }

    public Observable<ResponseBody> doUpdateSinglePkg(RequestBody requestBody) {
        return trackApi.doUpdateSinglePkg(requestBody);
    }

    public Observable<LoginResponse> doMultisiteLogin(MultisiteLoginRequest requestBody) {
        return trackApi.doMultisiteLogin(requestBody);
    }

    public Observable<LoginResponse> doSwitchAccounts(SwitchAccountRequest requestBody) {
        return trackApi.doSwitchAccounts(requestBody);
    }


}
