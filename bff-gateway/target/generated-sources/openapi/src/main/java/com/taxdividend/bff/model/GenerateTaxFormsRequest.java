package com.taxdividend.bff.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * GenerateTaxFormsRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-02-01T09:58:25.268465+01:00[Europe/Zurich]", comments = "Generator version: 7.17.0")
public class GenerateTaxFormsRequest {

  @Valid
  private List<String> dividendIds = new ArrayList<>();

  private @Nullable Integer taxYear;

  /**
   * Gets or Sets formType
   */
  public enum FormTypeEnum {
    _5000("5000"),
    
    _5001("5001"),
    
    BUNDLE("BUNDLE");

    private final String value;

    FormTypeEnum(String value) {
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
    public static FormTypeEnum fromValue(String value) {
      for (FormTypeEnum b : FormTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable FormTypeEnum formType;

  private Boolean includeAttestation = false;

  private Boolean includeDividends = false;

  public GenerateTaxFormsRequest dividendIds(List<String> dividendIds) {
    this.dividendIds = dividendIds;
    return this;
  }

  public GenerateTaxFormsRequest addDividendIdsItem(String dividendIdsItem) {
    if (this.dividendIds == null) {
      this.dividendIds = new ArrayList<>();
    }
    this.dividendIds.add(dividendIdsItem);
    return this;
  }

  /**
   * Get dividendIds
   * @return dividendIds
   */
  
  @Schema(name = "dividendIds", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dividendIds")
  public List<String> getDividendIds() {
    return dividendIds;
  }

  public void setDividendIds(List<String> dividendIds) {
    this.dividendIds = dividendIds;
  }

  public GenerateTaxFormsRequest taxYear(@Nullable Integer taxYear) {
    this.taxYear = taxYear;
    return this;
  }

  /**
   * Get taxYear
   * @return taxYear
   */
  
  @Schema(name = "taxYear", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("taxYear")
  public @Nullable Integer getTaxYear() {
    return taxYear;
  }

  public void setTaxYear(@Nullable Integer taxYear) {
    this.taxYear = taxYear;
  }

  public GenerateTaxFormsRequest formType(@Nullable FormTypeEnum formType) {
    this.formType = formType;
    return this;
  }

  /**
   * Get formType
   * @return formType
   */
  
  @Schema(name = "formType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("formType")
  public @Nullable FormTypeEnum getFormType() {
    return formType;
  }

  public void setFormType(@Nullable FormTypeEnum formType) {
    this.formType = formType;
  }

  public GenerateTaxFormsRequest includeAttestation(Boolean includeAttestation) {
    this.includeAttestation = includeAttestation;
    return this;
  }

  /**
   * Get includeAttestation
   * @return includeAttestation
   */
  
  @Schema(name = "includeAttestation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("includeAttestation")
  public Boolean getIncludeAttestation() {
    return includeAttestation;
  }

  public void setIncludeAttestation(Boolean includeAttestation) {
    this.includeAttestation = includeAttestation;
  }

  public GenerateTaxFormsRequest includeDividends(Boolean includeDividends) {
    this.includeDividends = includeDividends;
    return this;
  }

  /**
   * Get includeDividends
   * @return includeDividends
   */
  
  @Schema(name = "includeDividends", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("includeDividends")
  public Boolean getIncludeDividends() {
    return includeDividends;
  }

  public void setIncludeDividends(Boolean includeDividends) {
    this.includeDividends = includeDividends;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GenerateTaxFormsRequest generateTaxFormsRequest = (GenerateTaxFormsRequest) o;
    return Objects.equals(this.dividendIds, generateTaxFormsRequest.dividendIds) &&
        Objects.equals(this.taxYear, generateTaxFormsRequest.taxYear) &&
        Objects.equals(this.formType, generateTaxFormsRequest.formType) &&
        Objects.equals(this.includeAttestation, generateTaxFormsRequest.includeAttestation) &&
        Objects.equals(this.includeDividends, generateTaxFormsRequest.includeDividends);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dividendIds, taxYear, formType, includeAttestation, includeDividends);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GenerateTaxFormsRequest {\n");
    sb.append("    dividendIds: ").append(toIndentedString(dividendIds)).append("\n");
    sb.append("    taxYear: ").append(toIndentedString(taxYear)).append("\n");
    sb.append("    formType: ").append(toIndentedString(formType)).append("\n");
    sb.append("    includeAttestation: ").append(toIndentedString(includeAttestation)).append("\n");
    sb.append("    includeDividends: ").append(toIndentedString(includeDividends)).append("\n");
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

