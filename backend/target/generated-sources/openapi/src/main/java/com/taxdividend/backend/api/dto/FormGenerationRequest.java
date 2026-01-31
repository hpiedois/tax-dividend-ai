package com.taxdividend.backend.api.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
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
 * FormGenerationRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-31T11:27:14.708089+01:00[Europe/Zurich]", comments = "Generator version: 7.17.0")
public class FormGenerationRequest {

  private UUID userId;

  private Integer taxYear;

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

  private FormTypeEnum formType;

  @Valid
  private List<UUID> dividendIds = new ArrayList<>();

  private Boolean includeAttestation = false;

  private Boolean includeDividends = false;

  private @Nullable String canton;

  private @Nullable String address;

  private @Nullable String taxId;

  public FormGenerationRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public FormGenerationRequest(UUID userId, Integer taxYear, FormTypeEnum formType) {
    this.userId = userId;
    this.taxYear = taxYear;
    this.formType = formType;
  }

  public FormGenerationRequest userId(UUID userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Get userId
   * @return userId
   */
  @NotNull @Valid 
  @JsonProperty("userId")
  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public FormGenerationRequest taxYear(Integer taxYear) {
    this.taxYear = taxYear;
    return this;
  }

  /**
   * Get taxYear
   * @return taxYear
   */
  @NotNull 
  @JsonProperty("taxYear")
  public Integer getTaxYear() {
    return taxYear;
  }

  public void setTaxYear(Integer taxYear) {
    this.taxYear = taxYear;
  }

  public FormGenerationRequest formType(FormTypeEnum formType) {
    this.formType = formType;
    return this;
  }

  /**
   * Get formType
   * @return formType
   */
  @NotNull 
  @JsonProperty("formType")
  public FormTypeEnum getFormType() {
    return formType;
  }

  public void setFormType(FormTypeEnum formType) {
    this.formType = formType;
  }

  public FormGenerationRequest dividendIds(List<UUID> dividendIds) {
    this.dividendIds = dividendIds;
    return this;
  }

  public FormGenerationRequest addDividendIdsItem(UUID dividendIdsItem) {
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
  @Valid 
  @JsonProperty("dividendIds")
  public List<UUID> getDividendIds() {
    return dividendIds;
  }

  public void setDividendIds(List<UUID> dividendIds) {
    this.dividendIds = dividendIds;
  }

  public FormGenerationRequest includeAttestation(Boolean includeAttestation) {
    this.includeAttestation = includeAttestation;
    return this;
  }

  /**
   * Get includeAttestation
   * @return includeAttestation
   */
  
  @JsonProperty("includeAttestation")
  public Boolean getIncludeAttestation() {
    return includeAttestation;
  }

  public void setIncludeAttestation(Boolean includeAttestation) {
    this.includeAttestation = includeAttestation;
  }

  public FormGenerationRequest includeDividends(Boolean includeDividends) {
    this.includeDividends = includeDividends;
    return this;
  }

  /**
   * Get includeDividends
   * @return includeDividends
   */
  
  @JsonProperty("includeDividends")
  public Boolean getIncludeDividends() {
    return includeDividends;
  }

  public void setIncludeDividends(Boolean includeDividends) {
    this.includeDividends = includeDividends;
  }

  public FormGenerationRequest canton(@Nullable String canton) {
    this.canton = canton;
    return this;
  }

  /**
   * Get canton
   * @return canton
   */
  
  @JsonProperty("canton")
  public @Nullable String getCanton() {
    return canton;
  }

  public void setCanton(@Nullable String canton) {
    this.canton = canton;
  }

  public FormGenerationRequest address(@Nullable String address) {
    this.address = address;
    return this;
  }

  /**
   * Get address
   * @return address
   */
  
  @JsonProperty("address")
  public @Nullable String getAddress() {
    return address;
  }

  public void setAddress(@Nullable String address) {
    this.address = address;
  }

  public FormGenerationRequest taxId(@Nullable String taxId) {
    this.taxId = taxId;
    return this;
  }

  /**
   * Get taxId
   * @return taxId
   */
  
  @JsonProperty("taxId")
  public @Nullable String getTaxId() {
    return taxId;
  }

  public void setTaxId(@Nullable String taxId) {
    this.taxId = taxId;
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
    return Objects.equals(this.userId, formGenerationRequest.userId) &&
        Objects.equals(this.taxYear, formGenerationRequest.taxYear) &&
        Objects.equals(this.formType, formGenerationRequest.formType) &&
        Objects.equals(this.dividendIds, formGenerationRequest.dividendIds) &&
        Objects.equals(this.includeAttestation, formGenerationRequest.includeAttestation) &&
        Objects.equals(this.includeDividends, formGenerationRequest.includeDividends) &&
        Objects.equals(this.canton, formGenerationRequest.canton) &&
        Objects.equals(this.address, formGenerationRequest.address) &&
        Objects.equals(this.taxId, formGenerationRequest.taxId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, taxYear, formType, dividendIds, includeAttestation, includeDividends, canton, address, taxId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FormGenerationRequest {\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    taxYear: ").append(toIndentedString(taxYear)).append("\n");
    sb.append("    formType: ").append(toIndentedString(formType)).append("\n");
    sb.append("    dividendIds: ").append(toIndentedString(dividendIds)).append("\n");
    sb.append("    includeAttestation: ").append(toIndentedString(includeAttestation)).append("\n");
    sb.append("    includeDividends: ").append(toIndentedString(includeDividends)).append("\n");
    sb.append("    canton: ").append(toIndentedString(canton)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    taxId: ").append(toIndentedString(taxId)).append("\n");
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

