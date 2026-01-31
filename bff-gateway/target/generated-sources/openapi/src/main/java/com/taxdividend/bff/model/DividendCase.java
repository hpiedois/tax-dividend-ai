package com.taxdividend.bff.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * DividendCase
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-31T12:02:59.126017+01:00[Europe/Zurich]", comments = "Generator version: 7.17.0")
public class DividendCase {

  private @Nullable String id;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate date;

  private @Nullable String security;

  private @Nullable BigDecimal grossAmount;

  private @Nullable BigDecimal reclaimedAmount;

  /**
   * Gets or Sets status
   */
  public enum StatusEnum {
    PENDING("pending"),
    
    SUBMITTED("submitted"),
    
    REFUNDED("refunded");

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

  public DividendCase id(@Nullable String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  
  @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public @Nullable String getId() {
    return id;
  }

  public void setId(@Nullable String id) {
    this.id = id;
  }

  public DividendCase date(@Nullable LocalDate date) {
    this.date = date;
    return this;
  }

  /**
   * Get date
   * @return date
   */
  @Valid 
  @Schema(name = "date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("date")
  public @Nullable LocalDate getDate() {
    return date;
  }

  public void setDate(@Nullable LocalDate date) {
    this.date = date;
  }

  public DividendCase security(@Nullable String security) {
    this.security = security;
    return this;
  }

  /**
   * Get security
   * @return security
   */
  
  @Schema(name = "security", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("security")
  public @Nullable String getSecurity() {
    return security;
  }

  public void setSecurity(@Nullable String security) {
    this.security = security;
  }

  public DividendCase grossAmount(@Nullable BigDecimal grossAmount) {
    this.grossAmount = grossAmount;
    return this;
  }

  /**
   * Get grossAmount
   * @return grossAmount
   */
  @Valid 
  @Schema(name = "grossAmount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("grossAmount")
  public @Nullable BigDecimal getGrossAmount() {
    return grossAmount;
  }

  public void setGrossAmount(@Nullable BigDecimal grossAmount) {
    this.grossAmount = grossAmount;
  }

  public DividendCase reclaimedAmount(@Nullable BigDecimal reclaimedAmount) {
    this.reclaimedAmount = reclaimedAmount;
    return this;
  }

  /**
   * Get reclaimedAmount
   * @return reclaimedAmount
   */
  @Valid 
  @Schema(name = "reclaimedAmount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reclaimedAmount")
  public @Nullable BigDecimal getReclaimedAmount() {
    return reclaimedAmount;
  }

  public void setReclaimedAmount(@Nullable BigDecimal reclaimedAmount) {
    this.reclaimedAmount = reclaimedAmount;
  }

  public DividendCase status(@Nullable StatusEnum status) {
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
    DividendCase dividendCase = (DividendCase) o;
    return Objects.equals(this.id, dividendCase.id) &&
        Objects.equals(this.date, dividendCase.date) &&
        Objects.equals(this.security, dividendCase.security) &&
        Objects.equals(this.grossAmount, dividendCase.grossAmount) &&
        Objects.equals(this.reclaimedAmount, dividendCase.reclaimedAmount) &&
        Objects.equals(this.status, dividendCase.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, date, security, grossAmount, reclaimedAmount, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DividendCase {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
    sb.append("    security: ").append(toIndentedString(security)).append("\n");
    sb.append("    grossAmount: ").append(toIndentedString(grossAmount)).append("\n");
    sb.append("    reclaimedAmount: ").append(toIndentedString(reclaimedAmount)).append("\n");
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

