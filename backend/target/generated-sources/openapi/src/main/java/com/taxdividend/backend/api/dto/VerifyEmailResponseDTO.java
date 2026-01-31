package com.taxdividend.backend.api.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * VerifyEmailResponseDTO
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-31T11:27:14.708089+01:00[Europe/Zurich]", comments = "Generator version: 7.17.0")
public class VerifyEmailResponseDTO {

  private @Nullable Boolean verified;

  private @Nullable String message;

  public VerifyEmailResponseDTO verified(@Nullable Boolean verified) {
    this.verified = verified;
    return this;
  }

  /**
   * Get verified
   * @return verified
   */
  
  @JsonProperty("verified")
  public @Nullable Boolean getVerified() {
    return verified;
  }

  public void setVerified(@Nullable Boolean verified) {
    this.verified = verified;
  }

  public VerifyEmailResponseDTO message(@Nullable String message) {
    this.message = message;
    return this;
  }

  /**
   * Get message
   * @return message
   */
  
  @JsonProperty("message")
  public @Nullable String getMessage() {
    return message;
  }

  public void setMessage(@Nullable String message) {
    this.message = message;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VerifyEmailResponseDTO verifyEmailResponseDTO = (VerifyEmailResponseDTO) o;
    return Objects.equals(this.verified, verifyEmailResponseDTO.verified) &&
        Objects.equals(this.message, verifyEmailResponseDTO.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(verified, message);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VerifyEmailResponseDTO {\n");
    sb.append("    verified: ").append(toIndentedString(verified)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

