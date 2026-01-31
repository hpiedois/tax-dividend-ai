package com.taxdividend.backend.api.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * HealthCheckResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-31T11:27:14.708089+01:00[Europe/Zurich]", comments = "Generator version: 7.17.0")
public class HealthCheckResponse {

  private @Nullable String status;

  private @Nullable String application;

  private @Nullable Object database;

  private @Nullable Object storage;

  private @Nullable Object taxRules;

  private @Nullable Object services;

  public HealthCheckResponse status(@Nullable String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  
  @JsonProperty("status")
  public @Nullable String getStatus() {
    return status;
  }

  public void setStatus(@Nullable String status) {
    this.status = status;
  }

  public HealthCheckResponse application(@Nullable String application) {
    this.application = application;
    return this;
  }

  /**
   * Get application
   * @return application
   */
  
  @JsonProperty("application")
  public @Nullable String getApplication() {
    return application;
  }

  public void setApplication(@Nullable String application) {
    this.application = application;
  }

  public HealthCheckResponse database(@Nullable Object database) {
    this.database = database;
    return this;
  }

  /**
   * Get database
   * @return database
   */
  
  @JsonProperty("database")
  public @Nullable Object getDatabase() {
    return database;
  }

  public void setDatabase(@Nullable Object database) {
    this.database = database;
  }

  public HealthCheckResponse storage(@Nullable Object storage) {
    this.storage = storage;
    return this;
  }

  /**
   * Get storage
   * @return storage
   */
  
  @JsonProperty("storage")
  public @Nullable Object getStorage() {
    return storage;
  }

  public void setStorage(@Nullable Object storage) {
    this.storage = storage;
  }

  public HealthCheckResponse taxRules(@Nullable Object taxRules) {
    this.taxRules = taxRules;
    return this;
  }

  /**
   * Get taxRules
   * @return taxRules
   */
  
  @JsonProperty("taxRules")
  public @Nullable Object getTaxRules() {
    return taxRules;
  }

  public void setTaxRules(@Nullable Object taxRules) {
    this.taxRules = taxRules;
  }

  public HealthCheckResponse services(@Nullable Object services) {
    this.services = services;
    return this;
  }

  /**
   * Get services
   * @return services
   */
  
  @JsonProperty("services")
  public @Nullable Object getServices() {
    return services;
  }

  public void setServices(@Nullable Object services) {
    this.services = services;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HealthCheckResponse healthCheckResponse = (HealthCheckResponse) o;
    return Objects.equals(this.status, healthCheckResponse.status) &&
        Objects.equals(this.application, healthCheckResponse.application) &&
        Objects.equals(this.database, healthCheckResponse.database) &&
        Objects.equals(this.storage, healthCheckResponse.storage) &&
        Objects.equals(this.taxRules, healthCheckResponse.taxRules) &&
        Objects.equals(this.services, healthCheckResponse.services);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, application, database, storage, taxRules, services);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HealthCheckResponse {\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    application: ").append(toIndentedString(application)).append("\n");
    sb.append("    database: ").append(toIndentedString(database)).append("\n");
    sb.append("    storage: ").append(toIndentedString(storage)).append("\n");
    sb.append("    taxRules: ").append(toIndentedString(taxRules)).append("\n");
    sb.append("    services: ").append(toIndentedString(services)).append("\n");
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

