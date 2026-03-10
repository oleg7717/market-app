package ru.goncharenko.market.payment.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import ru.goncharenko.market.payment.ApiClient;
import ru.goncharenko.market.payment.model.Payment;
import ru.goncharenko.market.payment.model.PaymentStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2026-03-10T20:48:17.154035700+03:00[GMT+03:00]", comments = "Generator version: 7.20.0")
public class DefaultApi {
    private ApiClient apiClient;

    public DefaultApi() {
        this(new ApiClient());
    }

    public DefaultApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Подтверждение баланса пользователя
     * Получить подтверждение, что на балансе пользователя достаточно средств для осуществления покупки
     * <p><b>200</b> - Баланс счета получен
     * @param userName Логин пользователя
     * @param orderAmount Сумма заказа
     * @return PaymentStatus
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec apiBalanceGetRequestCreation(@jakarta.annotation.Nonnull String userName, @jakarta.annotation.Nonnull Double orderAmount) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'userName' is set
        if (userName == null) {
            throw new WebClientResponseException("Missing the required parameter 'userName' when calling apiBalanceGet", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'orderAmount' is set
        if (orderAmount == null) {
            throw new WebClientResponseException("Missing the required parameter 'orderAmount' when calling apiBalanceGet", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "userName", userName));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "orderAmount", orderAmount));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<PaymentStatus> localVarReturnType = new ParameterizedTypeReference<PaymentStatus>() {};
        return apiClient.invokeAPI("/api/balance", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Подтверждение баланса пользователя
     * Получить подтверждение, что на балансе пользователя достаточно средств для осуществления покупки
     * <p><b>200</b> - Баланс счета получен
     * @param userName Логин пользователя
     * @param orderAmount Сумма заказа
     * @return PaymentStatus
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<PaymentStatus> apiBalanceGet(@jakarta.annotation.Nonnull String userName, @jakarta.annotation.Nonnull Double orderAmount) throws WebClientResponseException {
        ParameterizedTypeReference<PaymentStatus> localVarReturnType = new ParameterizedTypeReference<PaymentStatus>() {};
        return apiBalanceGetRequestCreation(userName, orderAmount).bodyToMono(localVarReturnType);
    }

    /**
     * Подтверждение баланса пользователя
     * Получить подтверждение, что на балансе пользователя достаточно средств для осуществления покупки
     * <p><b>200</b> - Баланс счета получен
     * @param userName Логин пользователя
     * @param orderAmount Сумма заказа
     * @return ResponseEntity&lt;PaymentStatus&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<PaymentStatus>> apiBalanceGetWithHttpInfo(@jakarta.annotation.Nonnull String userName, @jakarta.annotation.Nonnull Double orderAmount) throws WebClientResponseException {
        ParameterizedTypeReference<PaymentStatus> localVarReturnType = new ParameterizedTypeReference<PaymentStatus>() {};
        return apiBalanceGetRequestCreation(userName, orderAmount).toEntity(localVarReturnType);
    }

    /**
     * Подтверждение баланса пользователя
     * Получить подтверждение, что на балансе пользователя достаточно средств для осуществления покупки
     * <p><b>200</b> - Баланс счета получен
     * @param userName Логин пользователя
     * @param orderAmount Сумма заказа
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec apiBalanceGetWithResponseSpec(@jakarta.annotation.Nonnull String userName, @jakarta.annotation.Nonnull Double orderAmount) throws WebClientResponseException {
        return apiBalanceGetRequestCreation(userName, orderAmount);
    }

    /**
     * Осуществление платежа
     * Осуществление платежа
     * <p><b>200</b> - Платеж осуществлен
     * @param payment The payment parameter
     * @return PaymentStatus
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec apiBalancePostRequestCreation(@jakarta.annotation.Nonnull Payment payment) throws WebClientResponseException {
        Object postBody = payment;
        // verify the required parameter 'payment' is set
        if (payment == null) {
            throw new WebClientResponseException("Missing the required parameter 'payment' when calling apiBalancePost", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<PaymentStatus> localVarReturnType = new ParameterizedTypeReference<PaymentStatus>() {};
        return apiClient.invokeAPI("/api/balance", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Осуществление платежа
     * Осуществление платежа
     * <p><b>200</b> - Платеж осуществлен
     * @param payment The payment parameter
     * @return PaymentStatus
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<PaymentStatus> apiBalancePost(@jakarta.annotation.Nonnull Payment payment) throws WebClientResponseException {
        ParameterizedTypeReference<PaymentStatus> localVarReturnType = new ParameterizedTypeReference<PaymentStatus>() {};
        return apiBalancePostRequestCreation(payment).bodyToMono(localVarReturnType);
    }

    /**
     * Осуществление платежа
     * Осуществление платежа
     * <p><b>200</b> - Платеж осуществлен
     * @param payment The payment parameter
     * @return ResponseEntity&lt;PaymentStatus&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<PaymentStatus>> apiBalancePostWithHttpInfo(@jakarta.annotation.Nonnull Payment payment) throws WebClientResponseException {
        ParameterizedTypeReference<PaymentStatus> localVarReturnType = new ParameterizedTypeReference<PaymentStatus>() {};
        return apiBalancePostRequestCreation(payment).toEntity(localVarReturnType);
    }

    /**
     * Осуществление платежа
     * Осуществление платежа
     * <p><b>200</b> - Платеж осуществлен
     * @param payment The payment parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec apiBalancePostWithResponseSpec(@jakarta.annotation.Nonnull Payment payment) throws WebClientResponseException {
        return apiBalancePostRequestCreation(payment);
    }
}
