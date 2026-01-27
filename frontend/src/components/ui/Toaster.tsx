import { Toaster as Sonner } from 'sonner';
import { useTheme } from '../../hooks/useTheme';

export function Toaster() {
  const { theme } = useTheme();

  return (
    <Sonner
      theme={theme === 'system' ? undefined : theme}
      position="top-center"
      offset={20}
      gap={12}
      visibleToasts={5}
      closeButton
      richColors
      expand
      toastOptions={{
        duration: 4000,
        classNames: {
          toast:
            'group toast group-[.toaster]:bg-white dark:group-[.toaster]:bg-slate-950 group-[.toaster]:text-slate-900 dark:group-[.toaster]:text-slate-50 group-[.toaster]:border group-[.toaster]:border-slate-200 dark:group-[.toaster]:border-slate-800 group-[.toaster]:shadow-2xl group-[.toaster]:rounded-xl group-[.toaster]:backdrop-blur-sm',
          description: 'group-[.toast]:text-slate-600 dark:group-[.toast]:text-slate-400',
          actionButton:
            'group-[.toast]:bg-brand-600 group-[.toast]:text-white group-[.toast]:rounded-lg group-[.toast]:font-medium',
          cancelButton:
            'group-[.toast]:bg-slate-100 dark:group-[.toast]:bg-slate-800 group-[.toast]:text-slate-700 dark:group-[.toast]:text-slate-300 group-[.toast]:rounded-lg',
          success:
            'group-[.toaster]:bg-emerald-50 dark:group-[.toaster]:bg-emerald-950/30 group-[.toaster]:border-emerald-200 dark:group-[.toaster]:border-emerald-800 group-[.toaster]:text-emerald-900 dark:group-[.toaster]:text-emerald-100',
          error:
            'group-[.toaster]:bg-red-50 dark:group-[.toaster]:bg-red-950/30 group-[.toaster]:border-red-200 dark:group-[.toaster]:border-red-800 group-[.toaster]:text-red-900 dark:group-[.toaster]:text-red-100',
          warning:
            'group-[.toaster]:bg-orange-50 dark:group-[.toaster]:bg-orange-950/30 group-[.toaster]:border-orange-200 dark:group-[.toaster]:border-orange-800 group-[.toaster]:text-orange-900 dark:group-[.toaster]:text-orange-100',
          info:
            'group-[.toaster]:bg-blue-50 dark:group-[.toaster]:bg-blue-950/30 group-[.toaster]:border-blue-200 dark:group-[.toaster]:border-blue-800 group-[.toaster]:text-blue-900 dark:group-[.toaster]:text-blue-100',
        },
      }}
    />
  );
}
