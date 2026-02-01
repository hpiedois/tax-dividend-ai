package com.taxdividend.backend.api.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
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
 * TaxRule
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-02-01T10:04:28.893062+01:00[Europe/Zurich]", comments = "Generator version: 7.17.0")
public class TaxRule {

  private @Nullable UUID id;

  private @Nullable String sourceCountry;

  private @Nullable String residenceCountry;

  private @Nullable String securityType;

  private @Nullable BigDecimal standardWithholdingRate;

  private @Nullable BigDecimal treatyRate;

  private @Nullable Boolean reliefAtSourceAvailable;

  private @Nullable Boolean refundProcedureAvailable;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate effectiveFrom;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate effectiveTo;

  private @Nullable String notes;

  public TaxRule id(@Nullable UUID id) {
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

  public TaxRule sourceCountry(@Nullable String sourceCountry) {
    this.sourceCountry = sourceCountry;
    return this;
  }

  /**
   * Get sourceCountry
   * @return sourceCountry
   */
  
  @JsonProperty("sourceCountry")
  public @Nullable String getSourceCountry() {
    return sourceCountry;
  }

  public void setSourceCountry(@Nullable String sourceCountry) {
    this.sourceCountry = sourceCountry;
  }

  public TaxRule residenceCountry(@Nullable String residenceCountry) {
    this.residenceCountry = residenceCountry;
    return this;
  }

  /**
   * Get residenceCountry
   * @return residenceCountry
   */
  
  @JsonProperty("residenceCountry")
  public @Nullable String getResidenceCountry() {
    return residenceCountry;
  }

  public void setResidenceCountry(@Nullable String residenceCountry) {
    this.residenceCountry = residenceCountry;
  }

  public TaxRule securityType(@Nullable String securityType) {
    this.securityType = securityType;
    return this;
  }

  /**
   * Get securityType
   * @return securityType
   */
  
  @JsonProperty("securityType")
  public @Nullable String getSecurityType() {
    return securityType;
  }

  public void setSecurityType(@Nullable String securityType) {
    this.securityType = securityType;
  }

  public TaxRule standardWithholdingRate(@Nullable BigDecimal standardWithholdingRate) {
    this.standardWithholdingRate = standardWithholdingRate;
    return this;
  }

  /**
   * Get standardWithholdingRate
   * @return standardWithholdingRate
   */
  @Valid 
  @JsonProperty("standardWithholdingRate")
  public @Nullable BigDecimal getStandardWithholdingRate() {
    return standardWithholdingRate;
  }

  public void setStandardWithholdingRate(@Nullable BigDecimal standardWithholdingRate) {
    this.standardWithholdingRate = standardWithholdingRate;
  }

  public TaxRule treatyRate(@Nullable BigDecimal treatyRate) {
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

  public TaxRule reliefAtSourceAvailable(@Nullable Boolean reliefAtSourceAvailable) {
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

  public TaxRule refundProcedureAvailable(@Nullable Boolean refundProcedureAvailable) {
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

  public TaxRule effectiveFrom(@Nullable LocalDate effectiveFrom) {
    this.effectiveFrom = effectiveFrom;
    return this;
  }

  /**
   * Get effectiveFrom
   * @return effectiveFrom
   */
  @Valid 
  @JsonProperty("effectiveFrom")
  public @Nullable LocalDate getEffectiveFrom() {
    return effectiveFrom;
  }

  public void setEffectiveFrom(@Nullable LocalDate effectiveFrom) {
    this.effectiveFrom = effectiveFrom;
  }

  public TaxRule effectiveTo(@Nullable LocalDate effectiveTo) {
    this.effectiveTo = effectiveTo;
    return this;
  }

  /**
   * Get effectiveTo
   * @return effectiveTo
   */
  @Valid 
  @JsonProperty("effectiveTo")
  public @Nullable LocalDate getEffectiveTo() {
    return effectiveTo;
  }

  public void setEffectiveTo(@Nullable LocalDate effectiveTo) {
    this.effectiveTo = effectiveTo;
  }

  public TaxRule notes(@Nullable String notes) {
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
    TaxRule taxRule = (TaxRule) o;
    return Objects.equals(this.id, taxRule.id) &&
        Objects.equals(this.sourceCountry, taxRule.sourceCountry) &&
        Objects.equals(this.residenceCountry, taxRule.residenceCountry) &&
        Objects.equals(this.securityType, taxRule.securityType) &&
        Objects.equals(this.standardWithholdingRate, taxRule.standardWithholdingRate) &&
        Objects.equals(this.treatyRate, taxRule.treatyRate) &&
        Objects.equals(this.reliefAtSourceAvailable, taxRule.reliefAtSourceAvailable) &&
        Objects.equals(this.refundProcedureAvailable, taxRule.refundProcedureAvailable) &&
        Objects.equals(this.effectiveFrom, taxRule.effectiveFrom) &&
        Objects.equals(this.effectiveTo, taxRule.effectiveTo) &&
        Objects.equals(this.notes, taxRule.notes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, sourceCountry, residenceCountry, securityType, standardWithholdingRate, treatyRate, reliefAtSourceAvailable, refundProcedureAvailable, effectiveFrom, effectiveTo, notes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TaxRule {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    sourceCountry: ").append(toIndentedString(sourceCountry)).append("\n");
    sb.append("    residenceCountry: ").append(toIndentedString(residenceCountry)).append("\n");
    sb.append("    securityType: ").append(toIndentedString(securityType)).append("\n");
    sb.append("    standardWithholdingRate: ").append(toIndentedString(standardWithholdingRate)).append("\n");
    sb.append("    treatyRate: ").append(toIndentedString(treatyRate)).append("\n");
    sb.append("    reliefAtSourceAvailable: ").append(toIndentedString(reliefAtSourceAvailable)).append("\n");
    sb.append("    refundProcedureAvailable: ").append(toIndentedString(refundProcedureAvailable)).append("\n");
    sb.append("    effectiveFrom: ").append(toIndentedString(effectiveFrom)).append("\n");
    sb.append("    effectiveTo: ").append(toIndentedString(effectiveTo)).append("\n");
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

