import { toast } from 'sonner';

/**
 * Show success toast
 */
export const showSuccess = (message: string, description?: string) => {
  toast.success(message, { description });
};

/**
 * Show error toast
 */
export const showError = (message: string, description?: string) => {
  toast.error(message, { description });
};

/**
 * Show info toast
 */
export const showInfo = (message: string, description?: string) => {
  toast.info(message, { description });
};

/**
 * Show warning toast
 */
export const showWarning = (message: string, description?: string) => {
  toast.warning(message, { description });
};

/**
 * Show loading toast (returns toast id for dismissal)
 */
export const showLoading = (message: string) => {
  return toast.loading(message);
};

/**
 * Dismiss a specific toast
 */
export const dismissToast = (toastId: string | number) => {
  toast.dismiss(toastId);
};

/**
 * Dismiss all toasts
 */
export const dismissAllToasts = () => {
  toast.dismiss();
};
