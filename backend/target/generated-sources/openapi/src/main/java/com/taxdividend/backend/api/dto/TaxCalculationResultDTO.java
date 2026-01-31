package com.taxdividend.backend.api.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * TaxCalculationResultDTO
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-31T11:27:14.708089+01:00[Europe/Zurich]", comments = "Generator version: 7.17.0")
public class TaxCalculationResultDTO {

  private @Nullable UUID dividendId;

  private @Nullable Boolean success;

  private @Nullable BigDecimal reclaimableAmount;

  private @Nullable BigDecimal standardRate;

  private @Nullable BigDecimal treatyRate;

  private @Nullable BigDecimal withheldAmount;

  @Valid
  private List<String> errors = new ArrayList<>();

  public TaxCalculationResultDTO dividendId(@Nullable UUID dividendId) {
    this.dividendId = dividendId;
    return this;
  }

  /**
   * Get dividendId
   * @return dividendId
   */
  @Valid 
  @JsonProperty("dividendId")
  public @Nullable UUID getDividendId() {
    return dividendId;
  }

  public void setDividendId(@Nullable UUID dividendId) {
    this.dividendId = dividendId;
  }

  public TaxCalculationResultDTO success(@Nullable Boolean success) {
    this.success = success;
    return this;
  }

  /**
   * Get success
   * @return success
   */
  
  @JsonProperty("success")
  public @Nullable Boolean getSuccess() {
    return success;
  }

  public void setSuccess(@Nullable Boolean success) {
    this.success = success;
  }

  public TaxCalculationResultDTO reclaimableAmount(@Nullable BigDecimal reclaimableAmount) {
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

  public TaxCalculationResultDTO standardRate(@Nullable BigDecimal standardRate) {
    this.standardRate = standardRate;
    return this;
  }

  /**
   * Get standardRate
   * @return standardRate
   */
  @Valid 
  @JsonProperty("standardRate")
  public @Nullable BigDecimal getStandardRate() {
    return standardRate;
  }

  public void setStandardRate(@Nullable BigDecimal standardRate) {
    this.standardRate = standardRate;
  }

  public TaxCalculationResultDTO treatyRate(@Nullable BigDecimal treatyRate) {
    this.treatyRate = treatyRate;
    return this;
  }

  /**
   * Get treatyRate
   * @return treatyRate
   */
  @Valid 
  @JsonProperty("treatyRate")
  public @Nullable BigDecimal getTreatyRate() {
    return treatyRate;
  }

  public void setTreatyRate(@Nullable BigDecimal treatyRate) {
    this.treatyRate = treatyRate;
  }

  public TaxCalculationResultDTO withheldAmount(@Nullable BigDecimal withheldAmount) {
    this.withheldAmount = withheldAmount;
    return this;
  }

  /**
   * Get withheldAmount
   * @return withheldAmount
   */
  @Valid 
  @JsonProperty("withheldAmount")
  public @Nullable BigDecimal getWithheldAmount() {
    return withheldAmount;
  }

  public void setWithheldAmount(@Nullable BigDecimal withheldAmount) {
    this.withheldAmount = withheldAmount;
  }

  public TaxCalculationResultDTO errors(List<String> errors) {
    this.errors = errors;
    return this;
  }

  public TaxCalculationResultDTO addErrorsItem(String errorsItem) {
    if (this.errors == null) {
      this.errors = new ArrayList<>();
    }
    this.errors.add(errorsItem);
    return this;
  }

  /**
   * Get errors
   * @return errors
   */
  
  @JsonProperty("errors")
  public List<String> getErrors() {
    return errors;
  }

  public void setErrors(List<String> errors) {
    this.errors = errors;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TaxCalculationResultDTO taxCalculationResultDTO = (TaxCalculationResultDTO) o;
    return Objects.equals(this.dividendId, taxCalculationResultDTO.dividendId) &&
        Objects.equals(this.success, taxCalculationResultDTO.success) &&
        Objects.equals(this.reclaimableAmount, taxCalculationResultDTO.reclaimableAmount) &&
        Objects.equals(this.standardRate, taxCalculationResultDTO.standardRate) &&
        Objects.equals(this.treatyRate, taxCalculationResultDTO.treatyRate) &&
        Objects.equals(this.withheldAmount, taxCalculationResultDTO.withheldAmount) &&
        Objects.equals(this.errors, taxCalculationResultDTO.errors);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dividendId, success, reclaimableAmount, standardRate, treatyRate, withheldAmount, errors);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TaxCalculationResultDTO {\n");
    sb.append("    dividendId: ").append(toIndentedString(dividendId)).append("\n");
    sb.append("    success: ").append(toIndentedString(success)).append("\n");
    sb.append("    reclaimableAmount: ").append(toIndentedString(reclaimableAmount)).append("\n");
    sb.append("    standardRate: ").append(toIndentedString(standardRate)).append("\n");
    sb.append("    treatyRate: ").append(toIndentedString(treatyRate)).append("\n");
    sb.append("    withheldAmount: ").append(toIndentedString(withheldAmount)).append("\n");
    sb.append("    errors: ").append(toIndentedString(errors)).append("\n");
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

