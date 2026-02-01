package com.taxdividend.bff.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import java.util.UUID;
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
 * GeneratedForm
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-02-01T09:58:25.268465+01:00[Europe/Zurich]", comments = "Generator version: 7.17.0")
public class GeneratedForm {

  private @Nullable UUID id;

  private @Nullable String formType;

  private @Nullable Integer taxYear;

  private @Nullable String fileName;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime generatedAt;

  public GeneratedForm id(@Nullable UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  @Valid 
  @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public @Nullable UUID getId() {
    return id;
  }

  public void setId(@Nullable UUID id) {
    this.id = id;
  }

  public GeneratedForm formType(@Nullable String formType) {
    this.formType = formType;
    return this;
  }

  /**
   * Get formType
   * @return formType
   */
  
  @Schema(name = "formType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("formType")
  public @Nullable String getFormType() {
    return formType;
  }

  public void setFormType(@Nullable String formType) {
    this.formType = formType;
  }

  public GeneratedForm taxYear(@Nullable Integer taxYear) {
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

  public GeneratedForm fileName(@Nullable String fileName) {
    this.fileName = fileName;
    return this;
  }

  /**
   * Get fileName
   * @return fileName
   */
  
  @Schema(name = "fileName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("fileName")
  public @Nullable String getFileName() {
    return fileName;
  }

  public void setFileName(@Nullable String fileName) {
    this.fileName = fileName;
  }

  public GeneratedForm generatedAt(@Nullable OffsetDateTime generatedAt) {
    this.generatedAt = generatedAt;
    return this;
  }

  /**
   * Get generatedAt
   * @return generatedAt
   */
  @Valid 
  @Schema(name = "generatedAt", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("generatedAt")
  public @Nullable OffsetDateTime getGeneratedAt() {
    return generatedAt;
  }

  public void setGeneratedAt(@Nullable OffsetDateTime generatedAt) {
    this.generatedAt = generatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GeneratedForm generatedForm = (GeneratedForm) o;
    return Objects.equals(this.id, generatedForm.id) &&
        Objects.equals(this.formType, generatedForm.formType) &&
        Objects.equals(this.taxYear, generatedForm.taxYear) &&
        Objects.equals(this.fileName, generatedForm.fileName) &&
        Objects.equals(this.generatedAt, generatedForm.generatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, formType, taxYear, fileName, generatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GeneratedForm {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    formType: ").append(toIndentedString(formType)).append("\n");
    sb.append("    taxYear: ").append(toIndentedString(taxYear)).append("\n");
    sb.append("    fileName: ").append(toIndentedString(fileName)).append("\n");
    sb.append("    generatedAt: ").append(toIndentedString(generatedAt)).append("\n");
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

