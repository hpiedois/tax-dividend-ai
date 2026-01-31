import { describe, it, expect } from 'vitest';
import { cn } from '../utils';

describe('cn utility', () => {
    it('should merge class names correctly', () => {
        expect(cn('bg-red-500', 'text-white')).toBe('bg-red-500 text-white');
    });

    it('should handle conditional classes', () => {
        const isActive = true;
        const isDisabled = false;
        expect(cn('base-class', isActive && 'active-class', isDisabled && 'disabled-class')).toBe('base-class active-class');
    });

    it('should merge tailwind classes properly (tailwind-merge)', () => {
        expect(cn('px-2 py-1', 'px-4')).toBe('py-1 px-4');
    });
});
