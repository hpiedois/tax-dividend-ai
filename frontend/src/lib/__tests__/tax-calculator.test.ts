import { describe, it, expect } from 'vitest';
import {
  calculateTaxReclaim,
  calculateNetAmount,
  calculateEffectiveRate,
  detectFrenchRate,
  TAX_RATES,
} from '../tax-calculator';

describe('tax-calculator', () => {
  describe('calculateTaxReclaim', () => {
    it('should calculate correctly with PFU rate (12.8%)', () => {
      const result = calculateTaxReclaim(1000, TAX_RATES.FRENCH_PFU);

      expect(result.grossAmount).toBe(1000);
      expect(result.withholdingTax).toBe(128); // 1000 * 0.128
      expect(result.treatyAmount).toBe(150); // 1000 * 0.15
      expect(result.reclaimableAmount).toBe(0); // 128 - 150 < 0, so 0
      expect(result.frenchRate).toBe(TAX_RATES.FRENCH_PFU);
    });

    it('should calculate correctly with progressive rate (25%)', () => {
      const result = calculateTaxReclaim(1000, TAX_RATES.FRENCH_PROGRESSIVE);

      expect(result.grossAmount).toBe(1000);
      expect(result.withholdingTax).toBe(250); // 1000 * 0.25
      expect(result.treatyAmount).toBe(150); // 1000 * 0.15
      expect(result.reclaimableAmount).toBe(100); // 250 - 150
      expect(result.frenchRate).toBe(TAX_RATES.FRENCH_PROGRESSIVE);
    });

    it('should never return negative reclaimable amount', () => {
      // Case where French rate is lower than treaty rate (shouldn't happen in reality)
      const result = calculateTaxReclaim(1000, 0.10);

      expect(result.reclaimableAmount).toBeGreaterThanOrEqual(0);
    });

    it('should handle decimal amounts correctly', () => {
      const result = calculateTaxReclaim(1234.56, TAX_RATES.FRENCH_PROGRESSIVE);

      expect(result.grossAmount).toBe(1234.56);
      expect(result.withholdingTax).toBe(308.64); // 1234.56 * 0.25
      expect(result.treatyAmount).toBe(185.18); // 1234.56 * 0.15
      expect(result.reclaimableAmount).toBe(123.46); // 308.64 - 185.18
    });
  });

  describe('calculateNetAmount', () => {
    it('should calculate net amount after withholding', () => {
      expect(calculateNetAmount(1000, TAX_RATES.FRENCH_PFU)).toBe(872); // 1000 * (1 - 0.128)
      expect(calculateNetAmount(1000, TAX_RATES.FRENCH_PROGRESSIVE)).toBe(750); // 1000 * (1 - 0.25)
    });

    it('should handle decimal values', () => {
      expect(calculateNetAmount(1234.56, TAX_RATES.FRENCH_PFU)).toBe(1076.54);
    });
  });

  describe('calculateEffectiveRate', () => {
    it('should calculate effective tax rate', () => {
      const gross = 1000;
      const withheld = 250;
      const reclaimable = 100;

      const effective = calculateEffectiveRate(gross, withheld, reclaimable);

      expect(effective).toBe(15); // (250 - 100) / 1000 * 100 = 15%
    });

    it('should return treaty rate when full reclaim', () => {
      const gross = 1000;
      const withheld = 250;
      const reclaimable = 100; // Will result in 15% effective

      const effective = calculateEffectiveRate(gross, withheld, reclaimable);

      expect(effective).toBe(15);
    });
  });

  describe('detectFrenchRate', () => {
    it('should detect PFU rate', () => {
      const rate = detectFrenchRate(1000, 128); // 12.8%
      expect(rate).toBe(TAX_RATES.FRENCH_PFU);
    });

    it('should detect progressive rate', () => {
      const rate = detectFrenchRate(1000, 250); // 25%
      expect(rate).toBe(TAX_RATES.FRENCH_PROGRESSIVE);
    });

    it('should handle close but not exact matches', () => {
      const rate = detectFrenchRate(1000, 127); // 12.7%, close to PFU
      expect(rate).toBe(TAX_RATES.FRENCH_PFU);
    });

    it('should return calculated rate for non-standard rates', () => {
      const rate = detectFrenchRate(1000, 300); // 30%, non-standard
      expect(rate).toBe(0.3);
    });
  });

  describe('TAX_RATES constants', () => {
    it('should have correct values', () => {
      expect(TAX_RATES.FRENCH_PFU).toBe(0.128);
      expect(TAX_RATES.FRENCH_PROGRESSIVE).toBe(0.25);
      expect(TAX_RATES.TREATY_RATE).toBe(0.15);
    });

    it('should ensure treaty rate is lower than progressive rate', () => {
      // Treaty rate (15%) is lower than progressive rate (25%) - reclaim possible
      expect(TAX_RATES.TREATY_RATE).toBeLessThan(TAX_RATES.FRENCH_PROGRESSIVE);

      // Treaty rate (15%) is actually HIGHER than PFU (12.8%) - no reclaim with PFU
      expect(TAX_RATES.TREATY_RATE).toBeGreaterThan(TAX_RATES.FRENCH_PFU);
    });
  });
});
