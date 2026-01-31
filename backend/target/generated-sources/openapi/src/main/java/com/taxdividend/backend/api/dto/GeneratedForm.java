package com.taxdividend.backend.api.dto;

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


import java.util.*;
import jakarta.annotation.Generated;

/**
 * GeneratedForm
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-31T11:27:14.708089+01:00[Europe/Zurich]", comments = "Generator version: 7.17.0")
public class GeneratedForm {

  private @Nullable UUID id;

  private @Nullable String formType;

  private @Nullable Integer taxYear;

  private @Nullable String fileName;

  private @Nullable String s3Key;

  private @Nullable Long fileSize;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime generatedAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime expiresAt;

  public GeneratedForm id(@Nullable UUID id) {
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

  public GeneratedForm formType(@Nullable String formType) {
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

  public GeneratedForm taxYear(@Nullable Integer taxYear) {
    this.taxYear = taxYear;
    return this;
  }

  /**
   * Get taxYear
   * @return taxYear
   */
  
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
  
  @JsonProperty("fileName")
  public @Nullable String getFileName() {
    return fileName;
  }

  public void setFileName(@Nullable String fileName) {
    this.fileName = fileName;
  }

  public GeneratedForm s3Key(@Nullable String s3Key) {
    this.s3Key = s3Key;
    return this;
  }

  /**
   * Get s3Key
   * @return s3Key
   */
  
  @JsonProperty("s3Key")
  public @Nullable String getS3Key() {
    return s3Key;
  }

  public void setS3Key(@Nullable String s3Key) {
    this.s3Key = s3Key;
  }

  public GeneratedForm fileSize(@Nullable Long fileSize) {
    this.fileSize = fileSize;
    return this;
  }

  /**
   * Get fileSize
   * @return fileSize
   */
  
  @JsonProperty("fileSize")
  public @Nullable Long getFileSize() {
    return fileSize;
  }

  public void setFileSize(@Nullable Long fileSize) {
    this.fileSize = fileSize;
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
  @JsonProperty("generatedAt")
  public @Nullable OffsetDateTime getGeneratedAt() {
    return generatedAt;
  }

  public void setGeneratedAt(@Nullable OffsetDateTime generatedAt) {
    this.generatedAt = generatedAt;
  }

  public GeneratedForm expiresAt(@Nullable OffsetDateTime expiresAt) {
    this.expiresAt = expiresAt;
    return this;
  }

  /**
   * Get expiresAt
   * @return expiresAt
   */
  @Valid 
  @JsonProperty("expiresAt")
  public @Nullable OffsetDateTime getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(@Nullable OffsetDateTime expiresAt) {
    this.expiresAt = expiresAt;
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
        Objects.equals(this.s3Key, generatedForm.s3Key) &&
        Objects.equals(this.fileSize, generatedForm.fileSize) &&
        Objects.equals(this.generatedAt, generatedForm.generatedAt) &&
        Objects.equals(this.expiresAt, generatedForm.expiresAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, formType, taxYear, fileName, s3Key, fileSize, generatedAt, expiresAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GeneratedForm {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    formType: ").append(toIndentedString(formType)).append("\n");
    sb.append("    taxYear: ").append(toIndentedString(taxYear)).append("\n");
    sb.append("    fileName: ").append(toIndentedString(fileName)).append("\n");
    sb.append("    s3Key: ").append(toIndentedString(s3Key)).append("\n");
    sb.append("    fileSize: ").append(toIndentedString(fileSize)).append("\n");
    sb.append("    generatedAt: ").append(toIndentedString(generatedAt)).append("\n");
    sb.append("    expiresAt: ").append(toIndentedString(expiresAt)).append("\n");
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

