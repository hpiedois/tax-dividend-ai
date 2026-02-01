/**
 * Tax calculation logic for France-Switzerland double taxation treaty
 *
 * French withholding rates:
 * - Standard PFU (Prélèvement Forfaitaire Unique): 12.8%
 * - Progressive tax scale option: 25% (or higher depending on bracket)
 *
 * Treaty rate (France-Switzerland): 15%
 *
 * Reclaimable amount = (French rate - Treaty rate) × Gross amount
 */

export const TAX_RATES = {
  FRENCH_PFU: 0.128,        // 12.8% - Flat tax option
  FRENCH_PROGRESSIVE: 0.25,  // 25% - Progressive scale (simplified)
  TREATY_RATE: 0.15,         // 15% - France-Switzerland treaty
} as const;

export interface TaxCalculationResult {
  grossAmount: number;
  withholdingTax: number;
  treatyAmount: number;
  reclaimableAmount: number;
  appliedRate: number;
}

/**
 * Calculate tax reclaim for a dividend payment
 *
 * @param grossAmount - Gross dividend amount before tax
 * @param frenchRate - French withholding rate applied (default: PFU 12.8%)
 * @returns Tax calculation breakdown
 */
export function calculateTaxReclaim(
  grossAmount: number,
  frenchRate: number = TAX_RATES.FRENCH_PFU
): TaxCalculationResult {
  // Amount withheld by French authorities
  const withholdingTax = grossAmount * frenchRate;

  // Amount that should have been withheld under treaty
  const treatyAmount = grossAmount * TAX_RATES.TREATY_RATE;

  // Amount that can be reclaimed
  const reclaimableAmount = withholdingTax - treatyAmount;

  return {
    grossAmount: Number(grossAmount.toFixed(2)),
    withholdingTax: Number(withholdingTax.toFixed(2)),
    treatyAmount: Number(treatyAmount.toFixed(2)),
    reclaimableAmount: Number(Math.max(0, reclaimableAmount).toFixed(2)),
    appliedRate: frenchRate,
  };
}

/**
 * Calculate net amount received after withholding
 */
export function calculateNetAmount(grossAmount: number, frenchRate: number): number {
  return Number((grossAmount * (1 - frenchRate)).toFixed(2));
}

/**
 * Calculate effective rate after reclaim
 */
export function calculateEffectiveRate(
  grossAmount: number,
  withholdingTax: number,
  reclaimableAmount: number
): number {
  const effectiveTax = withholdingTax - reclaimableAmount;
  return Number(((effectiveTax / grossAmount) * 100).toFixed(2));
}

/**
 * Determine which French rate was likely applied based on withholding amount
 */
export function detectFrenchRate(grossAmount: number, withholdingTax: number): number {
  const rate = withholdingTax / grossAmount;

  // Match to closest standard rate
  if (Math.abs(rate - TAX_RATES.FRENCH_PFU) < 0.01) {
    return TAX_RATES.FRENCH_PFU;
  }
  if (Math.abs(rate - TAX_RATES.FRENCH_PROGRESSIVE) < 0.01) {
    return TAX_RATES.FRENCH_PROGRESSIVE;
  }

  // Return actual rate if non-standard
  return Number(rate.toFixed(4));
}
