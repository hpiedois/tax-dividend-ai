import { atom } from 'jotai';
import type { Dividend } from '../types/dividend.types';

// Types
export type ScanStep = 'upload' | 'scanning' | 'result';

export interface ProcessingCount {
  current: number;
  total: number;
}

// Atoms
export const scanStepAtom = atom<ScanStep>('upload');
export const scanResultsAtom = atom<Dividend[]>([]);
export const processingCountAtom = atom<ProcessingCount>({ current: 0, total: 0 });

// Derived atoms
export const totalGrossAmountAtom = atom((get) => {
  const results = get(scanResultsAtom);
  return results.reduce((acc, curr) => acc + curr.grossAmount, 0);
});

export const totalReclaimableAtom = atom((get) => {
  const results = get(scanResultsAtom);
  return results.reduce((acc, curr) => acc + (curr.reclaimableAmount ?? 0), 0);
});

export const processingProgressAtom = atom((get) => {
  const { current, total } = get(processingCountAtom);
  return total > 0 ? (current / total) * 100 : 0;
});

// Actions
export const resetScanAtom = atom(null, (_get, set) => {
  set(scanStepAtom, 'upload');
  set(scanResultsAtom, []);
  set(processingCountAtom, { current: 0, total: 0 });
});

export const addScanResultAtom = atom(
  null,
  (get, set, result: Dividend) => {
    set(scanResultsAtom, [...get(scanResultsAtom), result]);
  }
);
