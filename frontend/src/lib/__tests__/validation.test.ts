import { describe, it, expect } from 'vitest';
import {
  validateFile,
  validateFiles,
  formatFileSize,
  MAX_FILE_SIZE,
  MAX_FILES,
} from '../validation';

describe('validation', () => {
  describe('validateFile', () => {
    it('should accept valid PDF files', () => {
      const file = new File(['content'], 'test.pdf', { type: 'application/pdf' });
      const error = validateFile(file);
      expect(error).toBeNull();
    });

    it('should reject non-PDF files', () => {
      const file = new File(['content'], 'test.txt', { type: 'text/plain' });
      const error = validateFile(file);
      expect(error).toBe('validation.error.invalid_type');
    });

    it('should reject files larger than MAX_FILE_SIZE', () => {
      const largeContent = new Array(MAX_FILE_SIZE + 1).fill('a').join('');
      const file = new File([largeContent], 'large.pdf', { type: 'application/pdf' });
      const error = validateFile(file);
      expect(error).toBe('validation.error.file_too_large');
    });

    it('should reject files with very long filenames', () => {
      const longName = 'a'.repeat(260) + '.pdf';
      const file = new File(['content'], longName, { type: 'application/pdf' });
      const error = validateFile(file);
      expect(error).toBe('validation.error.filename_too_long');
    });
  });

  describe('validateFiles', () => {
    it('should validate multiple files correctly', () => {
      const file1 = new File(['content'], 'valid1.pdf', { type: 'application/pdf' });
      const file2 = new File(['content'], 'valid2.pdf', { type: 'application/pdf' });

      const result = validateFiles([file1, file2]);

      expect(result.valid).toHaveLength(2);
      expect(result.invalid).toHaveLength(0);
    });

    it('should separate valid and invalid files', () => {
      const validFile = new File(['content'], 'valid.pdf', { type: 'application/pdf' });
      const invalidFile = new File(['content'], 'invalid.txt', { type: 'text/plain' });

      const result = validateFiles([validFile, invalidFile]);

      expect(result.valid).toHaveLength(1);
      expect(result.invalid).toHaveLength(1);
      expect(result.invalid[0].error).toBe('validation.error.invalid_type');
    });

    it('should reject all files if count exceeds MAX_FILES', () => {
      const files = Array.from({ length: MAX_FILES + 1 }, (_, i) =>
        new File(['content'], `file${i}.pdf`, { type: 'application/pdf' })
      );

      const result = validateFiles(files);

      expect(result.valid).toHaveLength(0);
      expect(result.invalid).toHaveLength(MAX_FILES + 1);
      expect(result.invalid[0].error).toBe('validation.error.too_many_files');
    });
  });

  describe('formatFileSize', () => {
    it('should format bytes correctly', () => {
      expect(formatFileSize(0)).toBe('0 B');
      expect(formatFileSize(100)).toBe('100 B');
      expect(formatFileSize(1024)).toBe('1 KB');
      expect(formatFileSize(1536)).toBe('1.5 KB');
      expect(formatFileSize(1048576)).toBe('1 MB');
      expect(formatFileSize(10485760)).toBe('10 MB');
    });
  });
});
