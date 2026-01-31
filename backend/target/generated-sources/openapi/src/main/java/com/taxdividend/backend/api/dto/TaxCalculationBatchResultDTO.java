package com.taxdividend.backend.api.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.taxdividend.backend.api.dto.TaxCalculationResultDTO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * TaxCalculationBatchResultDTO
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-31T11:27:14.708089+01:00[Europe/Zurich]", comments = "Generator version: 7.17.0")
public class TaxCalculationBatchResultDTO {

  private @Nullable Integer successCount;

  private @Nullable Integer failureCount;

  private @Nullable BigDecimal totalReclaimableAmount;

  @Valid
  private List<@Valid TaxCalculationResultDTO> results = new ArrayList<>();

  public TaxCalculationBatchResultDTO successCount(@Nullable Integer successCount) {
    this.successCount = successCount;
    return this;
  }

  /**
   * Get successCount
   * @return successCount
   */
  
  @JsonProperty("successCount")
  public @Nullable Integer getSuccessCount() {
    return successCount;
  }

  public void setSuccessCount(@Nullable Integer successCount) {
    this.successCount = successCount;
  }

  public TaxCalculationBatchResultDTO failureCount(@Nullable Integer failureCount) {
    this.failureCount = failureCount;
    return this;
  }

  /**
   * Get failureCount
   * @return failureCount
   */
  
  @JsonProperty("failureCount")
  public @Nullable Integer getFailureCount() {
    return failureCount;
  }

  public void setFailureCount(@Nullable Integer failureCount) {
    this.failureCount = failureCount;
  }

  public TaxCalculationBatchResultDTO totalReclaimableAmount(@Nullable BigDecimal totalReclaimableAmount) {
    this.totalReclaimableAmount = totalReclaimableAmount;
    return this;
  }

  /**
   * Get totalReclaimableAmount
   * @return totalReclaimableAmount
   */
  @Valid 
  @JsonProperty("totalReclaimableAmount")
  public @Nullable BigDecimal getTotalReclaimableAmount() {
    return totalReclaimableAmount;
  }

  public void setTotalReclaimableAmount(@Nullable BigDecimal totalReclaimableAmount) {
    this.totalReclaimableAmount = totalReclaimableAmount;
  }

  public TaxCalculationBatchResultDTO results(List<@Valid TaxCalculationResultDTO> results) {
    this.results = results;
    return this;
  }

  public TaxCalculationBatchResultDTO addResultsItem(TaxCalculationResultDTO resultsItem) {
    if (this.results == null) {
      this.results = new ArrayList<>();
    }
    this.results.add(resultsItem);
    return this;
  }

  /**
   * Get results
   * @return results
   */
  @Valid 
  @JsonProperty("results")
  public List<@Valid TaxCalculationResultDTO> getResults() {
    return results;
  }

  public void setResults(List<@Valid TaxCalculationResultDTO> results) {
    this.results = results;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TaxCalculationBatchResultDTO taxCalculationBatchResultDTO = (TaxCalculationBatchResultDTO) o;
    return Objects.equals(this.successCount, taxCalculationBatchResultDTO.successCount) &&
        Objects.equals(this.failureCount, taxCalculationBatchResultDTO.failureCount) &&
        Objects.equals(this.totalReclaimableAmount, taxCalculationBatchResultDTO.totalReclaimableAmount) &&
        Objects.equals(this.results, taxCalculationBatchResultDTO.results);
  }

  @Override
  public int hashCode() {
    return Objects.hash(successCount, failureCount, totalReclaimableAmount, results);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TaxCalculationBatchResultDTO {\n");
    sb.append("    successCount: ").append(toIndentedString(successCount)).append("\n");
    sb.append("    failureCount: ").append(toIndentedString(failureCount)).append("\n");
    sb.append("    totalReclaimableAmount: ").append(toIndentedString(totalReclaimableAmount)).append("\n");
    sb.append("    results: ").append(toIndentedString(results)).append("\n");
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

