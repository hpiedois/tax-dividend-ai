package com.taxdividend.backend.model;

import java.util.Set;

/**
 * Status enum for dividend statement workflow.
 * Defines valid status transitions.
 */
public enum DividendStatementStatus {
    /**
     * File uploaded, awaiting parsing by AI Agent
     */
    UPLOADED(Set.of("PARSING")),

    /**
     * Currently being parsed by AI Agent
     */
    PARSING(Set.of("PARSED")),

    /**
     * Parsing complete, dividends extracted
     */
    PARSED(Set.of("VALIDATED")),

    /**
     * Dividends validated, forms generated and downloaded by user
     */
    VALIDATED(Set.of("SENT")),

    /**
     * User has submitted forms to tax authority (offline)
     */
    SENT(Set.of("PAID")),

    /**
     * Reimbursement received (marked manually by user)
     */
    PAID(Set.of()); // Terminal state, no transitions

    private final Set<String> allowedTransitions;

    DividendStatementStatus(Set<String> allowedTransitions) {
        this.allowedTransitions = allowedTransitions;
    }

    /**
     * Check if transition to target status is valid.
     *
     * @param target Target status
     * @return true if transition is allowed
     */
    public boolean canTransitionTo(DividendStatementStatus target) {
        return allowedTransitions.contains(target.name());
    }

    /**
     * Get all valid next statuses from current status.
     *
     * @return Set of allowed target status names
     */
    public Set<String> getAllowedTransitions() {
        return allowedTransitions;
    }
}
