package com.taxdividend.backend.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * FormGenerationRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-26T22:31:55.344143+01:00[Europe/Zurich]")
public class FormGenerationRequest {

  private String taxpayerName;

  private String taxId;

  private Integer taxYear;

  private String country;

  @Valid
  private List<Object> dividends;

  public FormGenerationRequest taxpayerName(String taxpayerName) {
    this.taxpayerName = taxpayerName;
    return this;
  }

  /**
   * Get taxpayerName
   * @return taxpayerName
  */
  
  @Schema(name = "taxpayerName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("taxpayerName")
  public String getTaxpayerName() {
    return taxpayerName;
  }

  public void setTaxpayerName(String taxpayerName) {
    this.taxpayerName = taxpayerName;
  }

  public FormGenerationRequest taxId(String taxId) {
    this.taxId = taxId;
    return this;
  }

  /**
   * Get taxId
   * @return taxId
  */
  
  @Schema(name = "taxId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("taxId")
  public String getTaxId() {
    return taxId;
  }

  public void setTaxId(String taxId) {
    this.taxId = taxId;
  }

  public FormGenerationRequest taxYear(Integer taxYear) {
    this.taxYear = taxYear;
    return this;
  }

  /**
   * Get taxYear
   * @return taxYear
  */
  
  @Schema(name = "taxYear", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("taxYear")
  public Integer getTaxYear() {
    return taxYear;
  }

  public void setTaxYear(Integer taxYear) {
    this.taxYear = taxYear;
  }

  public FormGenerationRequest country(String country) {
    this.country = country;
    return this;
  }

  /**
   * Get country
   * @return country
  */
  
  @Schema(name = "country", example = "FR", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("country")
  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public FormGenerationRequest dividends(List<Object> dividends) {
    this.dividends = dividends;
    return this;
  }

  public FormGenerationRequest addDividendsItem(Object dividendsItem) {
    if (this.dividends == null) {
      this.dividends = new ArrayList<>();
    }
    this.dividends.add(dividendsItem);
    return this;
  }

  /**
   * Get dividends
   * @return dividends
  */
  
  @Schema(name = "dividends", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dividends")
  public List<Object> getDividends() {
    return dividends;
  }

  public void setDividends(List<Object> dividends) {
    this.dividends = dividends;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FormGenerationRequest formGenerationRequest = (FormGenerationRequest) o;
    return Objects.equals(this.taxpayerName, formGenerationRequest.taxpayerName) &&
        Objects.equals(this.taxId, formGenerationRequest.taxId) &&
        Objects.equals(this.taxYear, formGenerationRequest.taxYear) &&
        Objects.equals(this.country, formGenerationRequest.country) &&
        Objects.equals(this.dividends, formGenerationRequest.dividends);
  }

  @Override
  public int hashCode() {
    return Objects.hash(taxpayerName, taxId, taxYear, country, dividends);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FormGenerationRequest {\n");
    sb.append("    taxpayerName: ").append(toIndentedString(taxpayerName)).append("\n");
    sb.append("    taxId: ").append(toIndentedString(taxId)).append("\n");
    sb.append("    taxYear: ").append(toIndentedString(taxYear)).append("\n");
    sb.append("    country: ").append(toIndentedString(country)).append("\n");
    sb.append("    dividends: ").append(toIndentedString(dividends)).append("\n");
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

