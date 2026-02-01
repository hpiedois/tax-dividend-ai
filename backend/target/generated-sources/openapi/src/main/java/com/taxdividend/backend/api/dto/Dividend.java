package com.taxdividend.backend.api.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Dividend
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-02-01T10:04:28.893062+01:00[Europe/Zurich]", comments = "Generator version: 7.17.0")
public class Dividend {

  private @Nullable UUID id;

  private @Nullable String securityName;

  private @Nullable String isin;

  private @Nullable BigDecimal grossAmount;

  private @Nullable String currency;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate paymentDate;

  private @Nullable BigDecimal withholdingTax;

  private @Nullable BigDecimal reclaimableAmount;

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

  public Dividend id(@Nullable UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  @Valid 
  @JsonProperty("id")
  public @Nullable UUID getId() {
    return id;
  }

  public void setId(@Nullable UUID id) {
    this.id = id;
  }

  public Dividend securityName(@Nullable String securityName) {
    this.securityName = securityName;
    return this;
  }

  /**
   * Get securityName
   * @return securityName
   */
  
  @JsonProperty("securityName")
  public @Nullable String getSecurityName() {
    return securityName;
  }

  public void setSecurityName(@Nullable String securityName) {
    this.securityName = securityName;
  }

  public Dividend isin(@Nullable String isin) {
    this.isin = isin;
    return this;
  }

  /**
   * Get isin
   * @return isin
   */
  
  @JsonProperty("isin")
  public @Nullable String getIsin() {
    return isin;
  }

  public void setIsin(@Nullable String isin) {
    this.isin = isin;
  }

  public Dividend grossAmount(@Nullable BigDecimal grossAmount) {
    this.grossAmount = grossAmount;
    return this;
  }

  /**
   * Get grossAmount
   * @return grossAmount
   */
  @Valid 
  @JsonProperty("grossAmount")
  public @Nullable BigDecimal getGrossAmount() {
    return grossAmount;
  }

  public void setGrossAmount(@Nullable BigDecimal grossAmount) {
    this.grossAmount = grossAmount;
  }

  public Dividend currency(@Nullable String currency) {
    this.currency = currency;
    return this;
  }

  /**
   * Get currency
   * @return currency
   */
  
  @JsonProperty("currency")
  public @Nullable String getCurrency() {
    return currency;
  }

  public void setCurrency(@Nullable String currency) {
    this.currency = currency;
  }

  public Dividend paymentDate(@Nullable LocalDate paymentDate) {
    this.paymentDate = paymentDate;
    return this;
  }

  /**
   * Get paymentDate
   * @return paymentDate
   */
  @Valid 
  @JsonProperty("paymentDate")
  public @Nullable LocalDate getPaymentDate() {
    return paymentDate;
  }

  public void setPaymentDate(@Nullable LocalDate paymentDate) {
    this.paymentDate = paymentDate;
  }

  public Dividend withholdingTax(@Nullable BigDecimal withholdingTax) {
    this.withholdingTax = withholdingTax;
    return this;
  }

  /**
   * Get withholdingTax
   * @return withholdingTax
   */
  @Valid 
  @JsonProperty("withholdingTax")
  public @Nullable BigDecimal getWithholdingTax() {
    return withholdingTax;
  }

  public void setWithholdingTax(@Nullable BigDecimal withholdingTax) {
    this.withholdingTax = withholdingTax;
  }

  public Dividend reclaimableAmount(@Nullable BigDecimal reclaimableAmount) {
    this.reclaimableAmount = reclaimableAmount;
    return this;
  }

  /**
   * Get reclaimableAmount
   * @return reclaimableAmount
   */
  @Valid 
  @JsonProperty("reclaimableAmount")
  public @Nullable BigDecimal getReclaimableAmount() {
    return reclaimableAmount;
  }

  public void setReclaimableAmount(@Nullable BigDecimal reclaimableAmount) {
    this.reclaimableAmount = reclaimableAmount;
  }

  public Dividend status(@Nullable StatusEnum status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  
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
    Dividend dividend = (Dividend) o;
    return Objects.equals(this.id, dividend.id) &&
        Objects.equals(this.securityName, dividend.securityName) &&
        Objects.equals(this.isin, dividend.isin) &&
        Objects.equals(this.grossAmount, dividend.grossAmount) &&
        Objects.equals(this.currency, dividend.currency) &&
        Objects.equals(this.paymentDate, dividend.paymentDate) &&
        Objects.equals(this.withholdingTax, dividend.withholdingTax) &&
        Objects.equals(this.reclaimableAmount, dividend.reclaimableAmount) &&
        Objects.equals(this.status, dividend.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, securityName, isin, grossAmount, currency, paymentDate, withholdingTax, reclaimableAmount, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Dividend {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    securityName: ").append(toIndentedString(securityName)).append("\n");
    sb.append("    isin: ").append(toIndentedString(isin)).append("\n");
    sb.append("    grossAmount: ").append(toIndentedString(grossAmount)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    paymentDate: ").append(toIndentedString(paymentDate)).append("\n");
    sb.append("    withholdingTax: ").append(toIndentedString(withholdingTax)).append("\n");
    sb.append("    reclaimableAmount: ").append(toIndentedString(reclaimableAmount)).append("\n");
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

