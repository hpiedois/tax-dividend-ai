package com.taxdividend.backend.api.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
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
 * GenerateFormResultDTO
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-02-01T10:04:28.893062+01:00[Europe/Zurich]", comments = "Generator version: 7.17.0")
public class GenerateFormResultDTO {

  private @Nullable Boolean success;

  private @Nullable UUID formId;

  private @Nullable String formType;

  private @Nullable String downloadUrl;

  private @Nullable String fileName;

  private @Nullable Integer dividendCount;

  @Valid
  private List<String> errors = new ArrayList<>();

  public GenerateFormResultDTO success(@Nullable Boolean success) {
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

  public GenerateFormResultDTO formId(@Nullable UUID formId) {
    this.formId = formId;
    return this;
  }

  /**
   * Get formId
   * @return formId
   */
  @Valid 
  @JsonProperty("formId")
  public @Nullable UUID getFormId() {
    return formId;
  }

  public void setFormId(@Nullable UUID formId) {
    this.formId = formId;
  }

  public GenerateFormResultDTO formType(@Nullable String formType) {
    this.formType = formType;
    return this;
  }

  /**
   * Get formType
   * @return formType
   */
  
  @JsonProperty("formType")
  public @Nullable String getFormType() {
    return formType;
  }

  public void setFormType(@Nullable String formType) {
    this.formType = formType;
  }

  public GenerateFormResultDTO downloadUrl(@Nullable String downloadUrl) {
    this.downloadUrl = downloadUrl;
    return this;
  }

  /**
   * Get downloadUrl
   * @return downloadUrl
   */
  
  @JsonProperty("downloadUrl")
  public @Nullable String getDownloadUrl() {
    return downloadUrl;
  }

  public void setDownloadUrl(@Nullable String downloadUrl) {
    this.downloadUrl = downloadUrl;
  }

  public GenerateFormResultDTO fileName(@Nullable String fileName) {
    this.fileName = fileName;
    return this;
  }

  /**
   * Get fileName
   * @return fileName
   */
  
  @JsonProperty("fileName")
  public @Nullable String getFileName() {
    return fileName;
  }

  public void setFileName(@Nullable String fileName) {
    this.fileName = fileName;
  }

  public GenerateFormResultDTO dividendCount(@Nullable Integer dividendCount) {
    this.dividendCount = dividendCount;
    return this;
  }

  /**
   * Get dividendCount
   * @return dividendCount
   */
  
  @JsonProperty("dividendCount")
  public @Nullable Integer getDividendCount() {
    return dividendCount;
  }

  public void setDividendCount(@Nullable Integer dividendCount) {
    this.dividendCount = dividendCount;
  }

  public GenerateFormResultDTO errors(List<String> errors) {
    this.errors = errors;
    return this;
  }

  public GenerateFormResultDTO addErrorsItem(String errorsItem) {
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
    GenerateFormResultDTO generateFormResultDTO = (GenerateFormResultDTO) o;
    return Objects.equals(this.success, generateFormResultDTO.success) &&
        Objects.equals(this.formId, generateFormResultDTO.formId) &&
        Objects.equals(this.formType, generateFormResultDTO.formType) &&
        Objects.equals(this.downloadUrl, generateFormResultDTO.downloadUrl) &&
        Objects.equals(this.fileName, generateFormResultDTO.fileName) &&
        Objects.equals(this.dividendCount, generateFormResultDTO.dividendCount) &&
        Objects.equals(this.errors, generateFormResultDTO.errors);
  }

  @Override
  public int hashCode() {
    return Objects.hash(success, formId, formType, downloadUrl, fileName, dividendCount, errors);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GenerateFormResultDTO {\n");
    sb.append("    success: ").append(toIndentedString(success)).append("\n");
    sb.append("    formId: ").append(toIndentedString(formId)).append("\n");
    sb.append("    formType: ").append(toIndentedString(formType)).append("\n");
    sb.append("    downloadUrl: ").append(toIndentedString(downloadUrl)).append("\n");
    sb.append("    fileName: ").append(toIndentedString(fileName)).append("\n");
    sb.append("    dividendCount: ").append(toIndentedString(dividendCount)).append("\n");
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

