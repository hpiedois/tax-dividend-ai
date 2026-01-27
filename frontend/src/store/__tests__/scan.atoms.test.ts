import { describe, it, expect } from 'vitest';
import { createStore } from 'jotai';
import {
  scanStepAtom,
  scanResultsAtom,
  processingCountAtom,
  totalGrossAmountAtom,
  totalReclaimableAtom,
  processingProgressAtom,
  resetScanAtom,
  addScanResultAtom,
} from '../scan.atoms';

describe('scan.atoms', () => {
  it('should start at upload step', () => {
    const store = createStore();
    const step = store.get(scanStepAtom);

    expect(step).toBe('upload');
  });

  it('should start with no results', () => {
    const store = createStore();
    const results = store.get(scanResultsAtom);

    expect(results).toEqual([]);
  });

  it('should start with zero processing count', () => {
    const store = createStore();
    const count = store.get(processingCountAtom);

    expect(count).toEqual({ current: 0, total: 0 });
  });

  it('should calculate total gross amount from results', () => {
    const store = createStore();
    store.set(scanResultsAtom, [
      {
        securityName: 'Test 1',
        isin: 'FR123',
        grossAmount: 100,
        currency: 'EUR',
        paymentDate: '2024-01-01',
        withholdingTax: 25,
        reclaimableAmount: 10,
        frenchRate: 0.25,
      },
      {
        securityName: 'Test 2',
        isin: 'FR456',
        grossAmount: 200,
        currency: 'EUR',
        paymentDate: '2024-01-02',
        withholdingTax: 50,
        reclaimableAmount: 20,
        frenchRate: 0.25,
      },
    ]);

    const total = store.get(totalGrossAmountAtom);
    expect(total).toBe(300);
  });

  it('should calculate total reclaimable from results', () => {
    const store = createStore();
    store.set(scanResultsAtom, [
      {
        securityName: 'Test',
        isin: 'FR123',
        grossAmount: 100,
        currency: 'EUR',
        paymentDate: '2024-01-01',
        withholdingTax: 25,
        reclaimableAmount: 10,
        frenchRate: 0.25,
      },
      {
        securityName: 'Test 2',
        isin: 'FR456',
        grossAmount: 200,
        currency: 'EUR',
        paymentDate: '2024-01-02',
        withholdingTax: 50,
        reclaimableAmount: 20,
        frenchRate: 0.25,
      },
    ]);

    const total = store.get(totalReclaimableAtom);
    expect(total).toBe(30);
  });

  it('should calculate processing progress', () => {
    const store = createStore();
    store.set(processingCountAtom, { current: 3, total: 10 });

    const progress = store.get(processingProgressAtom);
    expect(progress).toBe(30);
  });

  it('should reset scan state', () => {
    const store = createStore();

    // Set some state
    store.set(scanStepAtom, 'result');
    store.set(scanResultsAtom, [
      {
        securityName: 'Test',
        isin: 'FR123',
        grossAmount: 100,
        currency: 'EUR',
        paymentDate: '2024-01-01',
        withholdingTax: 25,
        reclaimableAmount: 10,
        frenchRate: 0.25,
      },
    ]);
    store.set(processingCountAtom, { current: 1, total: 1 });

    // Reset
    store.set(resetScanAtom);

    expect(store.get(scanStepAtom)).toBe('upload');
    expect(store.get(scanResultsAtom)).toEqual([]);
    expect(store.get(processingCountAtom)).toEqual({ current: 0, total: 0 });
  });

  it('should add scan result', () => {
    const store = createStore();

    const result = {
      securityName: 'Test',
      isin: 'FR123',
      grossAmount: 100,
      currency: 'EUR',
      paymentDate: '2024-01-01',
      withholdingTax: 25,
      reclaimableAmount: 10,
      frenchRate: 0.25,
    };

    store.set(addScanResultAtom, result);

    const results = store.get(scanResultsAtom);
    expect(results).toHaveLength(1);
    expect(results[0]).toEqual(result);
  });
});
