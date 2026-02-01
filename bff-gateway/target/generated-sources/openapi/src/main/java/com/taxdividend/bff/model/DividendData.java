package com.taxdividend.bff.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
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
 * DividendData
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-02-01T09:58:25.268465+01:00[Europe/Zurich]", comments = "Generator version: 7.17.0")
public class DividendData {

  private String securityName;

  private @Nullable String isin;

  private BigDecimal grossAmount;

  private String currency;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate paymentDate;

  private @Nullable BigDecimal withholdingTax;

  private @Nullable BigDecimal reclaimableAmount;

  private @Nullable String sourceCountry;

  private @Nullable String appliedRateType;

  public DividendData() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public DividendData(String securityName, BigDecimal grossAmount, String currency) {
    this.securityName = securityName;
    this.grossAmount = grossAmount;
    this.currency = currency;
  }

  public DividendData securityName(String securityName) {
    this.securityName = securityName;
    return this;
  }

  /**
   * Get securityName
   * @return securityName
   */
  @NotNull 
  @Schema(name = "securityName", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("securityName")
  public String getSecurityName() {
    return securityName;
  }

  public void setSecurityName(String securityName) {
    this.securityName = securityName;
  }

  public DividendData isin(@Nullable String isin) {
    this.isin = isin;
    return this;
  }

  /**
   * Get isin
   * @return isin
   */
  
  @Schema(name = "isin", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isin")
  public @Nullable String getIsin() {
    return isin;
  }

  public void setIsin(@Nullable String isin) {
    this.isin = isin;
  }

  public DividendData grossAmount(BigDecimal grossAmount) {
    this.grossAmount = grossAmount;
    return this;
  }

  /**
   * Get grossAmount
   * @return grossAmount
   */
  @NotNull @Valid 
  @Schema(name = "grossAmount", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("grossAmount")
  public BigDecimal getGrossAmount() {
    return grossAmount;
  }

  public void setGrossAmount(BigDecimal grossAmount) {
    this.grossAmount = grossAmount;
  }

  public DividendData currency(String currency) {
    this.currency = currency;
    return this;
  }

  /**
   * Get currency
   * @return currency
   */
  @NotNull 
  @Schema(name = "currency", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("currency")
  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public DividendData paymentDate(@Nullable LocalDate paymentDate) {
    this.paymentDate = paymentDate;
    return this;
  }

  /**
   * Get paymentDate
   * @return paymentDate
   */
  @Valid 
  @Schema(name = "paymentDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentDate")
  public @Nullable LocalDate getPaymentDate() {
    return paymentDate;
  }

  public void setPaymentDate(@Nullable LocalDate paymentDate) {
    this.paymentDate = paymentDate;
  }

  public DividendData withholdingTax(@Nullable BigDecimal withholdingTax) {
    this.withholdingTax = withholdingTax;
    return this;
  }

  /**
   * Get withholdingTax
   * @return withholdingTax
   */
  @Valid 
  @Schema(name = "withholdingTax", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("withholdingTax")
  public @Nullable BigDecimal getWithholdingTax() {
    return withholdingTax;
  }

  public void setWithholdingTax(@Nullable BigDecimal withholdingTax) {
    this.withholdingTax = withholdingTax;
  }

  public DividendData reclaimableAmount(@Nullable BigDecimal reclaimableAmount) {
    this.reclaimableAmount = reclaimableAmount;
    return this;
  }

  /**
   * Get reclaimableAmount
   * @return reclaimableAmount
   */
  @Valid 
  @Schema(name = "reclaimableAmount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reclaimableAmount")
  public @Nullable BigDecimal getReclaimableAmount() {
    return reclaimableAmount;
  }

  public void setReclaimableAmount(@Nullable BigDecimal reclaimableAmount) {
    this.reclaimableAmount = reclaimableAmount;
  }

  public DividendData sourceCountry(@Nullable String sourceCountry) {
    this.sourceCountry = sourceCountry;
    return this;
  }

  /**
   * Get sourceCountry
   * @return sourceCountry
   */
  
  @Schema(name = "sourceCountry", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sourceCountry")
  public @Nullable String getSourceCountry() {
    return sourceCountry;
  }

  public void setSourceCountry(@Nullable String sourceCountry) {
    this.sourceCountry = sourceCountry;
  }

  public DividendData appliedRateType(@Nullable String appliedRateType) {
    this.appliedRateType = appliedRateType;
    return this;
  }

  /**
   * Tracks which rate was applied (e.g. PFU, PROGRESSIVE, TREATY)
   * @return appliedRateType
   */
  
  @Schema(name = "appliedRateType", description = "Tracks which rate was applied (e.g. PFU, PROGRESSIVE, TREATY)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("appliedRateType")
  public @Nullable String getAppliedRateType() {
    return appliedRateType;
  }

  public void setAppliedRateType(@Nullable String appliedRateType) {
    this.appliedRateType = appliedRateType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DividendData dividendData = (DividendData) o;
    return Objects.equals(this.securityName, dividendData.securityName) &&
        Objects.equals(this.isin, dividendData.isin) &&
        Objects.equals(this.grossAmount, dividendData.grossAmount) &&
        Objects.equals(this.currency, dividendData.currency) &&
        Objects.equals(this.paymentDate, dividendData.paymentDate) &&
        Objects.equals(this.withholdingTax, dividendData.withholdingTax) &&
        Objects.equals(this.reclaimableAmount, dividendData.reclaimableAmount) &&
        Objects.equals(this.sourceCountry, dividendData.sourceCountry) &&
        Objects.equals(this.appliedRateType, dividendData.appliedRateType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(securityName, isin, grossAmount, currency, paymentDate, withholdingTax, reclaimableAmount, sourceCountry, appliedRateType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DividendData {\n");
    sb.append("    securityName: ").append(toIndentedString(securityName)).append("\n");
    sb.append("    isin: ").append(toIndentedString(isin)).append("\n");
    sb.append("    grossAmount: ").append(toIndentedString(grossAmount)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    paymentDate: ").append(toIndentedString(paymentDate)).append("\n");
    sb.append("    withholdingTax: ").append(toIndentedString(withholdingTax)).append("\n");
    sb.append("    reclaimableAmount: ").append(toIndentedString(reclaimableAmount)).append("\n");
    sb.append("    sourceCountry: ").append(toIndentedString(sourceCountry)).append("\n");
    sb.append("    appliedRateType: ").append(toIndentedString(appliedRateType)).append("\n");
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

