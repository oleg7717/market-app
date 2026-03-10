package ru.goncharenko.payment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * Параметры запроса на списание средств
 */

@Schema(name = "Payment", description = "Параметры запроса на списание средств")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-03-10T20:41:32.954970400+03:00[GMT+03:00]", comments = "Generator version: 7.20.0")
public class Payment {

  private @Nullable String userName;

  private @Nullable Double orderAmount;

  public Payment userName(@Nullable String userName) {
    this.userName = userName;
    return this;
  }

  /**
   * Get userName
   * @return userName
   */
  
  @Schema(name = "userName", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("userName")
  public @Nullable String getUserName() {
    return userName;
  }

  public void setUserName(@Nullable String userName) {
    this.userName = userName;
  }

  public Payment orderAmount(@Nullable Double orderAmount) {
    this.orderAmount = orderAmount;
    return this;
  }

  /**
   * Get orderAmount
   * @return orderAmount
   */
  
  @Schema(name = "orderAmount", example = "150.0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("orderAmount")
  public @Nullable Double getOrderAmount() {
    return orderAmount;
  }

  public void setOrderAmount(@Nullable Double orderAmount) {
    this.orderAmount = orderAmount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Payment payment = (Payment) o;
    return Objects.equals(this.userName, payment.userName) &&
        Objects.equals(this.orderAmount, payment.orderAmount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userName, orderAmount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Payment {\n");
    sb.append("    userName: ").append(toIndentedString(userName)).append("\n");
    sb.append("    orderAmount: ").append(toIndentedString(orderAmount)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(@Nullable Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

