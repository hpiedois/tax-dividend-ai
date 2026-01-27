package com.taxdividend.backend.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PdfGenerationResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-26T22:31:55.344143+01:00[Europe/Zurich]")
public class PdfGenerationResponse {

  private String formId;

  private String downloadUrl;

  private String fileName;

  public PdfGenerationResponse formId(String formId) {
    this.formId = formId;
    return this;
  }

  /**
   * Get formId
   * @return formId
  */
  
  @Schema(name = "formId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("formId")
  public String getFormId() {
    return formId;
  }

  public void setFormId(String formId) {
    this.formId = formId;
  }

  public PdfGenerationResponse downloadUrl(String downloadUrl) {
    this.downloadUrl = downloadUrl;
    return this;
  }

  /**
   * Get downloadUrl
   * @return downloadUrl
  */
  
  @Schema(name = "downloadUrl", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("downloadUrl")
  public String getDownloadUrl() {
    return downloadUrl;
  }

  public void setDownloadUrl(String downloadUrl) {
    this.downloadUrl = downloadUrl;
  }

  public PdfGenerationResponse fileName(String fileName) {
    this.fileName = fileName;
    return this;
  }

  /**
   * Get fileName
   * @return fileName
  */
  
  @Schema(name = "fileName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("fileName")
  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PdfGenerationResponse pdfGenerationResponse = (PdfGenerationResponse) o;
    return Objects.equals(this.formId, pdfGenerationResponse.formId) &&
        Objects.equals(this.downloadUrl, pdfGenerationResponse.downloadUrl) &&
        Objects.equals(this.fileName, pdfGenerationResponse.fileName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(formId, downloadUrl, fileName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PdfGenerationResponse {\n");
    sb.append("    formId: ").append(toIndentedString(formId)).append("\n");
    sb.append("    downloadUrl: ").append(toIndentedString(downloadUrl)).append("\n");
    sb.append("    fileName: ").append(toIndentedString(fileName)).append("\n");
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

