package ru.goncharenko.payment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * PaymentStatus
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-03-10T20:48:34.501349100+03:00[GMT+03:00]", comments = "Generator version: 7.20.0")
public class PaymentStatus {

  private @Nullable String message;

  private @Nullable Boolean processed;

  private @Nullable Integer code;

  public PaymentStatus message(@Nullable String message) {
    this.message = message;
    return this;
  }

  /**
   * Get message
   * @return message
   */
  
  @Schema(name = "message", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("message")
  public @Nullable String getMessage() {
    return message;
  }

  public void setMessage(@Nullable String message) {
    this.message = message;
  }

  public PaymentStatus processed(@Nullable Boolean processed) {
    this.processed = processed;
    return this;
  }

  /**
   * Get processed
   * @return processed
   */
  
  @Schema(name = "processed", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("processed")
  public @Nullable Boolean getProcessed() {
    return processed;
  }

  public void setProcessed(@Nullable Boolean processed) {
    this.processed = processed;
  }

  public PaymentStatus code(@Nullable Integer code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code
   */
  
  @Schema(name = "code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("code")
  public @Nullable Integer getCode() {
    return code;
  }

  public void setCode(@Nullable Integer code) {
    this.code = code;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentStatus paymentStatus = (PaymentStatus) o;
    return Objects.equals(this.message, paymentStatus.message) &&
        Objects.equals(this.processed, paymentStatus.processed) &&
        Objects.equals(this.code, paymentStatus.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, processed, code);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentStatus {\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    processed: ").append(toIndentedString(processed)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
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

