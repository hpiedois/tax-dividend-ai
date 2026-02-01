package com.taxdividend.bff.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UpdateDividendStatusRequest
 */

@JsonTypeName("updateDividendStatus_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-02-01T09:58:25.268465+01:00[Europe/Zurich]", comments = "Generator version: 7.17.0")
public class UpdateDividendStatusRequest {

  @Valid
  private List<UUID> ids = new ArrayList<>();

  /**
   * Gets or Sets status
   */
  public enum StatusEnum {
    OPEN("OPEN"),
    
    SENT("SENT"),
    
    PAID("PAID");

    private final String value;

    StatusEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static StatusEnum fromValue(String value) {
      for (StatusEnum b : StatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable StatusEnum status;

  public UpdateDividendStatusRequest ids(List<UUID> ids) {
    this.ids = ids;
    return this;
  }

  public UpdateDividendStatusRequest addIdsItem(UUID idsItem) {
    if (this.ids == null) {
      this.ids = new ArrayList<>();
    }
    this.ids.add(idsItem);
    return this;
  }

  /**
   * Get ids
   * @return ids
   */
  @Valid 
  @Schema(name = "ids", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ids")
  public List<UUID> getIds() {
    return ids;
  }

  public void setIds(List<UUID> ids) {
    this.ids = ids;
  }

  public UpdateDividendStatusRequest status(@Nullable StatusEnum status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  
  @Schema(name = "status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public @Nullable StatusEnum getStatus() {
    return status;
  }

  public void setStatus(@Nullable StatusEnum status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateDividendStatusRequest updateDividendStatusRequest = (UpdateDividendStatusRequest) o;
    return Objects.equals(this.ids, updateDividendStatusRequest.ids) &&
        Objects.equals(this.status, updateDividendStatusRequest.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ids, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateDividendStatusRequest {\n");
    sb.append("    ids: ").append(toIndentedString(ids)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
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

