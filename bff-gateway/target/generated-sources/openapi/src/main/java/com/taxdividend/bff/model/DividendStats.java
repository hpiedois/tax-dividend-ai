package com.taxdividend.bff.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * DividendStats
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-31T12:02:59.126017+01:00[Europe/Zurich]", comments = "Generator version: 7.17.0")
public class DividendStats {

  private @Nullable BigDecimal totalReclaimed;

  private @Nullable BigDecimal pendingAmount;

  private @Nullable Integer casesCount;

  public DividendStats totalReclaimed(@Nullable BigDecimal totalReclaimed) {
    this.totalReclaimed = totalReclaimed;
    return this;
  }

  /**
   * Get totalReclaimed
   * @return totalReclaimed
   */
  @Valid 
  @Schema(name = "totalReclaimed", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalReclaimed")
  public @Nullable BigDecimal getTotalReclaimed() {
    return totalReclaimed;
  }

  public void setTotalReclaimed(@Nullable BigDecimal totalReclaimed) {
    this.totalReclaimed = totalReclaimed;
  }

  public DividendStats pendingAmount(@Nullable BigDecimal pendingAmount) {
    this.pendingAmount = pendingAmount;
    return this;
  }

  /**
   * Get pendingAmount
   * @return pendingAmount
   */
  @Valid 
  @Schema(name = "pendingAmount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pendingAmount")
  public @Nullable BigDecimal getPendingAmount() {
    return pendingAmount;
  }

  public void setPendingAmount(@Nullable BigDecimal pendingAmount) {
    this.pendingAmount = pendingAmount;
  }

  public DividendStats casesCount(@Nullable Integer casesCount) {
    this.casesCount = casesCount;
    return this;
  }

  /**
   * Get casesCount
   * @return casesCount
   */
  
  @Schema(name = "casesCount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("casesCount")
  public @Nullable Integer getCasesCount() {
    return casesCount;
  }

  public void setCasesCount(@Nullable Integer casesCount) {
    this.casesCount = casesCount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DividendStats dividendStats = (DividendStats) o;
    return Objects.equals(this.totalReclaimed, dividendStats.totalReclaimed) &&
        Objects.equals(this.pendingAmount, dividendStats.pendingAmount) &&
        Objects.equals(this.casesCount, dividendStats.casesCount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(totalReclaimed, pendingAmount, casesCount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DividendStats {\n");
    sb.append("    totalReclaimed: ").append(toIndentedString(totalReclaimed)).append("\n");
    sb.append("    pendingAmount: ").append(toIndentedString(pendingAmount)).append("\n");
    sb.append("    casesCount: ").append(toIndentedString(casesCount)).append("\n");
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

