// File validation constants
export const ALLOWED_FILE_TYPES = ['application/pdf'] as const;
export const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
export const MAX_FILES = 10;

export interface FileValidationError {
  file: File;
  error: string;
}

export interface FileValidationResult {
  valid: File[];
  invalid: FileValidationError[];
}

/**
 * Validate a single file
 */
export const validateFile = (file: File): string | null => {
  // Check file type
  if (!ALLOWED_FILE_TYPES.includes(file.type as typeof ALLOWED_FILE_TYPES[number])) {
    return 'validation.error.invalid_type';
  }

  // Check file size
  if (file.size > MAX_FILE_SIZE) {
    return 'validation.error.file_too_large';
  }

  // Check file name (basic sanitization)
  if (file.name.length > 255) {
    return 'validation.error.filename_too_long';
  }

  return null;
};

/**
 * Validate multiple files
 */
export const validateFiles = (files: File[]): FileValidationResult => {
  const result: FileValidationResult = {
    valid: [],
    invalid: [],
  };

  // Check number of files
  if (files.length > MAX_FILES) {
    // Reject all files if too many
    files.forEach(file => {
      result.invalid.push({
        file,
        error: 'validation.error.too_many_files',
      });
    });
    return result;
  }

  // Validate each file
  files.forEach(file => {
    const error = validateFile(file);
    if (error) {
      result.invalid.push({ file, error });
    } else {
      result.valid.push(file);
    }
  });

  return result;
};

/**
 * Format file size for display
 */
export const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return `${parseFloat((bytes / Math.pow(k, i)).toFixed(2))} ${sizes[i]}`;
};
