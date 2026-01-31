package com.taxdividend.backend.api.dto;

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


import java.util.*;
import jakarta.annotation.Generated;

/**
 * TreatyRateResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-31T11:27:14.708089+01:00[Europe/Zurich]", comments = "Generator version: 7.17.0")
public class TreatyRateResponse {

  private @Nullable BigDecimal standardRate;

  private @Nullable BigDecimal treatyRate;

  private @Nullable Boolean reliefAtSourceAvailable;

  private @Nullable Boolean refundProcedureAvailable;

  private @Nullable String notes;

  public TreatyRateResponse standardRate(@Nullable BigDecimal standardRate) {
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

  public TreatyRateResponse treatyRate(@Nullable BigDecimal treatyRate) {
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

  public TreatyRateResponse reliefAtSourceAvailable(@Nullable Boolean reliefAtSourceAvailable) {
    this.reliefAtSourceAvailable = reliefAtSourceAvailable;
    return this;
  }

  /**
   * Get reliefAtSourceAvailable
   * @return reliefAtSourceAvailable
   */
  
  @JsonProperty("reliefAtSourceAvailable")
  public @Nullable Boolean getReliefAtSourceAvailable() {
    return reliefAtSourceAvailable;
  }

  public void setReliefAtSourceAvailable(@Nullable Boolean reliefAtSourceAvailable) {
    this.reliefAtSourceAvailable = reliefAtSourceAvailable;
  }

  public TreatyRateResponse refundProcedureAvailable(@Nullable Boolean refundProcedureAvailable) {
    this.refundProcedureAvailable = refundProcedureAvailable;
    return this;
  }

  /**
   * Get refundProcedureAvailable
   * @return refundProcedureAvailable
   */
  
  @JsonProperty("refundProcedureAvailable")
  public @Nullable Boolean getRefundProcedureAvailable() {
    return refundProcedureAvailable;
  }

  public void setRefundProcedureAvailable(@Nullable Boolean refundProcedureAvailable) {
    this.refundProcedureAvailable = refundProcedureAvailable;
  }

  public TreatyRateResponse notes(@Nullable String notes) {
    this.notes = notes;
    return this;
  }

  /**
   * Get notes
   * @return notes
   */
  
  @JsonProperty("notes")
  public @Nullable String getNotes() {
    return notes;
  }

  public void setNotes(@Nullable String notes) {
    this.notes = notes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TreatyRateResponse treatyRateResponse = (TreatyRateResponse) o;
    return Objects.equals(this.standardRate, treatyRateResponse.standardRate) &&
        Objects.equals(this.treatyRate, treatyRateResponse.treatyRate) &&
        Objects.equals(this.reliefAtSourceAvailable, treatyRateResponse.reliefAtSourceAvailable) &&
        Objects.equals(this.refundProcedureAvailable, treatyRateResponse.refundProcedureAvailable) &&
        Objects.equals(this.notes, treatyRateResponse.notes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(standardRate, treatyRate, reliefAtSourceAvailable, refundProcedureAvailable, notes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TreatyRateResponse {\n");
    sb.append("    standardRate: ").append(toIndentedString(standardRate)).append("\n");
    sb.append("    treatyRate: ").append(toIndentedString(treatyRate)).append("\n");
    sb.append("    reliefAtSourceAvailable: ").append(toIndentedString(reliefAtSourceAvailable)).append("\n");
    sb.append("    refundProcedureAvailable: ").append(toIndentedString(refundProcedureAvailable)).append("\n");
    sb.append("    notes: ").append(toIndentedString(notes)).append("\n");
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

